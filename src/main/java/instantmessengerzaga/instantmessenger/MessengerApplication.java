package instantmessengerzaga.instantmessenger;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.stage.Stage;

import java.io.IOException;

public class MessengerApplication extends Application {

    private Stage mainStage;

    public static void main(String[] args) {
        launch();
    }

    @Override
    public void start(Stage stage) throws Exception {
        mainStage = stage;
        mainStage.setResizable(true);
        UserData.mainApplication = this;
        showWelcome();
        mainStage.show();
    }

    public void showWelcome() throws IOException {
        mainStage.setTitle("InstantMessenger");
        loadScene("WelcomeWindow.fxml");
    }

    public void showLogin() throws IOException {
        mainStage.setTitle("InstantMessenger – Join a Chat");
        loadScene("LoginWindow.fxml");
    }

    public void showServer() throws IOException {
        mainStage.setTitle("InstantMessenger – Server");
        loadScene("ServerWindow.fxml");
    }

    public void login() throws IOException {
        mainStage.setTitle("InstantMessenger – Chat");
        FXMLLoader loader = new FXMLLoader(
                getClass().getResource("/instantmessengerzaga/instantmessenger/MessageWindow.fxml"));
        Scene scene;
        try {
            scene = new Scene(loader.load());
        } catch (Exception e) {
            Throwable cause = e.getCause() != null ? e.getCause() : e;
            Alert alert = new Alert(Alert.AlertType.ERROR,
                    "Could not connect to " + UserData.IP + ":" + UserData.Port + "\n" + cause.getMessage(),
                    ButtonType.OK);
            alert.setTitle("Connection Failed");
            alert.setHeaderText("Unable to reach the server");
            alert.showAndWait();
            return;
        }
        addStylesheet(scene);
        mainStage.setScene(scene);
        mainStage.sizeToScene();
        mainStage.centerOnScreen();
    }

    private void loadScene(String fxml) throws IOException {
        FXMLLoader loader = new FXMLLoader(
                getClass().getResource("/instantmessengerzaga/instantmessenger/" + fxml));
        Scene scene = new Scene(loader.load());
        addStylesheet(scene);
        mainStage.setScene(scene);
        mainStage.sizeToScene();
        mainStage.centerOnScreen();
    }

    private void addStylesheet(Scene scene) {
        var css = getClass().getResource(
                "/instantmessengerzaga/instantmessenger/styles.css");
        if (css != null) {
            scene.getStylesheets().add(css.toExternalForm());
        }
    }
}
