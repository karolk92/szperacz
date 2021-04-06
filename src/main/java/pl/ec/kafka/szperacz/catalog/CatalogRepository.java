package pl.ec.kafka.szperacz.catalog;

import java.util.List;
import javax.inject.Inject;
import javax.inject.Singleton;
import pl.ec.kafka.szperacz.catalog.model.Catalog;
import pl.ec.kafka.szperacz.catalog.model.CatalogEntry;
import pl.ec.kafka.szperacz.SzperaczConfiguration.CatalogConfiguration;

@Singleton
public class CatalogRepository {

    @Inject
    private CatalogConfiguration configuration;

    List<Catalog> getCatalogs() {
        return List.of();
    }

    void saveCatalog(Catalog catalog) {

    }

    void deleteCatalog(String catalogId) {

    }

    void addCatalogEntry(String catalogId, CatalogEntry entry) {

    }
}
