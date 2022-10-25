package client.gui.controller;

import client.entity.process.Process;
import client.entity.Team;
import client.sender.Sender;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

import java.io.IOException;

public class SignInController extends Controller {
    @FXML
    public TextField login;
    @FXML
    public PasswordField password;

    public void switchToLogin(ActionEvent event) throws IOException {
        
        showStage(event, "login.fxml");
    }
    public void signIn(ActionEvent event) throws IOException {
        Sender.login(login.getText(), password.getText());
        var user_info = Sender.GetUserInfo();
        var listTeamsTitle = user_info.getTeams().stream().map(Team::getTitle).toList();
        var listProcessesTitle = user_info.getProcesses().stream().map(Process::getTitle).toList();
        FXMLLoader loader = new FXMLLoader(getClass().getResource("general_info.fxml"));
        GeneralInfoController generalInfoController = loader.getController();
        generalInfoController.loadInfo(user_info.getTeams(), user_info.getProcesses());
    
        showStage(event, "general_info.fxml");
    }
}
