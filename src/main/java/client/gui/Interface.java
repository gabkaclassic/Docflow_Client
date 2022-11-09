package client.gui;

import client.gui.data.Data;
import client.sender.Sender;
import com.fasterxml.jackson.core.JsonProcessingException;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class Interface extends Application {
    
    @Override
    public void start(Stage stage) throws IOException {

        FXMLLoader fxmlLoader = new FXMLLoader(Interface.class.getResource("controller/login.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 1440, 1024);
        fxmlLoader.setRoot(scene);
        stage.setTitle("Document flow");
        stage.setScene(scene);
        stage.setOnCloseRequest(event -> {
            event.consume();
            try {
                closeProject();
            } catch (JsonProcessingException e) {
                throw new RuntimeException(e);
            }
            stage.close();
        });
        stage.show();
    }
    private void closeProject() throws JsonProcessingException {
        var data =Data.getInstance();
        if(data.getParticipant()!=null){
            Sender.logout(data.getParticipant().getUsername());
        }
    }
    
    public static void main(String[] args) {

        launch();
    }
}