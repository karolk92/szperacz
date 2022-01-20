package pl.ec.kafka.szperacz.test;

import io.micronaut.http.MediaType;
import io.micronaut.http.annotation.Body;
import io.micronaut.http.annotation.Controller;
import io.micronaut.http.annotation.Get;
import io.micronaut.http.annotation.PathVariable;
import io.micronaut.http.annotation.Post;
import io.micronaut.http.annotation.Produces;
import javax.inject.Inject;
import pl.ec.kafka.szperacz.producer.KafkaProducingFacade;
import pl.ec.kafka.szperacz.search.kafka.KafkaSearchingRequestScopeFacade;
import pl.ec.kafka.szperacz.search.kafka.SearchRequest;

@Controller("/api/test")
public class TestController {

    @Inject
    private KafkaProducingFacade kafkaProducingFacade;

    @Inject
    private KafkaSearchingRequestScopeFacade kafkaSearchingFacade;

    /**
     * Creates a data dump from specified kafka topic
     * @param searchRequest
     * @param topic
     */
    @Post("/dump/{topic}")
    @Produces(MediaType.APPLICATION_JSON)
    void dumpTopic(@Body SearchRequest searchRequest, @PathVariable String topic) {
        kafkaSearchingFacade.dumpTopic(searchRequest, topic);
    }

    /**
     * Produces data from json file to kafka topic
     * @param file filename (containing extension) from test/data/ directory
     */
    @Get("/produce/{file}")
    @Produces(MediaType.APPLICATION_JSON)
    void produce(@PathVariable String file) {
        kafkaProducingFacade.produce(file);
    }
}
