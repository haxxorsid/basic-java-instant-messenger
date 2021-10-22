package bjim.client;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.EOFException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;

public class Client {

	// client input/output channels
	private ObjectOutputStream output;
	private ObjectInputStream input;

	private String serverIP;

	// the socket where the client is connected
	private Socket clientSocket;

	// Chat attributes
	private JFrame chatWindow;
	private JTextField userMessage;
	private JTextArea chatBox;

	private ExecutorService executorService = Executors.newSingleThreadExecutor();

	public Client(String host) {
		serverIP = host;
		userMessage = new JTextField();
		userMessage.setEditable(false);
		userMessage.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent event) {
				sendMessage(event.getActionCommand());
				userMessage.setText("");

			}
		});

		chatWindow = new JFrame("Client!");
		chatWindow.add(userMessage, BorderLayout.NORTH);
		chatBox = new JTextArea();
		chatWindow.add(new JScrollPane(chatBox), BorderLayout.CENTER);
		chatWindow.setSize(300, 180);
		chatWindow.setVisible(true);
	}

	public void startRunning() {

		executorService.submit(new Runnable() {

			@Override
			public void run() {
				try {
					connectToServer();
					setupStreams();
					whileChatting();
				} catch (EOFException eofException) {
					showMessage("\n Client terminated the connection");

				} catch (IOException ioException) {
					ioException.printStackTrace();
				} finally {
					closeCrap();
				}
			}
		});
	}

	private void connectToServer() throws IOException {
		showMessage("Attempting connection");
		clientSocket = new Socket(InetAddress.getByName(serverIP), 6789);
		showMessage("\nConnected to" + clientSocket.getInetAddress()
				.getHostName());

	}

	private void setupStreams() throws IOException {
		output = new ObjectOutputStream(clientSocket.getOutputStream());
		output.flush();
		input = new ObjectInputStream(clientSocket.getInputStream());
		showMessage("\nStreams are now good to go!");

	}

	private void whileChatting() throws IOException {
		ableToType(true);
		String message = "";
		do {
			try {
				message = String.valueOf(input.readObject());
				showMessage("\n" + message);

			} catch (ClassNotFoundException classNotFoundException) {
				showMessage("\nDont know ObjectType!");
			}
		} while (!message.equals("\nADMIN - END"));
	}

	private void closeCrap() {
		showMessage("\nClosing down!");
		ableToType(false);
		try {
			output.close();
			input.close();
			clientSocket.close();
		} catch (IOException ioException) {
			ioException.printStackTrace();
		}
	}

	private void sendMessage(String message) {
		try {
			output.writeObject("USER - " + message);
			output.flush();
			showMessage("\nUSER - " + message);
		} catch (IOException ioException) {
			chatBox.append("\nSomething is messed up!");
		}

	}

	private void showMessage(final String m) {
		SwingUtilities.invokeLater(new Runnable() {

			public void run() {
				chatBox.append(m);
			}
		});
	}

	private void ableToType(final boolean tof) {
		SwingUtilities.invokeLater(new Runnable() {

			public void run() {
				userMessage.setEditable(tof);
			}
		});
	}

	public void setDefaultCloseOperation(int exitOnClose) {
		chatWindow.setDefaultCloseOperation(exitOnClose);
	}
}
