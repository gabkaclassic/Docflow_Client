package client.gui.controller;

import client.response.InfoResponse;
import client.sender.Sender;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;

import java.io.IOException;

/**
 * Контроллер для отображения сцены аутентификации
 * @see Controller
 * */
public class SignInController extends Controller {
    
    @FXML
    private Label error;
    @FXML
    private TextField login;
    @FXML
    private PasswordField password;
    @FXML
    private ProgressIndicator indicator;
    @FXML
    private CheckBox checkBox;
    @FXML
    private TextField shownPassword;
    @FXML
    public void initialize() {
        
        indicator.setVisible(false);
        hideError();
    }
    
    public void switchToLogin(ActionEvent event) throws IOException {
        
        showStage(event, "login.fxml");
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
                    showError("Unknown connection error");
                }
                finally {
                    indicator.setVisible(false);
                }
            });
            
            new Thread(progress).start();

        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {
            indicator.setVisible(false);
        }
    }
    
    private void finishSignIn(InfoResponse response, ActionEvent event) throws IOException {
        
        if(response == null || response.isError()) {
            indicator.setVisible(false);
            showError((response == null) ? "Connection error" : response.getMessage());
            return;
        }
        
        data.setData(response);
        indicator.setVisible(false);
        showStage(event, "general_info.fxml");
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
        
        showStage(event, "login.fxml");
    }
}