package client.gui;
import java.awt.*;
import com.fasterxml.jackson.databind.ObjectMapper;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import javax.crypto.spec.DESKeySpec;
import java.io.IOException;
import java.util.List;

public class Interface extends Application {
    
    @Override
    public void start(Stage stage) throws IOException {

        FXMLLoader fxmlLoader = new FXMLLoader(Interface.class.getResource("controller/login.fxml"));
//        FXMLLoader fxmlLoader = new FXMLLoader(Interface.class.getResource("controller/create_process.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 1440, 1024);
        fxmlLoader.setRoot(scene);
        stage.setTitle("Document flow");
        stage.setScene(scene);
        stage.show();
    }
    
    public static void main(String[] args) throws IOException {

        launch();
        
    
    }
}