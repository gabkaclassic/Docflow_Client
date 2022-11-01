package client.entity.process;

import lombok.Getter;

@Getter
public enum Rules {
    
    CHANGE("Edit and saving documents", 2),
    READ("Reading and check documents", 1),
    CONTROL("Edit and saving documents, confirmation of the transition to the next step", 3);
    
    
    private String view;
    private int level;
    
    Rules(String view, int level) {
    
        this.view = view;
        this.level = level;
    }
}
