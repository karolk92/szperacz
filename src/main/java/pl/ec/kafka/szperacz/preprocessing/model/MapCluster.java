package pl.ec.kafka.szperacz.preprocessing.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import io.micronaut.core.annotation.Introspected;
import java.util.List;
import lombok.Builder;
import lombok.Getter;
import lombok.Value;

@SuppressFBWarnings(value = {"JLM_JSR166_UTILCONCURRENT_MONITORENTER", "RCN_REDUNDANT_NULLCHECK_OF_NONNULL_VALUE"})
@JsonIgnoreProperties(ignoreUnknown = true)
@Builder(builderMethodName = "aMapCluster")
@Value
@Introspected
public class MapCluster {

    Location clusterCenter;
    List<BoundingBox> boundingBoxes;
    List<VirtualGantry> gantries;
    List<RoadSegment> roads;

    @Getter(lazy = true)
    BoundingBox accumulatedBoundingBox = BoundingBox.accumulatedBoundingBox(boundingBoxes);
}
