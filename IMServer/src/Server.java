import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.EOFException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;

import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;

public class Server extends JFrame {

	private JTextField userMessage;
	private JTextArea chatBox;
	private ObjectOutputStream output;
	private ObjectInputStream input;
	private ServerSocket server;
	private Socket connection;

	public Server() {
		super("Instant Messenger");
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
		try {
			server = new ServerSocket(6789, 100);
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

	public void waitForConnection() throws IOException {
		showMessage("Waiting for someone to connect!");
		connection = server.accept();
		showMessage("\nNow connected to"
				+ connection.getInetAddress().getHostName() + " !");

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
			output.close();
			input.close();
			connection.close();
		} catch (IOException ioException) {
			ioException.printStackTrace();
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
}
