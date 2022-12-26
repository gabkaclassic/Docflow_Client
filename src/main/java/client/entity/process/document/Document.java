package client.entity.process.document;

import client.entity.deserializer.DocumentDeserializer;
import client.entity.process.Participant;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import lombok.Data;
import java.util.ArrayList;
import java.util.List;

/**
 * Сущность "Документ"
 * */
@Data
@JsonDeserialize(using = DocumentDeserializer.class)
public class Document {
    
    /**
     * @see DocumentId
     * */
    private DocumentId id;
    private List<String> comments = new ArrayList<>();
    
    private byte[] file;
    
    private List<String> resources = new ArrayList<>();
    
    private String format;
    
    public Document() {
        
        id = new DocumentId();
    }
    
    @JsonIgnore
    public String getTitle() {
        
        return id.getTitle();
    }
    
    public void addComment(String text, Participant author) {
        
        comments.add(String.format("--| From %s |-- %s", author.getUsername(), text));
    }
    
    public void addResource(String value, String description) {
        
        resources.add(String.format("%s: %s", description, value));
    }
    
    public void setTitle(String title) {
        
        id.setTitle(title);
    }
    
    public void addComments(List<String> comments) {
        
        this.comments.addAll(comments);
    }
    
    public void addResources(List<String> resources) {
        
        this.resources.addAll(resources);
    }
    
    public void setStepTitle(String title) {
        this.id.setStepTitle(title);
    }
}

