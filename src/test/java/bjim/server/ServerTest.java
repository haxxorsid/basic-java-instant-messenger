package bjim.server;

import org.junit.Test;

import static org.junit.Assert.assertTrue;

public class ServerTest {

	@Test
	public void startServer() throws InterruptedException {

		// given
		Server server = new Server();

		// when
		server.startRunning();

		// then
		Thread.sleep(5000);
		assertTrue(server.isActive());
	}
}