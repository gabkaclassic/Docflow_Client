package client.entity.process;

import client.entity.deserializer.StepDeserializer;
import client.entity.process.document.Document;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.*;

@NoArgsConstructor
@Data
@JsonDeserialize(using = StepDeserializer.class)
public class Step {
    
    private long id;

    private int number;

    private String title;
  
    private Set<Document> documents = new HashSet<>();

    private Map<Long, Rules> rules = new HashMap<>();

    private Set<Participant> participants = new HashSet<>();
    
    public boolean addDocument(Document document){
        
        return documents.add(document);
    }
    
}

