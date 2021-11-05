package bjim.client;

import bjim.common.Connection;
import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Client {

    public static final String LOCAL_HOST = "127.0.0.1";
    private static final int SERVER_PORT = 6789;
    private static final String CONNECTION_CLOSED = "Connection closed";

    private final ClientChatWindow chatWindow;
    private final String serverIP;
    private final int serverPort = SERVER_PORT; // todo: allow setting in constructor

    private Connection connection;

    private String lastReceivedMessage = "";

    private ExecutorService executorService = Executors.newSingleThreadExecutor();

    public static Client withUsername(String username) {
        return new Client(LOCAL_HOST, username);
    }

    public Client() {
        this(new ClientChatWindow());
    }

    public Client(String serverIP) {
        this(serverIP, new ClientChatWindow());
    }

    public Client(String serverIP, String username) {
        this(serverIP, new ClientChatWindow(username));
    }

    public Client(ClientChatWindow chatWindow) {
        this(LOCAL_HOST, chatWindow);
    }

    public Client(String serverIP, ClientChatWindow chatWindow) {
        this.serverIP = serverIP;
        this.chatWindow = chatWindow;
        this.chatWindow.onSend(event -> sendMessage(event.getActionCommand()));
    }

    public boolean isWindowVisibleClientSide() {
        return chatWindow.isVisible();
    }

    public void startRunning() {

        executorService.submit(new StartClient());
    }

    public String getLastReceivedMessage() {
        return lastReceivedMessage;
    }

    public void stopRunning() {
        System.out.println("Stopping client...");
        while (connection != null && !connection.isClosed()) {
            try {
                connection.close();
                return;
            } catch (IOException e) {
                System.out.println("Failed to stop client...");
            }
        }
    }

    public void sendMessage(String message) {

        String messageToSend = chatWindow.getUsername() + ":\n  " + message;
        try {
            sendMessage(messageToSend, connection);
            showMessage("\n" + messageToSend);
        } catch (IOException ioException) {
            chatWindow.append("\nSomething is messed up!");
        }
    }

    private void sendMessage(String messageToSend, Connection connection) throws IOException {
        connection.getOutput().writeObject(messageToSend);
        connection.getOutput().flush();
    }

    private void showMessage(final String m) {
        chatWindow.showMessage(m);
    }

    public void setDefaultCloseOperation(int exitOnClose) {
        chatWindow.setDefaultCloseOperation(exitOnClose);
    }

    public String getServerIP() {
        return serverIP;
    }

    public boolean isConnected() {
        return connection != null && connection.isConnected();
    }

    private class StartClient implements Runnable {

        @Override
        public void run() {
            try {
                connectToServer();
                whileChatting();
            } catch (IOException eofException) {
                setStatus(CONNECTION_CLOSED);
            } finally {
                disconnect();
            }
        }

        private void connectToServer() throws IOException {
            setStatus("Attempting to connect to server @" + serverIP + ":" + serverPort);
            connection = new Connection(new Socket(InetAddress.getByName(serverIP), serverPort));
            setStatus("Connected to server @" + serverIP + ":" + serverPort);
        }

        private void whileChatting() throws IOException {
            ableToType(true);
            do {
                try {
                    lastReceivedMessage = String.valueOf(connection.getInput().readObject());
                    showMessage("\n" + lastReceivedMessage);

                } catch (ClassNotFoundException classNotFoundException) {
                    showMessage("\nDont know ObjectType!");
                }
            } while (!lastReceivedMessage.equals("\nADMIN - END"));
        }

        private void disconnect() {
            setStatus(CONNECTION_CLOSED);
            ableToType(false);
            try {
                if (connection != null) {
                    connection.close();
                }
            } catch (IOException e) {
                setStatus(CONNECTION_CLOSED);
            }
        }

        private void ableToType(final boolean tof) {
            chatWindow.ableToType(tof);
        }

        private void setStatus(String text) {
            chatWindow.setStatus(text);
        }
    }
}
