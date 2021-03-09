package pl.ec.kafka.szperacz.kafka;

import io.micronaut.core.convert.format.Format;
import io.micronaut.http.MediaType;
import io.micronaut.http.annotation.Controller;
import io.micronaut.http.annotation.Get;
import io.micronaut.http.annotation.Produces;
import java.time.LocalDateTime;
import javax.inject.Inject;

@Controller("/api/szperacz")
public class SzperaczController {

    @Inject
    private KafkaSearchingRequestScopeFacade kafkaSearchingFacade;

    @Get("/topic={topic}&from={from}&to={to}&deviceId={deviceId}")
    @Produces(MediaType.APPLICATION_JSON)
    Events search(
        String topic,
        @Format("dd-M-yyyy'T'HH:mm:ss") LocalDateTime from,
        @Format("dd-M-yyyy'T'HH:mm:ss") LocalDateTime to, String deviceId) {
        return kafkaSearchingFacade.search(topic, from, to, deviceId);
    }
}
