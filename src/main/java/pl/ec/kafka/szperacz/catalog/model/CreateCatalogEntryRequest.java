package pl.ec.kafka.szperacz.catalog.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import java.time.LocalDateTime;
import lombok.Value;

@Value
public class CreateCatalogEntryRequest {

    String deviceId;
    String name;

    @JsonFormat(pattern = "dd-M-yyyy'T'HH:mm:ss")
    LocalDateTime from;

    @JsonFormat(pattern = "dd-M-yyyy'T'HH:mm:ss")
    LocalDateTime to;

    CreateKafkaEntryRequest kafkaEntryRequest;

    CreatePreprocessingEntryRequest preprocessingEntryRequest;
}
