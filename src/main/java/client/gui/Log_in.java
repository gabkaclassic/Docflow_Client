package client.gui;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Group;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

public class Log_in {

    @FXML
    private AnchorPane pane;
    
    @FXML
    private ResourceBundle resources;

    @FXML
    private URL location;

    @FXML
    public Button Sign_in_button;

    @FXML
    private Button Sign_up_button;

    @FXML
    void initialize() {
        Sign_in_button.setOnAction(actionEvent -> {
            Sign_in_button.getScene().getWindow().hide();

            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(getClass().getResource("Sign_in.fxml"));

            try {
                loader.load();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }

        });

        Sign_up_button.setOnAction(actionEvent -> {
            Sign_up_button.getScene().getWindow().hide();

            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(getClass().getResource("Sign_up.fxml"));

            try {
                loader.load();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });

        Button e = new Button();
        e.setLayoutX(500);
        e.setLayoutY(500);
        e.setText("NEW BUTTON");

        pane.getChildren().add(e);
        

        
        
    }

}
