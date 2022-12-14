package client.gui.controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;

import java.io.IOException;

/**
 * Контроллер для сцены выбора между аутентификацией и регистрацией
 * @see Controller
 * */
public class LoginController extends Controller {
    
    @FXML
    private Button back;
    
    @FXML
    public void initialize() {
    
        if(data.getPreviousScene() == null)
            back.setVisible(false);
    }
    public void switchToSignIn(ActionEvent event) throws IOException {
        
        showStage(event, "sign_in.fxml");
    }
    public void switchToSignUp(ActionEvent event) throws IOException {
    
        showStage(event, "sign_up.fxml");
    }
}
