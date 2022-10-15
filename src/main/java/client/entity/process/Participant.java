package client.entity.process;

import client.entity.Team;
import client.entity.user.User;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.io.Serializable;
import java.util.List;

@NoArgsConstructor
@Getter
@Setter
public class Participant implements Serializable {
    
    private long id;
    
    @ToString.Exclude
    private User owner;
    
    private List<Team> teams;
    
    public void addTeam(Team team) {
        
        teams.add(team);
    }
}
