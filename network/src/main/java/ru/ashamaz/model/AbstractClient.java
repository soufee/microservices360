package ru.ashamaz.model;

import ru.ashamaz.network.TCPConnection;
import ru.ashamaz.network.TCPConnectionListener;

import java.io.IOException;
import java.util.Objects;

public abstract class AbstractClient implements TCPConnectionListener {
    protected TCPConnection connection;
    public static final String IP_ADDR = "127.0.0.1";
    public static final int PORT = 8189;
    protected int countSentMessages = 0;

    public String getName(){
        return "Connection "+IP_ADDR+":"+PORT;
    }

    public int getCountSentMessages() {
        return countSentMessages;
    }

    public abstract TCPConnection createTCPConnection(String ip, int port) throws IOException;

    protected abstract void printMessage(String msg);

    public TCPConnection getConnection() {
        return connection;
    }

    public void setConnection(TCPConnection connection) {
        this.connection = connection;
    }

    public int incrementSentMessageCounter() {
        return ++countSentMessages;
    }

    public void initiateMessage(String playerTo) {
        Objects.requireNonNull(playerTo);
        Message message = Message.createMessage("hello", playerTo, connection.toString());
        connection.sendMessage(message);
        incrementSentMessageCounter();
    }

    @Override
    public void onConnectionReady(TCPConnection tcpConnection) {
        printMessage("Connection ready...");
    }

    @Override
    public void onDisconnect(TCPConnection tcpConnection) {
        printMessage("Disconnected...");
    }

    @Override
    public void onException(TCPConnection tcpConnection, Exception e) {
        printMessage("Exception: " + e.getMessage());
    }

    @Override
    public TCPConnectionListener getInstance() {
        return this;
    }

    @Override
    public String toString() {
        return "Client " + connection;
    }
}
