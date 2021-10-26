package bjim.client;

import bjim.server.Server;
import org.junit.Assert;
import org.junit.Test;

import static bjim.client.Client.LOCAL_HOST;
import static org.junit.Assert.assertEquals;

public class ClientTest {

	@Test
	public void isClientConnected() throws InterruptedException {

		// given
		Server server = new Server();
		Client client = new Client();
		server.startRunning();
		Thread.sleep(1000);
		client.startRunning();
		Thread.sleep(1000);

		// when
		boolean clientConnected = server.isClientConnected();

		// then
		Assert.assertTrue(clientConnected);

		// after
		server.stopServer();
		client.stopClient();
	}

	@Test
	public void serverIPIsLocalHostByDefault() {

		// given
		Client client = new Client();

		// whe
		String serverIP = client.getServerIP();

		Assert.assertEquals(LOCAL_HOST, serverIP);
	}

	@Test //checkingVisibilityof ClientWindow
	public void windowIsVisibleduringstartTheclient() throws InterruptedException
	{

		// given
		// given
		Server server = new Server();
		Client client = new Client("127.0.0.1");
		server.startRunning();
		Thread.sleep(1000);
		client.startRunning();
		Thread.sleep(1000);




		assertEquals(true, client.isWindowvisibleclientSide());


		server.stopServer();

		client.stopClient();
	}


	@Test
	public void clientSendsAMessageAndServerReceivesIt() throws InterruptedException {
		// given
		Server server = new Server();
		Client client = new Client("127.0.0.1");
		server.startRunning();
		Thread.sleep(1000);
		client.startRunning();
		Thread.sleep(1000);

		//when
		client.sendMessage("hi");
		Thread.sleep(500);

		// then
		assertEquals("USER - hi", server.getLastReceivedMessage());
		//after
		server.stopServer();
		client.stopClient();



	}
}
