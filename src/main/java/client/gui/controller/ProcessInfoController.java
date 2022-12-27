package client.gui.controller;

import client.entity.process.Participant;
import client.entity.process.Process;
import client.entity.process.Rules;
import client.entity.process.step.Step;
import client.entity.process.document.Document;
import client.file.FileManager;
import client.sender.Sender;
import com.fasterxml.jackson.core.JsonProcessingException;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;

import java.io.IOException;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.regex.Pattern;

/**
 * Контроллер для отображения сцены работы над процессом
 * @see Controller
 * */
public class ProcessInfoController extends Controller {
    @FXML
    private Label processTitle;
    
    @FXML
    private Label stepTitle;

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
    private Button addCommentButton;
    
    @FXML
    private Button addResourceButton;
    
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
    
    @FXML
    private Label documentName;
    
    @FXML
    private Label resourceName;
    
    @FXML
    private BorderPane commentPane;
    
    @FXML
    private BorderPane resourcePane;
    
    
    private Process process;
    private Step step;
    
    private Participant participant;
    
    private Rules permission;
    
    private Document currentDocument;
    private final FileManager fileManager = new FileManager();
    
    private final Pattern pattern = Pattern.compile(
            "((([A-Za-z]{3,9}:(?:\\/\\/)?)(?:[-;:&=\\+\\$,\\w]+@)?[A-Za-z0-9.-]+|(?:www.|[-;:&=\\+\\$,\\w]+@)[A-Za-z0-9.-]+)((?:\\/[\\+~%\\/.\\w_]*)?\\??(?:[-\\+=&;%@.\\w_]*)#?(?:[\\w]*))?)"
    );
    
    @FXML
    public void initialize() throws IOException {
        
        data.refresh();

        if(process != null)
            process = data.getProcesses().stream()
                    .filter(p -> p.getId().equals(process.getId()))
                    .findFirst().orElseThrow();
        else
            process = data.getCurrentProcess();
        
        try {
            step = process.getSteps().stream()
                    .filter(s -> Objects.equals(s.getNumber(), process.getCurrentStep()))
                    .findFirst().orElseThrow();
        }
        catch(NoSuchElementException ignored) {
            ignored.printStackTrace();
        }
        
        participant = data.getParticipant();
        permission = step.getRules().get(participant.getUsername());
        
        documentTitle.setVisible(!permission.equals(Rules.READ));
        documentExtension.setVisible(!permission.equals(Rules.READ));
        open.setVisible(!permission.equals(Rules.READ));
        filenameLabel.setVisible(!permission.equals(Rules.READ));
        createDocument.setVisible(!permission.equals(Rules.READ));
        
        acceptButton.setText("Accept changes");
        if(process.finished())
            acceptButton.setText("Progress completion");
        
        refuseButton.setVisible(!process.started());
            
        if(permission != null && !permission.equals(Rules.CONTROL)) {
            refuseButton.setVisible(false);
            acceptButton.setVisible(false);
        }
        
        processTitle.setText(process.getTitle());
        stepTitle.setText(step.getTitle());
        
        step.getDocuments().forEach(document -> {
            try {
                fileManager.saveDocument(document, process.getTitle());
            } catch (IOException ignored) {
            }
        });
        
        checkCurrentDocument();
        
        updateDocuments();
    }
    
    private void checkCurrentDocument() {
        boolean currentDocumentIsNotNull = currentDocument != null;
        documentName.setVisible(currentDocumentIsNotNull);
        resourceName.setVisible(currentDocumentIsNotNull);
        comments.setVisible(currentDocumentIsNotNull);
        commentText.setVisible(currentDocumentIsNotNull);
        resourcesFlow.setVisible(currentDocumentIsNotNull);
        addResourceButton.setVisible(currentDocumentIsNotNull);
        addCommentButton.setVisible(currentDocumentIsNotNull);
        commentPane.setVisible(currentDocumentIsNotNull);
        resourceText.setVisible(currentDocumentIsNotNull);
        descriptionText.setVisible(currentDocumentIsNotNull);
        resourcePane.setVisible(currentDocumentIsNotNull);
    }
    
    private void updateDocuments() {
        
        documents.getPanes().clear();
    
        for (Document document : step.getDocuments()) {
            addDocument(document);
        }
    }
    
    private void addDocument(Document document) {
    
        var saveButton = new Button("Save");
        saveButton.setTranslateX(-10);
        saveButton.getStylesheets().add(Objects.requireNonNull(this.getClass().getResource("document_button.css")).toExternalForm());
        saveButton.setOnAction(event -> {
            try {
                updateDocument(event, document);
            } catch (IOException ignored) {
            }
        });
        var openButton = new Button("Open");
        openButton.setTranslateX(-10);
        openButton.getStylesheets().add(Objects.requireNonNull(this.getClass().getResource("document_button.css")).toExternalForm());
        openButton.setOnAction(event -> {
            try {
                fileManager.openDocument(document, process.getTitle());
            } catch (IOException ignored) {
            }
        });
        var selectButton = new Button("Select");
        selectButton.setTranslateX(-10);
        selectButton.getStylesheets().add(Objects.requireNonNull(this.getClass().getResource("document_button.css")).toExternalForm());
        selectButton.setOnAction(event -> {
            currentDocument = document;
            checkCurrentDocument();
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
        pane.getStylesheets().add(Objects.requireNonNull(this.getClass().getResource("titled-pane.css")).toExternalForm());
        anchor.getChildren().add(bar);
        pane.setText(document.getTitle());
        pane.setContent(anchor);
        documents.getPanes().add(pane);
    }
    
    private void updateDocument(ActionEvent event, Document document) throws IOException {
    
        
        document.addComments(currentDocument.getComments());
        document.addResources(currentDocument.getResources());
    
        new Thread(() -> {
            try {
                Sender.updateStep(step);
            } catch (JsonProcessingException e) {
                throw new RuntimeException(e);
            }
        }).start();
        fileManager.updateDocument(event, document, process.getTitle());
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
        createDocument.setStyle(okStyle);
        var document = new Document();
        document.setTitle(documentTitle.getText());
        
        var extension = documentExtension.getText();
        if(!extension.startsWith("."))
            extension = "." + extension;
        document.setFormat(extension);
        
        document.setStepTitle(step.getTitle());
    
        fileManager.saveDocument(document, process.getTitle());
        if(open.isSelected())
            fileManager.openDocument(document, process.getTitle());
    
        documentTitle.clear();
        documentExtension.clear();
        
        if(checkDocument(document)) {
            showDocumentCreateError();
            return;
        }
        
        step.addDocument(document);
        
        new Thread(() -> {
            try {
                Sender.updateStep(step);
            } catch (JsonProcessingException e) {
                throw new RuntimeException(e);
            }
        }).start();
        
        addDocument(document);
    }
    
    public void saveAll(ActionEvent event) throws IOException {
        approveError.setVisible(false);
        fileManager.updateDocuments(process.getTitle(), step.getDocuments());
        var response = Sender.updateStep(step);
        response = Sender.updateDocuments(step.getDocuments());
    
        if(response.isError()) {
            showApproveError(response.getMessage());
            return;
        }
    
        initialize();
    }
    
    public void addComment(ActionEvent event) {
        
        commentError.setVisible(false);
        var comText = commentText.getText();
        
        if(!checkText(comText)) {
            showCommentError();
            return;
        }
        
        addCommentButton.setStyle(okStyle);
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
            showResourceError("description can't be empty");
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
                showStage(event, "general_info.fxml");
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
        catch (NoSuchElementException ignored) {
        }
        
        initialize();
    }
    public void previousStep(ActionEvent event) throws IOException {
    
        approveError.setVisible(false);
        process.previousStep();
        var response = Sender.refuse(process);
        refuseButton.setStyle(okStyle);
        
        if(response.isError()) {
            refuseButton.setStyle(errorStyle);
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
        }
        
        initialize();
    }
    private void showDocumentCreateError(){
        createDocument.setStyle(errorStyle);
        createDocumentError.setText("Document with such name already exist");
        createDocumentError.setVisible(true);
    }
    private void showApproveError(String error){
        acceptButton.setStyle(errorStyle);
        approveError.setText(error);
        approveError.setVisible(true);
    }
    private void showCommentError(){
        addCommentButton.setStyle(errorStyle);
        commentError.setText("Comments can't be empty");
        commentError.setVisible(true);
        
    }
    private void showResourceError(String error){
        addResourceButton.setStyle(errorStyle);
        resourceError.setText(error);
        resourceError.setVisible(true);
    }
    
    public void back(ActionEvent event) throws IOException {
        
        fileManager.updateDocuments(process.getTitle(), step.getDocuments());
        Sender.updateStep(step);
        
        showStage(event, data.getPreviousScene());
    }
}
