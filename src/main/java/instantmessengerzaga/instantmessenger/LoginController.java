package instantmessengerzaga.instantmessenger;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;

import java.io.IOException;
import java.net.InetAddress;

public class LoginController{

    @FXML
    private TextField IPField;
    @FXML
    private TextField PortField;
    @FXML
    private TextField usernameField;

    @FXML
    private Button loginButton;

    @FXML
    public void loginEvent() throws IOException {
        UserData.Port = Integer.parseInt(PortField.getText());
        UserData.IP = IPField.getText();
        UserData.UserName = usernameField.getText();

        UserData.mainApplication.login();
    }



    @FXML
    public void getLocalHost() throws IOException{
        IPField.setText(InetAddress.getLocalHost().getHostAddress());
    }
}
