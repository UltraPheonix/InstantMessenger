package instantmessengerzaga.instantmessenger;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;

public class MessengerController {

    @FXML private TextArea messageArea;
    @FXML private TextField textField;
    @FXML private Label usernameLabel;

    private final Client client;

    public MessengerController() {
        this.client = new Client(UserData.IP, UserData.Port, this);
    }

    @FXML
    public void initialize() {
        client.sendMessage(UserData.UserName);
        if (usernameLabel != null) {
            usernameLabel.setText("Logged in as: " + UserData.UserName);
        }
        // Allow Enter key to send a message
        textField.setOnKeyPressed(e -> {
            if (e.getCode() == KeyCode.ENTER) {
                sendMessage();
            }
        });
    }

    @FXML
    private void sendMessage() {
        String text = textField.getText().trim();
        if (!text.isEmpty()) {
            client.sendMessage(text);
            textField.clear();
        }
    }

    public void addText(String s) {
        Platform.runLater(() -> messageArea.appendText("\n" + s));
    }
}

