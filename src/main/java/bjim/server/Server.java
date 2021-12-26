package bjim.server;

import bjim.common.Connection;
import java.io.IOException;
import java.net.ServerSocket;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class Server {

    public static final int DEFAULT_PORT = 6789;

    // the port where the server is listening
    private final int port;

    // the socket where the server is listening
    private ServerSocket serverSocket;

    private final ServerChatWindow chatWindow;

    private final List<Connection> connections = new ArrayList<>();

    // checking last received message from client to server
    @Getter private String lastReceivedMessage = "";

    // A single thread for the server accept loop
    private final ExecutorService serverThreadPool = Executors.newSingleThreadExecutor();

    private final ExecutorService handlerThreadPool = Executors.newFixedThreadPool(10);

    public Server() {
        this(DEFAULT_PORT);
    }

    public Server(int port) {
        this(port, new ServerChatWindow());
    }

    public Server(ServerChatWindow chatWindow) {
        this(DEFAULT_PORT, chatWindow);
    }

    public boolean isWindowVisible() {
        return chatWindow.isVisible();
    }

    public boolean isServerMessageVisible() {
        return chatWindow.isUserMessageVisible();
    }

    public void startRunning() {

        chatWindow.onSend(event -> sendMessage(event.getActionCommand()));

        serverThreadPool.submit(new StartServer());
    }

    public synchronized void sendMessage(String message) {

        String messageToSend = chatWindow.getUsername() + ":\n  " + message;

        for (Connection connection : connections) {
            try {
                sendMessage(messageToSend, connection);
                showMessage("\n" + messageToSend);
            } catch (IOException ioException) {
                chatWindow.append("\nERROR: Can't send that message");
            }
        }
    }

    private void sendMessage(String messageToSend, Connection connection) throws IOException {
        connection.getOutput().writeObject(messageToSend);
        connection.getOutput().flush();
    }

    public void stopRunning() {
        System.out.println("Stopping server...");
        while (serverSocket != null && !serverSocket.isClosed()) {
            try {
                serverSocket.close();
                return;
            } catch (IOException e) {
                System.out.println("Failed to stop server...");
            }
        }
    }

    public synchronized void showMessage(String text) {
        chatWindow.showMessage(text);
    }

    public void ableToType(boolean tof) {
        chatWindow.ableToType(tof);
    }

    public int getPort() {
        return serverSocket.getLocalPort();
    }

    public boolean isRunning() {
        if (serverSocket == null) {
            return false;
        }
        return !serverSocket.isClosed();
    }

    public void setDefaultCloseOperation(int exitOnClose) {
        chatWindow.setDefaultCloseOperation(exitOnClose);
    }

    public int numberOfClientsConnected() {
        int count = 0;
        for (Connection connection : connections) {
            if (connection != null && !connection.getSocket().isClosed()) {
                ++count;
            }
        }
        return count;
    }

    private class StartServer implements Runnable {

        @Override
        public void run() {
            try {
                serverSocket = new ServerSocket(port, 100);
                setStatus("Waiting for clients to connect!");
                ableToType(true);

                while (true) {
                    waitForConnection();
                }

            } catch (IOException ioException) {
                System.out.println("Stopping server: " + ioException.getMessage());
            } finally {
                disconnectClients();
            }
        }

        private void waitForConnection() throws IOException {

            Connection connection = new Connection(serverSocket.accept());
            connections.add(connection);

            handlerThreadPool.submit(() -> readMessages(connection));

            setStatus("(" + connections.size() + ") client(s) are connected");
        }

        private void disconnectClients() {
            showMessage("\nClosing connections\n");
            ableToType(false);

            for (Connection connection : connections) {
                closeClientConnection(connection);
            }
            connections.clear();
        }

        private void readMessages(Connection connection) {

            while (connection != null && connection.getInput() != null) {
                try {
                    lastReceivedMessage = String.valueOf(connection.getInput().readObject());
                    showMessage("\n" + lastReceivedMessage);

                } catch (IOException e) {
                    closeClientConnection(connection);
                    setStatus("(" + connections.size() + ") client(s) are connected");
                    break;
                } catch (ClassNotFoundException e) {
                    setStatus("(" + connections.size() + ") client(s) are connected");
                }
            }
        }

        private void setStatus(String text) {
            chatWindow.setStatus(text);
        }

        private void closeClientConnection(Connection connection) {
            if (connection == null) {
                return;
            }
            try {
                connection.close();
                connections.remove(connection);
            } catch (IOException e) {
                System.out.println(
                        "Error while attempting to close client connection: " + e.getMessage());
            }
        }
    }
}
