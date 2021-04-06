package pl.ec.kafka.szperacz.search.preprocessing;

import com.fasterxml.jackson.databind.ObjectMapper;
import javax.inject.Singleton;
import lombok.Getter;
import lombok.SneakyThrows;

@Singleton
public class JsonMapper {

    @Getter
    private final ObjectMapper mapper = new ObjectMapper();

    @SneakyThrows
    public <V> V readValue(String value, Class<V> clazz) {
        return mapper.readValue(value, clazz);
    }
}

