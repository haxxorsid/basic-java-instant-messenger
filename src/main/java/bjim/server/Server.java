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

public class Server extends JFrame {

	public static final int DEFAULT_PORT = 6789;

	private JTextField userMessage;
	private JTextArea chatBox;
	private ObjectOutputStream output;
	private ObjectInputStream input;
	private ServerSocket server;
	private Socket connection;

	private final int port;

	private ExecutorService executorService = Executors.newSingleThreadExecutor();

	public Server() {
		this(DEFAULT_PORT);
	}

	public Server(int port) {
		super("Instant Messenger");
		this.port = port;
		userMessage = new JTextField();
		userMessage.setEditable(false);
		userMessage.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent event) {
				sendMessage(event.getActionCommand());
				userMessage.setText("");
			}
		});
		add(userMessage, BorderLayout.NORTH);
		chatBox = new JTextArea();
		add(new JScrollPane(chatBox));
		setSize(300, 180);
		setVisible(true);
	}

	public void startRunning() {

		executorService.submit(new Runnable() {

			@Override
			public void run() {
				try {
					server = new ServerSocket(port, 100);
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
					ioException.printStackTrace();
				}

			}
		});
	}

	public void waitForConnection() throws IOException {
		showMessage("Waiting for someone to connect!");
		connection = server.accept();
		showMessage("\nNow connected to" + connection.getInetAddress()
				.getHostName() + " !");

	}

	public void setupStreams() throws IOException {
		output = new ObjectOutputStream(connection.getOutputStream());
		output.flush();
		input = new ObjectInputStream(connection.getInputStream());
		showMessage("\nStreams are setup! \n");
	}

	public void whileChatting() throws IOException {
		String message = "\nYou are now connected!";
		sendMessage(message);
		ableToType(true);
		do {
			try {
				message = (String) input.readObject();
				showMessage("\n" + message);
			} catch (ClassNotFoundException classNotFoundException) {
				showMessage("\n I don't know what user send!");
			}
		} while (!message.equals("\nUSER-END"));
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

	public void closeCrap() {
		showMessage("\n Closing connections \n");
		ableToType(false);
		try {
			if (output != null) {
				output.close();
			}
			if (input != null) {
				input.close();
			}
			if (connection != null) {
				connection.close();
			}
		} catch (IOException ioException) {
			ioException.printStackTrace();
		}
	}

	public void stopServer() {
		System.out.println("Stopping server...");
		while (!server.isClosed()) {
			try {
				server.close();
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

	public void ableToType(final boolean tof) {
		SwingUtilities.invokeLater(new Runnable() {

			public void run() {
				userMessage.setEditable(tof);
			}
		});
	}

	public int getPort() {
		return server.getLocalPort();
	}
}
