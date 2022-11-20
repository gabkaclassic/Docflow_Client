package client.gui.controller;

import client.file.FileManager;
import client.gui.data.Data;
import client.sender.Sender;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.MenuItem;
import javafx.scene.control.SplitMenuButton;
import javafx.stage.Stage;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.util.Objects;

@Slf4j
public class GeneralInfoController extends Controller{
    @FXML
    private SplitMenuButton teams;
    @FXML
    private SplitMenuButton processes;
    @FXML
    private Button back;
    
    private static final String source = "general_info.fxml";
    @FXML
    public void initialize() throws IOException {
    
        data.refresh();
        
        if(data.getPreviousScene() == null)
            back.setVisible(false);
        
        teams.getItems().clear();
        processes.getItems().clear();
        
        for(var team: data.getTeams()) {
        
            var item = new MenuItem(team.getTitle());
        
            item.setOnAction(event -> {
                data.setCurrentTeam(team);
                try {
                    this.showStage(teams, "team_info.fxml");
                    
                } catch (IOException e) {
                    log.warn("Error of transition to team info scene", e);
                }
            });
        
            teams.getItems().add(item);
        }
        
        for(var process: data.getProcesses().stream()
                .filter(p -> p.checkRules(data.getParticipant()))
                .toList()
        ) {
        
            var item = new MenuItem(process.getTitle());
    
            item.setOnAction(event -> {
                data.setCurrentProcess(process);
                
                try {
                    showStage(processes, "process_info.fxml");
                } catch (IOException e) {
                    log.warn("Error of transition to process info scene", e);
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
        
        if(!data.getPreviousScene().contains("sign"))
            showStage(event, data.getPreviousScene(), source);
        else
            showStage(event, source, source);
    }
    public void logout(ActionEvent event) throws IOException{

        try {
            var data = Data.getInstance();
    
            if(data.getParticipant() != null) {
        
                Sender.logout(data.getParticipant().getUsername());
            }
        }
        catch (Exception e) {
    
            log.warn("Logout error", e);
        }
        finally {
            data.clear();
        }
        showStage(event, "sign_in.fxml", source);
    }
    
    private void showStage(Node node, String to) throws IOException {
    
        data.setPreviousScene(source);
    
        Parent root = FXMLLoader.load(Objects.requireNonNull(getClass().getResource(to)));
        var stage = (Stage)(node.getScene().getWindow());
        var scene = new Scene(root);
        stage.setScene(scene);
        stage.show();
    }
}
