package instantmessengerzaga.instantmessenger;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Vector;

public class MessengerServer {

    public static final Vector<String> MasterMessageLog = new Vector<>();

    private ServerSocket serverSocket;
    private Socket socket;

    public MessengerServer(int port){
        try {
            serverSocket = new ServerSocket(port);
            serverSocket.setReuseAddress(true);
            socket = serverSocket.accept();
            socket.setReuseAddress(true);
            System.out.println("server Started");
            String user = new DataInputStream(socket.getInputStream()).readUTF();
            MessengerServerClientHandler clientHandler = new MessengerServerClientHandler(socket, user );
            clientHandler.start();
            while(true){
                socket = serverSocket.accept();
                String s = new DataInputStream(socket.getInputStream()).readUTF();
                clientHandler.users.add(new User(socket, s));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        new MessengerServer(5065);
    }
}

record User(Socket socket,  String username){}

class MessengerServerClientHandler extends Thread{
    protected Vector<User> users = new Vector<User>();

    private class SocketListener extends Thread{
        protected Vector<String> messages = new Vector<>();
        @Override
        public void run() {
            while(true){
                for (int i = 0; i < users.size(); i++) {
                    try {
                        if(users.get(i).socket().getInputStream().available()>0){
                            DataInputStream inputStream = new DataInputStream(users.get(i).socket().getInputStream());
                            String rawMessage = inputStream.readUTF();
                            MessengerServer.MasterMessageLog.add(users.get(i).username()+": " +  rawMessage);
                            messages.add(users.get(i).username()+": " +  rawMessage);
                        }
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                }
            }
        }
    }

    private final SocketListener listener = new SocketListener();
    
    public MessengerServerClientHandler(Socket socket, String user){
        users.add(new User(socket, user));
    }
    @Override
    public void run() {
        boolean first = true;
        String username = "";
        listener.start();
        while (true){
            for(int i = 0; i<listener.messages.size(); i++) {
                for (User user: users) {
                    try {
                        DataOutputStream dataOutputStream = new DataOutputStream(user.socket().getOutputStream());
                        Broadcast broadcast = new Broadcast(dataOutputStream, listener.messages.get(i));
                        broadcast.start();
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                }

                if(i == listener.messages.size()-1){
                    listener.messages = new Vector<>();
                }
            }
        }
    }
}

class Broadcast extends Thread{

    private final DataOutputStream outputStream;
    private final String message;

    Broadcast(DataOutputStream outputStream,
              String message){
        this.outputStream = outputStream;
        this.message = message;
    }
    @Override
    public void run() {
        try {
            outputStream.writeUTF(message);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}


