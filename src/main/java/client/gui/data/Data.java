package client.gui.data;

import client.entity.Team;
import client.entity.process.Participant;
import client.entity.process.Process;
import client.sender.Sender;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@lombok.Data
public class Data {
    
    private Participant participant;
    
    private List<Team> teams = new ArrayList<>();
    
    private List<Process> processes = new ArrayList<>();
    
    private Team currentTeam;
    
    private Process currentProcess;
    
    private String previousScene;
    private static Data instance;
    
    public static Data getInstance() {
        
        if(instance == null)
            instance = new Data();
            
        return instance;
    }
    
    public void refresh() throws IOException {
        
        var response = Sender.getUserInfo();
        participant = response.getParticipant();
        teams = participant.getTeams();
        processes = teams.stream()
                .flatMap(t -> t.getProcesses().stream())
                .toList();
    }
}
