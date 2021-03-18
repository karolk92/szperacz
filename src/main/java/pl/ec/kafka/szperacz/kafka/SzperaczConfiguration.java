package pl.ec.kafka.szperacz.kafka;

import io.micronaut.context.annotation.ConfigurationProperties;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@ConfigurationProperties("szperacz")
public class SzperaczConfiguration {

    private int eventFetchLimit;
    private KafkaConfiguration kafka;

    @Setter
    @Getter
    @ConfigurationProperties("kafka")
    public static class KafkaConfiguration {

        private String groupId;
        private String bootstrapServers;
    }
}
