package client.entity;

import client.entity.process.Participant;
import client.entity.process.Process;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;
import client.entity.deserializer.TeamDeserializer;

@Data
@JsonDeserialize(using = TeamDeserializer.class)
public class Team {
    
    private Long id;
    
    private String title;

    private List<Participant> participants = new ArrayList<>();
    
    private List<Process> processes = new ArrayList<>();

    private Participant teamLeader;
    
    public void addParticipant(Participant participant) {
        
        participants.add(participant);
    }
    
}

