package client.gui.controller;

import client.gui.data.Data;
import client.sender.Sender;
import com.fasterxml.jackson.core.JsonProcessingException;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import javafx.scene.text.TextAlignment;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.Objects;

/**
 * Контроллер для сцены отображения главной информации, связанной с пользователем
 * @see Controller
 * */
public class GeneralInfoController extends Controller{
    @FXML
    private SplitMenuButton teams;
    @FXML
    private SplitMenuButton processes;
    
    @FXML
    private Label invitesLabel;
    @FXML
    private Accordion invites;
    @FXML
    private Label noProcessesMessage;
    
    @FXML
    private Label noTeamsMessage;
    
    @FXML
    public void initialize() throws IOException {
    
        data.refresh();
        
        noTeamsMessage.setVisible(false);
        noProcessesMessage.setVisible(false);
        teams.getItems().clear();
        processes.getItems().clear();
        invites.getPanes().clear();
        invites.setVisible(true);
        invitesLabel.setVisible(true);
        
        if(data.getInvites().isEmpty())
            invitesLabel.setVisible(false);
    
        for(var invite: data.getInvites()) {
            var acceptButton = new Button("Accept");
            acceptButton.getStylesheets().add(Objects.requireNonNull(this.getClass().getResource("acc_button.css")).toExternalForm());
            acceptButton.setTranslateX(-15);
            acceptButton.setOnAction(event -> {
                try {
                    invitesLabel.setVisible(false);
                    invites.setVisible(false);
                    
                    new Thread(() -> {
                        try {
                            Sender.accessInvite(invite);
                        } catch (JsonProcessingException e) {
                            throw new RuntimeException(e);
                        }
                    }).start();
                    
                    initialize();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            });
            var refuseButton = new Button("Refuse");
            refuseButton.getStylesheets().add(Objects.requireNonNull(this.getClass().getResource("refuse_button.css")).toExternalForm());
            refuseButton.setTranslateX(-5);
            refuseButton.setOnAction(event -> {
                try {
                    
                    invitesLabel.setVisible(false);
                    invites.setVisible(false);
                    
                    new Thread(() -> {
                        try {
                            Sender.refuseInvite(invite);
                        } catch (JsonProcessingException e) {
                            throw new RuntimeException(e);
                        }
                    }).start();
                    
                    initialize();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            });
    
            var bar = new ButtonBar();
            bar.getButtons().add(acceptButton);
            bar.getButtons().add(refuseButton);
    
            var anchor = new AnchorPane();
            var pane = new TitledPane();
            pane.getStylesheets().add(Objects.requireNonNull(this.getClass().getResource("titled-pane.css")).toExternalForm());
            anchor.getChildren().add(bar);
            pane.setTextAlignment(TextAlignment.CENTER);
            pane.setText(invite.getTeam().getTitle());
            pane.setContent(anchor);
            invites.getPanes().add(pane);
        }
        
        for(var team: data.getTeams()) {
        
            var item = new MenuItem(team.getTitle());
        
            item.setOnAction(event -> {
                data.setCurrentTeam(team);
                try {
                    this.showStage(teams, "team_info.fxml");
                    
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });
        
            teams.getItems().add(item);
        }
        
        for(var process: data.getProcesses().stream()
                .filter(p -> p.checkRules(data.getParticipant()))
                .toList()
        ) {
        
            var item = new MenuItem(process.getTitle());
    
            item.setOnAction(event -> {
                data.setCurrentProcess(process);
                
                try {
                    showStage(processes, "process_info.fxml");
                } catch (IOException ignored) {
                }
            });
        
            processes.getItems().add(item);
        }
        
        processes.setOnAction(event -> {
    
            if(processes.getItems().isEmpty())
                noProcessesMessage.setVisible(true);
            
        });
    
        teams.setOnAction(event -> {
        
            if(teams.getItems().isEmpty())
                noTeamsMessage.setVisible(true);
            
        });
        
    }
    
    public void createTeam(ActionEvent event) throws IOException{
        
        showStage(event, "create_team.fxml");
    }
    
    public void createProcess(ActionEvent event) throws IOException{
        
        showStage(event, "create_process.fxml");
    }
    
    public void back(ActionEvent event) throws IOException {
        
        showStage(event, data.getPreviousScene());
    }
    public void logout(ActionEvent event) throws IOException{

        try {
            var data = Data.getInstance();
    
            if(data.getParticipant() != null) {
        
                Sender.logout(data.getParticipant().getUsername());
            }
        }
        catch (Exception e) {
        }
        finally {
            data.clear();
        }
        showStage(event, "sign_in.fxml");
    }
    
    private void showStage(Node node, String to) throws IOException {
    
        Parent root = FXMLLoader.load(Objects.requireNonNull(getClass().getResource(to)));
        var stage = (Stage)(node.getScene().getWindow());
        var scene = new Scene(root);
        stage.setScene(scene);
        stage.show();
    }
}
