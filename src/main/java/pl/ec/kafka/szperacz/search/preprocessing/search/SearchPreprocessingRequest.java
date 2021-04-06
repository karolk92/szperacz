package pl.ec.kafka.szperacz.search.preprocessing.search;

import com.fasterxml.jackson.annotation.JsonFormat;
import java.time.LocalDateTime;
import lombok.Value;

@Value
public class SearchPreprocessingRequest {

    @JsonFormat(pattern = "dd-M-yyyy'T'HH:mm:ss")
    LocalDateTime from;

    @JsonFormat(pattern = "dd-M-yyyy'T'HH:mm:ss")
    LocalDateTime to;

    String inputTopic;
    String bufferTopic;
    String outputTopic;

    String key;

    int searchBroadeningLimitInMinutes = 60;
}
