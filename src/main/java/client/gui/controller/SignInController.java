package client.gui.controller;

import client.response.InfoResponse;
import client.sender.Sender;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.CheckBox;
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
    @FXML
    private CheckBox  checkBox;
    @FXML
    private TextField shownPassword;
    
    private final String source = "sign_in.fxml";
    private final String errorStyle = String.format("-fx-border-color: RED; -fx-border-width: 2; -fx-border-radius: 5;");
    @FXML
    public void initialize() {
        
        hideError();
    }
    
//    public void switchToLogin(ActionEvent event) throws IOException {
//
//        showStage(event, "login.fxml", source);
//    }
    public void signIn(ActionEvent event) throws IOException {

        try {
            var response = Sender.login(login.getText(), password.getText());
            if(response.isError()) {
                login.setStyle(errorStyle);
                showError(response.getMessage());
                return;
            }
            data.setParticipant(response.getParticipant());
            data.setTeams(response.getTeams());
            data.setProcesses(response.getProcesses());
            showStage(event, "general_info.fxml", source);
        }
        catch (Exception e) {
            e.printStackTrace();
            showError("Unknown connection error");
        }
    }
    private void showError(String message) {
        
        error.setText(message);
        error.setVisible(true);
    }
    private void hideError() {
        error.setVisible(false);
    }
    @FXML
    private void changeVisibility(ActionEvent event){
        if(checkBox.isSelected()){
            shownPassword.setText(password.getText());
            shownPassword.setVisible(true);
            password.setVisible(false);
            return;
        }
        password.setText(shownPassword.getText());
        password.setVisible(true);
        shownPassword.setVisible(false);
    }
    
    public void back(ActionEvent event) throws IOException {
        
        showStage(event, data.getPreviousScene(), source);
    }
}
