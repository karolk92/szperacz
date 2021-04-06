package pl.ec.kafka.szperacz.search.preprocessing.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Stream;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

/**
 * Klasa opisująca odcinek drogi
 */
@ToString
@JsonIgnoreProperties(ignoreUnknown = true)
@Builder(builderMethodName = "aRoadSegment")
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@NoArgsConstructor(access = AccessLevel.PRIVATE)
@Data
public class RoadSegment {

    public static final RoadSegment NOP_ROAD_SEGMENT = RoadSegment.aRoadSegment().build();

    private int id;
    private int osmId;
    private int mmId;
    private short azimuth;
    private Location startSpherical;
    private Location startCartesian;
    private Location endSpherical;
    private Location endCartesian;
    private RoadSegmentType type;
    private float width;

    private List<RoadSegment> nextRoadSegments;
    private List<RoadSegment> previousRoadSegments;

    private Set<Integer> nextRoadIds;
    private Set<Integer> previousRoadIds;

    private Set<Integer> nextRoadMmIds;
    private Set<Integer> previousRoadMmIds;

    /**
     * Dodaj dany segment jako poprzedzający
     *
     * @param roadSegment {@link RoadSegment}
     */
    public void addPreviousRoadSegment(RoadSegment roadSegment) {
        if (this.previousRoadSegments == null) {
            this.previousRoadSegments = new ArrayList<>();
        }
        this.previousRoadSegments.add(roadSegment);
    }

    /**
     * Dodaj dany segment jako następny
     *
     * @param roadSegment {@link RoadSegment}
     */
    public void addNextRoadSegment(RoadSegment roadSegment) {
        if (this.nextRoadSegments == null) {
            this.nextRoadSegments = new ArrayList<>();
        }
        this.nextRoadSegments.add(roadSegment);
    }

    /**
     * Pobierz następne segmenty
     *
     * @return Stream natępnych {@link RoadSegment}
     */
    public Stream<RoadSegment> getNextRoadSegments() {
        return nextRoadSegments == null ? Stream.empty() : nextRoadSegments.stream().filter(segment -> segment.getId() != id);
    }

    /**
     * Pobierz segmenty poprzedzające
     *
     * @return Stream poprzedających {@link RoadSegment}
     */
    public Stream<RoadSegment> getPreviousRoadSegments() {
        return previousRoadSegments == null ? Stream.empty() : previousRoadSegments.stream().filter(segment -> segment.getId() != id);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        RoadSegment that = (RoadSegment) o;
        return id == that.id;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
