package pl.ec.kafka.szperacz.catalog;

import java.util.List;
import javax.inject.Inject;
import javax.inject.Singleton;
import lombok.SneakyThrows;
import pl.ec.kafka.szperacz.SzperaczConfiguration.CatalogConfiguration;
import pl.ec.kafka.szperacz.catalog.model.Catalog;
import pl.ec.kafka.szperacz.catalog.model.CatalogEntry;

@Singleton
public class CatalogRepository {

    private final CatalogFileSystem catalogFileSystem;

    @Inject
    public CatalogRepository(CatalogConfiguration configuration) {
        this.catalogFileSystem = new CatalogFileSystem(
            configuration.getPath(),
            configuration.isCompressFiles());
    }

    List<Catalog> getCatalogsWithoutContent() {
        return catalogFileSystem.getCatalogsWithoutContent();
    }

    @SneakyThrows
    void saveCatalog(Catalog catalog) {
        catalogFileSystem.saveCatalog(catalog);
    }

    void deleteCatalog(String catalogId) {
        catalogFileSystem.deleteCatalog(catalogId);
    }

    void addCatalogEntry(String catalogId, CatalogEntry entry) {
        catalogFileSystem.saveCatalogEntry(catalogId, entry);
    }

    CatalogEntry getCatalogEntry(String catalogId, String entryId) {
        return catalogFileSystem.getCatalogEntry(catalogId, entryId);
    }

    void removeCatalogEntry(String catalogId, String entryId) {
        catalogFileSystem.removeCatalogEntry(catalogId, entryId);
    }

}
