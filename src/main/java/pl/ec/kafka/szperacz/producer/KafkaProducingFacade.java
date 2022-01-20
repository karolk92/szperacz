package pl.ec.kafka.szperacz.producer;

import com.fasterxml.jackson.databind.ObjectMapper;
import javax.inject.Singleton;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import pl.ec.kafka.szperacz.search.kafka.VehiclePositionEvent;

@RequiredArgsConstructor
@Singleton
public class KafkaProducingFacade {

    private final EventFileReader eventFileReader;
    private final KafkaProducer kafkaProducer;

    private ObjectMapper mapper = new ObjectMapper();

    @SneakyThrows
    public void produce(String fileName) {
        VehiclePositionEvent[] events = mapper.readValue(eventFileReader.read(fileName), VehiclePositionEvent[].class);

        for (VehiclePositionEvent event : events) {
            kafkaProducer.publishEvent(String.valueOf(event.getDeviceId()), event);
        }
    }
}
