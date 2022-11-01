package client.gui.controller;

import client.entity.Team;
import client.entity.process.Participant;
import client.entity.process.Process;
import client.entity.process.Rules;
import client.entity.process.Step;
import client.entity.process.document.Document;
import client.entity.user.User;
import client.sender.Sender;
import com.fasterxml.jackson.core.JsonProcessingException;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.MenuItem;
import javafx.scene.control.SplitMenuButton;
import javafx.scene.control.TextField;

import java.io.IOException;
import java.util.*;

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
    
    private final String source = "create_process.fxml";
    private Participant creator;
    
    private Set<Step> steps = new HashSet<>();
    
    private Map<String, Rules> rules = new HashMap<>();
    
    private Set<Document> documents = new HashSet<>();
    
    private Team team;
    public void initialize() {
    
        var u2 = new User();
        u2.setUsername("some2");
        creator = data.getParticipant();
        creator = new Participant();
        creator.setOwner(u2);
        var p = new Participant();
        p.setOwner(u2);
        var team = new Team();
        var team1 = new Team();
        team1.setTitle("lksldjf");
        team1.addParticipant(creator.getUsername());
        team.addParticipant(creator.getUsername());
        team.addParticipant(p.getUsername());
        team.setTitle("bla bla");
        creator.addTeam(team);
        creator.addTeam(team1);
        creator.addTeam(new Team());
        
        creator.getTeams().stream()
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
        
        team = creator.getTeams().stream().filter(t -> t.getTitle().equals(value)).findFirst().get();
        team.getParticipants().stream().forEach(participantsChoice.getItems()::add);
    }
    
    public void createProcess(ActionEvent event) throws IOException {
    
        if(!checkTitle(processTitle.getText())) {
//            showError();
            return;
        }
        
        var stepList = steps.stream().sorted(Comparator.comparingInt(Step::getNumber)).toList();
    
        for (int i = 0; i < stepList.size(); i++)
            stepList.get(i).setNumber(i+1);
        
        var process = new Process();
        process.setTitle(processTitle.getText());
        process.setSteps(stepList);
        process.setCurrentStep(steps.stream().min(Comparator.comparingInt(Step::getNumber)).get());
        
        Sender.createProcess(process);
        showStage(event, "general_info.fxml", source);
    }
    
    public void saveStep() {
    
        int number;
        
        try {
            number = Integer.parseInt(stepNumber.getText());
            
            if(number <= 0)
                throw new NumberFormatException();
        }
        catch (NumberFormatException e) {
//            showError();
            return;
        }
        
        steps.stream()
                .filter(s -> s.getNumber() >= number)
                .forEach(s -> s.setNumber(s.getNumber()+1));
        
        var step = steps.stream()
                .filter(s -> s.getNumber() == Integer.parseInt(stepNumber.getText()))
                .findFirst().orElse(new Step());
        step.setTitle(stepTitle.getText());
        step.getRules().putAll(rules);
        step.getDocuments().addAll(documents);
        step.setNumber(number);
        
        steps.add(step);
        refreshStepsList();
        
        documents.clear();
        rules.clear();
        rulesList.setValue("");
        stepTitle.clear();
        documentsList.getItems().clear();
        participantsList.getItems().clear();
        stepNumber.setText(String.valueOf(steps.stream().mapToInt(Step::getNumber).max().getAsInt() + 1));
    }
    
    private void selectStep(Step step) {
        
        stepTitle.setText(step.getTitle());
        stepNumber.setText(String.valueOf(step.getNumber()));
        
        refreshDocumentsList(step);
        refreshParticipantsList(step);
    }
    
    public void addDocument() {
        
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
        
        
        var participant = new MenuItem(String.format("%s (%s)", participantsChoice.getValue(), rulesList.getValue()));
        rules.put(participantsChoice.getValue(), Rules.valueOf(rulesList.getValue()));
        participant.setOnAction(e -> participantsList.getItems().remove(participant));
        participantsList.getItems().add(participant);
        
        rulesList.setValue("");
        participantsChoice.setValue("");
    }
    
    public boolean checkTitle(String title) throws JsonProcessingException {
        
        boolean valid = title != null && !title.isBlank() && !Sender.processExists(title).isExist();
        
//        if(!valid)
//            showError();
        
        return valid;
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
                && documents.stream()
                .map(Document::getTitle)
                .noneMatch(t -> t.equals(title));
    }
}