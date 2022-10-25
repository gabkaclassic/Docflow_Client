package client.gui.controller;

import client.sender.Sender;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

import java.io.IOException;

public class SignUpController extends Controller{
    
    @FXML
    private TextField login;
    @FXML
    private PasswordField password;
    public void switchToLogin(ActionEvent event) throws IOException {
        
        showStage(event, "login.fxml");
    }
    public void registrationUser(ActionEvent event) throws IOException {
        
        Sender.registration(login.getText(), password.getText());
        showStage(event, "login.fxml");
    }

}
