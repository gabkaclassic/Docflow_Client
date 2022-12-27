package client.gui.controller;

import client.entity.team.Team;
import client.sender.Sender;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.Objects;

/**
 * Контроллер для отображения сцены с главной информацией о команде
 * @see Controller
 * */
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
    
    @FXML
    private Button invite;
    
    @FXML
    private Button kickOutButton;
    
    private Team currentTeam;
    
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
                } catch (IOException ignored) {
                }
            });
            
            processes.getItems().add(item);
        }
    
        var isTeamLeader = Objects.equals(data.getParticipant().getId(), currentTeam.getTeamLeaderId());
    
        invite.setVisible(true);
        kickOutButton.setVisible(true);
        usernameField.setVisible(true);
        
        if(!isTeamLeader) {
            invite.setVisible(false);
            kickOutButton.setVisible(false);
            usernameField.setVisible(false);
        }
        
        currentTeam.getParticipants().stream().filter(p -> !p.isBlank()).forEach(p -> {
            var item = new MenuItem(p);
            
            if(isTeamLeader && !p.equals(data.getParticipant().getUsername()))
                item.setOnAction(e -> currentParticipant = p);
            
            participants.getItems().add(item);
        });
    }
    
    public void invite(ActionEvent e) throws IOException {
        
        inviteParticipantErrorFiled.setVisible(false);
        var username = usernameField.getText();
        usernameField.clear();
        if(!checkUsername(username)) {
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
        initialize();
    }
    
    private boolean checkUsername(String username) {
     
        return username != null && !username.isBlank() && !username.equals(data.getParticipant().getUsername());
    }
    
    public void kickOut(ActionEvent e) throws IOException {
    
        var username = usernameField.getText();
        
        if(!checkUsername(username)) {
            showInviteParticipantError("Invalid username");
            return;
        }
    
        var response = Sender.kickParticipant(username, currentTeam.getTitle());
        if(response.isError()) {
            showInviteParticipantError("Invalid username");
            return;
        }
        
        currentTeam.getParticipants().remove(currentParticipant);
        usernameField.clear();

        data.refresh();
        initialize();
    }
    
    private void showStage(Node node, String to) throws IOException {
        
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
    
    public void refresh(ActionEvent event) throws IOException {
        
        data.refresh();
        currentTeam = data.getTeams().stream()
                .filter(t -> t.getTitle().equals(currentTeam.getTitle()))
                .findFirst().orElseThrow();
        initialize();
    }
}
