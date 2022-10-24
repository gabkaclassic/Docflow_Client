package client.entity;

import client.entity.process.Participant;
import client.entity.process.Process;
import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;
import client.entity.deserializer.TeamDeserializer;

@Data
@JsonDeserialize(using = TeamDeserializer.class)
public class Team {
    
    private String title;
    
    private List<String> participants = new ArrayList<>();
    
    private List<Process> processes = new ArrayList<>();

    private Long teamLeaderId;
    
    public void addParticipant(String participant) {
        
        participants.add(participant);
    }
    
}

