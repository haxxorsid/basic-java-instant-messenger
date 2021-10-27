package bjim.server;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.net.ServerSocket;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import javax.swing.*;

public class Server {

    public static final int DEFAULT_PORT = 6789;

    // the port where the server is listening
    private final int port;

    // the socket where the server is listening
    private ServerSocket serverSocket;

    // Chat attributes
    private JFrame chatWindow;
    private JTextField userMessage;
    private JTextArea chatBox;

    private final List<ClientConnection> clientConnections = new ArrayList<>();

    // checking last received message from client to server
    private String lastReceivedMessage = "";

    // A single thread for the server accept loop
    private ExecutorService serverThreadPool = Executors.newSingleThreadExecutor();

    private ExecutorService handlerThreadPool = Executors.newFixedThreadPool(10);

    public Server() {
        this(DEFAULT_PORT);
    }

    public Server(int port) {
        this.port = port;

        chatWindow = new JFrame("Instant Messenger");
        userMessage = new JTextField();
        userMessage.setEditable(false);
        userMessage.addActionListener(
                new ActionListener() {

                    public void actionPerformed(ActionEvent event) {
                        sendMessage(event.getActionCommand());
                        userMessage.setText("");
                    }
                });
        chatWindow.add(userMessage, BorderLayout.NORTH);
        chatBox = new JTextArea();
        chatWindow.add(new JScrollPane(chatBox));
        chatWindow.setSize(300, 180);
        chatWindow.setVisible(true);
    }

    public boolean isWindowVisible() {
        return chatWindow.isVisible();
    }

    public boolean isServerMessageVisible() {
        return userMessage.isVisible();
    }

    public void startRunning() {

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

    public String getLastReceivedMessage() {
        return lastReceivedMessage;
    }

    public synchronized void sendMessage(String message) {
        for (ClientConnection clientConnection : clientConnections) {
            try {
                clientConnection.getOutput().writeObject("ADMIN- " + message);
                clientConnection.getOutput().flush();
                showMessage("\nADMIN- " + message);

            } catch (IOException ioException) {
                chatBox.append("\nERROR: Can't send that message");
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

    public synchronized void showMessage(final String text) {
        SwingUtilities.invokeLater(
                new Runnable() {

                    public void run() {
                        chatBox.append(text);
                    }
                });
    }

    public void ableToType(final boolean tof) {
        SwingUtilities.invokeLater(
                new Runnable() {

                    public void run() {
                        userMessage.setEditable(tof);
                    }
                });
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
        for (ClientConnection clientConnection : clientConnections) {
            if (clientConnection != null && !clientConnection.getSocket().isClosed()) {
                ++count;
            }
        }
        return count;
    }
}
