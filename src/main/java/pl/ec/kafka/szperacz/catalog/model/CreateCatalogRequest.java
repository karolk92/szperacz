package pl.ec.kafka.szperacz.catalog.model;

import lombok.Value;

@Value
public class CreateCatalogRequest {

    String name;
    String owner;
    String description;
}
