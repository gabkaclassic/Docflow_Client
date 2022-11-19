package client.entity.deserializer;

import client.entity.process.Process;
import client.entity.process.Step;
import client.util.JSONUtils;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;

import java.io.IOException;
import java.util.stream.Collectors;

public class ProcessDeserializer extends StdDeserializer<Process> {
    
    public ProcessDeserializer() {
        
        this(null);
    }
    
    public ProcessDeserializer(Class<?> vc) {
        
        super(vc);
    }
    
    @Override
    public Process deserialize(JsonParser jp, DeserializationContext ctxt)
            throws IOException {
        
        JsonNode node = jp.getCodec().readTree(jp);
        var process = new Process();
        
        process.setId(node.get("id").textValue());
        process.setTitle(node.get("title").textValue());
        process.setCurrentStep(node.get("currentStep").asInt());
        
        process.setSteps(JSONUtils.splitObjects(node, "steps", Step.class)
                .collect(Collectors.toList())
        );
        
        return process;
    }
}
