package instantmessengerzaga.instantmessenger;

import javafx.application.Platform;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

public class Client{

    private Socket socket;

    private DataOutputStream outputStream;

    public Client(String IP, int port, MessengerController messengerController){
        try {
            this.socket = new Socket(IP, port);
            this.socket.setReuseAddress(true);
            this.outputStream = new DataOutputStream(socket.getOutputStream());

            Platform.runLater(new Runnable() {
                @Override
                public void run() {
                    for (String s: MessengerServer.MasterMessageLog) {
                        messengerController.addText(s);
                    }
                }
            });

            ClientProcessor clientProcessor = new ClientProcessor(socket,IP, port, messengerController);
            clientProcessor.start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void sendMessage(String s){
        try {
            outputStream.writeUTF(s);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

class ClientProcessor extends Thread{
    private DataInputStream inputStream;

    private MessengerController messengerController;

    public ClientProcessor(Socket socket, String IP, int port, MessengerController messengerController){
        try {
            this.inputStream = new DataInputStream(socket.getInputStream());
            this.messengerController = messengerController;
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    @Override
    public void run() {
        while (true){
            try {
                if(inputStream.available() > 0){
                    System.out.println("looping");
                    String s = inputStream.readUTF();
                    System.out.println(s);
                    messengerController.addText(s);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
