package client.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Response {
    
    public static final String STATUS_ERROR = "isError";
    public static final String STATUS_SUCCESS = "success";
    
    public static final String SERVER_ERROR = "Error on server side";
    public static final String SUCCESS_LOADING = "Success info loading";
    public static final String SUCCESS_CREATING = "Success creating process";
    public static final String SUCCESS_INVITE = "Success invite";
    public static final String SUCCESS_REGISTRATION = "Registration was finished with success status";
    public static final String USER_ALREADY_EXISTS = "Account with this login already exists";
    public static final String SUCCESS_LOGOUT = "Logout process finished with success status";
    public static final String WEAK_PASSWORD = "Password must contain 8 characters, at least 1 special symbol and at least 1 integer";
    public static final String INVALID_LOGIN = "Login must contain from 2 to 64 symbols";
    public static final String TEAM_NOT_EXIST = "Team is not exists";
    
    protected String status;
    
    protected String message;
    
    public boolean isError() {
        return status == null || status.equals(STATUS_ERROR);
    }
}
