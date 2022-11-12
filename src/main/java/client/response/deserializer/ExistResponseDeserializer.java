package client.response.deserializer;

import client.response.ExistResponse;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;

import java.io.IOException;

public class ExistResponseDeserializer extends StdDeserializer<ExistResponse> {
    
    public ExistResponseDeserializer() {
        
        this(null);
    }
    
    public ExistResponseDeserializer(Class<?> vc) {
        
        super(vc);
    }
    
    @Override
    public ExistResponse deserialize(JsonParser jp, DeserializationContext context)
            throws IOException {
        
        JsonNode node = jp.getCodec().readTree(jp);
        
        return ExistResponse.builder().exist(node.get("exist").asBoolean())
                .build().status(node.get("status").textValue());
    }
}

