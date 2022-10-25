package client.gui.controller;

import client.entity.process.Process;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import client.entity.Team;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.MenuItem;
import javafx.scene.control.SplitMenuButton;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.List;
import java.util.Objects;

public class GeneralInfoController extends Controller{
    @FXML
    private SplitMenuButton teams;
    @FXML
    private SplitMenuButton process;
    
    @FXML
    public void initialize() {
    
        teams.getItems().clear();
        data.getTeams().stream()
                .map(Team::getTitle)
                .forEach(title -> teams.getItems().add(new MenuItem(title)));
    
        process.getItems().clear();
        data.getProcesses().stream()
                .map(Process::getTitle)
                .forEach(title -> process.getItems().add(new MenuItem(title)));
    }
    
    public void createTeam(ActionEvent event) throws IOException{
    
        showStage(event, "create_team.fxml");
    }

}
