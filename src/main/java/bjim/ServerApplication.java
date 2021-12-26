package bjim;

import static javax.swing.WindowConstants.EXIT_ON_CLOSE;

import bjim.server.Server;

public class ServerApplication {

    public static void main(String[] args) {
        Server server = new Server();
        server.setDefaultCloseOperation(EXIT_ON_CLOSE);
        server.startRunning();
    }
}
