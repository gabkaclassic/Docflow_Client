package client.gui.data;

import client.entity.team.Invite;
import client.entity.team.Team;
import client.entity.process.Participant;
import client.entity.process.Process;
import client.file.FileManager;
import client.response.InfoResponse;
import client.sender.Sender;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Класс, singleton-сущность которого хранит информацию, общую для всех контроллеров
 * @see client.gui.controller.Controller
 * */
@lombok.Data
public class Data {
    
    private Participant participant;
    
    private List<Team> teams = new ArrayList<>();
    
    private List<Process> processes = new ArrayList<>();
    
    private List<Invite> invites = new ArrayList<>();
    
    private Team currentTeam;
    
    private Process currentProcess;
    
    private final String previousScene = "general_info.fxml";
    private static Data instance;
    
    public static Data getInstance() {
        
        if(instance == null)
            instance = new Data();
            
        return instance;
    }
    
    public void setData(InfoResponse response) {
    
        setParticipant(response.getParticipant());
        setTeams(response.getTeams());
        setProcesses(response.getProcesses());
        setInvites(response.getInvites());
    }
    
    public void refresh() throws IOException {
        
        var oldProcesses = processes;
        var response = Sender.getUserInfo();
        participant = response.getParticipant();
        teams = participant.getTeams();
        processes = teams.stream()
                .flatMap(t -> t.getProcesses().stream())
                .toList();
        
        oldProcesses.stream().filter(this::processFinished).forEach(FileManager::removeProcessPath);
        
        invites = response.getInvites();
    }
    
    private boolean processFinished(Process process) {
        
        var processId = process.getId();
        
        return processes.stream().map(Process::getId).noneMatch(id -> id.equals(processId));
    }
    
    public void clear() {
        
        participant = null;
        teams = null;
        processes = null;
    }
}
