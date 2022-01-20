package pl.ec.kafka.szperacz.search.kafka;

import io.micronaut.runtime.http.scope.RequestScope;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;
import javax.inject.Inject;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import pl.ec.kafka.szperacz.SzperaczConfiguration;

@Slf4j
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

    public void dumpTopic(SearchRequest searchRequest, String topicName) {
        searchRequest.getTopics().stream()
            .map(topic -> search(topic, searchRequest.getFrom(), searchRequest.getTo(), searchRequest.getKey()))
            .filter(x -> x.getTopic().equals(topicName))
            .findFirst()
            .ifPresent(events -> writeToFile(readBody(events.getEvents()), topicName));
    }

    private void writeToFile(String body, String topic) {
        File file = new File(configuration.getTestProducerDirectory() + topic + "_" + System.currentTimeMillis() + ".json");
        try (FileOutputStream fos = new FileOutputStream(file.getPath())) {
            fos.write(body.getBytes());
        } catch (IOException ex) {
            log.error("An error occurred during file processing");
        }
    }

    private String readBody(List<Event> events) {
        StringBuilder sb = new StringBuilder("[");
        events.forEach(event -> sb.append(event.getBody()).append(","));
        return sb.substring(0, sb.length() - 1) + "]";
    }

    public Events search(String topic, LocalDateTime from, LocalDateTime to, String partitioningKey) {
        var consumer = kafkaClientFactory.create(topic, partitioningKey);
        return Events.anEvents()
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
