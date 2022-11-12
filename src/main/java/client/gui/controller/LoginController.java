package client.gui.controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;

import java.io.IOException;

public class LoginController extends Controller {
    private static final String source = "login.fxml";
    
    @FXML
    private Button back;
    
    @FXML
    public void initialize() {
    
        if(data.getPreviousScene() == null)
            back.setVisible(false);
    }
    public void switchToSignIn(ActionEvent event) throws IOException {
        
        showStage(event, "sign_in.fxml", source);
    }
    public void switchToSignUp(ActionEvent event) throws IOException {
    
        showStage(event, "sign_up.fxml", source);
    }
    public void back(ActionEvent event) throws IOException {
        
        showStage(event, data.getPreviousScene(), source);
    }
}
