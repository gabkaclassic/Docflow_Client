package client.response.deserializer;

import client.entity.Team;
import client.entity.process.Participant;
import client.entity.process.Process;
import client.response.InfoResponse;
import client.util.JSONUtils;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;

import java.io.IOException;

public class InfoResponseDeserializer extends StdDeserializer<InfoResponse> {
    
    
    public InfoResponseDeserializer() {
        
        this(null);
    }
    
    public InfoResponseDeserializer(Class<?> vc) {
        
        super(vc);
    }
    
    @Override
    public InfoResponse deserialize(JsonParser jp, DeserializationContext context)
            throws IOException {
        
        JsonNode node = jp.getCodec().readTree(jp);
    
            return InfoResponse.builder()
                .participant((Participant) JSONUtils.getObject(node, "participant", Participant.class))
                .processes(JSONUtils.splitObjects(node, "processes", Process.class)
                        .map(Process.class::cast)
                        .toList()
                )
                .teams(JSONUtils.splitObjects(node, "teams", Team.class)
                        .map(Team.class::cast)
                        .toList()
                )
                .build()
                .message(node.get("message").textValue())
                .status(node.get("status").textValue());
    }
}
