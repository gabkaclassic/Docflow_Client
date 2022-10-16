package client.response;

import client.entity.process.Step;
import lombok.*;

@Data
@Builder
public class StepResponse extends Response {
    
    public static final String DOES_NOT_EXISTS = "The entity with such parameters doesn't exists";
    
    private Step step;
    
    public StepResponse message(String message) {
        
        setMessage(message);
        
        return this;
    }
    
    public StepResponse status(String status) {
        
        setStatus(status);
        
        return this;
    }
}
