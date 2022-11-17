package client.gui.controller;

import client.entity.Team;
import client.entity.process.Participant;
import client.entity.process.Process;
import client.entity.process.Rules;
import client.entity.process.Step;
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

//        processTitleError.setVisible(false);
        if(!checkTitle(processTitle.getText())) {
//            showError();
            return;
        }
        
        if(steps.size() == 0) {
//            showError();
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
//            showError();
            return;
        }
        
        showStage(event, "general_info.fxml", source);
    }
    
    public void saveStep() {
    
        int number;
        String title;
        
        if(rules.isEmpty() || documents.isEmpty()) {
//            showError();
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
//            showError();
            return;
        }
    
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
    
    public void addDocument() {

//        documentNameError.setVisible(false);
        var document = new Document();
        var title = documentTitle.getText();
        var extension = documentExtension.getText();
        if(!checkDocument(title)) {
//            showError();
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
    
        var username = participantsChoice.getValue();
        var rule = Rules.valueOf(rulesList.getValue());
        
        if((rule.equals(Rules.CHANGE) && rules.containsValue(Rules.CHANGE))
            || rules.containsKey(username)) {
//            showError();
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

}
