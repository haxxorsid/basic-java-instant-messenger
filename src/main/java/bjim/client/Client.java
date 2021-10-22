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
import java.net.UnknownHostException;

import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;


public class Client extends JFrame {
	public JTextField userMessage;
	private JTextArea chatBox;
	private ObjectOutputStream output;
	private ObjectInputStream input;
	private String message = "";
	private String serverIP;
	private Socket connection;
	public boolean type;
	public int w;
	int checkstatus=0;
	public  boolean tow;
	public Client(String host) {
		super("Client!");
		serverIP = host;
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
		add(new JScrollPane(chatBox), BorderLayout.CENTER);
		setSize(300, 180);
		setVisible(true);

	}

	public void startRunning() {

		try {
			connectToServer();
			setupStreams();
			whileChatting();

		} catch (EOFException eofException) {
			showMessage("\n Client terminated the connection");


		} catch (IOException ioException) {
			ioException.printStackTrace();
		}
	}

	public void connectToServer() throws IOException {
		showMessage("Attempting connection");
		connection = new Socket(InetAddress.getByName(serverIP), 6789);
		System.out.println(connection);
		showMessage("\nConnected to" + connection.getInetAddress().getHostName());

	}

	private void setupStreams() throws IOException {
		output = new ObjectOutputStream(connection.getOutputStream());
		output.flush();
		input = new ObjectInputStream(connection.getInputStream());
		showMessage("\nStreams are now good to go!");

	}

	private void whileChatting() throws IOException {
		ableToType(true);

		do {
			try {
				message = (String) input.readObject();
				showMessage("\n" + message);

			} catch (ClassNotFoundException classNotFoundException) {
				showMessage("\nDont know ObjectType!");
			}
		} while (!message.equals("\nADMIN - END"));
	}

	public void closeCrap() {
		showMessage("\nClosing down!");
		ableToType(false);

		try {
			output.close();
			input.close();
			connection.close();

		} catch (IOException ioException) {
			ioException.printStackTrace();
		}
	}

	public void sendMessage(String message){
		try{
			output.writeObject("USER - "+ message);
			output.flush();
			showMessage("\nUSER - "+message);
		}catch(IOException ioException){
			chatBox.append("\nSomething is messed up!");
		}

	}
	public void checkconnection() throws IOException {
		try{if (connection.isClosed() ) {
			checkstatus=0;
		}
		else  checkstatus=1;}
		catch (Exception e) {
			e.printStackTrace();
		}

	}
	public int running()
	{
		if (checkstatus==1)
			return 1;
		else return 0;
	}

	public void showMessage(final String m){
		SwingUtilities.invokeLater(new Runnable(){
			public void run(){
				chatBox.append(m);
			}
		});
	}
	public void ableToType(final boolean tof){
		editable (tof);
		SwingUtilities.invokeLater(new Runnable(){


			public void run()
			{
				userMessage.setEditable(tof);
				if(userMessage.isEditable())
				{System.out.println("it is true");
					checkstatus=1;}

			}
		});
	}


	public boolean chek( )
	{if(userMessage.isEnabled()==true)
		return true;
	else return false;}



	public boolean checktypestatus() throws IOException {
		showMessage("Attempting connection");
		connection = new Socket(InetAddress.getByName(serverIP), 6789);
		System.out.println(connection);
		showMessage("\nConnected to" + connection.getInetAddress().getHostName());
		output = new ObjectOutputStream(connection.getOutputStream());
		output.flush();
		input = new ObjectInputStream(connection.getInputStream());
		showMessage("\nStreams are now good to go!");
		ableToType(true);


		if(userMessage.isEnabled()==true)
			return true;
		else return false;
	}


	public void editable(boolean s)
	{tow=s;

	}
	public boolean get()
	{return tow;}





	public String gettext()
	{
		return chatBox.getText();
	}

}
