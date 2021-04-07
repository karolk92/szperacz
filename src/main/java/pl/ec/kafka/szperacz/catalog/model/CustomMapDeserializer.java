package pl.ec.kafka.szperacz.catalog.model;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class CustomMapDeserializer extends JsonDeserializer<Map<String, String>> {

    @Override
    public Map<String, String> deserialize(JsonParser p, DeserializationContext ctxt) throws IOException, JsonProcessingException {
        Map<String, String> result = new HashMap<>();
        var readValueAsTree = p.readValueAsTree();
        if (readValueAsTree != null&& readValueAsTree instanceof ArrayNode) {
            Iterator<JsonNode> i = ((ArrayNode) readValueAsTree).elements();
            while (i.hasNext()) {
                var jsonNode = i.next();
                result.put(jsonNode.get(0).asText(), jsonNode.get(1).asText());
            }
        }
        return result;
    }
}
