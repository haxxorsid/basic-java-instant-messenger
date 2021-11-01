package bjim.server;

import java.awt.*;
import java.awt.event.ActionListener;
import javax.swing.*;

public class ServerChatWindow {

    private final JFrame chatWindow;
    private final JTextField userMessage;
    private final JTextArea chatBox;

    public ServerChatWindow() {
        this("Instant Messenger");
    }

    public ServerChatWindow(String username) {
        chatWindow = new JFrame(username);
        userMessage = new JTextField();
        userMessage.setEditable(false);
        chatWindow.add(userMessage, BorderLayout.NORTH);
        chatBox = new JTextArea();
        chatWindow.add(new JScrollPane(chatBox));
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

    public void append(String s) {
        chatBox.append(s);
    }

    public boolean isVisible() {
        return chatWindow.isVisible();
    }

    public void showMessage(final String text) {
        SwingUtilities.invokeLater(() -> chatBox.append(text));
    }

    public void ableToType(final boolean tof) {
        SwingUtilities.invokeLater(() -> userMessage.setEditable(tof));
    }

    public void setDefaultCloseOperation(int exitOnClose) {
        chatWindow.setDefaultCloseOperation(exitOnClose);
    }

    public boolean isUserMessageVisible() {
        return userMessage.isVisible();
    }
}
