package client.gui.controller;

import java.io.IOException;
import java.util.Objects;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.stage.Stage;

public class LoginController extends Controller {
    private final String source = "login.fxml";
    
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
    public void switchToMenu(ActionEvent event) throws IOException {
    
        showStage(event, "sign_in.fxml", source);
    }
    
    public void back(ActionEvent event) throws IOException {
        
        showStage(event, data.getPreviousScene(), source);
    }
}
