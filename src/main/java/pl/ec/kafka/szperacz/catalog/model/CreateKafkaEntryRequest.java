package pl.ec.kafka.szperacz.catalog.model;

import java.util.Map;
import lombok.Value;

@Value
public class CreateKafkaEntryRequest {

    Map<String, String> topicToContent;
}
