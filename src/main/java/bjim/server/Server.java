package bjim.server;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.EOFException;
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
    private ExecutorService executorService = Executors.newSingleThreadExecutor();

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

        executorService.submit(
                new Runnable() {

                    @Override
                    public void run() {
                        try {
                            serverSocket = new ServerSocket(port, 100);
                            while (true) {
                                try {
                                    ClientConnection clientConnection = waitForConnection();
                                    whileChatting(clientConnection);
                                } catch (EOFException eofException) {
                                    showMessage("\n Server ended the connection!");
                                } finally {
                                    disconnectClients();
                                }
                            }
                        } catch (IOException ioException) {
                            System.out.println("Stopping server: " + ioException.getMessage());
                        }
                    }
                });
    }

    private ClientConnection waitForConnection() throws IOException {
        showMessage("Waiting for someone to connect!");
        ClientConnection clientConnection = new ClientConnection(serverSocket.accept());
        clientConnections.add(clientConnection);
        showMessage(
                "\nNow connected to: "
                        + clientConnection.getSocket().getInetAddress().getHostName()
                        + " !");
        return clientConnection;
    }

    private void whileChatting(ClientConnection clientConnection) throws IOException {
        String message = "\nYou are now connected!";
        sendMessage(message);
        ableToType(true);
        do {
            try {
                lastReceivedMessage = String.valueOf(clientConnection.getInput().readObject());
                showMessage("\n" + lastReceivedMessage);

            } catch (ClassNotFoundException classNotFoundException) {
                showMessage("\n I don't know what user send!");
            }
        } while (!lastReceivedMessage.equals("\nUSER-END"));
    }

    public String getLastReceivedMessage() {
        return lastReceivedMessage;
    }

    public void sendMessage(String message) {
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
        showMessage("\n Closing connections \n");
        ableToType(false);

        for (ClientConnection clientConnection : clientConnections) {
            try {
                if (clientConnection.getOutput() != null) {
                    clientConnection.getOutput().close();
                }
                if (clientConnection.getInput() != null) {
                    clientConnection.getInput().close();
                }
                if (clientConnection.getSocket() != null) {
                    clientConnection.getSocket().close();
                }
            } catch (IOException ioException) {
                ioException.printStackTrace();
            }
        }
        clientConnections.clear();
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

    public void showMessage(final String text) {
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
