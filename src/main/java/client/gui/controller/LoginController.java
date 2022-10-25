package client.gui.controller;

import java.io.IOException;
import java.util.Objects;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class LoginController extends Controller {
    
    public void switchToSignIn(ActionEvent event) throws IOException {
    
        showStage(event, "sign_in.fxml");
    }
    public void switchToSignUp(ActionEvent event) throws IOException {
    
        showStage(event, "sign_up.fxml");
    }
    public void switchToMenu(ActionEvent event) throws IOException {

        showStage(event, "sign_in.fxml");
    }
}
