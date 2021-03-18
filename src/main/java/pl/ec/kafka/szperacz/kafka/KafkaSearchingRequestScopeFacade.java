package pl.ec.kafka.szperacz.kafka;

import io.micronaut.runtime.http.scope.RequestScope;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;
import javax.inject.Inject;
import org.apache.kafka.clients.consumer.ConsumerRecord;

@RequestScope
public class KafkaSearchingRequestScopeFacade {

    private final KafkaConsumerFactory kafkaClientFactory;
    private final SzperaczConfiguration configuration;

    @Inject
    public KafkaSearchingRequestScopeFacade(SzperaczConfiguration szperaczConfiguration) {
        this.kafkaClientFactory = new KafkaConsumerFactory(szperaczConfiguration.getKafka());
        this.configuration = szperaczConfiguration;
    }

    public Events search(String topic, LocalDateTime from, LocalDateTime to, String deviceId) {
        var consumer = kafkaClientFactory.create(topic, deviceId);
        return Events.anEvents()
            .deviceId(deviceId)
            .partition(consumer.getTopicPartition().partition())
            .topic(consumer.getTopicPartition().topic())
            .events(mapConsumerRecords(consumer.readRecords(from, to, configuration.getEventFetchLimit())))
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
