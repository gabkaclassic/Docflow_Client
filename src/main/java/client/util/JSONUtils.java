package client.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;

import java.util.Arrays;
import java.util.Objects;
import java.util.regex.Pattern;
import java.util.stream.Stream;

/**
 * Класс-утилита для десериализации сущностей из формата JSON
 * */
@Slf4j
public class JSONUtils {
    
    private static final ObjectMapper mapper = new ObjectMapper();
    private static final Pattern patternClose = Pattern.compile("\\}");
    private static final Pattern patternOpen = Pattern.compile("\\{");
    public static <T> Stream<T> splitObjects(JsonNode node, String title, Class<T> cl) {
        
        var objects = node.get(title).toPrettyString();
        objects = objects.replace("null", "[ ]");
        objects = objects.substring(objects.indexOf("[") + 1, objects.lastIndexOf("]"));
        return Arrays.stream(objects.split("\n},"))
                .map(s -> {
                    if (!s.isBlank()
                            && patternClose.matcher(s).results().count() < patternOpen.matcher(s).results().count()
                    )
                        s += "}";
                    return s;
                })
                .map(s -> {
                    try {
                        return s.isBlank() ? null : mapper.readValue(s, cl);
                    } catch (JsonProcessingException e) {
                        log.warn("Split objects parser error", e);
                        return null;
                    }
                })
                .filter(Objects::nonNull);
    }
    
    public static <T> T getObject(JsonNode node, String title, Class<T> cl) throws JsonProcessingException {
        
        return mapper.readValue(node.get(title).toPrettyString(), cl);
    }
    
}
