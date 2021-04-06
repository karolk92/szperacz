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

@Disabled
@TestInstance(Lifecycle.PER_CLASS)
@MicronautTest(environments = "integration")
public class CatalogControllerIT {

    @Inject
    @Client("/api/szperacz/catalog")
    private RxHttpClient client;

    @Test
    void shouldCreateCatalog() {
        // given
        var request = new CreateCatalogRequest("catalog name", "description", "grzegorz brzęczyszczykiewicz");

        // when
        var actual = client.toBlocking().retrieve(HttpRequest.POST("/", request));

        // then
        assertNotNull(actual);
    }

    @Test
    void shouldCreateCatalogEntry() {
        // given
        var content = new CreateKafkaEntryRequest(Map.of(
            "sorted_out", "{ abc: 123}",
            "preprocessed_out", "{ abc: 321}"));
        var request = new CreateCatalogEntryRequest(
            "12345",
            "Nejm oho",
            LocalDateTime.now().minusHours(1),
            LocalDateTime.now().plusHours(1),
            content,
            null);

        // when
        var actual = client.toBlocking().retrieve(HttpRequest.PUT("/catalog_name", request));

        // then
        assertNotNull(actual);
    }
}
