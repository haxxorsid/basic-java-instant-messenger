package bjim.client;

import bjim.common.AbstractChatWindow;

public class ClientChatWindow extends AbstractChatWindow {

    public ClientChatWindow() {
        this("Client");
    }

    public ClientChatWindow(String username) {
        super(username);
    }
}
