package pl.ec.kafka.szperacz.kafka;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder(builderMethodName = "anEvents")
@NoArgsConstructor
@AllArgsConstructor
@Data
public class Events {

    private String topic;
    private int partition;
    private String deviceId;
    private List<Event> events;
}
