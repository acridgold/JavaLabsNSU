package chat.serialized;

import chat.common.ChatMessage;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

public class ChatClientSerialize {

    private ServerConnectionSerialize connection;
    private String myName;

    private JFrame mainFrame;
    private JPanel messagesPanel;
    private JScrollPane messagesScroll;
    private JTextField messageField;
    private JTextArea userListArea;

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new ChatClientSerialize().showLoginDialog());
    }

    private void showLoginDialog() {
        JTextField hostField = new JTextField("localhost", 15);
        JTextField portField = new JTextField("8888", 6);
        JTextField nameField = new JTextField(15);

        JPanel panel = new JPanel(new GridLayout(3, 2, 5, 5));
        panel.add(new JLabel("Сервер:"));
        panel.add(hostField);
        panel.add(new JLabel("Порт:"));
        panel.add(portField);
        panel.add(new JLabel("Ник:"));
        panel.add(nameField);

        int result = JOptionPane.showConfirmDialog(
                null, panel, "Подключение к чату",
                JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

        if (result != JOptionPane.OK_OPTION) {
            System.exit(0);
        }

        String host = hostField.getText().trim();
        String name = nameField.getText().trim();
        int port;
        try {
            port = Integer.parseInt(portField.getText().trim());
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(null, "Неверный порт!");
            showLoginDialog();
            return;
        }
        if (name.isEmpty()) {
            JOptionPane.showMessageDialog(null, "Введите ник!");
            showLoginDialog();
            return;
        }
        connectToServer(host, port, name);
    }

    private void connectToServer(String host, int port, String name) {
        this.myName = name;
        connection = new ServerConnectionSerialize();
        connection.setOnMessage(this::onMessage);
        connection.setOnError(err ->
                SwingUtilities.invokeLater(() ->
                        JOptionPane.showMessageDialog(mainFrame, err, "Ошибка",
                                JOptionPane.ERROR_MESSAGE)));

        boolean ok = connection.connect(host, port, name, "SwingSerializeClient");
        if (!ok) {
            showLoginDialog();
            return;
        }
        showMainWindow(name);
    }

    private void showMainWindow(String myName) {
        mainFrame = new JFrame("Чат — " + myName);
        mainFrame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        mainFrame.setSize(750, 520);
        mainFrame.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                connection.logout();
                System.exit(0);
            }
        });

        // --- центр ---
        messagesPanel = new JPanel();
        messagesPanel.setLayout(new BoxLayout(messagesPanel, BoxLayout.Y_AXIS));
        messagesPanel.setBackground(Color.WHITE);
        messagesPanel.setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 8));

        messagesScroll = new JScrollPane(messagesPanel);
        messagesScroll.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY));
        messagesScroll.getVerticalScrollBar().setUnitIncrement(12);

        messageField = new JTextField();
        JButton sendBtn = new JButton("Отправить");
        messageField.addActionListener(_ -> sendMessage());
        sendBtn.addActionListener(_ -> sendMessage());

        JPanel inputPanel = new JPanel(new BorderLayout(4, 0));
        inputPanel.add(messageField, BorderLayout.CENTER);
        inputPanel.add(sendBtn, BorderLayout.EAST);

        JPanel centerPanel = new JPanel(new BorderLayout(0, 4));
        centerPanel.add(messagesScroll, BorderLayout.CENTER);
        centerPanel.add(inputPanel, BorderLayout.SOUTH);

        // --- левая панель ---
        JLabel logoLabel = new JLabel("Better_MAX");
        logoLabel.setFont(new Font("SansSerif", Font.BOLD, 13));
        logoLabel.setBorder(BorderFactory.createEmptyBorder(0, 0, 6, 0));

        userListArea = new JTextArea();
        userListArea.setEditable(false);

        JButton refreshBtn = new JButton("Обновить");
        refreshBtn.addActionListener(_ -> connection.requestUserList());

        JPanel topLeft = new JPanel(new BorderLayout());
        topLeft.add(logoLabel, BorderLayout.NORTH);
        topLeft.add(new JLabel("Участники:"), BorderLayout.SOUTH);

        JPanel leftPanel = new JPanel(new BorderLayout(0, 4));
        leftPanel.setBorder(BorderFactory.createEmptyBorder(4, 4, 4, 4));
        leftPanel.add(topLeft, BorderLayout.NORTH);
        leftPanel.add(new JScrollPane(userListArea), BorderLayout.CENTER);
        leftPanel.add(refreshBtn, BorderLayout.SOUTH);
        leftPanel.setPreferredSize(new Dimension(160, 0));

        mainFrame.add(leftPanel, BorderLayout.WEST);
        mainFrame.add(centerPanel, BorderLayout.CENTER);
        mainFrame.setLocationRelativeTo(null);
        mainFrame.setVisible(true);

        connection.requestUserList();
    }

    private void sendMessage() {
        String text = messageField.getText().trim();
        if (!text.isEmpty()) {
            connection.sendMessage(text);
            messageField.setText("");
        }
    }

    private void onMessage(ChatMessage msg) {
        SwingUtilities.invokeLater(() -> {
            if ("event".equals(msg.getType())) {
                switch (msg.getEvent() != null ? msg.getEvent() : "") {
                    case "message" -> appendMessage(msg.getName(), msg.getMessage());
                    case "userlogin" -> appendSystem(msg.getName() + " вошёл в чат");
                    case "userlogout" -> appendSystem(msg.getName() + " покинул чат");
                }
            }
            if ("success".equals(msg.getType()) && msg.getMessage() != null) {
                StringBuilder sb = new StringBuilder();
                for (String n : msg.getMessage().split(","))
                    if (!n.isBlank()) sb.append(n.trim()).append("\n");
                userListArea.setText(sb.toString());
            }
        });
    }

    private void appendMessage(String sender, String text) {
        boolean isMe = myName.equals(sender);

        JPanel row = new JPanel(new BorderLayout());
        row.setOpaque(false);
        row.setBorder(BorderFactory.createEmptyBorder(2, 0, 2, 0));

        String html = "<html><b>" + (isMe ? "Я" : sender) + ":</b> " +
                text.replace("&", "&amp;").replace("<", "&lt;") + "</html>";
        JLabel lbl = new JLabel(html);
        lbl.setBorder(BorderFactory.createEmptyBorder(4, 8, 4, 8));

        row.add(lbl, isMe ? BorderLayout.EAST : BorderLayout.WEST);

        messagesPanel.add(row);
        messagesPanel.revalidate();
        scrollToBottom();
    }

    private void appendSystem(String text) {
        JPanel row = new JPanel(new FlowLayout(FlowLayout.CENTER));
        row.setOpaque(false);
        JLabel lbl = new JLabel("<html><i><font color='gray'>" + text + "</font></i></html>");
        row.add(lbl);
        row.setMaximumSize(new Dimension(Integer.MAX_VALUE, row.getPreferredSize().height + 6));

        messagesPanel.add(row);
        messagesPanel.revalidate();
        scrollToBottom();
    }

    private void scrollToBottom() {
        SwingUtilities.invokeLater(() -> {
            JScrollBar sb = messagesScroll.getVerticalScrollBar();
            sb.setValue(sb.getMaximum());
        });
    }
}