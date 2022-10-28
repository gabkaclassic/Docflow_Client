package client.gui.controller;

import client.gui.data.Data;
import client.sender.Sender;
import com.fasterxml.jackson.core.JsonProcessingException;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.control.SplitMenuButton;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

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
    
    @FXML
    public void initialize() {
        
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
                        .map(MenuItem::getText).toList();
        var title = teamTitle.getText();
        
        if(!checkTitle(title)) return;
            
        Sender.createTeam(title, leader, participants);
        hideTeamError();
        
        showStage(event, "general_info.fxml");
    }
    
    public void addParticipant(ActionEvent event) throws JsonProcessingException {
    
        if(!checkUsername(username.getText())) return;
        
        var participant = new MenuItem(username.getText());
        participant.setOnAction(event1 -> participantsList.getItems().remove(participantsList.getItems().size()-1));
        participantsList.getItems().add(participant);
        username.setText("");
        hideUserError();
    }
    
    private void showUserError() {
        
        userError.setText("Account with this username doesn't exists");
    }
    
    private void showTeamError() {
        
        teamError.setText("Team with this title already exists");
    }
    
    private void hideUserError() {
        
        userError.setText("");
    }
    
    private void hideTeamError() {
        
        teamError.setText("");
    }
    
    private boolean checkUsername(String username) throws JsonProcessingException {
    
        boolean valid = username != null && !username.isBlank() && Sender.userExists(username).isExist();
        
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
}