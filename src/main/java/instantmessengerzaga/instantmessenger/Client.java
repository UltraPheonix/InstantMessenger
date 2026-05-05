package instantmessengerzaga.instantmessenger;

import javafx.application.Platform;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

public class Client {

    private final DataOutputStream outputStream;

    public Client(String ip, int port, MessengerController messengerController) {
        try {
            var socket = new Socket(ip, port);
            socket.setReuseAddress(true);
            this.outputStream = new DataOutputStream(socket.getOutputStream());

            Platform.runLater(() -> {
                for (String s : MessengerServer.MasterMessageLog) {
                    messengerController.addText(s);
                }
            });

            Thread.ofVirtual().start(new ClientProcessor(socket, messengerController));
        } catch (IOException e) {
            throw new RuntimeException("Failed to connect to server", e);
        }
    }

    public void sendMessage(String s) {
        try {
            outputStream.writeUTF(s);
        } catch (IOException e) {
            System.err.println("Send error: " + e.getMessage());
        }
    }
}

class ClientProcessor implements Runnable {

    private final DataInputStream inputStream;
    private final MessengerController messengerController;

    ClientProcessor(Socket socket, MessengerController messengerController) throws IOException {
        this.inputStream = new DataInputStream(socket.getInputStream());
        this.messengerController = messengerController;
    }

    @Override
    public void run() {
        try {
            while (true) {
                String s = inputStream.readUTF();
                messengerController.addText(s);
            }
        } catch (IOException e) {
            System.err.println("Connection closed: " + e.getMessage());
        }
    }
}
