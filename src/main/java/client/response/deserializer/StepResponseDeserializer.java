package client.response.deserializer;

import client.entity.process.Step;
import client.response.StepResponse;
import client.util.JSONUtils;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;

import java.io.IOException;

public class StepResponseDeserializer extends StdDeserializer<StepResponse> {
    
    public StepResponseDeserializer() {
        
        this(null);
    }
    
    public StepResponseDeserializer(Class<?> vc) {
        
        super(vc);
    }
    
    @Override
    public StepResponse deserialize(JsonParser jp, DeserializationContext context)
            throws IOException {
        
        JsonNode node = jp.getCodec().readTree(jp);
        
        return StepResponse.builder()
                .step((Step) JSONUtils.getObject(node, "step", Step.class))
                .build()
                .status(node.get("status").textValue())
                .message(node.get("message").textValue());
    }
}
