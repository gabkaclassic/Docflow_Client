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
    @FXML
    private TextField id;
    public void createTeam(ActionEvent event) throws IOException{
        
        var data = Data.getInstance();
        var participant = data.getParticipant();
        
        Sender.createTeam(TeamName.getText(), participant);
        Sender.invite(participant.getOwner().getUsername(), TeamName.getText());
        
        var loader_2 = new FXMLLoader(getClass().getResource("general_info.fxml"));

        GeneralInfoController generalInfoController = loader_2.getController();
        var response = Sender.GetUserInfo();
        generalInfoController.loadInfo(response.getTeams(), response.getProcesses());
    
        showStage(event, "general_info.fxml");
    }
}