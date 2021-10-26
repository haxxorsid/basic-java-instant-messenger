package bjim.client;

import bjim.server.Server;
import org.junit.Assert;
import org.junit.Test;

import static bjim.client.Client.LOCAL_HOST;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

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

	@Test
	public void windowIsVisibleDuringStartTheClient() throws InterruptedException
	{

		// given
		Server server = new Server();
		Client client = new Client("127.0.0.1");
		server.startRunning();
		Thread.sleep(1000);
		client.startRunning();
		Thread.sleep(1000);

		assertTrue(client.isWindowVisibleClientSide());

		// after
		client.stopClient();
		server.stopServer();
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

		// then
		Thread.sleep(500);
		assertEquals("USER - hi", server.getLastReceivedMessage());

		//after
		client.stopClient();
		server.stopServer();
	}
	@Test
	public void multipleClientGetsConnection() throws InterruptedException {
		Server server=new Server();
		Client client1=new Client();
		Client client2=new Client();
		server.startRunning();
		client1.startRunning();
		client2.startRunning();
		assertTrue(client1.isconnected());
		assertTrue(client2.isconnected());
		server.stopServer();
		client1.stopClient();
		client2.stopClient();
	}

	@Test
	public void MultipleClientsendmessagetoServer() throws InterruptedException {
		Server server=new Server();
		Client client1=new Client();
		Client client2=new Client();
		server.startRunning();
		client1.startRunning();
		client2.startRunning();
		client1.sendMessage("hi");
		assertEquals("client1:hi",server.getLastReceivedMessage());
		client2.sendMessage("hello");
		assertEquals("client2:hello",server.getLastReceivedMessage());

		client1.stopClient();
		client2.stopClient();
	}
	@Test
	public void serversendmessagetobothclients() throws InterruptedException {
		Server server=new Server();
		Client client1=new Client();
		Client client2=new Client();
		server.startRunning();
		client1.startRunning();
		client2.startRunning();
	server.sendMessage("hi");
		assertEquals("server:hi",client1.getLastReceivedMessage());

		assertEquals("server:hi",client2.getLastReceivedMessage());

		client1.stopClient();
		client2.stopClient();
	}

	@Test
	public void ServerShowsMessageOnCLientDisconnection() throws InterruptedException
	{
		Server server=new Server();
		Client client1=new Client();
		Client client2=new Client();
		server.startRunning();
		client1.startRunning();
		client2.startRunning();
		client2.stopClient();
		assertEquals("client2 is disconnected",server.getLastReceivedMessage());
		server.stopServer();
		client1.stopClient();
		client2.stopClient();

	}
}
