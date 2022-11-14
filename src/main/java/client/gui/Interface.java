package client.gui;

import client.file.FileManager;
import client.gui.data.Data;
import client.sender.Sender;
import com.fasterxml.jackson.core.JsonProcessingException;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;

@Slf4j
public class Interface extends Application {
    
    private static final String HOME_SCENE = "controller/login.fxml";
    
    @Override
    public void start(Stage stage) throws IOException {
        
        FXMLLoader fxmlLoader = new FXMLLoader(Interface.class.getResource(HOME_SCENE));
        Scene scene = new Scene(fxmlLoader.load(), 1440, 1024);
        fxmlLoader.setRoot(scene);
        stage.setTitle("Document flow");
        stage.setScene(scene);
        stage.setOnCloseRequest(event -> {
            event.consume();
            try {
                closeProject();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            stage.close();
        });
        stage.show();
    }
    private void closeProject() throws IOException {
        var data = Data.getInstance();
        var fileManager = new FileManager();
        
        if(data.getParticipant() != null) {
    
            for(var process: data.getProcesses()) {
                var step = process.currentStep();
                fileManager.updateDocuments(process.getTitle(), step.getDocuments());
                Sender.updateDocuments(step.getDocuments());
            }
            
            Sender.logout(data.getParticipant().getUsername());
        }
    }
    
    public static void main(String[] args) {
    
        log.trace("Application was started with home scene: " + HOME_SCENE);
        
        launch();
        
        log.trace("Application was finished");
    }
}