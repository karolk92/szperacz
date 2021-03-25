package pl.ec.kafka.szperacz.preprocessing.search;

import com.google.common.collect.Lists;
import java.util.List;
import lombok.Builder;
import lombok.Data;
import lombok.Singular;
import pl.ec.kafka.szperacz.kafka.Event;

@Builder(builderMethodName = "aBufferedEvent")
@Data
public class BufferedEvent {

    private Event bufferEvent;

    @Singular
    private List<Event> inputEvents;
    private List<Event> outputEvents;

    private int bufferPartition;
    private int inputPartition;
    private int outputPartition;

    public void addOutputEvents(Event event) {
        if (this.outputEvents == null) {
            this.outputEvents = Lists.newArrayList();
        }
        this.outputEvents.add(event);
    }
}
