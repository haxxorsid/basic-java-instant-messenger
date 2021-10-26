package bjim.server;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.EOFException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Server  {

	public static final int DEFAULT_PORT = 6789;

	// the port where the server is listening
	private final int port;

	// the socket where the server is listening
	private ServerSocket serverSocket;

	// the connection created once a client is accepted by the server
	private Socket clientConnection;

	// client input/output channels
	private ObjectOutputStream output;
	private ObjectInputStream input;

	// Chat attributes
	private JFrame chatWindow;
	private JTextField userMessage;
	private JTextArea chatBox;


	//checking variables

	public String msg;
    public boolean connected_sr_cl=false;

	public boolean windowvisible=false;
    public boolean userMessageVisible=false;


	//checking last received message from client to server
	private String lastReceivedMessagetoServer = "";


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
		userMessage.addActionListener(new ActionListener() {

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

		windowvisible=chatWindow.isVisible();
		userMessageVisible=userMessage.isVisible();
	}


	//check server window is Visible
	public boolean isWindowvisible()
	{
		return windowvisible;
	}


	//check user messagefrom server  is Visible
	public boolean isServerMessageVisible()
	{
		return userMessageVisible;
	}


	public void startRunning() {

		executorService.submit(new Runnable() {

			@Override
			public void run() {
				try {
					serverSocket = new ServerSocket(port, 100);
					while (true) {
						try {
							waitForConnection();
							setupStreams();
							whileChatting();
						} catch (EOFException eofException) {
							showMessage("\n Server ended the connection!");
						} finally {
							closeCrap();
						}
					}
				} catch (IOException ioException) {
					System.out.println("Stopping server: " + ioException.getMessage());
				}

			}
		});
	}

	public void waitForConnection() throws IOException {
		showMessage("Waiting for someone to connect!");
		clientConnection = serverSocket.accept();
		connected_sr_cl=true;
		showMessage("\nNow connected to" + clientConnection.getInetAddress()
				.getHostName() + " !");


	}



	//check server and client are connected

	public boolean connected()
	{
		return connected_sr_cl;
	}
	public void setupStreams() throws IOException {
		output = new ObjectOutputStream(clientConnection.getOutputStream());
		output.flush();
		input = new ObjectInputStream(clientConnection.getInputStream());
		showMessage("\nStreams are setup! \n");
	}


	public String servermsg="";
	public void whileChatting() throws IOException {
		String message = "\nYou are now connected!";
		sendMessage(message);
		ableToType(true);
		do {
			try {
				lastReceivedMessagetoServer = String.valueOf(input.readObject());
				//message = (String) input.readObject();
				//showMessage("\n" + message);
				showMessage("\n" + lastReceivedMessagetoServer);


			} catch (ClassNotFoundException classNotFoundException) {
				showMessage("\n I don't know what user send!");
			}
		} while (!lastReceivedMessagetoServer.equals("\nUSER-END"));
	}

	//added to show last received message
	public String getLastReceivedMessage() {
		return lastReceivedMessagetoServer;
	}



	public String servermessagereturn()
	{
		return servermsg;
	}

	public void sendMessage(String message) {

		try {
			output.writeObject("ADMIN- " + message);
			output.flush();
			showMessage("\nADMIN- " + message);

		} catch (IOException ioException) {
			chatBox.append("\nERROR: Can't send that message");
		}
	}


	public String getchatText()
	{
		//return chatBox.getText();
		//return msg;

		return userMessage.getText();
	}

	private void closeCrap() {
		showMessage("\n Closing connections \n");
		ableToType(false);
		try {
			if (output != null) {
				output.close();
			}
			if (input != null) {
				input.close();
			}
			if (clientConnection != null) {
				clientConnection.close();
			}
		} catch (IOException ioException) {
			ioException.printStackTrace();
		}
	}

	public void stopServer() {
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
		SwingUtilities.invokeLater(new Runnable() {

			public void run() {
				chatBox.append(text);
			}
		});
	}


	public  String messagefromserver()
	{
		return chatBox.getText();
	}

	public void ableToType(final boolean tof) {
		SwingUtilities.invokeLater(new Runnable() {

			public void run() {
				userMessage.setEditable(tof);

				//servermsg=userMessage.getText();
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

	public boolean isClientConnected() {
		return clientConnection != null && !clientConnection.isClosed();
	}
}
