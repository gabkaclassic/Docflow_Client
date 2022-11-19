package client.entity.deserializer;

import client.entity.Team;
import client.entity.process.Process;
import client.util.JSONUtils;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class TeamDeserializer extends StdDeserializer<Team> {
    
    
    public TeamDeserializer() {
        
        this(null);
    }
    
    public TeamDeserializer(Class<?> vc) {
        
        super(vc);
    }
    
    @Override
    public Team deserialize(JsonParser jp, DeserializationContext context)
            throws IOException {
        
        JsonNode node = jp.getCodec().readTree(jp);
        var team = new Team();
        team.setTitle(node.get("title").textValue());
    
        team.setTeamLeaderId(node.get("teamLeaderId").asLong());
        team.setParticipants(Arrays.stream(node.get("participants").toPrettyString()
                        .replace("\"", "")
                        .replace("[", "").replace("]", "")
                        .split(","))
                .map(String::trim)
                .collect(Collectors.toSet())
        );
        team.setProcesses(JSONUtils.splitObjects(node, "processes", Process.class)
                .map(Process.class::cast)
                .toList()
        );
        return team;
    }
}
