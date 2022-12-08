package client.gui.controller;

import client.response.Response;
import client.sender.Sender;
import client.util.DataUtils;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;

/**
 * Контроллер для отображения сцены регистрации
 * @see Controller
 * */
@Slf4j
public class SignUpController extends Controller{
    @FXML
    private TextField login;
    @FXML
    private PasswordField password;
    @FXML
    private Label error;
    @FXML
    private CheckBox checkBox;
    
    @FXML
    private ProgressIndicator indicator;
    @FXML
    private TextField shownPassword;
    
    @FXML
    private Button signUpButton;
    
    private String messageResponse;
    
    private boolean errorResponse;
    
    @FXML
    public void initialize() {
        
        indicator.setVisible(false);
        signUpButton.setDisable(true);
        shownPassword.setVisible(false);

        this.password.setOnKeyReleased(keyEvent -> {
            var password = (checkBox.isSelected() ? shownPassword : this.password).getText();
            var login = this.login.getText();
            checkLoginAndPassword(login, password);
        });
        shownPassword.setOnKeyReleased(keyEvent -> {
            var password = (checkBox.isSelected() ? shownPassword : this.password).getText();
            var login = this.login.getText();
            checkLoginAndPassword(login, password);
        });
        this.login.setOnKeyReleased(keyEvent -> {
            var password = (checkBox.isSelected() ? shownPassword : this.password).getText();
            var login = this.login.getText();
            checkLoginAndPassword(login, password);
        });
        
        hideError();
    }
    public void registrationUser(ActionEvent event) {
    
        
            indicator.setVisible(true);
            var progress = new Progress<>(() -> {
            
                indicator.setVisible(true);
            
                Response result = null;
                try {
                    messageResponse = "";
                    errorResponse = false;
                    result = Sender.registration(login.getText(), checkBox.isSelected() ? shownPassword.getText() : password.getText());
                    errorResponse = result == null || result.isError();
                    messageResponse = (result == null) ? "Connection error" : result.getMessage();
                    
                }
                catch (Exception e) {
                    log.warn("Login error", e);
                    e.printStackTrace();
                    showRegistrationError(result);
                }
                finally {
                    indicator.setVisible(false);
                }
                
                return result;
            });
        
            indicator.progressProperty().bind(progress.progressProperty());
        
            progress.setOnSucceeded(workerStateEvent -> {
                try {
                    finishRegistration(event);
                } catch (IOException e) {
                    e.printStackTrace();
                    log.warn("Login error", e);
                    error.setText("Connection error");
                    showError();
                }
                finally {
                    indicator.setVisible(false);
                }
            });
        
            new Thread(progress).start();
    }
    
    private void showRegistrationError(Response result) {
        
        login.setStyle(errorStyle);
        error.setText(result.getMessage());
        showError();
    }
    
    private void finishRegistration(ActionEvent event) throws IOException {
    
        if(errorResponse) {
            indicator.setVisible(false);
            error.setText(messageResponse);
            error.setVisible(true);
            return;
        }
        
        try {
            showStage(event, "login.fxml");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    
    private boolean checkLoginAndPassword(String login, String password) {
        
        var result = checkLogin(login) && checkPassword(password);
        signUpButton.setDisable(true);
        
        if(result)
            signUpButton.setDisable(false);
        
        return result;
    }
    
    private boolean checkPassword(String password) {
        
        error.setVisible(false);
        
        if(!DataUtils.checkPassword((checkBox.isSelected() ? shownPassword.getText() : this.password.getText()))) {
            error.setText("Invalid password");
            error.setVisible(true);
            this.shownPassword.setStyle(errorStyle);
            this.password.setStyle(errorStyle);
            
            return false;
        }
        else {
            error.setText("");
            error.setVisible(false);
            this.password.setStyle("");
            this.shownPassword.setStyle(okStyle);
            
            return true;
        }
    }
    
    private boolean checkLogin(String login) {
        
        if(!DataUtils.checkLogin(login)) {
            signUpButton.setDisable(true);
            error.setText("Invalid login");
            error.setVisible(true);
            this.login.setStyle(errorStyle);
            
            return false;
        }
        else {
            signUpButton.setDisable(false);
            error.setText("");
            error.setVisible(false);
            this.login.setStyle(okStyle);
            
            return true;
        }
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
    
    private void showError() {
        
        error.setVisible(true);
    }
    private void hideError() {
        error.setVisible(false);
    }
    
    public void back(ActionEvent event) throws IOException {
        
        showStage(event, "login.fxml");
    }
}
