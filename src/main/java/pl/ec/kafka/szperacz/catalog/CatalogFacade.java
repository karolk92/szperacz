package pl.ec.kafka.szperacz.catalog;

import java.util.List;
import java.util.stream.Collectors;
import javax.inject.Inject;
import javax.inject.Singleton;
import pl.ec.kafka.szperacz.catalog.model.Catalog;
import pl.ec.kafka.szperacz.catalog.model.CatalogContent;
import pl.ec.kafka.szperacz.catalog.model.CatalogEntry;
import pl.ec.kafka.szperacz.catalog.model.CatalogKafkaContent;
import pl.ec.kafka.szperacz.catalog.model.CatalogPreprocessingContent;
import pl.ec.kafka.szperacz.catalog.model.CreateCatalogEntryRequest;
import pl.ec.kafka.szperacz.catalog.model.CreateCatalogRequest;
import pl.ec.kafka.szperacz.catalog.model.CreateKafkaEntryRequest;
import pl.ec.kafka.szperacz.catalog.model.CreatePreprocessingEntryRequest;

@Singleton
public class CatalogFacade {

    @Inject
    private CatalogRepository repository;

    public List<Catalog> getCatalogs() {
        return repository.getCatalogsWithoutContent();
    }

    public void createCatalog(CreateCatalogRequest request) {
        repository.saveCatalog(Catalog.aCatalog()
            .name(request.getName())
            .description(request.getDescription())
            .owner(request.getOwner())
            .build());
    }

    public void deleteCatalog(String catalogId) {
        repository.deleteCatalog(catalogId);
    }

    public void addEntry(String catalogId, CreateCatalogEntryRequest request) {
        repository.addCatalogEntry(catalogId, toCatalogEntry(request));
    }

    public CatalogEntry getEntry(String catalogId, String entryId) {
        return repository.getCatalogEntry(catalogId, entryId);
    }

    public void removeEntry(String catalogId, String entryId) {
        repository.removeCatalogEntry(catalogId, entryId);
    }

    private CatalogEntry toCatalogEntry(CreateCatalogEntryRequest request) {
        List<CatalogContent> catalogContent;
        String type;

        if (request.getKafkaEntryRequest() != null) {
            catalogContent = toCatalogContents(request.getKafkaEntryRequest());
            type = "kafka";
        } else if (request.getPreprocessingEntryRequest() != null) {
            catalogContent = toCatalogContents(request.getPreprocessingEntryRequest());
            type = "preprocessing";
        } else {
            throw new IllegalStateException("Bad request");
        }

        return CatalogEntry.aCatalogEntry()
            .deviceId(request.getDeviceId())
            .name(request.getName())
            .type(type)
            .from(request.getFrom())
            .to(request.getTo())
            .contents(catalogContent)
            .build();
    }


    private List<CatalogContent> toCatalogContents(CreateKafkaEntryRequest request) {
        return request.getTopicToContent().entrySet().stream()
            .map(entry -> CatalogKafkaContent.aCatalogKafkaContent()
                .topic(entry.getKey())
                .content(entry.getValue())
                .build())
            .collect(Collectors.toList());
    }

    private List<CatalogContent> toCatalogContents(CreatePreprocessingEntryRequest request) {
        return List.of(CatalogPreprocessingContent.aCatalogPreprocessingContent()
            .inputTopic(request.getInputTopic())
            .bufferTopic(request.getBufferTopic())
            .outputTopic(request.getOutputTopic())
            .content(request.getContent())
            .build());
    }
}
