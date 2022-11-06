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
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.control.TextField;

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
    
    private final String source = "sign_in.fxml";
    @FXML
    public void initialize() {
        
        hideError();
    }
    
    public void switchToLogin(ActionEvent event) throws IOException {
        
        showStage(event, "login.fxml", source);
    }
    public void signIn(ActionEvent event) {
        
        try {
            
            var progress = new Progress<>(() -> Sender.login(login.getText(), password.getText()));
            indicator.visibleProperty().bind(progress.runningProperty());
            progress.start();
            var response = progress.getValue();
            
            if(response.isError()) {
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
    
    public void back(ActionEvent event) throws IOException {
        
        showStage(event, data.getPreviousScene(), source);
    }
}

