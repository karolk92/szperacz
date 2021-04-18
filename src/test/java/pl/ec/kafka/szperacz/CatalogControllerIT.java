package pl.ec.kafka.szperacz;

import static org.junit.jupiter.api.Assertions.assertNotNull;

import io.micronaut.http.HttpRequest;
import io.micronaut.http.client.RxHttpClient;
import io.micronaut.http.client.annotation.Client;
import io.micronaut.test.annotation.MicronautTest;
import java.time.LocalDateTime;
import java.util.Map;
import javax.inject.Inject;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;
import pl.ec.kafka.szperacz.catalog.model.CreateCatalogEntryRequest;
import pl.ec.kafka.szperacz.catalog.model.CreateCatalogRequest;
import pl.ec.kafka.szperacz.catalog.model.CreateKafkaEntryRequest;
import pl.ec.kafka.szperacz.catalog.model.CreatePreprocessingEntryRequest;

@Disabled
@TestInstance(Lifecycle.PER_CLASS)
@MicronautTest(environments = "integration")
public class CatalogControllerIT {

    @Inject
    @Client("/api/szperacz/catalog")
    private RxHttpClient client;

    @Test
    void shouldReturnAllCatalogs() {
        // when
        var actual = client.toBlocking().retrieve(HttpRequest.GET("/"));

        // then
        assertNotNull(actual);
    }

    @Test
    void shouldReturnCatalogEntry() {
        // given
        var catalogId = "catalog_name";
        var entryId = "preprocessing_drilldown";

        // when
        var actual = client.toBlocking().retrieve(HttpRequest.GET("/" + catalogId + "/entries/" + entryId));

        // then
        assertNotNull(actual);
    }

    @Test
    void shouldCreateCatalog() {
        // given
        var request = new CreateCatalogRequest("catalog name 2", "description", "grzegorz brzęczyszczykiewicz");

        // when
        var actual = client.toBlocking().retrieve(HttpRequest.POST("/", request));

        // then
        assertNotNull(actual);
    }

    @Test
    void shouldDeleteCatalog() {
        // given
        var catalogId = "catalog_name";

        // when
        var actual = client.toBlocking().retrieve(HttpRequest.DELETE("/" + catalogId));

        // then
        assertNotNull(actual);
    }

    @Test
    void shouldDeleteEntry() {
        // given
        var catalogId = "catalog_name_2";
        var entryId = "Nejm_oho2";

        // when
        var actual = client.toBlocking().retrieve(HttpRequest.DELETE("/" + catalogId + "/entries/" + entryId));

        // then
        assertNotNull(actual);
    }

    @Test
    void shouldCreateCatalogKafkaEntry() {
        // given
        var content = new CreateKafkaEntryRequest(Map.of(
            "sorted_out", "{ abc: 123}",
            "preprocessed_out", "{ abc: 321}"));
        var request = new CreateCatalogEntryRequest(
            "12345",
            "Nejm oho2",
            "desciption",
            LocalDateTime.now().minusHours(1),
            LocalDateTime.now().plusHours(1),
            content,
            null);

        // when
        var actual = client.toBlocking().retrieve(HttpRequest.PUT("/catalog_name", request));

        // then
        assertNotNull(actual);
    }

    @Test
    void shouldCreateCatalogPreprocessingEntry() {
        // given
        var content = new CreatePreprocessingEntryRequest("sorted_out", "buffered_out", "preprocessed_out", "{content: abc}");
        var request = new CreateCatalogEntryRequest(
            "12345",
            "preprocessing drilldown",
            "description",
            LocalDateTime.now().minusHours(1),
            LocalDateTime.now().plusHours(1),
            null,
            content);

        // when
        var actual = client.toBlocking().retrieve(HttpRequest.PUT("/catalog_name", request));

        // then
        assertNotNull(actual);
    }
}
