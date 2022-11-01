package client.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.Arrays;
import java.util.Objects;
import java.util.stream.Stream;

public class JSONUtils {
    
    private static final ObjectMapper mapper = new ObjectMapper();
    
    public static Stream<Object> splitObjects(JsonNode node, String title, Class cl) {
    
        var objects = node.get(title).toPrettyString();
        if(objects.equals("null"))
            return Stream.empty();
        objects = objects.substring(objects.indexOf("[") + 1, objects.lastIndexOf("]"));
        return Arrays.stream(objects.split("\n},"))
                .map(s -> {
                    if (!s.trim().endsWith("}") && !s.isBlank())
                        s += "}";
                    return s;
                })
                .map(s -> {
                    try {
                        return s.isBlank() ? null : mapper.readValue(s, cl);
                    } catch (JsonProcessingException e) {
                        e.printStackTrace();
                    }
                    return null;
                })
                .filter(Objects::nonNull);
    }
    
    public static Object getObject(JsonNode node, String title, Class cl) throws JsonProcessingException {
        
        return mapper.readValue(node.get(title).toPrettyString(), cl);
    }
    
}
