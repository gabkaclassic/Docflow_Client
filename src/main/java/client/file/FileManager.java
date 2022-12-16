package client.file;

import client.entity.process.Process;
import client.entity.process.document.Document;
import javafx.event.ActionEvent;
import javafx.scene.Node;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;

import java.awt.*;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Objects;
import java.util.Set;

/**
 * Класс-утилита для работы с файлами
 * */
public class FileManager {
    
    private static final String WORKDIR = System.getenv("WORKDIR");
    private static final String SEPARATOR = System.getProperty("file.separator");
    
    /**
     * Сохранение содержимого документов в файлы
     * */
    public void saveDocument(Document document, String processTitle) throws IOException {
        
        var file = new File(getFilename(document, processTitle));
        
        createDirectory(document, processTitle);
        
        try(var out = new FileOutputStream(file)) {
            var data = document.getFile() == null ? new byte[1] : document.getFile();
            out.write(data);
        } catch (IOException ignored) {
        }
    }
    
    /**
     * Обновление содержимого документов из файлов
     * */
    public void updateDocuments(String processTitle, Set<Document> documents) {
        
        for(var document: documents) {
    
            var file = new File(getFilename(document, processTitle));
            
            if(file.exists()) {
    
                try (var in = new FileInputStream(file)) {
        
                    document.setFile(in.readAllBytes());
                } catch (IOException e) {
                }
    
            }
        }
    }
    
    /**
     * Обновление содержимого документа из файла
     * */
    public void updateDocument(ActionEvent event, Document document, String processTitle) throws IOException {
        
        var fileChooser = new FileChooser();
        fileChooser.setTitle("Select document " + document.getTitle());
        fileChooser.setSelectedExtensionFilter(new ExtensionFilter(document.getTitle(), "*" + document.getFormat()));
        fileChooser.setInitialDirectory(new File(getFilename(processTitle)));
        var file = fileChooser.showSaveDialog((((Node)event.getSource()).getScene().getWindow()));
        var filename = file.getName();
        document.setTitle(filename.substring(0, filename.lastIndexOf('.')));
        document.setFormat(filename.substring(filename.lastIndexOf('.')));
        
        createDirectory(document, processTitle);
        
        try (var in = new FileInputStream(file); var out = new FileOutputStream(getFilename(document, processTitle))) {
            var data = in.readAllBytes();
            document.setFile(data);
            out.write(data);
        } catch (IOException e) {
        }
    }
    
    /**
     * Открытие документа программой по умолчанию
     * */
    public void openDocument(Document document, String processTitle) throws IOException {
        
        Desktop.getDesktop().open(new File(getFilename(document, processTitle)));
    }
    
    private void createDirectory(Document document, String processTitle) throws IOException {
        
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
    
    /**
     * Сохранение файлов, которые являются конечным результатом процесса
     * */
    public boolean saveResult(ActionEvent event, Process process) {
    
        var directory = new File(getFilename(process.getTitle()));
        
        if(!directory.exists() || !directory.isDirectory()) {
            return false;
        }
    
        var directoryChooser = new DirectoryChooser();
        var resultDirectory = directoryChooser.showDialog((((Node)event.getSource()).getScene().getWindow()));
        directoryChooser.setTitle("Select directory for saving result");
        
        while(resultDirectory == null)
            resultDirectory = directoryChooser.showDialog((((Node)event.getSource()).getScene().getWindow()));
        
        for(var doc: Objects.requireNonNull(directory.listFiles()))
            doc.renameTo(new File(resultDirectory.getAbsolutePath() + SEPARATOR + doc.getName()));
        
        return true;
    }
    
    /**
     * Удаление папки проекта
     * */
    public boolean removeProcessPath(Process process) {
        
        var directory = new File(getFilename(process.getTitle()));
        if(!directory.exists() || !directory.isDirectory()) {
            return false;
        }
        
        return directory.delete();
    }
}
