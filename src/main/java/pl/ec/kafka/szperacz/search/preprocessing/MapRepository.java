package pl.ec.kafka.szperacz.search.preprocessing;

import io.lettuce.core.api.StatefulRedisConnection;
import java.util.List;
import java.util.stream.Collectors;
import javax.inject.Inject;
import javax.inject.Singleton;
import lombok.SneakyThrows;
import pl.ec.kafka.szperacz.search.preprocessing.model.MapCluster;

@Singleton
public class MapRepository {

    public static final String VERSION_KEY = "map_cache_version";
    public static final String CLUSTER_KEY = "map_cache_clusters";

    protected final StatefulRedisConnection<String, String> redisConnection;
    private final JsonMapper mapper;

    @Inject
    public MapRepository(StatefulRedisConnection<String, String> redisConnection, JsonMapper mapper) {
        this.redisConnection = redisConnection;
        this.mapper = mapper;
    }

    /**
     * Pobierz aktualną wersję
     *
     * @return wersja
     */
    public String getVersion() {
        return redisConnection.sync().get(VERSION_KEY);
    }

    /**
     * Pobierz aktualne mapy
     *
     * @return Zbiór map
     */
    @SneakyThrows
    public List<MapCluster> getMapClusters() {
        return redisConnection.sync().lrange(CLUSTER_KEY, 0, -1).stream()
            .map(clusterJson -> mapper.readValue(clusterJson, MapCluster.class))
            .collect(Collectors.toList());
    }
}
