package bjim.common;

import static java.awt.BorderLayout.NORTH;
import static java.awt.Font.BOLD;
import static javax.swing.SwingUtilities.invokeLater;

import java.awt.*;
import java.awt.event.ActionListener;
import javax.swing.*;
import javax.swing.border.BevelBorder;
import lombok.Getter;

public class AbstractChatWindow {

    private static final int FONT_SIZE = 18;
    private static final int WINDOW_WIDTH = 500;
    private static final int WINDOW_HEIGHT = 300;
    private static final String FONT_NAME = "Segoe Script";

    protected final JFrame chatWindow;
    protected final JTextField userInput;
    protected final JTextArea chatText;
    protected final JLabel status;

    @Getter private final String username;

    public AbstractChatWindow(String username) {

        this.username = username;

        chatWindow = new JFrame(username);
        chatWindow.setLayout(new BorderLayout());

        userInput = new JTextField();
        chatWindow.add(userInput, NORTH);

        JPanel statusPanel = new JPanel();
        statusPanel.setPreferredSize(new Dimension(WINDOW_WIDTH, 25));
        statusPanel.setBorder(new BevelBorder(BevelBorder.LOWERED));
        status = new JLabel("");
        statusPanel.add(status);

        chatWindow.add(statusPanel, BorderLayout.SOUTH);

        chatText = createChatText();
        JScrollPane comp = new JScrollPane(chatText);
        comp.setSize(WINDOW_WIDTH, WINDOW_HEIGHT);
        comp.setEnabled(false);
        chatWindow.add(comp, status.getY() + status.getSize().height);

        chatWindow.setSize(WINDOW_WIDTH, WINDOW_HEIGHT);
        chatWindow.setVisible(true);
    }

    private JTextArea createChatText() {
        JTextArea textArea = new JTextArea();
        Font font = new Font(FONT_NAME, BOLD, FONT_SIZE);
        textArea.setFont(font);
        textArea.setEditable(false);
        return textArea;
    }

    public void setStatus(String statusText) {
        status.setText(statusText);
    }

    public void onSend(ActionListener actionListener) {
        userInput.addActionListener(
                e -> {
                    actionListener.actionPerformed(e);
                    userInput.setText("");
                });
    }

    public void append(String s) {
        chatText.append(s);
    }

    public boolean isVisible() {
        return chatWindow.isVisible();
    }

    public void showMessage(final String text) {
        invokeLater(() -> chatText.append(text));
        invokeLater(() -> chatText.append("\n___________________________________"));
    }

    public void ableToType(final boolean tof) {
        invokeLater(() -> userInput.setEditable(tof));
    }

    public void setDefaultCloseOperation(int exitOnClose) {
        chatWindow.setDefaultCloseOperation(exitOnClose);
    }
}
