package client.gui.controller;

import client.gui.data.Data;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.Objects;

/**
 * Класс-родитель для всех контроллеров
 * @see CreateProcessController
 * @see CreateTeamController
 * @see GeneralInfoController
 * @see LoginController
 * @see ProcessInfoController
 * @see SignInController
 * @see SignUpController
 * @see TeamInfoController
 * */
public class Controller {
    
    /**
     * Общие данные для контроллеров
     * */
    protected Data data;
    
    protected static String source;
    
    protected final String errorStyle = "-fx-border-color: RED; -fx-border-width: 2; -fx-border-radius: 5;";
    protected final String okStyle = "-fx-border-color: BLUE; -fx-border-width: 1; -fx-border-radius: 5;";
    
    {
        data = Data.getInstance();
    }
    
    /**
     * Переход на другую сцену (контроллер)
     * */
    protected void showStage(ActionEvent event, String resource) throws IOException {
        
        Parent root = FXMLLoader.load(Objects.requireNonNull(getClass().getResource(resource)));
        var stage = (Stage)(((Node)event.getSource()).getScene().getWindow());
        var scene = new Scene(root);
        stage.setScene(scene);
        stage.show();
    }
    
    public void back(ActionEvent event) throws IOException {
        
        data.refresh();
        showStage(event, data.getPreviousScene());
    }
}
