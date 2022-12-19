package client;

import client.file.FileManager;
import client.gui.controller.SignInController;
import client.gui.data.Data;
import client.sender.Sender;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;

public class Interface extends Application {
    
    private static final String HOME_SCENE = "gui/controller/login.fxml";
    
    private static final FileManager fileManager = new FileManager();
    
    @Override
    public void start(Stage stage) throws IOException {
        
        
        FXMLLoader fxmlLoader = new FXMLLoader(Interface.class.getResource(HOME_SCENE));
        Scene scene = new Scene(fxmlLoader.load(), 831, 732);
        fxmlLoader.setRoot(scene);
        stage.setTitle("Document flow");
        stage.setScene(scene);
        stage.setOnCloseRequest(event -> {
            event.consume();
            try {
                closeProject(stage);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            stage.close();
        });
        stage.show();
    }
    
    /**
     * Настройка сохранения данных при выходе пользователя из системы
     * */
    private void closeProject(Stage stage) throws IOException {
        
        stage.close();
        var data = Data.getInstance();
        
        if(data.getParticipant() != null) {
    
            for(var process: data.getProcesses()) {
                var step = process.currentStep();
                Sender.updateStep(step);
                fileManager.updateDocuments(process.getTitle(), step.getDocuments());
                Sender.updateDocuments(step.getDocuments());
            }
            
            Sender.logout(data.getParticipant().getUsername());
            data.clear();
        }
    }
    
    public static void main(String[] args) {
    
        launch();
        
    }
}