package pl.ec.kafka.szperacz.catalog.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonView;
import java.time.LocalDateTime;
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

    @JsonView({CatalogOnly.class})
    @JsonFormat(pattern = "dd-M-yyyy'T'HH:mm:ss")
    private LocalDateTime created;

    @Singular
    @JsonView(CatalogOnly.class)
    private List<CatalogEntry> entries;
}
