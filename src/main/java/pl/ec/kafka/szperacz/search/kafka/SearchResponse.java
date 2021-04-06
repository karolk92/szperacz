package pl.ec.kafka.szperacz.search.kafka;

import java.util.List;
import lombok.Value;

@Value
public class SearchResponse {

    String deviceId;
    List<Events> events;
}
