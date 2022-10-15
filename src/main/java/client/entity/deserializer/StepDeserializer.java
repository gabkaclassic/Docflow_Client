package client.entity.deserializer;

import client.entity.process.Participant;
import client.entity.process.Rules;
import client.entity.process.Step;
import client.entity.process.document.Document;
import client.util.JSONUtils;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;

import java.io.IOException;
import java.util.Map;
import java.util.stream.Collectors;

public class StepDeserializer extends StdDeserializer<Step> {
    
    public StepDeserializer() {
        
        this(null);
    }
    
    public StepDeserializer(Class<?> vc) {
        
        super(vc);
    }
    
    @Override
    public Step deserialize(JsonParser jp, DeserializationContext context)
            throws IOException {
        
        JsonNode node = jp.getCodec().readTree(jp);
        var step = new Step();
        step.setId(node.get("id").asLong());
        step.setTitle(node.get("title").textValue());
        step.setNumber(node.get("number").asInt());
        var map = (Map) JSONUtils.getObject(node, "rules", Map.class);
        for (var o : map.keySet())
            step.getRules().put(Long.valueOf(String.valueOf(o)), Rules.valueOf(String.valueOf(map.get(o))));
        step.setParticipants(JSONUtils.splitObjects(node, "participants", Participant.class)
                .map(Participant.class::cast)
                .collect(Collectors.toSet())
        );
        
        step.setDocuments(JSONUtils.splitObjects(node, "documents", Document.class)
                .map(Document.class::cast)
                .collect(Collectors.toSet())
        );
        
        return step;
    }
}
