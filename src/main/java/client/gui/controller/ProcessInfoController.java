package client.gui.controller;

import client.entity.process.Participant;
import client.entity.process.Process;
import client.entity.process.Rules;
import client.entity.process.Step;
import client.entity.process.document.Document;
import client.file.FileManager;
import client.sender.Sender;
import com.fasterxml.jackson.core.JsonProcessingException;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;

import java.io.IOException;
import java.util.List;
import java.util.Objects;
import java.util.regex.Pattern;

public class ProcessInfoController extends Controller {
    @FXML
    private Label processTitle;
    
    @FXML
    private Label stepTitle;
    @FXML
    private Label permissions;

    @FXML
    private Accordion documents;
    
    @FXML
    private TextField documentTitle;
    
    @FXML
    private TextField documentExtension;
    
    @FXML
    private Button refuseButton;
    @FXML
    private Button acceptButton;
    
    @FXML
    private TextFlow comments;
    
    @FXML
    private TextFlow resourcesFlow;
    
    @FXML
    private TextArea commentText;
    
    @FXML
    private TextField resourceText;
    @FXML
    private TextArea descriptionText;
    
    @FXML
    private CheckBox open;
    private Process process;
    private Step step;
    
    private Participant participant;
    
    private Rules permission;
    
    private Document currentDocument;
    private final FileManager fileManager = new FileManager();
    
    private final Pattern pattern = Pattern.compile("((([A-Za-z]{3,9}:(?:\\/\\/)?)(?:[-;:&=\\+\\$,\\w]+@)?[A-Za-z0-9.-]+|(?:www.|[-;:&=\\+\\$,\\w]+@)[A-Za-z0-9.-]+)((?:\\/[\\+~%\\/.\\w_]*)?\\??(?:[-\\+=&;%@.\\w_]*)#?(?:[\\w]*))?)");
    private final String source = "process_info.fxml";
    
    @FXML
    public void initialize() {
        
        process = data.getCurrentProcess();
        step = process.getSteps().stream()
                .filter(s -> Objects.equals(s.getNumber(), process.getCurrentStep()))
                .findFirst().get();
        participant = data.getParticipant();
        permission = step.getRules().get(participant.getUsername());
        
        if(process.finished())
            acceptButton.setText("Progress completion");
        
        if(process.started())
            refuseButton.setVisible(false);
            
        if(permission != null && !permission.equals(Rules.CONTROL)) {
            refuseButton.setVisible(false);
            acceptButton.setVisible(false);
        }
        
        processTitle.setText(process.getTitle());
        stepTitle.setText(step.getTitle());
        permissions.setText("Your permissions: " + permission.getView());
        
        step.getDocuments().forEach(document -> {
            try {
                fileManager.saveDocument(document, process.getTitle());
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });
        
        updateDocuments();
    }
    private void updateDocuments() {
        
        documents.getPanes().clear();
        
        step.getDocuments().forEach(this::addDocument);
    }
    
    private void addDocument(Document document) {
    
        var saveButton = new Button("Save");
        saveButton.setOnAction(event -> {
            try {
                updateDocument(event, document);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });
        var openButton = new Button("Open");
        openButton.setOnAction(event -> {
            try {
                fileManager.openDocument(document, process.getTitle());
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });
        var selectButton = new Button("Select");
        selectButton.setOnAction(event -> {
            currentDocument = document;
            defineComments();
            defineResources();
        });
    
        var bar = new ButtonBar();
        bar.getButtons().add(openButton);
        bar.getButtons().add(selectButton);
        if(permission.getLevel() > Rules.READ.getLevel())
            bar.getButtons().add(saveButton);
    
        var anchor = new AnchorPane();
        var pane = new TitledPane();
        anchor.getChildren().add(bar);
        pane.setText(document.getTitle());
        pane.setContent(anchor);
        documents.getPanes().add(pane);
    }
    
    private void updateDocument(ActionEvent event, Document document) throws IOException {
    
        document.addComments(currentDocument.getComments());
        document.addResources(currentDocument.getResources());
        
        fileManager.updateDocument(event, document, process.getTitle());
        
        Sender.updateStep(step);
    }
    
    private void defineComments() {
        
        comments.getChildren().clear();
        
        comments.getChildren().add(
                new Text(
                        String.join("\n", currentDocument.getComments())
                )
        );
    }
    
    private void defineResources() {
    
        resourcesFlow.getChildren().clear();
        
        resourcesFlow.getChildren().add(
                new Text(
                        String.join("\n---------------------\n", currentDocument.getResources())
                )
        );
    }
    
    public void addDocument(ActionEvent event) throws IOException {
        
        var document = new Document();
        document.setTitle(documentTitle.getText());
        document.setFormat(documentExtension.getText());
        document.setProcessId(process.getId());
        
        if(checkDocument(document)) {
        
//            showError();
            return;
        }
        
        step.addDocument(document);
        Sender.updateStep(step);
        
        fileManager.saveDocument(document, process.getTitle());
        if(open.isSelected())
            fileManager.openDocument(document, process.getTitle());
        
        documentTitle.clear();
        documentExtension.clear();
        
        addDocument(document);
    }
    
    public void saveAll(ActionEvent event) throws IOException {
        
        fileManager.updateDocuments(process.getTitle(), step.getDocuments());
        updateDocuments();
        var response = Sender.updateStep(step);
        
        if(response.isError()) {
//            showError();
        }
        
        data.refresh();
        initialize();
    }
    
    
    
    public void nextStep(ActionEvent event) throws JsonProcessingException {
    
        if(process.finished()) {  // TO DO
        
        }
        
        process.nextStep();
        var response = Sender.approve(process);
        step = response.getStep();
        
        initialize();
    }
    
    public void addComment(ActionEvent event) {
        
        var comText = commentText.getText();
        
        if(!checkText(comText)) {
//            showError();
            return;
        }
        
        currentDocument.addComment(comText, participant);
        commentText.clear();
        
        defineComments();
    }
    
    public void addResource(ActionEvent event) {
        
        var resText = resourceText.getText();
        var description = descriptionText.getText();
        
        if(!checkUrl(resText) || !checkText(description)) {
//            showError();
            return;
        }
        
        currentDocument.addResource(resText, description);
        resourceText.clear();
        descriptionText.clear();
        
        defineResources();
    }
    
    private boolean checkDocument(Document document) throws JsonProcessingException {
        
        return Sender.documentExists(document).isExist();
    }
    private boolean checkText(String text) {
        
        return text != null
                && !text.isBlank();
    }
    
    private boolean checkUrl(String url) {
        
        return checkText(url) && pattern.matcher(url).find();
    }
    
    public void previousStep(ActionEvent event) throws JsonProcessingException {
        
        process.previousStep();
        var response = Sender.refuse(process);
        step = response.getStep();
        
        initialize();
    }
    
    public void back(ActionEvent event) throws IOException {
        
        showStage(event, data.getPreviousScene(), source);
    }
}
