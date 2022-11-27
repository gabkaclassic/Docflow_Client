package client.gui.controller;

import client.entity.Team;
import client.entity.process.Participant;
import client.entity.process.Process;
import client.entity.process.Rules;
import client.entity.process.step.Step;
import client.entity.process.document.Document;
import client.sender.Sender;
import com.fasterxml.jackson.core.JsonProcessingException;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.security.InvalidParameterException;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Контроллер для сцены создания процесса
 * @see Controller
 * */
@Slf4j
public class CreateProcessController extends Controller {
    @FXML
    private TextField processTitle;
    
    @FXML
    private TextField stepTitle;
    @FXML
    private TextField documentTitle;
    
    @FXML
    private TextField stepNumber;
    @FXML
    private TextField documentExtension;
    
    @FXML
    private ChoiceBox<String> teamsList;
    
    @FXML
    private ChoiceBox<String> rulesList;
    
    @FXML
    private SplitMenuButton stepsList;
    
    @FXML
    private SplitMenuButton participantsList;
    
    @FXML
    private ChoiceBox<String> participantsChoice;
    
    @FXML
    private SplitMenuButton documentsList;
    @FXML
    private Label createProcessErrorField;
    @FXML
    private Label addStepError;
    @FXML
    private Label addDocumentError;
    @FXML
    private Label addParticipantErrorField;
    
    private final static String source = "create_process.fxml";
    private Participant creator;
    
    private Set<Step> steps = new HashSet<>();
    
    private final Map<String, Rules> rules = new HashMap<>();
    
    private final Set<Document> documents = new HashSet<>();
    
    private Team team;
    
    public void initialize() {

        creator = data.getParticipant();
        
        creator.getTeams().stream()
                .filter(t -> t.getTeamLeaderId().equals(creator.getId()))
                .map(Team::getTitle)
                .forEach(teamsList.getItems()::add);
        participantsList.getItems().clear();
        documentsList.getItems().clear();
        stepsList.getItems().clear();
        stepNumber.setText("1");
        teamsList.setOnAction(e -> {
            selectTeam(teamsList.getValue());
            steps.clear();
            participantsList.getItems().clear();
            documentsList.getItems().clear();
            stepsList.getItems().clear();
        }
        );
        Arrays.stream(Rules.values()).map(Rules::name).forEach(rulesList.getItems()::add);
    }
    
    private void selectTeam(String value) {

        try {
            team = creator.getTeams().stream().filter(t -> t.getTitle().equals(value)).findFirst().orElseThrow();
        }
        catch (NoSuchElementException e) {
            log.debug("No such team error", e);
            
//            showError();
        }
        
        team.getParticipants().forEach(participantsChoice.getItems()::add);
    }
    
    public void createProcess(ActionEvent event) throws IOException {

        createProcessErrorField.setVisible(false);
        if(processTitle.getText()==null || processTitle.getText().isBlank()) {
            showCreateProcessError("Process can't be blank");
            return;
        }
        if (Sender.processExists(processTitle.getText()).isExist()){
            showCreateProcessError("Process with such title already exist");
            return;
        }
        
        if(steps.size() == 0) {
            showCreateProcessError("Process can't have 0 steps");
            return;

        }
        
        var stepList = steps.stream().sorted(Comparator.comparingInt(Step::getNumber)).toList();
    
        for (int i = 0; i < stepList.size(); i++)
            stepList.get(i).setNumber(i+1);
        
        var process = new Process();
        process.setTitle(processTitle.getText());
        process.setSteps(stepList);
        process.setCurrentStep(process.getSteps().stream().map(Step::getNumber).min(Integer::compareTo).get());

        var response = Sender.createProcess(team, process);  // TO DO
        
        if(response == null || response.isError()) {
            showCreateProcessError(response.getMessage());
            return;
        }
        
        showStage(event, "general_info.fxml", source);
    }
    
    public void saveStep() {
    
        addStepError.setVisible(false);
        int number;
        String title;
        
        if(rules.isEmpty() || documents.isEmpty()) {
            showAddStepError("Rules and documents shouldn't be empty");
            return;
        }
        
        try {
            
            number = Integer.parseInt(stepNumber.getText());
            title = stepTitle.getText();
            
            if(number <= 0)
                throw new NumberFormatException();
            
            if(title == null || title.isBlank())
                throw new InvalidParameterException();
        }
        catch (InvalidParameterException | NumberFormatException e) {
            log.info("Invalid input data error", e);
            showAddStepError("Incorrect value in step number");
            return;
        }
        
        rules.put(data.getParticipant().getUsername(), Rules.CONTROL);
        
        var step = steps.stream()
                .filter(s -> s.getNumber() == number)
                .findFirst().orElse(new Step());
        
        steps.stream()
                .filter(s -> s.getNumber() >= number && !s.getTitle().equals(step.getTitle()))
                .forEach(s -> s.setNumber(s.getNumber()+1));
        
        step.setTitle(stepTitle.getText());
        step.getRules().putAll(rules);
        
        step.getDocuments().addAll(documents);
        step.setNumber(number);
        step.getDocuments().forEach(document -> document.setStepTitle(title));
        
        steps = steps.stream()
                .filter(s -> !s.getTitle().equals(title))
                .collect(Collectors.toSet());
        steps.add(step);
        refreshStepsList();
        
        documents.clear();
        rules.clear();
        rulesList.setValue("");
        stepTitle.clear();
        documentsList.getItems().clear();
        participantsList.getItems().clear();
        
        try {
            stepNumber.setText(String.valueOf(steps.stream().mapToInt(Step::getNumber).max().orElseThrow() + 1));
        }
        catch (NoSuchElementException e) {
            log.warn("No such step error", e);
//            showError();
        }
    }
    
    private void selectStep(Step step) {
        
        stepTitle.setText(step.getTitle());
        stepNumber.setText(String.valueOf(step.getNumber()));
        
        refreshDocumentsList(step);
        refreshParticipantsList(step);
    }
    
    public void removeStep(ActionEvent e) {
        
        var title = stepTitle.getText();
        
        try {
            var step = steps.stream()
                    .filter(s -> s.getTitle().equals(title))
                    .findFirst().orElseThrow();
            steps.remove(step);
            selectStep(
                    steps.stream()
                    .min(Comparator.comparing(Step::getNumber)).orElseThrow()
            );
        }
        catch (NoSuchElementException exception) {
            log.debug("No such step error", exception);
//            showError();
            return;
        }
        
        refreshStepsList();
    }
    
    /**
     * Регистрация нового документа в процессе
     * */
    public void addDocument() {

        addDocumentError.setVisible(false);
        var document = new Document();
        var title = documentTitle.getText();
        var extension = documentExtension.getText();
        if(title == null || title.isBlank()) {
            showAddDocumentError("Document name field can't be empty");
            return;
        }
        if (!steps.stream().flatMap(s -> s.getDocuments().stream())
                .map(Document::getTitle)
                .noneMatch(t -> t.equals(title))){
            showAddDocumentError("Document with such name");
            return;
        }
        if(!extension.startsWith("."))
            extension = "." + extension;
        
        document.setTitle(title);
        document.setFormat(extension);
        
        documents.add(document);
        var doc = new MenuItem(String.format("%s (%s)", document.getTitle(), document.getFormat()));
        doc.setOnAction(e -> documentsList.getItems().remove(doc));
        documentsList.getItems().add(doc);
        
        documentTitle.clear();
        documentExtension.clear();
    }
    
    public void addParticipant(ActionEvent event) {
    
        if(participantsChoice.getValue().isBlank()) {
//            showError();
            return;
        }
        
        if(rulesList.getValue().isBlank()) {
//            showError();
            return;
        }
        
        var username = participantsChoice.getValue();
        var rule = Rules.valueOf(rulesList.getValue());
        
        addParticipantErrorField.setVisible(false);
        if((rule.equals(Rules.CHANGE) && rules.containsValue(Rules.CHANGE))
            || rules.containsKey(username)) {
            showAddParticipantError("Participant can't have no rules");
            return;
        }
        
        var participant = new MenuItem(String.format("%s (%s)", username, rule));
        rules.put(username, rule);
        participant.setOnAction(e -> participantsList.getItems().remove(participant));
        participantsList.getItems().add(participant);
        
        rulesList.setValue("");
        participantsChoice.setValue("");
    }
    
    public boolean checkTitle(String title) throws JsonProcessingException {
        
        return title != null && !title.isBlank() && !Sender.processExists(title).isExist();
    }
    
    public void back(ActionEvent event) throws IOException {
        
        showStage(event, data.getPreviousScene(), source);
    }
    private void refreshStepsList() {
        stepsList.getItems().clear();
        steps.stream().sorted(Comparator.comparingInt(Step::getNumber)).forEach(s -> {
                    var item = new MenuItem(String.format("(%d) %s", s.getNumber(), s.getTitle()));
                    item.setOnAction(e -> selectStep(s));
                    stepsList.getItems().add(item);
                }
        );
    }
    
    private void refreshParticipantsList(Step step) {
        participantsList.getItems().clear();
        step.getRules().keySet().stream()
                .sorted(String::compareTo)
                .forEach(p -> {
                    var item = new MenuItem(String.format("%s (%s)", p, step.getRules().get(p)));
                    item.setOnAction(e -> documentsList.getItems().remove(item));
                    participantsList.getItems().add(item);
                }
                );
    }
    
    private void refreshDocumentsList(Step step) {
        documentsList.getItems().clear();
        step.getDocuments().stream()
                .sorted(Comparator.comparing(Document::getTitle))
                .forEach(d -> {
                    var item = new MenuItem(String.format("%s (%s)", d.getTitle(), d.getFormat()));
                    item.setOnAction(e -> documentsList.getItems().remove(item));
                    documentsList.getItems().add(item);
                }
                );
    }
    private boolean checkDocument(String title) {
        
        return title != null
                && !title.isBlank()
                && steps.stream().flatMap(s -> s.getDocuments().stream())
                .map(Document::getTitle)
                .noneMatch(t -> t.equals(title));
    }

    private void  showAddStepError(String error){
        addStepError.setText(error);
        addStepError.setVisible(true);

    }
    private void  showCreateProcessError(String error){
        addStepError.setText(error);
        addStepError.setVisible(true);

    }
    private void  showAddDocumentError(String error){
        addDocumentError.setText(error);
        addDocumentError.setVisible(true);

    }
    private void  showAddParticipantError(String error){
        addParticipantErrorField.setText(error);
        addParticipantErrorField.setVisible(true);

    }

}
