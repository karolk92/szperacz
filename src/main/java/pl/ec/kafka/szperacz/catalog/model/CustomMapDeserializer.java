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
        if (readValueAsTree != null && readValueAsTree instanceof ArrayNode) {
            Iterator<JsonNode> i = ((ArrayNode) readValueAsTree).elements();
            while (i.hasNext()) {
                var jsonNode = i.next();

                applyResult(result, jsonNode, "sorted_out");
                applyResult(result, jsonNode, "preprocessed_out");
            }
        }
        return result;
    }

    private void applyResult(Map<String, String> result, JsonNode jsonNode, String fieldName) {
        var sortedOut = jsonNode.get(fieldName);
        if (sortedOut == null) return;

        var text = sortedOut.asText();
        if (text != null  && !text.isEmpty()) {
            result.put(fieldName, text);
        }
    }
}
