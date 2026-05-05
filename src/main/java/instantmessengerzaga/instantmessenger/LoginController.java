package instantmessengerzaga.instantmessenger;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;

import java.io.IOException;
import java.net.InetAddress;

public class LoginController {

    @FXML private TextField IPField;
    @FXML private TextField PortField;
    @FXML private TextField usernameField;
    @FXML private Label errorLabel;

    @FXML
    public void initialize() {
        // Allow pressing Enter on any field to trigger login
        usernameField.setOnKeyPressed(e -> {
            if (e.getCode() == KeyCode.ENTER) {
                try {
                    loginEvent();
                } catch (IOException ex) {
                    errorLabel.setText("Unexpected error: " + ex.getMessage());
                }
            }
        });
        PortField.setOnKeyPressed(e -> {
            if (e.getCode() == KeyCode.ENTER) {
                usernameField.requestFocus();
            }
        });
        IPField.setOnKeyPressed(e -> {
            if (e.getCode() == KeyCode.ENTER) {
                PortField.requestFocus();
            }
        });
    }

    @FXML
    public void loginEvent() throws IOException {
        String ip = IPField.getText().trim();
        String portText = PortField.getText().trim();
        String username = usernameField.getText().trim();

        if (ip.isEmpty()) {
            errorLabel.setText("Please enter a server IP address.");
            IPField.requestFocus();
            return;
        }
        if (portText.isEmpty()) {
            errorLabel.setText("Please enter a port number.");
            PortField.requestFocus();
            return;
        }
        int port;
        try {
            port = Integer.parseInt(portText);
        } catch (NumberFormatException e) {
            errorLabel.setText("Port must be a number (e.g. 5065).");
            PortField.requestFocus();
            return;
        }
        if (port < 1 || port > 65535) {
            errorLabel.setText("Port must be between 1 and 65535.");
            PortField.requestFocus();
            return;
        }
        if (username.isEmpty()) {
            errorLabel.setText("Please enter a username.");
            usernameField.requestFocus();
            return;
        }

        errorLabel.setText("");
        UserData.Port = port;
        UserData.IP = ip;
        UserData.UserName = username;
        UserData.mainApplication.login();
    }

    @FXML
    public void getLocalHost() throws IOException {
        IPField.setText(InetAddress.getLocalHost().getHostAddress());
    }

    @FXML
    private void goBack() throws IOException {
        UserData.mainApplication.showWelcome();
    }
}

