package pl.ec.kafka.szperacz.kafka;

import com.fasterxml.jackson.annotation.JsonFormat;
import java.time.LocalDateTime;
import java.util.List;
import lombok.Value;

@Value
public class SearchRequest {

    @JsonFormat(pattern = "dd-M-yyyy'T'HH:mm:ss")
    LocalDateTime from;
    @JsonFormat(pattern = "dd-M-yyyy'T'HH:mm:ss")
    LocalDateTime to;
    List<String> topics;
    String key;
    boolean preprocessingDrillDown;
}
