package pl.ec.kafka.szperacz;

import io.micronaut.http.MediaType;
import io.micronaut.http.annotation.Body;
import io.micronaut.http.annotation.Controller;
import io.micronaut.http.annotation.Get;
import io.micronaut.http.annotation.Post;
import io.micronaut.http.annotation.Produces;
import javax.inject.Inject;
import pl.ec.kafka.szperacz.kafka.KafkaSearchingRequestScopeFacade;
import pl.ec.kafka.szperacz.preprocessing.search.SearchPreprocessingRequest;
import pl.ec.kafka.szperacz.preprocessing.search.SearchPreprocessingResponse;
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

    @Post("/search")
    @Produces(MediaType.APPLICATION_JSON)
    SearchResponse search(@Body SearchRequest searchRequest) {
        return kafkaSearchingFacade.search(searchRequest);
    }

    @Post("/searchPreprocessing")
    @Produces(MediaType.APPLICATION_JSON)
    SearchPreprocessingResponse search(@Body SearchPreprocessingRequest searchPreprocessingRequest) {
        return preprocessingFacade.search(searchPreprocessingRequest);
    }

    @Get("/maps/gantry={gantryName}")
    @Produces(MediaType.APPLICATION_JSON)
    MapCluster searchForCluster(String gantryName) {
        return preprocessingFacade.getMapByGantry(gantryName);
    }

}
