package client.entity.team;

import client.entity.deserializer.InviteDeserializer;
import client.entity.process.Participant;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@JsonDeserialize(using = InviteDeserializer.class)
public class Invite {
    
    private Long id;
    
    private Team team;
    
    private Participant candidate;
    
    public Invite(Team team, Participant participant) {
        
        this.team = team;
        this.candidate = participant;
    }
}
