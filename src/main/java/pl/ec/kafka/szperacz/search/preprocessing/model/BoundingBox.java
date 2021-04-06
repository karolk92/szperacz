package pl.ec.kafka.szperacz.search.preprocessing.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.Collection;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@JsonIgnoreProperties(ignoreUnknown = true)
@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder(builderMethodName = "aBoundingBox")
public class BoundingBox {

    private static final double NEUTRAL_MIN = 361;
    private static final double NEUTRAL_MAX = -1;

    @JsonProperty("minLat")
    private double minLatitude;

    @JsonProperty("minLng")
    private double minLongitude;

    @JsonProperty("maxLat")
    private double maxLatitude;

    @JsonProperty("maxLng")
    private double maxLongitude;

    public static BoundingBox accumulatedBoundingBox(Collection<BoundingBox> boundingBoxes) {
        double maxLatitude = NEUTRAL_MAX;
        double maxLongitude = NEUTRAL_MAX;
        double minLatitude = NEUTRAL_MIN;
        double minLongitude = NEUTRAL_MIN;

        for (var boundingBox : boundingBoxes) {
            if (maxLatitude < boundingBox.getMaxLatitude()) {
                maxLatitude = boundingBox.getMaxLatitude();
            }
            if (maxLongitude < boundingBox.getMaxLongitude()) {
                maxLongitude = boundingBox.getMaxLongitude();
            }
            if (minLatitude > boundingBox.getMinLatitude()) {
                minLatitude = boundingBox.getMinLatitude();
            }
            if (minLongitude > boundingBox.getMinLongitude()) {
                minLongitude = boundingBox.getMinLongitude();
            }
        }

        return new BoundingBox(minLatitude, minLongitude, maxLatitude, maxLongitude);
    }
}
