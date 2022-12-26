package client.entity.deserializer;

import client.entity.team.Invite;
import client.entity.team.Team;
import client.entity.process.Participant;
import client.entity.user.User;
import client.util.JSONUtils;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;

import java.io.IOException;
import java.util.stream.Collectors;

public class ParticipantDeserializer extends StdDeserializer<Participant> {
    
    
    public ParticipantDeserializer() {
        
        this(null);
    }
    
    public ParticipantDeserializer(Class<?> vc) {
        
        super(vc);
    }
    
    @Override
    public Participant deserialize(JsonParser jp, DeserializationContext context)
            throws IOException {
    
        JsonNode node = jp.getCodec().readTree(jp);
    
        var participant = new Participant();
        
        participant.setId(node.get("id").asLong());
        participant.setOwner(JSONUtils.getObject(node, "owner", User.class));
        participant.setTeams(JSONUtils.splitObjects(node,"teams", Team.class).toList());
        
        return participant;
    }
}
