package client.gui.controller;

import client.sender.Sender;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
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
        var infoResponse = Sender.GetUserInfo();
        data.setParticipant(infoResponse.getParticipant());
        data.setTeams(infoResponse.getTeams());
        data.setProcesses(infoResponse.getProcesses());
    
        showStage(event, "general_info.fxml");
    }
}
