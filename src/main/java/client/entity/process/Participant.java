package client.entity.process;

import client.entity.Team;
import client.entity.user.User;
import client.entity.deserializer.ParticipantDeserializer;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@NoArgsConstructor
@Getter
@Setter
@JsonDeserialize(using = ParticipantDeserializer.class)
public class Participant implements Serializable {
    
    private long id;
    
    @ToString.Exclude
    private User owner;
    
    private List<Team> teams;
    
    public void addTeam(Team team) {
        
        if(teams == null)
            teams = new ArrayList<>();
        
        teams.add(team);
    }
    
    public String getUsername() {
        return owner.getUsername();
    }
}
