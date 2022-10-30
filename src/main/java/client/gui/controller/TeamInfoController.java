package client.gui.controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.control.SplitMenuButton;

import java.io.IOException;

public class TeamInfoController extends Controller {
    
    @FXML
    private Label teamTitle;
    
    @FXML
    private SplitMenuButton processes;
    
    @FXML
    private SplitMenuButton participants;
    
    private final String source = "team_info.fxml";
    
    @FXML
    public void initialize() {
        
        var currentTeam = data.getCurrentTeam();
        processes.getItems().clear();
        participants.getItems().clear();
    
        teamTitle.setText(currentTeam.getTitle());
        
        for(var process: currentTeam.getProcesses()) {
            
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
    
        currentTeam.getParticipants().stream()
                .map(MenuItem::new)
                .forEach(participants.getItems()::add);
    }
    
    public void back(ActionEvent event) throws IOException {
        
        showStage(event, data.getPreviousScene(), source);
    }
}
