package client.gui;

import client.entity.process.Participant;
import client.entity.process.Process;
import client.entity.Team;
import client.gui.data.Data;
import client.response.InfoResponse;
import client.sender.Sender;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import org.springframework.util.LinkedMultiValueMap;

import java.io.IOException;
import java.util.List;
import java.util.Objects;

public class NewSignIn {

    private Sender sender;
    private Stage stage;
    private Scene scene;
    private Parent root;
    //не забыть поменять на private
    @FXML
    public TextField login;
    @FXML
    public PasswordField password;

    private Data data = Data.getInstance();

    public void switchToLogin(ActionEvent event) throws IOException {
        Parent root = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("new_Log_in.fxml")));
        stage = (Stage)(((Node)event.getSource()).getScene().getWindow());
        scene = new Scene(root);
        stage.setScene(scene);
        stage.show();
        data.setParticipant(new Participant());

    }
    public void signin(ActionEvent event) throws IOException {
        Sender.login(login.getText(), password.getText());
        var user_info = Sender.GetUserInfo();
        var listTeamsTitle = user_info.getTeams().stream().map(Team::getTitle).toList();
        var listProcessesTitle = user_info.getProcesses().stream().map(Process::getTitle).toList();
        FXMLLoader loader = new FXMLLoader(getClass().getResource("new_Main_Menu.fxml"));
        root =loader.load();
        NewMainMenu newMainMenu = loader.getController();
        newMainMenu.LoadInfo(user_info.getTeams(), user_info.getProcesses());
        stage = (Stage)((Node)event.getSource()).getScene().getWindow();
        scene = new Scene(root);
        stage.setScene(scene);
        stage.show();


    }
}
