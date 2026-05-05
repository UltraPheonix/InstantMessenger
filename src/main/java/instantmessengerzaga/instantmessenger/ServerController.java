package instantmessengerzaga.instantmessenger;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;

import java.io.IOException;

public class ServerController {

    @FXML private TextField portField;
    @FXML private Button startStopButton;
    @FXML private Label statusLabel;
    @FXML private TextArea logArea;
    @FXML private ListView<String> userList;

    private final MessengerServer server = new MessengerServer();
    private boolean running = false;

    @FXML
    public void initialize() {
        server.setListener(new MessengerServer.Listener() {
            @Override
            public void onClientConnected(String username) {
                Platform.runLater(() -> {
                    userList.getItems().add(username);
                    logArea.appendText("\n[+] " + username + " joined");
                });
            }

            @Override
            public void onClientDisconnected(String username) {
                Platform.runLater(() -> {
                    userList.getItems().remove(username);
                    logArea.appendText("\n[-] " + username + " left");
                });
            }

            @Override
            public void onMessageLogged(String message) {
                Platform.runLater(() -> logArea.appendText("\n" + message));
            }
        });
    }

    @FXML
    private void toggleServer() {
        if (!running) {
            startServer();
        } else {
            stopServer();
        }
    }

    private void startServer() {
        String portText = portField.getText().trim();
        int port;
        try {
            port = Integer.parseInt(portText);
        } catch (NumberFormatException e) {
            logArea.appendText("\nError: Invalid port number.");
            return;
        }
        if (port < 1 || port > 65535) {
            logArea.appendText("\nError: Port must be between 1 and 65535.");
            return;
        }

        try {
            server.start(port);
            running = true;
            portField.setDisable(true);
            startStopButton.setText("Stop Server");
            startStopButton.getStyleClass().removeAll("primary-button");
            startStopButton.getStyleClass().add("danger-button");
            statusLabel.setText("Running on port " + port);
            statusLabel.getStyleClass().removeAll("header-sub", "status-stopped");
            statusLabel.getStyleClass().add("status-running");
            logArea.appendText("\nServer started on port " + port + ". Waiting for connections...");
        } catch (IOException e) {
            logArea.appendText("\nFailed to start server: " + e.getMessage());
        }
    }

    private void stopServer() {
        server.stop();
        running = false;
        portField.setDisable(false);
        startStopButton.setText("Start Server");
        startStopButton.getStyleClass().removeAll("danger-button");
        startStopButton.getStyleClass().add("primary-button");
        statusLabel.setText("Stopped");
        statusLabel.getStyleClass().removeAll("status-running");
        statusLabel.getStyleClass().add("header-sub");
        userList.getItems().clear();
        logArea.appendText("\nServer stopped.");
    }

    @FXML
    private void goBack() throws IOException {
        if (running) {
            stopServer();
        }
        UserData.mainApplication.showWelcome();
    }
}
