package pl.ec.kafka.szperacz.catalog.model;

import java.util.List;
import lombok.Builder;
import lombok.Data;
import lombok.Singular;

@Builder(builderMethodName = "aCatalog")
@Data
public class Catalog {

    private String name;
    private String description;
    private String owner;

    @Singular
    private List<CatalogEntry> entries;
}
