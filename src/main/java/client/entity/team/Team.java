package client.entity.team;

import client.entity.deserializer.TeamDeserializer;

import client.entity.process.Process;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import lombok.Data;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Сущность "Команда"
 * */
@Data
@JsonDeserialize(using = TeamDeserializer.class)
public class Team implements Serializable {
 
    private String title;

    private Set<String> participants = new HashSet<>();
    
    private List<Process> processes = new ArrayList<>();
    

    private Long teamLeaderId;
    
    public void addParticipants(List<String> participants) {
        
        this.participants.addAll(participants);
    }
}

