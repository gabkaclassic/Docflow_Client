package client.gui.data;

import client.entity.Team;
import client.entity.process.Participant;
import client.entity.process.Process;

import java.util.List;

@lombok.Data
public class Data {
    
    private Participant participant;
    
    private List<Team> teams;
    
    private List<Process> processes;
    
    private static Data instance;
    
    public static Data getInstance() {
        
        if(instance == null)
            instance = new Data();
            
        return instance;
    }
    
}
