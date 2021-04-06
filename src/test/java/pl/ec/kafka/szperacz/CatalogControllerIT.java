package pl.ec.kafka.szperacz;

import static org.junit.jupiter.api.Assertions.assertNotNull;

import io.micronaut.http.HttpRequest;
import io.micronaut.http.client.RxHttpClient;
import io.micronaut.http.client.annotation.Client;
import io.micronaut.test.annotation.MicronautTest;
import javax.inject.Inject;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;
import pl.ec.kafka.szperacz.catalog.model.CreateCatalogRequest;

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
}
