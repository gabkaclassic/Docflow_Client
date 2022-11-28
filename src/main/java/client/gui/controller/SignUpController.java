package client.gui.controller;

import client.sender.Sender;
import client.util.DataUtils;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
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
    private TextField shownPassword;
    
    private final static String source = "sign_up.fxml";
    
    private final String errorStyle = "-fx-border-color: RED; -fx-border-width: 2; -fx-border-radius: 5;";
    private final String okStyle = "-fx-border-color: BLUE; -fx-border-width: 1; -fx-border-radius: 5;";
    @FXML
    public void initialize() {
        
        password.setOnKeyPressed(keyEvent -> checkPassword((checkBox.isSelected() ? shownPassword.getText() : this.password.getText())));
        shownPassword.setOnKeyPressed(keyEvent -> checkPassword((checkBox.isSelected() ? shownPassword.getText() : this.password.getText())));
        login.setOnKeyPressed(keyEvent -> checkLogin(login.getText()));
        
        hideError();
    }
    public void registrationUser(ActionEvent event) {
        try {
            var response = Sender.registration(login.getText(), checkBox.isSelected() ? shownPassword.getText():password.getText());
    
            if(response.isError()) {
                login.setStyle(errorStyle);
                error.setText(response.getMessage());
                showError();
                return;
            }
            showStage(event, "login.fxml", source);
        }
        catch (IOException e) {
            log.warn("Registration error", e);
            showError();
        }
        
    }
    
    private void checkPassword(String password) {
        
        
        error.setVisible(false);
        
        if(!DataUtils.checkPassword((checkBox.isSelected() ? shownPassword.getText() : this.password.getText()))) {
            error.setText("Invalid password");
            error.setVisible(true);
            this.shownPassword.setStyle(errorStyle);
            this.password.setStyle(errorStyle);
        }
        else {
            error.setText("");
            error.setVisible(false);
            this.password.setStyle("");
            this.shownPassword.setStyle(okStyle);
        }
    }
    
    private void checkLogin(String login) {
        
        if(!DataUtils.checkLogin(login)) {
            error.setText("Invalid login");
            error.setVisible(true);
            this.login.setStyle(errorStyle);
        }
        else {
            error.setText("");
            error.setVisible(false);
            this.login.setStyle(okStyle);
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
