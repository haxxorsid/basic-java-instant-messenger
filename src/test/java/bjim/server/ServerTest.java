package bjim.server;

import static bjim.server.Server.DEFAULT_PORT;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertTrue;

import bjim.client.Client;
import org.junit.Test;

public class ServerTest {

    private static final int CUSTOM_PORT = 1234;

    // Test by Saleh
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
        server.stopRunning();
    }

    @Test
    public void stopServer() throws InterruptedException {

        // given
        Server server = new Server();
        server.startRunning();
        Thread.sleep(1000);

        // when
        server.stopRunning();

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
        server.stopRunning();
    }

    @Test
    public void serverStartsOnCustomPort() throws InterruptedException {

        // given
        Server server = new Server(CUSTOM_PORT);

        // before
        assertNotEquals(
                "Precondition violated: `customPort` MUST NOT be equal to DEFAULT_PORT: "
                        + DEFAULT_PORT,
                DEFAULT_PORT,
                CUSTOM_PORT);

        // when
        server.startRunning();

        // then
        Thread.sleep(1000);
        assertEquals(CUSTOM_PORT, server.getPort());

        // after code
        server.stopRunning();
    }

    @Test
    public void numberOfConnectedClientsIsZero() throws InterruptedException {

        // given
        Server server = new Server();

        // when
        server.startRunning();
        Thread.sleep(1000);

        // then
        assertEquals(0, server.numberOfClientsConnected());

        // after
        server.stopRunning();
    }

    @Test
    public void serverSendsAMessageAndClientReceivesIt() throws InterruptedException {

        // given
        Server server = new Server();
        Client client = new Client("127.0.0.1");
        server.startRunning();
        Thread.sleep(1000);
        client.startRunning();
        Thread.sleep(1000);

        // when
        server.sendMessage("hi");
        Thread.sleep(500);

        // then
        assertEquals("ADMIN- hi", client.getLastReceivedMessage());
        // after
        server.stopRunning();
        client.stopRunning();
    }

    @Test
    public void serverUserMessageVisibleTrue() throws InterruptedException {
        // given
        Server server = new Server();
        server.startRunning();
        Thread.sleep(1000);

        // then
        assertEquals(true, server.isServerMessageVisible());

        // after
        server.stopRunning();
    }

    @Test
    public void windowIsVisibleduringWhenstartTheServer() throws InterruptedException {
        // given
        Server server = new Server();
        server.startRunning();
        Thread.sleep(1000);

        // then
        assertEquals(true, server.isWindowVisible());

        // after
        server.stopRunning();
    }
}
