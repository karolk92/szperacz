package pl.ec.kafka.szperacz.catalog.model;

import com.fasterxml.jackson.annotation.JsonView;
import java.util.List;
import lombok.Builder;
import lombok.Data;
import lombok.Singular;
import pl.ec.kafka.szperacz.catalog.JsonViews.CatalogOnly;

@Builder(builderMethodName = "aCatalog")
@Data
public class Catalog {

    @JsonView(CatalogOnly.class)
    private String id;

    @JsonView(CatalogOnly.class)
    private String name;

    @JsonView(CatalogOnly.class)
    private String description;

    @JsonView(CatalogOnly.class)
    private String owner;

    @Singular
    @JsonView(CatalogOnly.class)
    private List<CatalogEntry> entries;
}
