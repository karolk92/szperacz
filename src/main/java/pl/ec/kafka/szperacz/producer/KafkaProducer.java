package pl.ec.kafka.szperacz.producer;

import io.micronaut.configuration.kafka.annotation.KafkaClient;
import io.micronaut.configuration.kafka.annotation.KafkaKey;
import io.micronaut.configuration.kafka.annotation.Topic;
import pl.ec.kafka.szperacz.search.kafka.VehiclePositionEvent;

@KafkaClient
public interface KafkaProducer {

    @Topic("sorted_out")
    void publishEvent(@KafkaKey String key, final VehiclePositionEvent event);
}
