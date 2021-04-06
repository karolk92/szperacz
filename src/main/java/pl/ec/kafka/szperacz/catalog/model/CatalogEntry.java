package pl.ec.kafka.szperacz.catalog.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import java.time.LocalDateTime;
import lombok.Builder;
import lombok.Data;

@Builder(builderMethodName = "aCatalogEntry")
@Data
public class CatalogEntry {

    private String deviceId;

    @JsonFormat(pattern = "dd-M-yyyy'T'HH:mm:ss")
    private LocalDateTime from;

    @JsonFormat(pattern = "dd-M-yyyy'T'HH:mm:ss")
    private LocalDateTime to;

    private CatalogContent content;
}
