package pl.ec.kafka.szperacz.catalog.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonView;
import java.time.LocalDateTime;
import java.util.List;
import lombok.Builder;
import lombok.Data;
import pl.ec.kafka.szperacz.catalog.JsonViews.CatalogOnly;
import pl.ec.kafka.szperacz.catalog.JsonViews.ReadEntries;

@Builder(builderMethodName = "aCatalogEntry")
@Data
public class CatalogEntry {

    @JsonView(CatalogOnly.class)
    private String id;

    @JsonView(CatalogOnly.class)
    private String name;

    @JsonView(CatalogOnly.class)
    private String type;

    @JsonView(CatalogOnly.class)
    private String deviceId;

    @JsonView(CatalogOnly.class)
    @JsonFormat(pattern = "dd-M-yyyy'T'HH:mm:ss")
    private LocalDateTime from;

    @JsonView(CatalogOnly.class)
    @JsonFormat(pattern = "dd-M-yyyy'T'HH:mm:ss")
    private LocalDateTime to;

    @JsonView(ReadEntries.class)
    private List<CatalogContent> contents;

    boolean isKafka() {
        return getType().equals("kafka");
    }

    boolean isPreprocessing() {
        return getType().equals("preprocessing");
    }
}
