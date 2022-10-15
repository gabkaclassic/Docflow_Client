package client.entity.process.document;

import client.entity.deserializer.DocumentDeserializer;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.List;

@NoArgsConstructor
@Data
@JsonDeserialize(using = DocumentDeserializer.class)
public class Document {
    
    private String id;
    
    private String title;

    private List<Comment> comments;

    private byte[] file;

    private List<Resource> resources;

    private DocumentType type;
}

