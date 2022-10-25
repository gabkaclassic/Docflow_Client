package client.gui.controller;

import client.entity.process.Process;
import client.sender.Sender;
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

public class GeneralInfoController {
    private Sender sender;
    private Stage stage;
    private Scene scene;
    private Parent root;
    @FXML
    private SplitMenuButton teams;
    @FXML
    private SplitMenuButton proccess;

    public void LoadInfo(List<Team> teamList, List<Process> processList){
        teams.getItems().clear();

        teamList.stream().map(Team::getTitle).forEach(title -> teams.getItems().add(new MenuItem(title)));

        proccess.getItems().clear();

        processList.stream().map(Process::getTitle).forEach(title -> proccess.getItems().add(new MenuItem(title)));

    }
    public void  CreateTeam(ActionEvent event) throws IOException{
        Parent root = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("create_team.fxml")));
        stage = (Stage)(((Node)event.getSource()).getScene().getWindow());
        scene = new Scene(root);
        stage.setScene(scene);
        stage.show();


    }

}
