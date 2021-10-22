package bjim.server;

import bjim.client.Client;
import org.junit.Assert;
import org.junit.Test;

import java.io.IOException;

import static bjim.server.Server.DEFAULT_PORT;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertTrue;

public class ServerTest {

	private static final int CUSTOM_PORT = 1234;



	@Test
	public void server_accept_client_request_to_connect() throws InterruptedException, IOException {

		// given
		Server server = new Server();
		Client client = new Client("127.0.0.1");

		// before
		server.startRunning();

		Thread.sleep(3000);
		client.connectToServer();
		Thread.sleep(3000);

		// then

assertTrue(server.serversocketcondition());
		// after
		server.stopServer();
	}

	@Test
	public void server_send_wlcome_message() throws InterruptedException
	{    //given
		Server server = new Server();
		server.startRunning();
         //then
		Thread.sleep(5000);
		assertEquals("Waiting for someone to connect!", server.gettext());
		//after
		server.stopServer();

	}

	@Test
	public void ServerCannotWriteIfClientIsNotConnected() throws InterruptedException
	{

		// given

		Server server = new Server();
		Client client = new Client("127.0.0.1");

		// when
		server.startRunning();

     //then
		Thread.sleep(6000);
		assertFalse(server.abletowrite());



		// after
		server.stopServer();





	}
	@Test
	public void ServerCannotWriteIfServerappStartsAfterClientappStarts() throws InterruptedException {

		// given

		Server server = new Server();
		Client client = new Client("127.0.0.1");
		client.startRunning();
		Thread.sleep(6000);
		// when
		server.startRunning();


		Thread.sleep(6000);
		assertFalse(server.abletowrite());



		// then





	}



}