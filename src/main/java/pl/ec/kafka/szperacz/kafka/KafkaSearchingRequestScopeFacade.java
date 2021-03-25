package pl.ec.kafka.szperacz.kafka;

import io.micronaut.runtime.http.scope.RequestScope;
import java.time.LocalDateTime;
import java.util.List;
import java.util.function.BiPredicate;
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

    public SearchResponse search(SearchRequest searchRequest) {
        var eventsMap = searchRequest.getTopics().stream()
            .map(topic -> search(topic, searchRequest.getFrom(), searchRequest.getTo(), searchRequest.getKey()))
            .collect(Collectors.toList());
        return new SearchResponse(searchRequest.getKey(), eventsMap);
    }

    public Events search(String topic, LocalDateTime from, LocalDateTime to, String partitioningKey) {
        var consumer = kafkaClientFactory.create(topic, partitioningKey);
        return Events.anEvents()
            .partition(consumer.getTopicPartition().partition())
            .topic(consumer.getTopicPartition().topic())
            .events(mapConsumerRecords(consumer.readRecords(from, to, configuration.getEventFetchLimit())))
            .build();
    }

    public <T> Events search(String topic, LocalDateTime form, LocalDateTime to, String partitioningKey, List<T> elements, BiPredicate<T, T> correlatingFunction) {
        var consumer = kafkaClientFactory.create(topic, partitioningKey);
        return Events.anEvents().build();
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
