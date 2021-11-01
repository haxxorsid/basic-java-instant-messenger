package bjim.client;

import static java.awt.BorderLayout.*;
import static java.awt.BorderLayout.NORTH;

import java.awt.*;
import java.awt.event.ActionListener;
import javax.swing.*;

public class ClientChatWindow {

    private final JFrame chatWindow;
    private final JTextField userMessage;
    private final JTextArea chatBox;

    public ClientChatWindow() {
        this("Client!");
    }

    public ClientChatWindow(String username) {
        userMessage = new JTextField();
        userMessage.setEditable(false);
        chatWindow = new JFrame(username);
        chatWindow.add(userMessage, NORTH);
        chatBox = new JTextArea();
        chatWindow.add(new JScrollPane(chatBox), CENTER);
        chatWindow.setSize(300, 180);
        chatWindow.setVisible(true);
    }

    public void onSend(ActionListener actionListener) {
        userMessage.addActionListener(
                e -> {
                    actionListener.actionPerformed(e);
                    userMessage.setText("");
                });
    }

    public void showMessage(final String m) {
        SwingUtilities.invokeLater(() -> chatBox.append(m));
    }

    public void ableToType(final boolean tof) {
        SwingUtilities.invokeLater(() -> userMessage.setEditable(tof));
    }

    public void setDefaultCloseOperation(int exitOnClose) {
        chatWindow.setDefaultCloseOperation(exitOnClose);
    }

    public boolean isVisible() {
        return chatWindow.isVisible();
    }

    public void append(String s) {
        chatBox.append(s);
    }
}
