package client.gui.controller;

import client.response.InfoResponse;
import client.response.Response;
import client.sender.Sender;
import javafx.concurrent.Service;
import javafx.concurrent.Task;
import javafx.concurrent.WorkerStateEvent;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;

import javafx.scene.control.*;


import java.io.IOException;
import java.lang.ProcessHandle.Info;

public class SignInController extends Controller {
    @FXML
    private TextField login;
    @FXML
    private PasswordField password;
    
    @FXML
    private ProgressIndicator indicator;
    @FXML
    private Label error;
    @FXML
    private CheckBox checkBox;
    @FXML
    private TextField shownPassword;
    @FXML
    private ProgressIndicator progressIndicator;
    
    private final String source = "sign_in.fxml";
    private final String errorStyle = String.format("-fx-border-color: RED; -fx-border-width: 2; -fx-border-radius: 5;");
    @FXML
    public void initialize() {
        hideError();
    }
    

    public void switchToLogin(ActionEvent event) throws IOException {
        
        showStage(event, "login.fxml", source);
    }
    public void signIn(ActionEvent event) {
        
        try {
            
//            var progress = new Progress<>(() -> Sender.login(login.getText(), password.getText()));
//            indicator.visibleProperty().bind(progress.runningProperty());
//            progress.start();
            var response = Sender.login(login.getText(), checkBox.isSelected() ? shownPassword.getText(): password.getText());
//            var response = Sender.login(login.getText(), password.getText());


            if(response.isError()) {
                login.setStyle(errorStyle);
                showError(response.getMessage());
                return;
            }
            data.setParticipant(response.getParticipant());
            data.setTeams(response.getTeams());
            data.setProcesses(response.getProcesses());
            //progressIndicator.setVisible(false);
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
        
        showStage(event, "login.fxml", source);
    }
}

