package client.entity;

import client.entity.process.Participant;
import client.entity.process.Process;
import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import lombok.Data;

import java.io.Serializable;
import java.util.*;

import client.entity.deserializer.TeamDeserializer;

@Data
@JsonDeserialize(using = TeamDeserializer.class)
public class Team implements Serializable {
 
    private String title;

    private Set<String> participants = new HashSet<>();
    
    private List<Process> processes = new ArrayList<>();
    

    private Long teamLeaderId;
    
    public void addParticipant(String participant) {
        
        participants.add(participant);
    }
    
    public void removeParticipant(String username) {
        
        participants.remove(username);
    }
    
    public void addProcess(Process process) {
        
        processes.add(process);
    }
    
    public void addParticipants(List<String> participants) {
        
        this.participants.addAll(participants);
    }
}

