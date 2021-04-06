package pl.ec.kafka.szperacz.catalog.model;

import lombok.Builder;
import lombok.Data;

@Builder(builderMethodName = "aCatalogPreprocessingContent")
@Data
public class CatalogPreprocessingContent implements CatalogContent {

    private String inputTopic;
    private String bufferTopic;
    private String outputTopic;

    private String content;
}
