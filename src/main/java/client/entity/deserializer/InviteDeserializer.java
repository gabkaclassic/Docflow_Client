package client.entity.deserializer;

import client.entity.process.Participant;
import client.entity.team.Invite;
import client.entity.team.Team;
import client.util.JSONUtils;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;

import java.io.IOException;

public class InviteDeserializer extends StdDeserializer<Invite> {
    
    public InviteDeserializer() {
        
        this(null);
    }
    
    public InviteDeserializer(Class<?> vc) {
        
        super(vc);
    }
    
    @Override
    public Invite deserialize(JsonParser jp, DeserializationContext ctxt)
            throws IOException {
        
        JsonNode node = jp.getCodec().readTree(jp);
        
        var invite = new Invite();
        invite.setId(node.get("id").asLong());
        invite.setTeam(JSONUtils.getObject(node, "team", Team.class));
        invite.setCandidate(JSONUtils.getObject(node, "candidate", Participant.class));
        
        return invite;
    }
    
}
