package bjim.common;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import lombok.Getter;

@Getter
public class Connection {

    private final Socket socket;
    private final ObjectOutputStream output;
    private final ObjectInputStream input;

    public Connection(Socket socket) throws IOException {
        this.socket = socket;
        this.output = new ObjectOutputStream(socket.getOutputStream());
        this.output.flush();
        this.input = new ObjectInputStream(socket.getInputStream());
    }

    public String getHostName() {
        return socket.getInetAddress().getHostName();
    }

    public void close() throws IOException {
        if (getOutput() != null) {
            getOutput().close();
        }
        if (getInput() != null) {
            getInput().close();
        }
        if (getSocket() != null) {
            getSocket().close();
        }
    }

    public boolean isClosed() {
        return socket == null || socket.isClosed();
    }

    public boolean isConnected() {
        return socket != null && socket.isConnected();
    }
}
