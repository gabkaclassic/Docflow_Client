package client.file;

import client.entity.process.document.Document;
import javafx.event.ActionEvent;
import javafx.scene.Node;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;
import lombok.extern.slf4j.Slf4j;

import java.awt.*;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Set;

@Slf4j
public class FileManager {
    
    private static final String WORKDIR = "documents";
    private static final String SEPARATOR = System.getProperty("file.separator");
    
    public void saveDocument(Document document, String processTitle) throws IOException {
        
        var file = new File(getFilename(document, processTitle));
        
        checkDirectories(document, processTitle);
        
        try(var out = new FileOutputStream(file)) {
            var data = document.getFile() == null ? new byte[1] : document.getFile();
            out.write(data);
        } catch (IOException e) {
            log.debug("Save document error", e);
        }
    }
    
    public void updateDocuments(String processTitle, Set<Document> documents) throws IOException {
        
        for(var document: documents) {
    
            var file = new File(getFilename(document, processTitle));
            
            if(file.exists()) {
    
                try (var in = new FileInputStream(file)) {
        
                    document.setFile(in.readAllBytes());
                } catch (IOException e) {
                    log.debug("Update documents error", e);
                }
    
            }
        }
    }
    
    public void updateDocument(ActionEvent event, Document document, String processTitle) throws IOException {
        
        var fileChooser = new FileChooser();
        fileChooser.setTitle("Select document " + document.getTitle());
        fileChooser.setSelectedExtensionFilter(new ExtensionFilter(document.getTitle(), "*" + document.getFormat()));
        fileChooser.setInitialDirectory(new File(getFilename(processTitle)));
        var file = fileChooser.showSaveDialog((((Node)event.getSource()).getScene().getWindow()));
        var filename = file.getName();
        document.setTitle(filename.substring(0, filename.lastIndexOf('.')));
        document.setFormat(filename.substring(filename.lastIndexOf('.')));
        
        checkDirectories(document, processTitle);
        
        try (var in = new FileInputStream(file); var out = new FileOutputStream(getFilename(document, processTitle))) {
            var data = in.readAllBytes();
            document.setFile(data);
            out.write(data);
        } catch (IOException e) {
            log.debug("Update document error", e);
        }
    }
    
    public void openDocument(Document document, String processTitle) throws IOException {
        
        Desktop.getDesktop().open(new File(getFilename(document, processTitle)));
    }
    
    private void checkDirectories(Document document, String processTitle) throws IOException {
        
        var workdir = new File(WORKDIR);
        var dir = new File(getFilename(processTitle));
        var file = new File(getFilename(document, processTitle));
        
        if(!workdir.exists())
            workdir.mkdir();
        if(!dir.exists())
            dir.mkdir();
        if(!file.exists())
            file.createNewFile();
    }
    
    private String getFilename(Document document, String processTitle) {
        return String.join(SEPARATOR, WORKDIR, processTitle, document.getTitle()) + document.getFormat();
    }
    
    private String getFilename(String processTitle) {
        return String.join(SEPARATOR, WORKDIR, processTitle);
    }
    
}
