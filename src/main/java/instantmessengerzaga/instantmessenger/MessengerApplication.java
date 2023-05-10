package instantmessengerzaga.instantmessenger;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.Objects;

public class MessengerApplication extends Application {

    private Stage mainStage;

    public static void main(String[] args) {
        launch();
    }

    @Override
    public void start(Stage stage) throws Exception {
        mainStage = stage;
        UserData.mainApplication = this;
        Parent root = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("/instantmessengerzaga/instantmessenger/LoginWindow.fxml")));
        Scene scene = new Scene(root);
        this.mainStage.setScene(scene);
        this.mainStage.show();
    }

    public void login() throws IOException {

        FXMLLoader loader = new FXMLLoader(getClass().getResource("/instantmessengerzaga/instantmessenger//MessageWindow.fxml"));

        Parent root  = loader.load();

        this.mainStage.setScene(new Scene(root));
    }
}