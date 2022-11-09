package client.entity.process.document;

import client.entity.deserializer.DocumentIdDeserializer;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonDeserialize(using = DocumentIdDeserializer.class)
public class DocumentId implements Serializable {
    
    private String stepTitle;
    
    private String title;
 
}