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

import java.io.IOException;
import java.util.List;

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
    private CheckBox open;
    private Process process;
    private Step step;
    
    private Participant participant;
    
    private Rules permission;
    
    private FileManager fileManager = new FileManager();
    
    private String source = "process_info.fxml";
    
    @FXML
    public void initialize() {
        
//        process = data.getCurrentProcess();
//        step = process.getCurrentStep();
//        participant = data.getParticipant();
//        permission = step.getRules().get(participant.getId());
    
        process = new Process();
        process.setTitle("sal;kjasfl");
        step = new Step();
        step.setNumber(1);
        var s = new Step();
        s.setId(2);
        s.setNumber(2);
        step.setId(1);
        process.setSteps(List.of(step, s));
        process.setCurrentStep(step);
        permission = Rules.CONTROL;
        
        var doc = new Document();
        doc.setTitle("Title");
        doc.setFormat(".txt");
        doc.setFile("Croak".getBytes());
        step.addDocument(doc);
        
        if(process.finished())
            acceptButton.setText("Process completion");
        
        if(process.started())
            refuseButton.setVisible(false);
            
        if(!permission.equals(Rules.CONTROL)) {
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
                fileManager.updateDocument(event, document, process.getTitle());
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
    
        var bar = new ButtonBar();
        bar.getButtons().add(openButton);
        if(permission.getLevel() > Rules.READ.getLevel())
            bar.getButtons().add(saveButton);
    
        var anchor = new AnchorPane();
        var pane = new TitledPane();
        anchor.getChildren().add(bar);
        pane.setText(document.getTitle());
        pane.setContent(anchor);
        documents.getPanes().add(pane);
    }
    public void addDocument(ActionEvent event) throws IOException {
        
        var document = new Document();
        document.setTitle(documentTitle.getText());
        document.setFormat(documentExtension.getText());
        step.addDocument(document);
        
        fileManager.saveDocument(document, process.getTitle());
        if(open.isSelected())
            fileManager.openDocument(document, process.getTitle());
        
        documentTitle.clear();
        documentExtension.clear();
        
        addDocument(document);
    }
    
    public void saveAll(ActionEvent event) throws IOException {
        
        step.setDocuments(fileManager.updateDocuments(process.getTitle()));
        updateDocuments();
        Sender.updateStep(step);
    }
    
    public void nextStep(ActionEvent event) throws JsonProcessingException {
    
        if(process.finished()) {  // TO DO
        
        }
        
        process.nextStep();
        var response = Sender.approve(process);
        step = response.getStep();
        
        initialize();
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
