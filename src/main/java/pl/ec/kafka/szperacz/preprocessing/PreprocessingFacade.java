package pl.ec.kafka.szperacz.preprocessing;

import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import javax.inject.Singleton;
import lombok.RequiredArgsConstructor;
import pl.ec.kafka.szperacz.preprocessing.model.MapCluster;

@RequiredArgsConstructor
@Singleton
public class PreprocessingFacade {

    private final MapRepository mapRepository;

    private Map<String, MapCluster> clusters;
    private String mapVersion;

    public MapCluster getMapByGantry(String gantryName) {
        tryToInitialize();
        return clusters.get(gantryName);
    }

    private void tryToInitialize() {
        var currentVersion = mapRepository.getVersion();
        if (mapVersion == null || !mapVersion.equals(currentVersion)) {
            var maps = mapRepository.getMapClusters();
            this.clusters = maps.stream()
                .flatMap(this::toPerGantryEntryStream)
                .collect(Collectors.toMap(Entry::getKey, Entry::getValue));
            this.mapVersion = currentVersion;
        }
    }

    private Stream<Entry<String, MapCluster>> toPerGantryEntryStream(MapCluster map) {
        return map.getGantries().stream().map(gantry -> Map.entry(gantry.getName(), map));
    }
}
