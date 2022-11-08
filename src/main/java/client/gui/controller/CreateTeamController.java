package client.gui.controller;

import client.entity.Team;
import client.gui.data.Data;
import client.sender.Sender;
import com.fasterxml.jackson.core.JsonProcessingException;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.control.SplitMenuButton;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;

import java.io.IOException;
import java.util.stream.Collectors;

public class CreateTeamController extends Controller {
    @FXML
    private TextField teamTitle;
    @FXML
    private TextField username;
    @FXML
    private SplitMenuButton participantsList;
    @FXML
    private Label teamError;
    @FXML
    private Label userError;
    
    private final String source = "create_team.fxml";
    @FXML
    public void initialize() {
        
        hideTeamError();
        hideUserError();
        
        participantsList.getItems().clear();
    
        username.setOnKeyPressed(event -> {
                if (event.getCode().equals(KeyCode.ENTER)) {
                    try {
                        addParticipant(new ActionEvent());
                    } catch (JsonProcessingException e) {
                        throw new RuntimeException(e);
                    }
                }
        });
        
        teamTitle.setOnKeyPressed(event -> {
            if (event.getCode().equals(KeyCode.ENTER)) {
                try {
                    checkTitle(teamTitle.getText());
                } catch (JsonProcessingException e) {
                    throw new RuntimeException(e);
                }
            }
        });
    
        teamTitle.setOnInputMethodTextChanged(event -> {
                try {
                    checkTitle(teamTitle.getText());
                } catch (JsonProcessingException e) {
                    throw new RuntimeException(e);
                }
        });
        
    }
    
    public void createTeam(ActionEvent event) throws IOException{
        
        var data = Data.getInstance();
        var leader = data.getParticipant();
        var participants = participantsList.getItems().stream()
                        .map(MenuItem::getText).collect(Collectors.toList());
        var title = teamTitle.getText();
        
        if(!checkTitle(title)) return;
    
        var team = new Team();
    
        team.setTitle(title);
        team.setTeamLeaderId(leader.getId());
        participants.add(leader.getOwner().getUsername());
        team.addParticipants(participants);
        
        var response = Sender.createTeam(team);
        hideTeamError();
        
        if(response.isError()) {
//            showError();
            return;
        }
        
        showStage(event, "general_info.fxml", source);
    }
    
    public void switchToMainMenu(ActionEvent event) throws IOException {
    
        showStage(event, "general_info.fxml", source);
    }
    
    public void addParticipant(ActionEvent event) throws JsonProcessingException {
    
        if(!checkUsername(username.getText())) return;
        
        var participant = new MenuItem(username.getText());
        participant.setOnAction(event1 -> participantsList.getItems().remove(participant));
        participantsList.getItems().add(participant);
        username.setText("");
        hideUserError();
    }
    
    private void showUserError() {
        
        userError.setVisible(true);
    }
    
    private void showTeamError() {
        
        teamError.setVisible(true);
    }
    
    private void hideUserError() {
        
        userError.setVisible(false);
    }
    
    private void hideTeamError() {
        
        teamError.setVisible(false);
    }
    
    private boolean checkUsername(String username) throws JsonProcessingException {
    
        boolean valid = username != null
                && !username.isBlank()
                && !username.equals(data.getParticipant().getUsername())
                && Sender.userExists(username).isExist();
        
        if(!valid)
            showUserError();
        
        return valid;
    }
    
    public boolean checkTitle(String title) throws JsonProcessingException {
    
        boolean valid = title != null && !title.isBlank() && !Sender.teamExists(title).isExist();
        
        if(!valid)
            showTeamError();
        
        return valid;
    }
    
    public void back(ActionEvent event) throws IOException {
        
        showStage(event, data.getPreviousScene(), source);
    }
}