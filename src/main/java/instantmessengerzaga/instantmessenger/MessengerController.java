package instantmessengerzaga.instantmessenger;

import javafx.fxml.FXML;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;

public class MessengerController{

    @FXML
    private TextArea messageArea;

    @FXML
    private TextField textField;

    private final Client client;

    public MessengerController(){
        this.client = new Client(UserData.IP, UserData.Port, this);
    }

    @FXML
    public void initialize(){
        client.sendMessage(UserData.UserName);
    }
    @FXML
    private void sendMessage(){
        client.sendMessage(textField.getText());
        textField.clear();
    }

    public void addText(String s){
        messageArea.appendText("\r\n" + s);
    }

}
