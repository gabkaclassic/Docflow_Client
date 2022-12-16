package client.gui.controller;

import client.entity.Team;
import client.gui.data.Data;
import client.response.Response;
import client.sender.Sender;
import com.fasterxml.jackson.core.JsonProcessingException;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * Контроллер для сцены создания команды
 * @see Controller
 * */
@Slf4j
public class CreateTeamController extends Controller {
    @FXML
    private TextField teamTitle;
    @FXML
    private TextField username;
    @FXML
    private SplitMenuButton participantsList;
    @FXML
    private Label teamError;
    @FXML
    private Button addParticipantButton;
    
    @FXML
    private Label noParticipantsMessage;
    @FXML
    private Label userError;
    @FXML
    private  Label creationError;
    @FXML
    private Button createTeamButton;
    @FXML
    private ProgressIndicator indicator;
    
    private final static String source = "create_team.fxml";
    private final String alreadyExistTeamError = "Team with this title already exists";
    private final String blankFieldError = "The text field can't be blank";
    private final String dontExistAccountError = "Account with this username doesn't exists";
    private final  String selfAddingError = "You can't try your self as participant";
    @FXML
    public void initialize() {
        
        hideTeamError();
        hideUserError();
        hideCreationError();
        participantsList.getItems().clear();

        username.setOnKeyPressed(event -> {
                if (event.getCode().equals(KeyCode.ENTER)) {
                    try {
                        addParticipant(new ActionEvent());
                    } catch (JsonProcessingException e) {
                        log.debug(e.getMessage());
                    }
                }
        });
        teamTitle.setOnMouseEntered(keyEvent->{
            if(Objects.equals(teamTitle.getText(), "")){
                teamTitle.setStyle(errorStyle);
                createTeamButton.setDisable(true);
            }
            else{
                createTeamButton.setDisable(false);
                teamTitle.setStyle(okStyle);
            }
        });
        teamTitle.setOnMouseExited(keyEvent->{
            if(Objects.equals(teamTitle.getText(), "")){
                teamTitle.setStyle(errorStyle);
                createTeamButton.setDisable(true);
            }
            else{
                createTeamButton.setDisable(false);
                teamTitle.setStyle(okStyle);
            }
        });
        username.setOnMouseEntered(keyEvent->{
            if(Objects.equals(username.getText(), "")){
                username.setStyle(errorStyle);
                addParticipantButton.setDisable(true);
            }
            else{
                addParticipantButton.setDisable(false);
                username.setStyle(okStyle);
            }
        });
        username.setOnMouseExited(keyEvent->{
            if(Objects.equals(username.getText(), "")){
                username.setStyle(errorStyle);
                addParticipantButton.setDisable(true);
            }
            else{
                addParticipantButton.setDisable(false);
                username.setStyle(okStyle);
            }
        });
        
        teamTitle.setOnKeyPressed(event -> {
            if (event.getCode().equals(KeyCode.ENTER)) {
                try {
                    checkTitle(teamTitle.getText());
                } catch (JsonProcessingException e) {
                    log.debug(e.getMessage());
                }
            }
        });
    
        teamTitle.setOnInputMethodTextChanged(event -> {
                try {
                    checkTitle(teamTitle.getText());
                } catch (JsonProcessingException e) {
                    log.debug(e.getMessage());
                }
        });
    
        participantsList.setOnContextMenuRequested(event -> {
        
            if(participantsList.getItems().isEmpty()) {
                noParticipantsMessage.setVisible(true);
                noParticipantsMessage.setText("There are no invited participants");
            }
        });
    }
    
    public void createTeam(ActionEvent event) throws IOException{

        hideTeamError();
        hideUserError();
        hideCreationError();
        if(!checkTitle(teamTitle.getText())) return;
        indicator.setVisible(true);
        var progress = new Progress<>(() -> {
            
            indicator.setVisible(true);
            var data = Data.getInstance();
            var leader = data.getParticipant();
            var participants = participantsList.getItems().stream()
                    .map(MenuItem::getText).collect(Collectors.toList());
            var title = teamTitle.getText();
            var team = new Team();

            team.setTitle(title);
            team.setTeamLeaderId(leader.getId());
            participants.add(leader.getOwner().getUsername());
            team.addParticipants(participants);

            var result = Sender.createTeam(team);
            hideTeamError();
            indicator.setVisible(false);
            return result;
        });
        indicator.progressProperty().bind(progress.progressProperty());
        progress.setOnSucceeded(workerStateEvent -> {
            try {
                finishCreateTeam(progress.get(), event);

            } catch (IOException e) {
                indicator.setVisible(false);
                log.warn("Create team error", e);
            }
        });
        progress.setOnFailed(workerStateEvent -> indicator.setVisible(false));
        
        new Thread(progress).start();
    }
    private void finishCreateTeam(Response response, ActionEvent event) throws IOException {
        if (response.isError()){
            indicator.setVisible(false);
            showCreationError(response.getMessage());
            return;
        }
        showStage(event, "general_info.fxml", source);
    }
    
    public void addParticipant(ActionEvent event) throws JsonProcessingException {
    
        if(!checkUsername(username.getText())) return;
        
        var participant = new MenuItem(username.getText());
        participant.setOnAction(event1 -> participantsList.getItems().remove(participant));
        participantsList.getItems().add(participant);
        username.setText("");
        noParticipantsMessage.setVisible(false);
        hideUserError();
    }
    
    private void showUserError() {
        
        userError.setVisible(true);
    }
    
    private void showTeamError() {
        
        teamError.setVisible(true);
    }
    private void showCreationError(String errorMessage) {
        creationError.setText(errorMessage);
        creationError.setVisible(true);
    }

    private void hideUserError() {
        
        userError.setVisible(false);
    }
    
    private void hideTeamError() {
        
        teamError.setVisible(false);
    }
    private void hideCreationError(){
        creationError.setVisible(false);
    }
    private boolean checkUsername(String username) throws JsonProcessingException {

        boolean valid = username != null && !username.isBlank();
        if(!valid){
            userError.setText(blankFieldError);
            showUserError();
            return false;

        }
        valid = !(username.equals(data.getParticipant().getUsername()));
        if (!valid){
            userError.setText(selfAddingError);
            showUserError();
            return false;
        }
        valid = Sender.userExists(username).isExist();
        
        if(!valid){
            userError.setText(dontExistAccountError);
            showUserError();
        }
        
        return valid;
    }
    
    public boolean checkTitle(String title) throws JsonProcessingException {
        boolean valid = title != null && !title.isBlank();
        if(!valid){
            teamError.setText(blankFieldError);
            showTeamError();
            return false;

        }
        valid = !Sender.teamExists(title).isExist();
        
        if(!valid){
            teamError.setText(alreadyExistTeamError);
            showTeamError();

        }
        return valid;
    }
    public void back(ActionEvent event) throws IOException {
        
        showStage(event, data.getPreviousScene(), source);
    }
}