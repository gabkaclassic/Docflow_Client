package client.gui.controller;

import java.io.IOException;
import java.util.Objects;

import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class LoginController {

    public void switchToSignIn(ActionEvent event) throws IOException {
    
        showStage(event, "sign_in.fxml");
    }
    public void switchToSignUp(ActionEvent event) throws IOException {
    
        showStage(event, "sign_up.fxml");
    }
    public void switchToMenu(ActionEvent event) throws IOException {

        showStage(event, "sign_in.fxml");
    }
    
    private void showStage(ActionEvent event, String resource) throws IOException {
        
        Parent root = FXMLLoader.load(Objects.requireNonNull(getClass().getResource(resource)));
        var stage = (Stage)(((Node)event.getSource()).getScene().getWindow());
        var scene = new Scene(root);
        stage.setScene(scene);
        stage.show();
    }
}
