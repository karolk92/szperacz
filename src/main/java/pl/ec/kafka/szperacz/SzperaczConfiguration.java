package pl.ec.kafka.szperacz;

import io.micronaut.context.annotation.ConfigurationProperties;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@ConfigurationProperties("szperacz")
public class SzperaczConfiguration {

    private int eventFetchLimit;
    private String testProducerDirectory;
    private KafkaConfiguration kafka;
    private CatalogConfiguration catalog;

    @Setter
    @Getter
    @ConfigurationProperties("kafka")
    public static class KafkaConfiguration {

        private String groupId;
        private String bootstrapServers;
    }

    @Setter
    @Getter
    @ConfigurationProperties("catalog")
    public static class CatalogConfiguration {
        private String path;
        private boolean compressFiles;
    }
}
