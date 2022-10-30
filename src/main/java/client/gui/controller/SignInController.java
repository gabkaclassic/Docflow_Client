package client.gui.controller;

import client.sender.Sender;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

import java.io.IOException;

public class SignInController extends Controller {
    @FXML
    private TextField login;
    @FXML
    private PasswordField password;
    
    @FXML
    private Label error;
    
    private final String source = "sign_in.fxml";
    @FXML
    public void initialize() {
        
        hideError();
    }
    
    public void switchToLogin(ActionEvent event) throws IOException {
        
        showStage(event, "login.fxml", source);
    }
    public void signIn(ActionEvent event) throws IOException {
        
        
        try {
            var response = Sender.login(login.getText(), password.getText());
            if(response.isError()) {
                showError();
                return;
            }
        }
        catch (Exception e) {
            
            showError();
            System.out.println(e);
        }

        
        var infoResponse = Sender.GetUserInfo();
        data.setParticipant(infoResponse.getParticipant());
        data.setTeams(infoResponse.getTeams());
        data.setProcesses(infoResponse.getProcesses());
        showStage(event, "general_info.fxml", source);
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
