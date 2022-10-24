package client.gui;

import client.entity.Team;
import client.entity.process.Participant;
import client.gui.data.Data;
import client.sender.Sender;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.List;

public class CreateTeam {
    private Sender sender;
    private Stage stage;
    private Scene scene;
    private Parent root;
    @FXML
    private TextField TeamName;
    @FXML
    private TextField id;
    public void createteam(ActionEvent event) throws IOException{
        
        var data = Data.getInstance();
        data.getParticipant();
        
        FXMLLoader loader_1 = new FXMLLoader(getClass().getResource("new_Sign_in.fxml"));
        root =loader_1.load();
        NewSignIn newSignIn = loader_1.getController();
        Participant participant = Sender.GetUserInfo().getParticipant();
        Sender.createTeam(TeamName.getText(), participant);
        Sender.invite(participant.getOwner().getUsername(), TeamName.getText());
        FXMLLoader loader_2 = new FXMLLoader(getClass().getResource("new_Main_Menu.fxml"));
        root =loader_2.load();
        NewMainMenu newMainMenu = loader_2.getController();
        var response = Sender.GetUserInfo();
        newMainMenu.LoadInfo(response.getTeams(), response.getProcesses());
        stage = (Stage)((Node)event.getSource()).getScene().getWindow();
        scene = new Scene(root);
        stage.setScene(scene);
        stage.show();
    }
}
