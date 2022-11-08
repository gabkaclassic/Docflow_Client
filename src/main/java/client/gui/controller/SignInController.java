package client.gui.controller;

import client.response.InfoResponse;
import client.sender.Sender;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.control.TextField;

import java.io.IOException;

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
            
            indicator.setVisible(true);
            var progress = new Progress<>(() -> {
    
                indicator.setVisible(true);
                var result = Sender.login(login.getText(), password.getText());
                indicator.setVisible(false);
                
                return result;
            });

            indicator.progressProperty().bind(progress.progressProperty());
            
            progress.setOnSucceeded(workerStateEvent -> {
                try {
                    finishSignIn(progress.get(), event);
                } catch (IOException e) {
                    indicator.setVisible(false);
                    throw new RuntimeException(e);
                }
            });
            
            new Thread(progress).start();
        }
        catch (Exception e) {
            indicator.setVisible(false);
            e.printStackTrace();
            showError("Unknown connection error");
        }
        indicator.setVisible(false);
    }
    
    private void finishSignIn(InfoResponse response, ActionEvent event) throws IOException {
    
        if(response.isError()) {
            indicator.setVisible(false);
            showError(response.getMessage());
            return;
        }
    
        data.setParticipant(response.getParticipant());
        data.setTeams(response.getTeams());
        data.setProcesses(response.getProcesses());
        indicator.setVisible(false);
        showStage(event, "general_info.fxml", source);
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