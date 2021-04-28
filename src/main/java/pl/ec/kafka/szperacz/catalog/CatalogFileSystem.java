package pl.ec.kafka.szperacz.catalog;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.Writer;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.zip.GZIPOutputStream;
import lombok.SneakyThrows;
import pl.ec.kafka.szperacz.catalog.JsonViews.CatalogOnly;
import pl.ec.kafka.szperacz.catalog.model.Catalog;
import pl.ec.kafka.szperacz.catalog.model.CatalogContent;
import pl.ec.kafka.szperacz.catalog.model.CatalogEntry;
import pl.ec.kafka.szperacz.catalog.model.CatalogKafkaContent;
import pl.ec.kafka.szperacz.catalog.model.CatalogPreprocessingContent;

class CatalogFileSystem {

    private static final String CATALOG_META_FILENAME = "catalog.json";
    private static final String CATALOG_ENTRY_META_FILENAME = "entry.json";

    private static final boolean DO_NOT_TRY_TO_COMPRESS = false;
    private static final boolean TRY_TO_COMPRESS = true;

    private final ObjectMapper objectMapper;
    private final URI root;
    private final boolean compress;

    CatalogFileSystem(String root, boolean compress) {
        this.root = new File(root).toURI();
        this.compress = compress;
        this.objectMapper = new ObjectMapper();
        this.objectMapper.registerModule(new JavaTimeModule());
        createDirectory(this.root);
    }

    List<Catalog> getCatalogsWithoutContent() {
        return listDirectories(new File(root)).stream()
            .map(this::readCatalog)
            .collect(Collectors.toList());
    }

    @SneakyThrows
    void saveCatalog(Catalog catalog) {
        catalog.setId(replaceWhitespaces(catalog.getName()));
        validateCatalogUniqueness(catalog.getId());
        var catalogUri = getCatalogUri(catalog.getId());
        createDirectory(catalogUri);

        saveFile(
            new File(catalogUri.getPath(), CATALOG_META_FILENAME),
            objectMapper.writerWithView(CatalogOnly.class).writeValueAsString(catalog),
            DO_NOT_TRY_TO_COMPRESS);
    }

    @SneakyThrows
    void deleteCatalog(String catalogId) {
        var catalogPath = new File(root.resolve(catalogId)).toPath();
        Files.walk(catalogPath)
            .sorted(Comparator.reverseOrder())
            .map(Path::toFile)
            .forEach(File::delete);
    }

    @SneakyThrows
    void saveCatalogEntry(String catalogId, CatalogEntry entry) {
        entry.setId(replaceWhitespaces(entry.getName()));
        entry.setCompressed(compress);
        validateCatalogExistence(catalogId);

        var entryDirectoryPath = new File(getCatalogUri(catalogId)).toPath().resolve(entry.getId());
        createDirectory(entryDirectoryPath.toUri());

        List<String> topics = null;

        if (entry.isKafkaType()) {
            topics = saveKafkaEntries(entryDirectoryPath, entry.getContents());
        } else if (entry.isPreprocessingType()) {
            topics = savePreprocessingEntries(entryDirectoryPath, entry.getContents());
        }

        entry.setTopics(topics);

        saveFile(
            new File(entryDirectoryPath.toFile(), CATALOG_ENTRY_META_FILENAME),
            objectMapper.writerWithView(CatalogOnly.class).writeValueAsString(entry),
            DO_NOT_TRY_TO_COMPRESS);
    }

    @SneakyThrows
    CatalogEntry getCatalogEntry(String catalogId, String entryId) {
        var entryPath = new File(root.resolve(catalogId)).toPath().resolve(entryId);
        var catalogEntry = objectMapper.readValue(entryPath.resolve(CATALOG_ENTRY_META_FILENAME).toFile(), CatalogEntry.class);

        List<CatalogContent> catalogContents = null;

        if (catalogEntry.isPreprocessingType()) {
            catalogContents = List.of(CatalogPreprocessingContent.aCatalogPreprocessingContent()
                .inputTopic(catalogEntry.getTopics().get(0))
                .bufferTopic(catalogEntry.getTopics().get(1))
                .outputTopic(catalogEntry.getTopics().get(2))
                .content(
                    readFile(entryPath.resolve(
                        FileNameAssignor.preprocessingContentFileName(
                            catalogEntry.getTopics(),
                            catalogEntry.isCompressed())).toFile()))
                .build());
        } else if (catalogEntry.isKafkaType()) {
            catalogContents = catalogEntry.getTopics().stream()
                .map(topic -> CatalogKafkaContent.aCatalogKafkaContent()
                    .topic(topic)
                    .content(readFile(entryPath.resolve(FileNameAssignor.kafkaContentFileName(topic, catalogEntry.isCompressed())).toFile()))
                    .build())
                .collect(Collectors.toList());
        }

        catalogEntry.setContents(catalogContents);

        return catalogEntry;
    }

    @SneakyThrows
    void removeCatalogEntry(String catalogId, String entryId) {
        var catalogPath = new File(root.resolve(catalogId)).toPath().resolve(entryId);
        Files.walk(catalogPath)
            .sorted(Comparator.reverseOrder())
            .map(Path::toFile)
            .forEach(File::delete);
    }

    @SneakyThrows
    private Catalog readCatalog(File catalogFile) {
        var catalog = objectMapper.readValue(catalogFile.toPath().resolve(CATALOG_META_FILENAME).toFile(), Catalog.class);
        catalog.setEntries(readCatalogEntries(catalogFile));
        return catalog;
    }

    private List<CatalogEntry> readCatalogEntries(File catalogFile) {
        return List.of(catalogFile.listFiles(File::isDirectory)).stream()
            .map(this::readCatalogEntryMetaFile)
            .collect(Collectors.toList());
    }

    @SneakyThrows
    private CatalogEntry readCatalogEntryMetaFile(File entryFile) {
        return objectMapper.readValue(entryFile.toPath().resolve(CATALOG_ENTRY_META_FILENAME).toFile(), CatalogEntry.class);
    }

    private List<String> saveKafkaEntries(Path directoryPath, List<CatalogContent> entries) {
        List<String> topics = new ArrayList<>();
        entries.stream().map(CatalogKafkaContent.class::cast).forEach(content -> {
            topics.add(content.getTopic());
            saveFile(
                new File(directoryPath.toFile(), FileNameAssignor.assignFileName(content, compress)),
                content.getContent(),
                TRY_TO_COMPRESS);
        });
        return topics;
    }

    private List<String> savePreprocessingEntries(Path directoryPath, List<CatalogContent> entries) {
        var content = (CatalogPreprocessingContent) entries.get(0);
        saveFile(
            new File(directoryPath.toFile(), FileNameAssignor.assignFileName(content, compress)), content.getContent(),
            TRY_TO_COMPRESS);
        return List.of(content.getInputTopic(), content.getBufferTopic(), content.getOutputTopic());
    }

    List<String> listCatalogs() {
        return listDirectories(new File(root)).stream()
            .map(File::getName)
            .collect(Collectors.toList());
    }

    @SuppressFBWarnings("NP_NULL_ON_SOME_PATH_FROM_RETURN_VALUE")
    private List<File> listDirectories(File file) {
        return List.of(Objects.requireNonNull(file.listFiles(File::isDirectory)));
    }

    @SneakyThrows
    private void saveFile(File file, String content, boolean tryToCompress) {
        var compress = tryToCompress && this.compress;
        if (!compress) {
            try (Writer writer = new BufferedWriter(new FileWriter(file, StandardCharsets.UTF_8))) {
                writer.write(content);
            }
        } else {
            try (GZIPOutputStream gzipOutputStream = new GZIPOutputStream(new FileOutputStream(file))) {
                gzipOutputStream.write(content.getBytes(StandardCharsets.UTF_8));
                gzipOutputStream.finish();
            }
        }
    }

    @SneakyThrows
    private String readFile(File file) {
        return Files.readString(file.toPath(), StandardCharsets.UTF_8);
    }

    @SuppressFBWarnings("RV_RETURN_VALUE_IGNORED_BAD_PRACTICE")
    private void createDirectory(URI path) {
        File theDirectory = new File(path);
        if (!theDirectory.exists()) {
            theDirectory.mkdirs();
        }
    }

    private URI getCatalogUri(String catalogName) {
        return root.resolve(catalogName);
    }

    static String replaceWhitespaces(String name) {
        return name.replaceAll(" ", "_");
    }

    private void validateCatalogUniqueness(String catalogName) {
        if (listCatalogs().stream().anyMatch(catalog -> Objects.equals(catalog, catalogName))) {
            throw new IllegalStateException("Catalog " + catalogName + " already exists");
        }
    }

    private void validateCatalogExistence(String catalogName) {
        if (listCatalogs().stream().noneMatch(catalog -> Objects.equals(catalog, catalogName))) {
            throw new IllegalStateException("Catalog " + catalogName + " does not exist");
        }
    }
}
