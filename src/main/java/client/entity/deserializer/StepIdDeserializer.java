package client.entity.deserializer;

import client.entity.process.step.StepId;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;

import java.io.IOException;

public class StepIdDeserializer extends StdDeserializer<StepId> {
    
    public StepIdDeserializer() {
        
        this(null);
    }
    
    public StepIdDeserializer(Class<?> vc) {
        
        super(vc);
    }
    
    @Override
    public StepId deserialize(JsonParser jp, DeserializationContext ctxt)
            throws IOException {
        
        JsonNode node = jp.getCodec().readTree(jp);
        
        return new StepId(node.get("processId").textValue(), node.get("title").textValue());
    }
    
}
