package bjim.server;
import bjim.client.Client;

import org.junit.Assert;
import org.junit.Test;

import static bjim.server.Server.DEFAULT_PORT;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertTrue;

public class ServerTest {

	private static final int CUSTOM_PORT = 1234;

	@Test
	public void startServer() throws InterruptedException {

		// given
		Server server = new Server();

		// when
		server.startRunning();

		// then
		Thread.sleep(1000);
		assertTrue(server.isRunning());

		// after
		server.stopServer();
	}

	@Test
	public void stopServer() throws InterruptedException {

		// given
		Server server = new Server();
		server.startRunning();
		Thread.sleep(1000);

		// when
		server.stopServer();

		// then
		Thread.sleep(1000);
		assertFalse(server.isRunning());
	}

	@Test
	public void serverStartsOnDefaultPort() throws InterruptedException {

		// given
		Server server = new Server();

		// when
		server.startRunning();

		// then
		Thread.sleep(1000);
		assertEquals(DEFAULT_PORT, server.getPort());

		// after
		server.stopServer();
	}

	@Test
	public void serverStartsOnCustomPort() throws InterruptedException {

		// given
		Server server = new Server(CUSTOM_PORT);

		// before
		assertNotEquals("Precondition violated: `customPort` MUST NOT be equal to DEFAULT_PORT: " + DEFAULT_PORT,
				DEFAULT_PORT, CUSTOM_PORT);

		// when
		server.startRunning();

		// then
		Thread.sleep(1000);
		Assert.assertEquals(CUSTOM_PORT, server.getPort());

		// after code
		server.stopServer();
	}



	@Test
	public void checkConnectiontrue() throws InterruptedException {

		// given

		Server server = new Server();
		//Client cl = new Client("127.0.0.1");
		// when
		server.startRunning();
		Thread.sleep(6000);

		//then
		Assert.assertEquals(false, server.connected());

		//after
		server.stopServer();

	}



	@Test
	public void messagearecorrect() throws InterruptedException {
		// given

		Server server = new Server();
		Client cl = new Client("127.0.0.1");
		//
		// when
		server.startRunning();
		Thread.sleep(6000);

		cl.startRunning();
		Thread.sleep(6000);



		Assert.assertEquals("hi", server.servermessagereturn());
		server.stopServer();
	}



}