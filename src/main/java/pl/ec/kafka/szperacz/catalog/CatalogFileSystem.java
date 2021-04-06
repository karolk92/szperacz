package pl.ec.kafka.szperacz.catalog;

import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.Writer;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import lombok.SneakyThrows;
import pl.ec.kafka.szperacz.catalog.JsonViews.CatalogOnly;
import pl.ec.kafka.szperacz.catalog.model.Catalog;
import pl.ec.kafka.szperacz.catalog.model.CatalogEntry;

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
        return List.of();
    }

    @SneakyThrows
    void saveCatalog(Catalog catalog) {
        catalog.setId(replaceWhitespaces(catalog.getName()));
        validateCatalogUniqueness(catalog.getId());
        var catalogURI = getCatalogURI(catalog.getId());
        createDirectory(catalogURI);

        saveFile(
            new File(catalogURI.getPath(), CATALOG_META_FILENAME),
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
        validateCatalogExistence(catalogId);

        var entryDirectoryPath = new File(getCatalogURI(catalogId)).toPath().resolve(entry.getId());
        createDirectory(entryDirectoryPath.toUri());

        saveFile(
            new File(entryDirectoryPath.toFile(), CATALOG_ENTRY_META_FILENAME),
            objectMapper.writerWithView(CatalogOnly.class).writeValueAsString(entry),
            DO_NOT_TRY_TO_COMPRESS);

    }

    CatalogEntry getCatalogEntry(String catalogId, String entryId) {
        return null;
    }

    void removeCatalogEntry(String catalogId, String entryId) {

    }

    List<String> listCatalogs() {
        return listDirectories(new File(root));
    }

    List<String> listCatalogEntries(String catalogName) {
        return listDirectories(new File(root.resolve(catalogName)));
    }

    private List<String> listDirectories(File file) {
        return List.of(file.listFiles(File::isDirectory)).stream()
            .map(File::getName)
            .collect(Collectors.toList());
    }

    @SneakyThrows
    private void saveFile(File file, String content, boolean tryToCompress) {
        try (Writer writer = new BufferedWriter(new FileWriter(file))) {
            writer.write(content);
        }
    }

    private void createDirectory(URI path) {
        File theDirectory = new File(path);
        if (!theDirectory.exists()) {
            theDirectory.mkdirs();
        }
    }

    private URI getCatalogURI(String catalogName) {
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
