package pl.ec.kafka.szperacz.kafka;

import java.net.InetAddress;
import java.util.List;
import java.util.Properties;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.PartitionInfo;
import org.apache.kafka.common.TopicPartition;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.kafka.common.utils.Utils;
import pl.ec.kafka.szperacz.kafka.SzperaczConfiguration.KafkaConfiguration;

@RequiredArgsConstructor
public class KafkaConsumerFactory {

    private final KafkaConfiguration configuration;

    @SneakyThrows
    KafkaConsumerWrapper create(String topic, String partitioningKey) {
        var consumer = new KafkaConsumer<String, String>(configuration());
        var topicPartition = assignToPartition(consumer, topic, partitioningKey);
        return new KafkaConsumerWrapper(partitioningKey, topicPartition, consumer);
    }

    @SneakyThrows
    private Properties configuration() {
        Properties config = new Properties();
        config.put("client.id", InetAddress.getLocalHost().getHostName());
        config.put("group.id", configuration.getGroupId());
        config.put("key.deserializer", StringDeserializer.class.getName());
        config.put("value.deserializer", StringDeserializer.class.getName());
        config.put("bootstrap.servers", configuration.getBootstrapServers());
        return config;
    }

    private TopicPartition assignToPartition(KafkaConsumer<String, String> consumer, String topic, String partitioningKey) {
        var topicPartition = toTopicPartition(findPartitionForKey(consumer.partitionsFor(topic), partitioningKey));
        consumer.assign(List.of(topicPartition));
        return topicPartition;
    }

    private TopicPartition toTopicPartition(PartitionInfo partitionInfo) {
        return new TopicPartition(partitionInfo.topic(), partitionInfo.partition());
    }

    private PartitionInfo findPartitionForKey(List<PartitionInfo> partitions, String partitionKey) {
        var partitionNumber = determinePartitionNumber(partitionKey, partitions.size());
        return partitions.stream().filter(partition -> partition.partition() == partitionNumber).findFirst().orElseThrow();
    }

    private int determinePartitionNumber(String deviceId, int numberOfPartitions) {
        return Utils.toPositive(Utils.murmur2(deviceId.getBytes())) % numberOfPartitions;
    }
}
