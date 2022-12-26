package client.response;

import client.entity.process.Process;
import client.response.deserializer.ProcessResponseDeserializer;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import lombok.Builder;
import lombok.Data;

/**
 * Тип ответа для получения полной информации о процессе
 * @see Response
 * @see Process
 * */
@Data
@Builder
@JsonDeserialize(using = ProcessResponseDeserializer.class)
public class ProcessResponse extends Response {
    
    private Process process;
    
    public ProcessResponse message(String message) {
        
        setMessage(message);
        
        return this;
    }
    
    public ProcessResponse status(String status) {
        
        setStatus(status);
        
        return this;
    }
    
}
