package client.gui.controller;

import client.response.InfoResponse;
import client.sender.Sender;
import client.util.DataUtils;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;

@Slf4j
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
    
    private final static String source = "sign_in.fxml";
    @FXML
    public void initialize() {
        
        hideError();
       
        password.setOnKeyPressed(keyEvent -> checkPassword(password.getText()));
        login.setOnKeyPressed(keyEvent -> checkLogin(login.getText()));
    }
    
    private void checkPassword(String password) {
        
        if(!DataUtils.checkPassword(password)) {
//            showError();
        }
    }
    
    private void checkLogin(String login) {
        
        if(!DataUtils.checkLogin(login)) {
//            showError();
        }
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
                catch (IOException e) {
                    log.warn("Login error", e);
                    e.printStackTrace();
                    showError("Unknown connection error");
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
                } catch (IOException e) {
                    e.printStackTrace();
                    log.warn("Login error", e);
                    showError("Unknown connection error");
                }
                finally {
                    indicator.setVisible(false);
                }
            });
            
            new Thread(progress).start();

        } finally {
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