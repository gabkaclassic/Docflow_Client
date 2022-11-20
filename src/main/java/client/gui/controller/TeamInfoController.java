package client.gui.controller;

import client.entity.Team;
import client.entity.process.Participant;
import client.sender.Sender;
import com.fasterxml.jackson.core.JsonProcessingException;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.control.SplitMenuButton;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import lombok.extern.slf4j.Slf4j;

import javax.swing.*;
import java.io.IOException;
import java.util.Objects;

@Slf4j
public class TeamInfoController extends Controller {
    
    @FXML
    private Label teamTitle;
    
    @FXML
    private SplitMenuButton processes;
    
    @FXML
    private SplitMenuButton participants;
    
    @FXML
    private TextField usernameField;
    @FXML
    private Label inviteParticipantErrorFiled;
    
    private String currentParticipant;
    
    private Team currentTeam;
    
    private final String source = "team_info.fxml";
    
    @FXML
    public void initialize() {
        
        currentTeam = data.getCurrentTeam();
        processes.getItems().clear();
        participants.getItems().clear();
    
        teamTitle.setText(currentTeam.getTitle());
        
        for(var process: currentTeam.getProcesses()) {
            
            var item = new MenuItem(process.getTitle());
            
            item.setOnAction(event -> {
                data.setCurrentProcess(process);
                try {
                    showStage(teamTitle, "process_info.fxml");
                } catch (IOException e) {
                    log.debug("Error of transition to process info scene", e);
                }
            });
            
            processes.getItems().add(item);
        }
    
        var isTeamLeader = Objects.equals(data.getParticipant().getId(), currentTeam.getTeamLeaderId());
        
        currentTeam.getParticipants().forEach(p -> {
            var item = new MenuItem(p);
            
            if(isTeamLeader && !p.equals(data.getParticipant().getUsername()))
                item.setOnAction(e -> currentParticipant = p);
            
            participants.getItems().add(item);
        });
    }
    
    public void invite(ActionEvent e) throws IOException {
        
        inviteParticipantErrorFiled.setVisible(false);
        var username = usernameField.getText();
        
        if(username == null || username.isBlank()) {
            showInviteParticipantError("This field can't be empty");
            return;
        }
        usernameField.clear();
        
        var response = Sender.invite(username, currentTeam.getTitle());
        if(response.isError()) {
            showInviteParticipantError("Unsuccessful invite");
            return;

        }
        
        data.refresh();
        refreshCurrentTeam();
        initialize();
    }
    
    public void kickOut(ActionEvent e) throws IOException {
    
        if(currentParticipant == null) {
            showInviteParticipantError("User is not selected");
            return;
        }
    
        var response = Sender.refuseInvite(currentParticipant, currentTeam.getTitle());
        if(response.isError()) {
            showInviteParticipantError("Unsuccessful kick out");
            return;
        }
        
        data.refresh();
        refreshCurrentTeam();
        initialize();
    }
    
    private void refreshCurrentTeam() {
        data.setCurrentTeam(
                data.getTeams().stream()
                        .filter(t -> t.getTitle().equals(currentTeam.getTitle()))
                        .findFirst().get()
        );
    }
    
    private void showStage(Node node, String to) throws IOException {
        
        data.setPreviousScene(source);
        
        Parent root = FXMLLoader.load(Objects.requireNonNull(getClass().getResource(to)));
        var stage = (Stage)(node.getScene().getWindow());
        var scene = new Scene(root);
        stage.setScene(scene);
        stage.show();
    }
    private void showInviteParticipantError(String error){
        inviteParticipantErrorFiled.setText(error);
        inviteParticipantErrorFiled.setVisible(true);
    }
    
    public void back(ActionEvent event) throws IOException {
        
        showStage(event, data.getPreviousScene(), source);
    }
}
