package pl.ec.kafka.szperacz.catalog.model;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import java.util.Map;
import lombok.Value;

@Value
public class CreateKafkaEntryRequest {

    @JsonDeserialize(using = CustomMapDeserializer.class)
    Map<String, String> topicToContent;
}
