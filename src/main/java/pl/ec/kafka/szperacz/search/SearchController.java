package pl.ec.kafka.szperacz.search;

import io.micronaut.http.MediaType;
import io.micronaut.http.annotation.Body;
import io.micronaut.http.annotation.Controller;
import io.micronaut.http.annotation.Get;
import io.micronaut.http.annotation.Post;
import io.micronaut.http.annotation.Produces;
import javax.inject.Inject;
import pl.ec.kafka.szperacz.search.kafka.KafkaSearchingRequestScopeFacade;
import pl.ec.kafka.szperacz.search.preprocessing.search.SearchPreprocessingRequest;
import pl.ec.kafka.szperacz.search.preprocessing.search.SearchPreprocessingResponse;
import pl.ec.kafka.szperacz.search.kafka.SearchRequest;
import pl.ec.kafka.szperacz.search.kafka.SearchResponse;
import pl.ec.kafka.szperacz.search.preprocessing.PreprocessingFacade;
import pl.ec.kafka.szperacz.search.preprocessing.model.MapCluster;

@Controller("/api/szperacz")
public class SearchController {

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
