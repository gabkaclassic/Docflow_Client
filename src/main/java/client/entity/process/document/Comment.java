package client.entity.process.document;

import client.entity.process.Participant;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Data
public class Comment {
    
    private Long id;
    
    private String text;
    
    private Participant author;
}

