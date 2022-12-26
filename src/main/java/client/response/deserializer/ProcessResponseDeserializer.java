package client.response.deserializer;

import client.entity.process.Process;
import client.response.ProcessResponse;
import client.util.JSONUtils;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;

import java.io.IOException;

public class ProcessResponseDeserializer extends StdDeserializer<ProcessResponse> {
    public ProcessResponseDeserializer() {
        
        this(null);
    }
    
    public ProcessResponseDeserializer(Class<?> vc) {
        
        super(vc);
    }
    
    @Override
    public ProcessResponse deserialize(JsonParser jp, DeserializationContext context)
            throws IOException {
        
        JsonNode node = jp.getCodec().readTree(jp);
        
        return ProcessResponse.builder()
                .process(JSONUtils.getObject(node, "process", Process.class))
                .build().status(node.get("status").textValue()).message(node.get("message").textValue());
    }
}
