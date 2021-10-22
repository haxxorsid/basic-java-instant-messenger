package bjim.client;

import bjim.server.Server;
import org.junit.Assert;
import org.junit.Test;

//import static bjim.client.Client.LOCAL_HOST;
import static org.junit.Assert.assertEquals;

public class ClientTest
{

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
