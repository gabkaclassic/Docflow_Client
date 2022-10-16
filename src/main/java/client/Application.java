package client;

import client.entity.process.document.Document;
import client.entity.process.document.DocumentType;
import client.file.FileManager;
import client.sender.Sender;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.URISyntaxException;

public class Application {
    
    public static void main(String[] args) throws IOException, URISyntaxException {
        
//        var sender = new Sender();
//        sender.login("user31", "1231231sdf2341!");
//        sender.invite("user31", 1L);

        var manager = new FileManager();
        var document = new Document();
        var type = new DocumentType();
        type.setDefaultFormat(".pdf");
        type.setTitle("pdfka))");
        document.setTitle("Some file");
        document.setType(type);
        var file = new File("C:\\Users\\Kuzmi\\Desktop\\Discrete matematic\\464.pdf");
        document.setFile(new FileInputStream(file).readAllBytes());
        manager.saveDocument(document);
        
        }
}
