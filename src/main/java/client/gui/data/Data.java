package client.gui.data;

import client.entity.process.Participant;

@lombok.Data
public class Data {
    
    private Participant participant;
    
    private static Data instance;
    
    public static Data getInstance() {
        
        if(instance == null)
            instance = new Data();
            
        return instance;
    }
    
}
