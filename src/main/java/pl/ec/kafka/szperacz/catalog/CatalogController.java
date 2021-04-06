package pl.ec.kafka.szperacz.catalog;

import io.micronaut.http.MediaType;
import io.micronaut.http.annotation.Body;
import io.micronaut.http.annotation.Controller;
import io.micronaut.http.annotation.Delete;
import io.micronaut.http.annotation.Get;
import io.micronaut.http.annotation.Post;
import io.micronaut.http.annotation.Produces;
import io.micronaut.http.annotation.Put;
import java.util.List;
import javax.inject.Inject;
import pl.ec.kafka.szperacz.catalog.model.Catalog;
import pl.ec.kafka.szperacz.catalog.model.CatalogContent;
import pl.ec.kafka.szperacz.catalog.model.CreateCatalogEntryRequest;
import pl.ec.kafka.szperacz.catalog.model.CreateCatalogRequest;

@Controller("/api/szperacz/catalog")
public class CatalogController {

    @Inject
    private CatalogFacade facade;

    @Get
    @Produces(MediaType.APPLICATION_JSON)
    List<Catalog> allCatalogs() {
        return facade.getCatalogs();
    }

    @Post
    @Produces(MediaType.APPLICATION_JSON)
    void createCatalog(@Body CreateCatalogRequest request) {
        facade.createCatalog(request);
    }

    @Delete("/{catalogId}")
    @Produces(MediaType.APPLICATION_JSON)
    void deleteCatalog(String catalogId) {
        facade.deleteCatalog(catalogId);
    }

    @Put("/{catalogId}")
    @Produces(MediaType.APPLICATION_JSON)
    void addCatalogEntry(String catalogId, @Body CreateCatalogEntryRequest request) {
        facade.addEntry(catalogId, request);
    }

    @Get("/{catalogId}/entries/{entryId}")
    @Produces(MediaType.APPLICATION_JSON)
    CatalogContent getCatalogEntry(String catalogId, String entryId) {
        return facade.getEntry(catalogId, entryId);
    }

    @Delete("/{catalogId}/entries/{entryId}")
    @Produces(MediaType.APPLICATION_JSON)
    void removeCatalogEntry(String catalogId, String entryId){
        facade.removeEntry(catalogId, entryId);
    }
}
