package client.gui.controller;

import client.gui.data.Data;
import client.sender.Sender;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.TextField;

import java.io.IOException;

public class CreateTeamController extends Controller {
    @FXML
    private TextField TeamName;

    public void createTeam(ActionEvent event) throws IOException{
        
        var data = Data.getInstance();
        var participant = data.getParticipant();
        
        Sender.createTeam(TeamName.getText(), participant);
    
        showStage(event, "general_info.fxml");
    }
}