package pl.ec.kafka.szperacz.search.kafka;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder(builderMethodName = "anEvent")
@NoArgsConstructor
@AllArgsConstructor
@Data
public class Event {

    private long timestamp;
    private long offset;
    private String body;
    private int bufferIndex;
}
