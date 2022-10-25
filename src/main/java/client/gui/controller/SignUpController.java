package client.gui.controller;

import client.sender.Sender;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.Objects;

public class SignUpController {
    private Sender sender;
    private Stage stage;
    private Scene scene;
    private Parent root;
    @FXML
    private TextField login;
    @FXML
    private PasswordField password;
    public void switchToLogin(ActionEvent event) throws IOException {
        Parent root = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("log_in.fxml")));
        stage = (Stage)(((Node)event.getSource()).getScene().getWindow());
        scene = new Scene(root);
        stage.setScene(scene);
        stage.show();

    }
    public void RegistrateUser(ActionEvent event) throws IOException {
        Sender.registration(login.getText(),password.getText());
        FXMLLoader loader = new FXMLLoader(getClass().getResource("log_in.fxml"));
        root = loader.load();
        stage = (Stage)(((Node)event.getSource()).getScene().getWindow());
        scene = new Scene(root);
        stage.setScene(scene);
        stage.show();



    }

}
