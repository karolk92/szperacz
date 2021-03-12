package pl.ec.kafka.szperacz;

import static org.junit.jupiter.api.Assertions.assertNotNull;

import io.micronaut.core.type.Argument;
import io.micronaut.http.HttpRequest;
import io.micronaut.http.client.RxHttpClient;
import io.micronaut.http.client.annotation.Client;
import io.micronaut.test.annotation.MicronautTest;
import javax.inject.Inject;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;
import pl.ec.kafka.szperacz.kafka.Events;

@TestInstance(Lifecycle.PER_CLASS)
@MicronautTest(environments = "integration")
class SzperaczControllerIT {

    @Inject
    @Client("/api/szperacz")
    private RxHttpClient client;

    @Test
    void shouldSearchForEvents() {
        // given
        String topic = "sorted_out";
        String from = "04-02-2021T12:50:00";
        String to = "04-02-2021T13:50:00";
        String deviceId = "4935";

        // when
        var actual = client.toBlocking().retrieve(
            HttpRequest.GET("/topic=" + topic + "&from=" + from + "&to=" + to + "&deviceId=" + deviceId),
            Argument.listOf(Events.class));

        // then
        assertNotNull(actual);
    }
}