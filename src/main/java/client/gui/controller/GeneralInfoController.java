package client.gui.controller;

import client.sender.Sender;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.MenuItem;
import javafx.scene.control.SplitMenuButton;

import java.io.IOException;

public class GeneralInfoController extends Controller{
    @FXML
    private SplitMenuButton teams;
    @FXML
    private SplitMenuButton processes;
    @FXML
    private Button back;
    
    private final String source = "general_info.fxml";
    @FXML
    public void initialize() throws IOException {
    
        if(data.getPreviousScene() == null)
            back.setVisible(false);
        
        var response = Sender.GetUserInfo();
        teams.getItems().clear();
        processes.getItems().clear();
        
        data.setParticipant(response.getParticipant());
        data.setTeams(response.getTeams());
        data.setProcesses(response.getProcesses());
    
        for(var team: data.getTeams()) {
        
            var item = new MenuItem(team.getTitle());
        
            item.setOnAction(event -> {
                data.setCurrentTeam(team);
                try {
                    showStage(event, "team_info.fxml", source);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            });
        
            teams.getItems().add(item);
        }
    
        for(var process: data.getProcesses()) {
        
            var item = new MenuItem(process.getTitle());
        
            item.setOnAction(event -> {
                data.setCurrentProcess(process);
                try {
                    showStage(event, "process_info.fxml", source);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            });
        
            processes.getItems().add(item);
        }
    }
    
    public void createTeam(ActionEvent event) throws IOException{
        
        showStage(event, "create_team.fxml", source);
    }
    
    public void createProcess(ActionEvent event) throws IOException{
        
        showStage(event, "create_process.fxml", source);
    }
    
    public void back(ActionEvent event) throws IOException {
        
        showStage(event, data.getPreviousScene(), "create_process.fxml");
    }
}
