package bjim.server;

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

    private final ServerChatWindow serverChatWindow;

    private final List<ClientConnection> clientConnections = new ArrayList<>();

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

    public Server(ServerChatWindow serverChatWindow) {
        this(DEFAULT_PORT, serverChatWindow);
    }

    public boolean isWindowVisible() {
        return serverChatWindow.isVisible();
    }

    public boolean isServerMessageVisible() {
        return serverChatWindow.isUserMessageVisible();
    }

    public void startRunning() {

        serverChatWindow.onSend(event -> sendMessage(event.getActionCommand()));

        serverThreadPool.submit(
                new Runnable() {

                    @Override
                    public void run() {
                        try {
                            serverSocket = new ServerSocket(port, 100);
                            showMessage("\nWaiting for someone to connect!");
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
                });
    }

    private void waitForConnection() throws IOException {

        ClientConnection clientConnection = new ClientConnection(serverSocket.accept());
        clientConnections.add(clientConnection);

        handlerThreadPool.submit(() -> readMessages(clientConnection));

        showMessage("\nNow connected to: " + clientConnection.getHostName() + " !");
    }

    private void readMessages(ClientConnection clientConnection) {

        while (clientConnection != null && clientConnection.getInput() != null) {
            try {
                lastReceivedMessage = String.valueOf(clientConnection.getInput().readObject());
                showMessage("\n" + lastReceivedMessage);

            } catch (IOException e) {
                showMessage("\nClient: " + clientConnection.getHostName() + " closed");
                closeClientConnection(clientConnection);
                break;
            } catch (ClassNotFoundException e) {
                showMessage("\nI don't know what user send!");
            }
        }
    }

    public synchronized void sendMessage(String message) {
        for (ClientConnection clientConnection : clientConnections) {
            try {
                clientConnection.getOutput().writeObject("ADMIN- " + message);
                clientConnection.getOutput().flush();
                showMessage("\nADMIN- " + message);

            } catch (IOException ioException) {
                serverChatWindow.append("\nERROR: Can't send that message");
            }
        }
    }

    private void disconnectClients() {
        showMessage("\nClosing connections\n");
        ableToType(false);

        for (ClientConnection clientConnection : clientConnections) {
            closeClientConnection(clientConnection);
        }
        clientConnections.clear();
    }

    private void closeClientConnection(ClientConnection clientConnection) {
        if (clientConnection == null) {
            return;
        }
        try {
            clientConnection.close();
            clientConnections.remove(clientConnection);
        } catch (IOException e) {
            System.out.println(
                    "Error while attempting to close client connection: " + e.getMessage());
        }
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
        serverChatWindow.showMessage(text);
    }

    public void ableToType(boolean tof) {
        serverChatWindow.ableToType(tof);
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
        serverChatWindow.setDefaultCloseOperation(exitOnClose);
    }

    public int numberOfClientsConnected() {
        int count = 0;
        for (ClientConnection clientConnection : clientConnections) {
            if (clientConnection != null && !clientConnection.getSocket().isClosed()) {
                ++count;
            }
        }
        return count;
    }
}
