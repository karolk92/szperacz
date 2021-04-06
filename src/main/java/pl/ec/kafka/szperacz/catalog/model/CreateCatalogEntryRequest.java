package pl.ec.kafka.szperacz.catalog.model;

import lombok.Value;

@Value
public class CreateCatalogEntryRequest {

    String type;
    String content;
}
