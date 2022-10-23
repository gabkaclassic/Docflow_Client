package client.gui;

import client.entity.Team;
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
        FXMLLoader loader_1 = new FXMLLoader(getClass().getResource("new_Sign_in.fxml"));
        root =loader_1.load();
        NewSignIn newSignIn = loader_1.getController();
       //var leader = Sender.GetUserInfo(newSignIn.login.getText()).getParticipant();
        Sender.createTeam(TeamName.getText(), Long.parseLong(id.getText()), Sender.GetUserInfo(newSignIn.login.getText()).getParticipant());
        FXMLLoader loader_2 = new FXMLLoader(getClass().getResource("new_Main_Menu.fxml"));
        root =loader_2.load();
        NewMainMenu newMainMenu = loader_2.getController();
        newMainMenu.LoadInfo(Sender.GetUserInfo(newSignIn.login.getText()).getTeams(), Sender.GetUserInfo(newSignIn.login.getText()).getProcesses());
        stage = (Stage)((Node)event.getSource()).getScene().getWindow();
        scene = new Scene(root);
        stage.setScene(scene);
        stage.show();
    }
}
