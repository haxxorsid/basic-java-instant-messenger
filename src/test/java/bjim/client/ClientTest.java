package bjim.client;

import static bjim.client.Client.LOCAL_HOST;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import bjim.server.Server;
import org.junit.Test;

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
        assertTrue(clientConnected);

        // after
        server.stopServer();
        client.stopClient();
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
        Server server = new Server();
        Client client = new Client("127.0.0.1");
        server.startRunning();
        Thread.sleep(1000);
        client.startRunning();
        Thread.sleep(1000);

        // when...then
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

        // when
        client.sendMessage("hi");

        // then
        Thread.sleep(500);
        assertEquals("USER - hi", server.getLastReceivedMessage());

        // after
        client.stopClient();
        server.stopServer();
    }

    @Test
    public void multipleClientGetsConnection() {

        // given
        Server server = new Server();
        Client client1 = new Client();
        Client client2 = new Client();
        server.startRunning();

        // when
        client1.startRunning();
        client2.startRunning();

        // then
        assertTrue(client1.isconnected());
        assertTrue(client2.isconnected());

        // after
        server.stopServer();
        client1.stopClient();
        client2.stopClient();
    }

    @Test
    public void MultipleClientSendMessagesToServer() {

        // given
        Server server = new Server();
        Client client1 = new Client();
        Client client2 = new Client();
        server.startRunning();
        client1.startRunning();
        client2.startRunning();

        // when...then
        client1.sendMessage("hi");
        assertEquals("USER1 - hi", server.getLastReceivedMessage());
        client2.sendMessage("hello");
        assertEquals("USER2 - hello", server.getLastReceivedMessage());

        // after
        client1.stopClient();
        client2.stopClient();
    }

    @Test
    public void serverSendsMessagesToBothClients() {

        // given
        Server server = new Server();
        Client client1 = new Client();
        Client client2 = new Client();
        server.startRunning();
        client1.startRunning();
        client2.startRunning();

        // when
        server.sendMessage("hi");

        // then
        assertEquals("ADMIN- hi", client1.getLastReceivedMessage());
        assertEquals("ADMIN- hi", client2.getLastReceivedMessage());

        // after
        client1.stopClient();
        client2.stopClient();
        server.stopServer();
    }

    @Test
    public void ServerShowsMessageOnClientDisconnection() {

        // given
        Server server = new Server();
        Client client1 = new Client();
        Client client2 = new Client();
        server.startRunning();
        client1.startRunning();
        client2.startRunning();

        // when...then
        client1.stopClient();
        assertEquals("client1 is disconnected", server.getLastReceivedMessage());

        client2.stopClient();
        assertEquals("client2 is disconnected", server.getLastReceivedMessage());

        // after
        server.stopServer();
    }
}
