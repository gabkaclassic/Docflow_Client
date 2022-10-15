package client.entity.process.document;

import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Data
public class DocumentType {
    
    private Long id;
    
    private String title;
    
    private String defaultFormat;
    
}
