package client.entity.process;

import client.entity.deserializer.StepDeserializer;
import client.entity.process.document.Document;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import lombok.Data;
import java.util.*;

@Data
@JsonDeserialize(using = StepDeserializer.class)
public class Step {
    
    private StepId id;
    
    private Integer number;
    
    private Set<Document> documents = new HashSet<>();
    
    private Map<String, Rules> rules = new HashMap<>();
    
    public Step() {
        
        id = new StepId();
    }
    public void addDocument(Document document) {
        
        this.documents.add(document);
    }
    public void addDocuments(Set<Document> documents) {
        
        this.documents.addAll(documents);
    }
    
    public String getProcessId() {
        
        return id.getProcessId();
    }
    
    public String getTitle() {
        
        return id.getTitle();
    }
    
    public void setTitle(String title) {
        
        id.setTitle(title);
    }
}

