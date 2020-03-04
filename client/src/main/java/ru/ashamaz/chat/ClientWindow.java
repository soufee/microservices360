package ru.ashamaz.chat;

import ru.ashamaz.network.TCPConnection;
import ru.ashamaz.network.TCPConnectionListener;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;

public class ClientWindow extends JFrame implements ActionListener, TCPConnectionListener {
    private static final String IP_ADDR = "127.0.0.1";
    private static final int PORT = 8189;
    private static final int WIDTH = 600;
    private static final int HEIGHT = 400;

    private final JTextArea log = new JTextArea();
    private final JTextField fieldNickName = new JTextField("unknown");
    private final JTextField fieldInput = new JTextField();

    private TCPConnection connection;

    private ClientWindow() {
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setSize(WIDTH, HEIGHT);
        setLocationRelativeTo(null);
        setAlwaysOnTop(true);
        log.setEditable(false);
        log.setLineWrap(true);

        fieldInput.addActionListener(this);

        add(log, BorderLayout.CENTER);
        add(fieldInput, BorderLayout.SOUTH);
        add(fieldNickName, BorderLayout.NORTH);

        setVisible(true);

        try {
            connection = new TCPConnection(this, IP_ADDR, PORT);
        } catch (IOException e) {
            printMessage("Connection exception: " + e);
        }
    }


    public static void main(String[] args) {
        SwingUtilities.invokeLater(ClientWindow::new);
    }

    private synchronized void printMessage(String msg) {
        SwingUtilities.invokeLater(() -> {
            log.append(msg + "\n");
            log.setCaretPosition(log.getDocument().getLength());
        });
    }

    @Override
    public void actionPerformed(ActionEvent actionEvent) {
        String msg = fieldInput.getText();
        if (msg.isEmpty()) return;
        fieldInput.setText(null);
        connection.sendString(fieldNickName.getText()+": "+msg);
    }

    @Override
    public void onConnectionReady(TCPConnection tcpConnection) {
        printMessage("Connection ready...");

    }

    @Override
    public void onDisconnect(TCPConnection tcpConnection) {
        printMessage("Connection closed...");

    }

    @Override
    public void onReceiveString(TCPConnection tcpConnection, String value) {
        printMessage(value);

    }

    @Override
    public void onException(TCPConnection tcpConnection, Exception e) {
        printMessage("Connection exception: " + e);
    }
}
