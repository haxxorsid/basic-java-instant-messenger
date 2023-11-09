package bjim;

import bjim.server.Server;

import javax.swing.*;

public class ServerApplication {

	public static void main(String[] args) {

		// run server
		Server admin = new Server();
		admin.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		admin.startRunning();
	}
}
