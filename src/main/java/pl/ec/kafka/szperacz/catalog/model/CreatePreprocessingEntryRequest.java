package pl.ec.kafka.szperacz.catalog.model;

import lombok.Value;

@Value
public class CreatePreprocessingEntryRequest {
    String inputTopic;
    String bufferTopic;
    String outputTopic;

    String content;
}
