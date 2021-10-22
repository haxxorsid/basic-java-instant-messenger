package bjim.client;

import bjim.server.Server;
import org.junit.Test;

import java.io.IOException;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import static org.junit.Assert.*;


public class Clienttestt {



    private CountDownLatch controlLatch;

    @Test
    public void ClientAppDoNotGetConnectionIfServerAppDonotStarts() throws InterruptedException, IOException {

        // given
        Client client = new Client("127.0.0.1");
        Server server = new Server();

        // when

        Thread.sleep(5000);

        client.startRunning();
        Thread.sleep(5000);
       client.checkconnection();

        // then

        assertEquals(0,client.running());

    }






    @Test
    public void CLientCanBeAbleToTypeIfClientGetConnectionToServer() throws InterruptedException, IOException {
        // given
        controlLatch = new CountDownLatch(2);

        Server server = new Server();
        Client client = new Client("127.0.0.1");

        // when
        server.startRunning();
        controlLatch.await(4, TimeUnit.SECONDS);
//then

    assertTrue(client.checktypestatus());


        // then





    }


}