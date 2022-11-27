package client.entity.process;

import client.entity.Team;
import client.entity.deserializer.ParticipantDeserializer;
import client.entity.user.User;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.io.Serializable;
import java.util.List;

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
    
    @JsonIgnore
    public String getUsername() {
        return owner.getUsername();
    }
}
