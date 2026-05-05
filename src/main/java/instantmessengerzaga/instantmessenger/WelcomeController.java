package instantmessengerzaga.instantmessenger;

import javafx.fxml.FXML;

import java.io.IOException;

public class WelcomeController {

    @FXML
    private void startServer() throws IOException {
        UserData.mainApplication.showServer();
    }

    @FXML
    private void joinChat() throws IOException {
        UserData.mainApplication.showLogin();
    }
}
