package client.gui.controller;

import client.response.InfoResponse;
import client.sender.Sender;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;

import javafx.scene.control.*;


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

            indicator.setVisible(true);
            var progress = new Progress<>(() -> {
    
                indicator.setVisible(true);
                
                InfoResponse result = null;
                try {
                    result = Sender.login(login.getText(), checkBox.isSelected() ? shownPassword.getText() : password.getText());
                }
                catch (Exception e) {
                    e.printStackTrace();
                }
                finally {
                    indicator.setVisible(false);
                }
                
                
                return result;
            });

            indicator.progressProperty().bind(progress.progressProperty());
            
            progress.setOnSucceeded(workerStateEvent -> {
                try {
                    finishSignIn(progress.get(), event);
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
                finally {
                    indicator.setVisible(false);
                }
            });
            
            new Thread(progress).start();

        }
        catch (Exception e) {
            e.printStackTrace();
            showError("Unknown connection error");
        }
        finally {
            indicator.setVisible(false);
        }
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