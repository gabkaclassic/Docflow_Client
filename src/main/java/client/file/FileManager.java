package client.file;

import client.entity.process.document.Document;

import java.io.*;

public class FileManager {
    
    private static final String DEFAULT_PATH = "C:\\Users\\Kuzmi\\Desktop\\Discrete matematic";
    private static final String SEPARATOR = System.getProperty("file.separator");
    
    public void saveDocument(Document document) throws IOException {
        
        var file = new File(String.join(SEPARATOR, DEFAULT_PATH, document.getTitle()) + document.getType().getDefaultFormat());
        try(var out = new FileOutputStream(file)) {
            
            if(!file.exists())
                file.createNewFile();
            out.write(document.getFile());
        } catch (IOException e) {
            throw e;
        }
    }
    
    public Document updateDocument(Document document) throws IOException {
        
        var file = new File(String.join(DEFAULT_PATH, document.getTitle()) + document.getType().getDefaultFormat());
        
        try(var in = new FileInputStream(file)) {
         
            document.setFile(in.readAllBytes());
        } catch (IOException e) {
            throw e;
        }
    
        return document;
    }
    
}
