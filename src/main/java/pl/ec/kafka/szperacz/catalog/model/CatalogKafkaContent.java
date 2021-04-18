package pl.ec.kafka.szperacz.catalog.model;

import lombok.Builder;
import lombok.Data;

@Builder(builderMethodName = "aCatalogKafkaContent")
@Data
public class CatalogKafkaContent implements CatalogContent {

    private String topic;
    private String content;
    private int eventsNumber;
}
