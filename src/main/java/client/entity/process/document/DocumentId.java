package client.entity.process.document;

import lombok.Data;

import java.io.Serializable;

@Data
public class DocumentId implements Serializable {
    
    private Long processId;
    
    private String title;
}
