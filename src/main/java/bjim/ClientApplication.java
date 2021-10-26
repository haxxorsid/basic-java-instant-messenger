package bjim;

import bjim.client.Client;
import javax.swing.*;

public class ClientApplication {

    public static void main(String[] args) {

        // run client
        Client user;
        user = new Client("127.0.0.1");
        user.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        user.startRunning();
    }
}
