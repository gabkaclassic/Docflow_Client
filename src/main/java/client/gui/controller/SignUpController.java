package client.gui.controller;

import client.sender.Sender;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

import java.io.IOException;

public class SignUpController extends Controller{
    @FXML
    private TextField login;
    @FXML
    private PasswordField password;
    @FXML
    private Label error;
    
    private final String source = "sign_up.fxml";
    @FXML
    public void initialize() {
        
        hideError();
    }
    public void switchToLogin(ActionEvent event) throws IOException {
        
        showStage(event, "login.fxml", source);
    }
    public void registrationUser(ActionEvent event) throws IOException {
        
        try {
            var response = Sender.registration(login.getText(), password.getText());
    
            if(response.isError()) {
                error.setText(response.getMessage());
                showError();
                return;
            }
            showStage(event, "login.fxml", source);
        }
        catch (Exception e) {
            e.printStackTrace();
            showError();
        }
        
    }
    
    private void showError() {
        
        error.setVisible(true);
    }
    private void hideError() {
        error.setVisible(false);
    }
    
    public void back(ActionEvent event) throws IOException {
        
        showStage(event, data.getPreviousScene(), source);
    }
}
