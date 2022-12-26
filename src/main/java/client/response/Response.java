package client.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Родительский класс для всех типов ответов
 * @see ExistResponse
 * @see InfoResponse
 * @see ProcessResponse
 * @see StepResponse
 * */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Response {
    
    public static final String STATUS_ERROR = "error";
    public static final String STATUS_SUCCESS = "success";
    
    protected String status;
    
    protected String message;
    
    public boolean isError() {
        return status == null || status.equals(STATUS_ERROR);
    }
}
