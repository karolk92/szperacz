package pl.ec.kafka.szperacz;

import io.micronaut.core.convert.format.Format;
import io.micronaut.http.MediaType;
import io.micronaut.http.annotation.Body;
import io.micronaut.http.annotation.Controller;
import io.micronaut.http.annotation.Get;
import io.micronaut.http.annotation.Post;
import io.micronaut.http.annotation.Produces;
import java.time.LocalDateTime;
import javax.inject.Inject;
import pl.ec.kafka.szperacz.kafka.Events;
import pl.ec.kafka.szperacz.kafka.KafkaSearchingRequestScopeFacade;
import pl.ec.kafka.szperacz.kafka.SearchRequest;
import pl.ec.kafka.szperacz.kafka.SearchResponse;
import pl.ec.kafka.szperacz.preprocessing.PreprocessingFacade;
import pl.ec.kafka.szperacz.preprocessing.model.MapCluster;

@Controller("/api/szperacz")
public class SzperaczController {

    @Inject
    private KafkaSearchingRequestScopeFacade kafkaSearchingFacade;

    @Inject
    private PreprocessingFacade preprocessingFacade;

    @Get("/topic={topic}&from={from}&to={to}&key={partitioningKey}")
    @Produces(MediaType.APPLICATION_JSON)
    Events search(
        String topic,
        @Format("dd-M-yyyy'T'HH:mm:ss") LocalDateTime from,
        @Format("dd-M-yyyy'T'HH:mm:ss") LocalDateTime to, String partitioningKey) {
        return kafkaSearchingFacade.search(topic, from, to, partitioningKey);
    }

    @Post("/search")
    @Produces(MediaType.APPLICATION_JSON)
    SearchResponse search(@Body SearchRequest searchRequest) {
        return kafkaSearchingFacade.search(searchRequest);
    }

    @Get("/maps/gantry={gantryName}")
    @Produces(MediaType.APPLICATION_JSON)
    MapCluster searchForCluster(String gantryName) {
        return preprocessingFacade.getMapByGantry(gantryName);
    }

}
