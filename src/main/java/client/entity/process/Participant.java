package client.entity.process;

import client.entity.team.Invite;
import client.entity.team.Team;
import client.entity.deserializer.ParticipantDeserializer;
import client.entity.user.User;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.io.Serializable;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Сущность "Участник"
 * */
@NoArgsConstructor
@Getter
@Setter
@JsonDeserialize(using = ParticipantDeserializer.class)
public class Participant implements Serializable {
    
    private Long id;
    
    @ToString.Exclude
    private User owner;
    
    private List<Team> teams;
    
    @ToString.Exclude
    private Set<Invite> invites = new HashSet<>();
    
    public void addInvite(Invite invite) {
        
        invites.add(invite);
    }
    public void removeInvite(Invite invite) {
        
        invites = invites.stream()
                .filter(i -> !Objects.equals(i.getId(), invite.getId()))
                .collect(Collectors.toSet());;
    }
    
    @JsonIgnore
    public String getUsername() {
        return owner.getUsername();
    }
}
