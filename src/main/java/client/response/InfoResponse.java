package client.response;

import client.entity.Team;
import client.entity.process.Participant;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@Builder
@Data
public class InfoResponse extends Response {
    
    private Participant participant;
    
    private List<Team> teams;
    
    private List<Process> processes;
    
    public InfoResponse message(String message) {
        
        setMessage(message);
        
        return this;
    }
    
    public InfoResponse status(String status) {
        
        setStatus(status);
        
        return this;
    }
}
