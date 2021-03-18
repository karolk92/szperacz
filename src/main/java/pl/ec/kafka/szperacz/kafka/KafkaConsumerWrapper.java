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

    private static final int POLL_TIMEOUT_MILLISECOND = 500;
    private static final ZoneId LOCAL_DATE_TIME_ZONE_ID = ZoneId.of("UTC");
    private static final long NOT_FOUND_OFFSET_VALUE = -1L;

    String partitioningKey;
    TopicPartition topicPartition;
    KafkaConsumer<String, String> consumer;

    List<ConsumerRecord<String, String>> readRecords(LocalDateTime from, LocalDateTime to, int limit) {
        long lastOffset = lastOffsetForPartition();
        long firstOffset = findOffsetForDateTime(from);

        if (lastOffset == NOT_FOUND_OFFSET_VALUE) {
            return List.of();
        }

        consumer.seek(topicPartition, firstOffset);

        if (lastOffset == consumer.position(topicPartition)) {
            return List.of();
        }

        long toTimestamp = dateToTimestamp(to);
        var result = new ArrayList<ConsumerRecord<String, String>>();

        boolean pollEndFlag = false;

        while (!pollEndFlag) {
            var records = consumer.poll(Duration.ofSeconds(POLL_TIMEOUT_MILLISECOND)).records(topicPartition);
            for (ConsumerRecord<String, String> record : records) {
                if (record.timestamp() > toTimestamp || record.offset() == (lastOffset - 1) || result.size() >= limit) {
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

    private Long lastOffsetForPartition() {
        consumer.seekToEnd(List.of(topicPartition));
        return consumer.position(topicPartition);
    }

    private boolean recordContainsPartitioningKey(ConsumerRecord<String, String> record) {
        return record.key().equals(partitioningKey);
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

        if (beginningOffset.get(topicPartition) == null) {
            return NOT_FOUND_OFFSET_VALUE;
        }

        return beginningOffset.get(topicPartition).offset();
    }

    private Long dateToTimestamp(LocalDateTime localDateTime) {
        return localDateTime.atZone(LOCAL_DATE_TIME_ZONE_ID).toInstant().toEpochMilli();
    }
}
