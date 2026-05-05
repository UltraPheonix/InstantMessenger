package instantmessengerzaga.instantmessenger;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class MessengerServer {

    public static final List<String> MasterMessageLog = new CopyOnWriteArrayList<>();

    private final List<ClientSession> sessions = new CopyOnWriteArrayList<>();

    public MessengerServer(int port) {
        try (var serverSocket = new ServerSocket(port)) {
            serverSocket.setReuseAddress(true);
            System.out.println("Server started on port " + port);
            while (true) {
                var socket = serverSocket.accept();
                Thread.ofVirtual().start(() -> handleClient(socket));
            }
        } catch (IOException e) {
            System.err.println("Server error: " + e.getMessage());
        }
    }

    private void handleClient(Socket socket) {
        ClientSession session = null;
        try (socket;
             var in = new DataInputStream(socket.getInputStream());
             var out = new DataOutputStream(socket.getOutputStream())) {
            String username = in.readUTF();
            session = new ClientSession(socket, username, out);
            sessions.add(session);
            for (String msg : MasterMessageLog) {
                out.writeUTF(msg);
            }
            while (true) {
                String raw = in.readUTF();
                String message = username + ": " + raw;
                MasterMessageLog.add(message);
                broadcast(message);
            }
        } catch (IOException e) {
            System.err.println("Client disconnected: " + e.getMessage());
        } finally {
            if (session != null) {
                sessions.remove(session);
            }
        }
    }

    private void broadcast(String message) {
        for (var s : sessions) {
            if (s.socket().isClosed()) {
                continue;
            }
            try {
                synchronized (s.out()) {
                    s.out().writeUTF(message);
                }
            } catch (IOException e) {
                System.err.println("Broadcast error: " + e.getMessage());
            }
        }
    }

    public static void main(String[] args) {
        new MessengerServer(5065);
    }
}

record ClientSession(Socket socket, String username, DataOutputStream out) {}
