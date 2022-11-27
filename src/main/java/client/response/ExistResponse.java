package client.response;

import client.response.deserializer.ExistResponseDeserializer;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import lombok.Builder;
import lombok.Data;

/**
 * Тип ответа для получения информации о наличии сущности в БД
 * @see Response
 * */
@Builder
@Data
@JsonDeserialize(using = ExistResponseDeserializer.class)
public class ExistResponse extends Response {
    
    private boolean exist;
    
    public ExistResponse status(String status) {
        
        setStatus(status);
        
        return this;
    }
}
