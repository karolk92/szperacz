package pl.ec.kafka.szperacz.kafka;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import lombok.Value;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.TopicPartition;

@Value
class KafkaConsumerWrapper {

    private static final int MAX_NUMBER_OF_RECORDS = 25000;
    private static final int POLL_TIMEOUT_MILLISECOND = 500;
    private static final ZoneId LOCAL_DATE_TIME_ZONE_ID = ZoneId.of("UTC");

    String partitioningKeyRecordEntry;
    String partitioningKey;
    TopicPartition topicPartition;
    KafkaConsumer<String, String> consumer;

    public KafkaConsumerWrapper(String partitioningKey, TopicPartition topicPartition,
        KafkaConsumer<String, String> consumer) {
        this.partitioningKey = partitioningKey;
        this.partitioningKeyRecordEntry = "\"deviceId\":" + partitioningKey;
        this.topicPartition = topicPartition;
        this.consumer = consumer;
    }

    List<ConsumerRecord<String, String>> readRecords(LocalDateTime from, LocalDateTime to) {

        consumer.seek(topicPartition, findOffsetForDateTime(from));

        var toTimestamp = dateToTimestamp(to);
        var result = new ArrayList<ConsumerRecord<String, String>>();

        boolean pollEndFlag = false;

        while (!pollEndFlag) {
            var records = consumer.poll(Duration.ofSeconds(POLL_TIMEOUT_MILLISECOND)).records(topicPartition);
            for (ConsumerRecord<String, String> record : records) {
                if (record.timestamp() > toTimestamp) {
                    pollEndFlag = true;
                    break;
                }
                if (recordContainsPartitioningKey(record)) {
                    result.add(record);
                }
            }
        }

        return result;
    }

    private boolean recordContainsPartitioningKey(ConsumerRecord<String, String> record) {
        return record.value().contains(partitioningKeyRecordEntry);
    }

    private Long findOffsetForDateTime(LocalDateTime localDateTime) {
        var beginningOffset = consumer.offsetsForTimes(
            Map.of(topicPartition, dateToTimestamp(localDateTime)));

        if (beginningOffset.isEmpty()) {
            throw new RuntimeException(
                "Unable to find beginning offset for partitioning key:" + partitioningKey + " at partition: " + topicPartition.toString());
        }

        if (beginningOffset.size() > 1) {
            throw new RuntimeException(
                "Given partitioning key: " + partitioningKey + " present on multiple partitions" + " at partition: " + topicPartition.toString());
        }

        return beginningOffset.get(topicPartition).offset();
    }

    private Long dateToTimestamp(LocalDateTime localDateTime) {
        return localDateTime.atZone(LOCAL_DATE_TIME_ZONE_ID).toInstant().toEpochMilli();
    }
}
