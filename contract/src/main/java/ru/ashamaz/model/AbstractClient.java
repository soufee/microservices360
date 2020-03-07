package ru.ashamaz.model;

import ru.ashamaz.network.TCPConnection;
import ru.ashamaz.network.TCPConnectionListener;

import java.io.IOException;
import java.util.EnumMap;
import java.util.Map;

/**
 * AbstractClient with implementation of TCPConnectionListener
 * Due to 'contract' module must not depend on other modules, we get here Abstract Client with base functionality
 */

public abstract class AbstractClient implements TCPConnectionListener {
    protected TCPConnection connection;
    public static final String IP_ADDR = "127.0.0.1";
    public static final int PORT = 8189;
    protected int countSentMessages = 0;
    protected CommandFactory factory = new CommandFactoryImpl();

    protected Map<Commands, Function> commands = new EnumMap<>(Commands.class);

    public String getName() {
        return "Connection " + IP_ADDR + ":" + PORT;
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

    /**
     * on Connection ready, we request to Server to register new Player
     */
    @Override
    public void onConnectionReady(TCPConnection tcpConnection) {
        printMessage("Connection ready...");
        connection.sendMessage(factory.getRegistrationCommand(this));
    }

    /**
     * When the client disconnects, we can exit the process
     */
    @Override
    public void onDisconnect(TCPConnection tcpConnection) {
        printMessage(getName() + " Disconnected...");
        connection.sendMessage(factory.getUnregister(this));
        System.exit(0);
    }

    @Override
    public void onException(TCPConnection tcpConnection, Exception e) {
        printMessage("Exception: " + e.getMessage());
    }

    @Override
    public String toString() {
        return "Client " + connection + " " + getName();
    }
}
