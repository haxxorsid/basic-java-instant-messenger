package bjim.client;

import java.io.EOFException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Client {

    public static final String LOCAL_HOST = "127.0.0.1";

    private final ClientChatWindow clientChatWindow;
    private String host;
    private Socket clientSocket;
    private ObjectOutputStream output;
    private ObjectInputStream input;

    private String lastReceivedMessage = "";

    private ExecutorService executorService = Executors.newSingleThreadExecutor();

    public Client() {
        this(new ClientChatWindow());
    }

    public Client(String host) {
        this(host, new ClientChatWindow());
    }

    public Client(ClientChatWindow clientChatWindow) {
        this(LOCAL_HOST, clientChatWindow);
    }

    public Client(String host, ClientChatWindow clientChatWindow) {
        this.host = host;
        this.clientChatWindow = clientChatWindow;
        this.clientChatWindow.onSend(event -> sendMessage(event.getActionCommand()));
    }

    public boolean isWindowVisibleClientSide() {
        return clientChatWindow.isVisible();
    }

    public void startRunning() {

        executorService.submit(
                new Runnable() {

                    @Override
                    public void run() {
                        try {
                            connectToServer();
                            setupStreams();
                            whileChatting();
                        } catch (EOFException eofException) {
                            showMessage("\n Client terminated the connection");

                        } catch (IOException ioException) {
                            System.out.println("Stopping client: " + ioException.getMessage());
                        } finally {
                            disconnect();
                        }
                    }
                });
    }

    public String getLastReceivedMessage() {
        return lastReceivedMessage;
    }

    private void connectToServer() throws IOException {
        showMessage("Attempting connection");
        clientSocket = new Socket(InetAddress.getByName(host), 6789);
        showMessage("\nConnected to: " + clientSocket.getInetAddress().getHostName());
    }

    private void setupStreams() throws IOException {
        output = new ObjectOutputStream(clientSocket.getOutputStream());
        output.flush();
        input = new ObjectInputStream(clientSocket.getInputStream());
        showMessage("\nStreams are now good to go!");
    }

    private void whileChatting() throws IOException {
        ableToType(true);
        do {
            try {
                lastReceivedMessage = String.valueOf(input.readObject());
                showMessage("\n" + lastReceivedMessage);

            } catch (ClassNotFoundException classNotFoundException) {
                showMessage("\nDont know ObjectType!");
            }
        } while (!lastReceivedMessage.equals("\nADMIN - END"));
    }

    private void disconnect() {
        showMessage("\nClosing down!");
        ableToType(false);
        try {
            if (output != null) {
                output.close();
            }
            if (input != null) {
                input.close();
            }
            if (clientSocket != null) {
                clientSocket.close();
            }
        } catch (IOException ioException) {
            ioException.printStackTrace();
        }
    }

    public void stopRunning() {
        System.out.println("Stopping client...");
        while (clientSocket != null && !clientSocket.isClosed()) {
            try {
                clientSocket.close();
                return;
            } catch (IOException e) {
                System.out.println("Failed to stop client...");
            }
        }
    }

    public void sendMessage(String message) {

        if (output == null) {
            return;
        }

        try {
            String messageToSend = "USER - " + message;
            output.writeObject(messageToSend);
            output.flush();
            showMessage("\n" + messageToSend);
        } catch (IOException ioException) {
            clientChatWindow.append("\nSomething is messed up!");
        }
    }

    private void showMessage(final String m) {
        clientChatWindow.showMessage(m);
    }

    private void ableToType(final boolean tof) {
        clientChatWindow.ableToType(tof);
    }

    public void setDefaultCloseOperation(int exitOnClose) {
        clientChatWindow.setDefaultCloseOperation(exitOnClose);
    }

    public String getServerIP() {
        return host;
    }

    public boolean isConnected() {
        return clientSocket != null && clientSocket.isConnected();
    }
}
