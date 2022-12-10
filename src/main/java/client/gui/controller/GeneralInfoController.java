package client.gui.controller;

import client.gui.data.Data;
import client.sender.Sender;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
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
    private Button back;
    
    @FXML
    public void initialize() throws IOException {
    
        data.refresh();
        
        if(data.getPreviousScene() == null)
            back.setVisible(false);
        
        teams.getItems().clear();
        processes.getItems().clear();
        invites.getPanes().clear();
        invitesLabel.setVisible(true);
        
        if(data.getInvites().isEmpty())
            invitesLabel.setVisible(false);
    
        for(var invite: data.getInvites()) {
            var acceptButton = new Button("Accept");
            acceptButton.setOnAction(event -> {
                try {
                    Sender.accessInvite(invite);
                    data.refresh();
                    initialize();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            });
            var refuseButton = new Button("Refuse");
            refuseButton.setOnAction(event -> {
                try {
                    Sender.refuseInvite(invite);
                    data.refresh();
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
            anchor.getChildren().add(bar);
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
                    
                } catch (IOException ignored) {
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
        
        processes.setOnContextMenuRequested(event -> {
            
            if(processes.getItems().isEmpty()) {
                noProcessesMessage.setVisible(true);
                noProcessesMessage.setText("There are no available processes");
            }
        });
    
        teams.setOnContextMenuRequested(event -> {
        
            if(teams.getItems().isEmpty()) {
                noTeamsMessage.setVisible(true);
                noTeamsMessage.setText("There are no available teams");
            }
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
