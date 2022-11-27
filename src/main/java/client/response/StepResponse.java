package client.response;

import client.entity.process.step.Step;
import client.response.deserializer.StepResponseDeserializer;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import lombok.Builder;
import lombok.Data;

/**
 * Тип ответа для получения информации о шаге
 * @see Response
 * @see Step
 * */
@Data
@Builder
@JsonDeserialize(using = StepResponseDeserializer.class)
public class StepResponse extends Response {
    
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
