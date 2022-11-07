package client.gui.controller;

import client.sender.Sender;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;

import java.io.IOException;

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
    @FXML
    private ProgressBar progressBar;

    
    private final String source = "sign_up.fxml";

    private final String errorStyle = String.format("-fx-border-color: RED; -fx-border-width: 2; -fx-border-radius: 5;");
    @FXML
    public void initialize() {
        
        hideError();
    }
//     public void switchToLogin(ActionEvent event) throws IOException {
//
//        showStage(event, "login.fxml", source);
//    }
    public void registrationUser(ActionEvent event) throws IOException {
        
        try {
            var response = Sender.registration(login.getText(), password.getText());
    
            if(response.isError()) {
                password.setStyle(errorStyle);
                shownPassword.setStyle(errorStyle);
                error.setText(response.getMessage());
                showError();
                return;
            }
            showStage(event, "login.fxml", source);
        }
        catch (Exception e) {
            e.printStackTrace();
            showError();
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
