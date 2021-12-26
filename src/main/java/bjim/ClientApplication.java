package bjim;

import static javax.swing.WindowConstants.EXIT_ON_CLOSE;

import bjim.client.Client;

public class ClientApplication {

    public static void main(String[] args) {
        Client client = new Client();
        client.setDefaultCloseOperation(EXIT_ON_CLOSE);
        client.startRunning();
    }
}
