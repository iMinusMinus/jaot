package ml.iamwhatiam.baostock.infrastructure.rpc;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

public abstract class JsonParser {

    private static ObjectMapper om = new ObjectMapper();

    static List<List<String>> parseRecord(String json) {
        if(json == null || json.length() == 0) {
            return Collections.emptyList();
        }
        try {
            List<List<String>> data = new ArrayList<>();
            Iterator<JsonNode>  nodes = om.readTree(json).path("record").iterator();
            while (nodes.hasNext()) {
                List<String> row = new ArrayList<>();
                JsonNode node = nodes.next();
                Iterator<JsonNode> fields = node.elements();
                while (fields.hasNext()) {
                    row.add(fields.next().textValue());
                }
                data.add(row);
            }
            return data;
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    public static <T> T parse(String json, TypeReference<T> klazz) {
        try {
            return om.readValue(json, klazz);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
