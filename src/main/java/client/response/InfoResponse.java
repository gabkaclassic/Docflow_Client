package client.response;

import client.entity.Team;
import client.entity.process.Participant;
import client.entity.process.Process;
import client.response.deserializer.InfoResponseDeserializer;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import lombok.Builder;
import lombok.Data;

import java.util.List;

/**
 * Тип ответа для получения всей информации, связанной с конкретным аккаунтом участника
 * @see Response
 * @see Participant
 * */
@Builder
@Data
@JsonDeserialize(using = InfoResponseDeserializer.class)
public class InfoResponse extends Response {
    
    private Participant participant;
    
    private List<Team> teams;
    
    private List<Process> processes;
    
    public InfoResponse message(String message) {
        
        setMessage(message);
        
        return this;
    }
    
    public InfoResponse status(String status) {
        
        setStatus(status);
        
        return this;
    }
}
