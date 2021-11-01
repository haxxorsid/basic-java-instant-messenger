package bjim.client;

import static bjim.client.Client.LOCAL_HOST;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import bjim.server.Server;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class ClientTest {

    private static final int WAIT_SECS = 100;
    Server server = new Server();

    @Before
    public void setUp() {
        server.startRunning();
    }

    @After
    public void tearDown() {
        server.stopRunning();
    }

    @Test
    public void numberOfConnectedClientsIsOne() throws InterruptedException {

        // given
        Client client = new Client();
        Thread.sleep(WAIT_SECS);
        client.startRunning();
        Thread.sleep(WAIT_SECS);

        // when...then
        assertEquals(1, server.numberOfClientsConnected());

        // after
        client.stopRunning();
    }

    @Test
    public void serverIPIsLocalHostByDefault() {

        // given
        Client client = new Client();

        // when
        String serverIP = client.getServerIP();

        // then
        assertEquals(LOCAL_HOST, serverIP);
    }

    @Test
    public void windowIsVisibleDuringStartTheClient() throws InterruptedException {

        // given
        Client client = new Client("127.0.0.1");
        Thread.sleep(WAIT_SECS);
        client.startRunning();
        Thread.sleep(WAIT_SECS);

        // when...then
        assertTrue(client.isWindowVisibleClientSide());

        // after
        client.stopRunning();
    }

    @Test
    public void clientSendsAMessageAndServerReceivesIt() throws InterruptedException {

        // given
        Client client = new Client("127.0.0.1");
        Thread.sleep(WAIT_SECS);
        client.startRunning();
        Thread.sleep(WAIT_SECS);

        // when
        client.sendMessage("hi");

        // then
        Thread.sleep(WAIT_SECS);
        assertEquals("USER - hi", server.getLastReceivedMessage());

        // after
        client.stopRunning();
    }

    @Test
    public void multipleClientsConnected() throws InterruptedException {

        // given
        Client client1 = new Client();
        Client client2 = new Client();

        // when
        client1.startRunning();
        client2.startRunning();
        Thread.sleep(WAIT_SECS);

        // then
        assertTrue(client1.isConnected());
        assertTrue(client2.isConnected());

        // after
        client1.stopRunning();
        client2.stopRunning();
    }

    @Test
    public void twoClientsSendMessagesToServer() throws InterruptedException {

        // given
        Client client1 = new Client();
        Client client2 = new Client();
        client1.startRunning();
        client2.startRunning();
        Thread.sleep(WAIT_SECS);

        // when...then
        client1.sendMessage("hi");
        Thread.sleep(WAIT_SECS);
        assertEquals("USER - hi", server.getLastReceivedMessage());

        client2.sendMessage("hello");
        Thread.sleep(WAIT_SECS);
        assertEquals("USER - hello", server.getLastReceivedMessage());

        // after
        client1.stopRunning();
        client2.stopRunning();
    }

    @Test
    public void serverSendsMessagesToTwoClients() throws InterruptedException {

        // given
        Client client1 = new Client();
        Client client2 = new Client();
        client1.startRunning();
        client2.startRunning();
        Thread.sleep(WAIT_SECS);

        // when
        server.sendMessage("hi");

        // then
        Thread.sleep(WAIT_SECS);
        assertEquals("ADMIN- hi", client1.getLastReceivedMessage());
        assertEquals("ADMIN- hi", client2.getLastReceivedMessage());

        // after
        client1.stopRunning();
        client2.stopRunning();
    }
}
