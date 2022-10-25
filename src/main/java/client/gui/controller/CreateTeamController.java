package client.gui.controller;

import client.gui.data.Data;
import client.sender.Sender;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.TextField;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class CreateTeamController extends Controller {
    @FXML
    private TextField TeamName;

    public void createTeam(ActionEvent event) throws IOException{
        
        var data = Data.getInstance();
        var leader = data.getParticipant();
        
        Sender.createTeam(TeamName.getText(), leader, new ArrayList<String>()); // TO DO
        
        showStage(event, "general_info.fxml");
    }
}