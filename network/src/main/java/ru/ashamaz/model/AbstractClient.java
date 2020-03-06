package ru.ashamaz.model;

import ru.ashamaz.network.TCPConnection;
import ru.ashamaz.network.TCPConnectionListener;

import java.io.IOException;
import java.util.EnumMap;
import java.util.Map;
import java.util.Objects;

public abstract class AbstractClient implements TCPConnectionListener {
    protected TCPConnection connection;
    public static final String IP_ADDR = "127.0.0.1";
    public static final int PORT = 8189;
    protected int countSentMessages = 0;
    protected CommandFactory factory = new CommandFactoryImpl();

    protected Map<Commands, Function> commands = new EnumMap<>(Commands.class);

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

    @Override
    public void onConnectionReady(TCPConnection tcpConnection) {
        printMessage("Connection ready...");
        connection.sendMessage(factory.getRegistrationCommand(this));
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
    public String toString() {
        return "Client " + connection;
    }
}
