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
    
    private final static String source = "sign_up.fxml";
    @FXML
    public void initialize() {
        
        indicator.setVisible(false);
        signUpButton.setDisable(true);
        var password = (checkBox.isSelected() ? shownPassword : this.password).getText();
        var login = this.login.getText();
        this.password.setOnKeyPressed(keyEvent -> checkLoginAndPassword(login, password));
        shownPassword.setOnKeyPressed(keyEvent -> checkLoginAndPassword(login, password));
        this.login.setOnKeyPressed(keyEvent -> checkLoginAndPassword(login, password));
        
        hideError();
    }
    public void registrationUser(ActionEvent event) {
    
        indicator.setVisible(true);
        var progress = new Progress<>(() -> {
        
            indicator.setVisible(true);
        
            Response result = null;
            try {
                result = Sender.registration(login.getText(), checkBox.isSelected() ? shownPassword.getText() : password.getText());
    
                if (result.isError()) {
                    login.setStyle(errorStyle);
                    error.setText(result.getMessage());
                    showError();
                    return result;
                }
            } catch (IOException e) {
                log.warn("Registration error", e);
                showError();
            }
    
            return result;
        });
    
        indicator.progressProperty().bind(progress.progressProperty());
    
        progress.setOnSucceeded(workerStateEvent -> {
    
            try {
                showStage(event, "login.fxml", source);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            finally {
                indicator.setVisible(false);
            }
    
        });
        progress.setOnFailed(workerStateEvent -> indicator.setVisible(false));
        
        new Thread(progress).start();
    }
    
    private boolean checkLoginAndPassword(String login, String password) {
        
        var result = checkLogin(login) && checkPassword(password);
        
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
        
        showStage(event, "login.fxml", source);
    }
}
