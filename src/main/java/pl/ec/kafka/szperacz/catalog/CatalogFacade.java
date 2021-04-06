package pl.ec.kafka.szperacz.catalog;

import java.util.List;
import javax.inject.Inject;
import javax.inject.Singleton;
import pl.ec.kafka.szperacz.catalog.model.Catalog;
import pl.ec.kafka.szperacz.catalog.model.CatalogContent;
import pl.ec.kafka.szperacz.catalog.model.CreateCatalogEntryRequest;
import pl.ec.kafka.szperacz.catalog.model.CreateCatalogRequest;

@Singleton
public class CatalogFacade {

    @Inject
    private CatalogRepository repository;

    public List<Catalog> getCatalogs() {
        return repository.getCatalogs();
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
        repository.addCatalogEntry(
            catalogId, null);
    }

    public CatalogContent getEntry(String catalogId, String entryId) {
        return null;
    }

    public void removeEntry(String catalogId, String entryId) {

    }
}
