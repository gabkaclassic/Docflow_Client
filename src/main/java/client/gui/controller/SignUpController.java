package client.gui.controller;

import client.sender.Sender;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.Objects;

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
