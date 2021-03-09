package pl.ec.kafka.szperacz.kafka;

import io.micronaut.runtime.http.scope.RequestScope;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;
import org.apache.kafka.clients.consumer.ConsumerRecord;

@RequestScope
public class KafkaSearchingRequestScopeFacade {

    private static final KafkaConsumerFactory KAFKA_CLIENT_FACTORY = new KafkaConsumerFactory();

    public Events search(String topic, LocalDateTime from, LocalDateTime to, String deviceId) {
        var consumer = KAFKA_CLIENT_FACTORY.create(topic, deviceId);
        return Events.anEvents()
            .deviceId(deviceId)
            .partition(consumer.getTopicPartition().partition())
            .topic(consumer.getTopicPartition().topic())
            .events(mapConsumerRecords(consumer.readRecords(from, to)))
            .build();
    }

    private List<Event> mapConsumerRecords(List<ConsumerRecord<String, String>> records) {
        return records.stream().map(this::mapConsumerRecord).collect(Collectors.toList());
    }

    private Event mapConsumerRecord(ConsumerRecord<String, String> record) {
        return Event.anEvent()
            .timestamp(record.timestamp())
            .offset(record.offset())
            .body(record.value())
            .build();
    }
}
