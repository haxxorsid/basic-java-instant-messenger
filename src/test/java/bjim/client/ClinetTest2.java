package bjim.client;

import bjim.server.Server;
import org.junit.Test;
import javax.swing.JFrame;
import java.io.IOException;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import static org.junit.Assert.*;


public class ClinetTest2 {


    public void testcheckcl() throws InterruptedException, IOException
    {
        Client user;
        user = new Client("127.0.0.1");
        user.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        user.startRunning();
    }


}
