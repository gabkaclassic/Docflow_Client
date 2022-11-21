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
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.regex.Pattern;

@Slf4j
public class ProcessInfoController extends Controller {
    @FXML
    private Label processTitle;
    
    @FXML
    private Label stepTitle;
    @FXML
    private Label permissions;

    @FXML
    private Button createDocument;
    
    @FXML
    private Label filenameLabel;
    
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
    @FXML
    private Label createDocumentError;
    @FXML
    private Label approveError;
    @FXML
    private Label commentError;
    @FXML
    private Label resourceError;
    private Process process;
    private Step step;
    
    private Participant participant;
    
    private Rules permission;
    
    private Document currentDocument;
    private final FileManager fileManager = new FileManager();
    
    private final Pattern pattern = Pattern.compile(
            "((([A-Za-z]{3,9}:(?:\\/\\/)?)(?:[-;:&=\\+\\$,\\w]+@)?[A-Za-z0-9.-]+|(?:www.|[-;:&=\\+\\$,\\w]+@)[A-Za-z0-9.-]+)((?:\\/[\\+~%\\/.\\w_]*)?\\??(?:[-\\+=&;%@.\\w_]*)#?(?:[\\w]*))?)"
    );
    private static final String source = "process_info.fxml";
    
    @FXML
    public void initialize() throws JsonProcessingException {
        
        process = data.getCurrentProcess();
        
        try {
            step = process.getSteps().stream()
                    .filter(s -> Objects.equals(s.getNumber(), process.getCurrentStep()))
                    .findFirst().orElseThrow();
        }
        catch(NoSuchElementException e) {
            log.debug("No such step error", e);
//            showError();
        }
        
        participant = data.getParticipant();
        permission = step.getRules().get(participant.getUsername());
        
        if(permission.equals(Rules.READ)) {
            
            documentTitle.setVisible(false);
            documentExtension.setVisible(false);
            open.setVisible(false);
            filenameLabel.setVisible(false);
            createDocument.setVisible(false);
        }
        
        
        acceptButton.setText("Accept changes");
        if(process.finished())
            acceptButton.setText("Progress completion");
        refuseButton.setVisible(true);
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
                log.warn("Save document error", e);
            }
        });
        
        updateDocuments();
    }
    private void updateDocuments() {
        
        documents.getPanes().clear();
    
        for (Document document : step.getDocuments()) {
            addDocument(document);
        }
    }
    
    private void addDocument(Document document) {
    
        var saveButton = new Button("Save");
        saveButton.setOnAction(event -> {
            try {
                updateDocument(event, document);
            } catch (IOException e) {
                log.warn("Update document error", e);
            }
        });
        var openButton = new Button("Open");
        openButton.setOnAction(event -> {
            try {
                fileManager.openDocument(document, process.getTitle());
            } catch (IOException e) {
                log.warn("Open document error", e);
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
        
        createDocumentError.setVisible(false);
        var document = new Document();
        document.setTitle(documentTitle.getText());
        
        var extension = documentExtension.getText();
        if(!extension.startsWith("."))
            extension = "." + extension;
        document.setFormat(extension);
        
        document.setStepTitle(step.getTitle());
        
        if(checkDocument(document)) {
            showDocumentCreateError("Document with such name already exist");
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
        approveError.setVisible(false);
        fileManager.updateDocuments(process.getTitle(), step.getDocuments());
        var response = Sender.updateStep(step);
    
        if(response.isError()) {
            showApproveError(response.getMessage());
            return;
        }
    
        data.refresh();
        initialize();
    }
    
    public void addComment(ActionEvent event) {
        
        commentError.setVisible(false);
        var comText = commentText.getText();
        
        if(!checkText(comText)) {
            showCommentError("Comments can't be empty");
            return;
        }
        
        currentDocument.addComment(comText, participant);
        commentText.clear();
        
        defineComments();
    }
    
    public void addResource(ActionEvent event) {
        resourceError.setVisible(false);
        var resText = resourceText.getText();
        var description = descriptionText.getText();
        if(!checkUrl(resText)){
            showResourceError("incorrect url format");
            return;
        }
        if(!checkText(description)) {
            showResourceError("description can't be emty");
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
    
    public void nextStep(ActionEvent event) throws IOException {

        approveError.setVisible(false);
        saveAll(event);
        
        if(process.finished()) {
        
            fileManager.saveResult(event, process);
            var result = fileManager.removeProcessPath(process);
            var response = Sender.finishProcess(process);
            
            if(result && response != null && !response.isError())
                showStage(event, "general_info.fxml", source);
            else
                return;
        }
        
        process.nextStep();
        var response = Sender.approve(process);
        
        if(response.isError()) {
            showApproveError(response.getMessage());
            return;
        }
        var info = Sender.getUserInfo();
        if(info.isError()) {
            showApproveError(info.getMessage());
            return;
        }
    
        var processResponse = Sender.getProcessInfo(process);
        if(processResponse.isError()) {
            showApproveError(processResponse.getMessage());
            return;
        }
        try {
        
            data.setCurrentProcess(processResponse.getProcess());
        }
        catch (NoSuchElementException e) {
            log.warn("No such process error", e);
        }
        
        initialize();
    }
    public void previousStep(ActionEvent event) throws IOException {
    
        approveError.setVisible(false);
        process.previousStep();
        var response = Sender.refuse(process);
        
        if(response.isError()) {
            showApproveError(response.getMessage());
            return;
        }
        var processResponse = Sender.getProcessInfo(process);
        if(processResponse.isError()) {
            showApproveError(processResponse.getMessage());
            return;
        }
        try {
    
            data.setCurrentProcess(processResponse.getProcess());
        }
        catch (NoSuchElementException e) {
            log.warn("No such process error", e);
        }
        
        initialize();
    }
    private void showDocumentCreateError(String error){
        createDocumentError.setText(error);
        createDocumentError.setVisible(true);
    }
    private void showApproveError(String error){
        approveError.setText(error);
        createDocumentError.setVisible(true);
    }
    private void showCommentError(String error){
        commentError.setText(error);
        createDocumentError.setVisible(true);
    }
    private void showResourceError(String error){
        commentError.setText(error);
        resourceError.setVisible(true);
    }
    
    public void back(ActionEvent event) throws IOException {
        
        fileManager.updateDocuments(process.getTitle(), step.getDocuments());
        Sender.updateStep(step);
        
        
        showStage(event, data.getPreviousScene(), source);
    }
}
