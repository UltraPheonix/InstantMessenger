package instantmessengerzaga.instantmessenger;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class MessengerServer {

    public interface Listener {
        void onClientConnected(String username);
        void onClientDisconnected(String username);
        void onMessageLogged(String message);
    }

    public static final List<String> MasterMessageLog = new CopyOnWriteArrayList<>();

    private final List<ClientSession> sessions = new CopyOnWriteArrayList<>();
    private ServerSocket serverSocket;
    private volatile boolean running;
    private Listener listener;

    public void setListener(Listener listener) {
        this.listener = listener;
    }

    public void start(int port) throws IOException {
        MasterMessageLog.clear();
        serverSocket = new ServerSocket(port);
        serverSocket.setReuseAddress(true);
        running = true;
        Thread.ofVirtual().start(this::acceptLoop);
    }

    public void stop() {
        running = false;
        try {
            if (serverSocket != null && !serverSocket.isClosed()) {
                serverSocket.close();
            }
        } catch (IOException ignored) {}
    }

    private void acceptLoop() {
        while (running) {
            try {
                var clientSocket = serverSocket.accept();
                Thread.ofVirtual().start(() -> handleClient(clientSocket));
            } catch (IOException e) {
                if (running) System.err.println("Accept error: " + e.getMessage());
            }
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
            if (listener != null) listener.onClientConnected(username);
            for (String msg : MasterMessageLog) {
                out.writeUTF(msg);
            }
            while (true) {
                String raw = in.readUTF();
                String message = username + ": " + raw;
                MasterMessageLog.add(message);
                if (listener != null) listener.onMessageLogged(message);
                broadcast(message);
            }
        } catch (IOException e) {
            System.err.println("Client disconnected: " + e.getMessage());
        } finally {
            if (session != null) {
                sessions.remove(session);
                if (listener != null) listener.onClientDisconnected(session.username());
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
        var server = new MessengerServer();
        try {
            server.start(5065);
            System.out.println("Server started on port 5065. Press Ctrl+C to stop.");
            Thread.currentThread().join();
        } catch (IOException e) {
            System.err.println("Failed to start server: " + e.getMessage());
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        } finally {
            server.stop();
        }
    }
}

record ClientSession(Socket socket, String username, DataOutputStream out) {}

