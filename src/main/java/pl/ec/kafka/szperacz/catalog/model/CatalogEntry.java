package pl.ec.kafka.szperacz.catalog.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonView;
import java.time.LocalDateTime;
import java.util.List;
import lombok.Builder;
import lombok.Data;
import pl.ec.kafka.szperacz.catalog.JsonViews.CatalogOnly;
import pl.ec.kafka.szperacz.catalog.JsonViews.WholeCatalog;

@Builder(builderMethodName = "aCatalogEntry")
@Data
public class CatalogEntry {

    @JsonView({CatalogOnly.class, WholeCatalog.class})
    private String id;

    @JsonView({CatalogOnly.class, WholeCatalog.class})
    private String name;

    @JsonView({CatalogOnly.class, WholeCatalog.class})
    private String description;

    @JsonView({CatalogOnly.class, WholeCatalog.class})
    private String type;

    @JsonView({CatalogOnly.class, WholeCatalog.class})
    private boolean compressed;

    @JsonView({CatalogOnly.class, WholeCatalog.class})
    private String deviceId;

    @JsonView({CatalogOnly.class, WholeCatalog.class})
    @JsonFormat(pattern = "dd-M-yyyy'T'HH:mm:ss")
    private LocalDateTime created;

    @JsonView({CatalogOnly.class, WholeCatalog.class})
    @JsonFormat(pattern = "dd-M-yyyy'T'HH:mm:ss")
    private LocalDateTime from;

    @JsonView({CatalogOnly.class, WholeCatalog.class})
    @JsonFormat(pattern = "dd-M-yyyy'T'HH:mm:ss")
    private LocalDateTime to;

    @JsonView({CatalogOnly.class, WholeCatalog.class})
    private List<String> topics;

    @JsonView(WholeCatalog.class)
    private List<CatalogContent> contents;

    @JsonIgnore
    public boolean isKafkaType() {
        return getType().equals("kafka");
    }

    @JsonIgnore
    public boolean isPreprocessingType() {
        return getType().equals("preprocessing");
    }
}
