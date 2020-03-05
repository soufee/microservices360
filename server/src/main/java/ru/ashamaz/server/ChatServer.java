package ru.ashamaz.server;

import ru.ashamaz.model.Message;
import ru.ashamaz.model.Notification;
import ru.ashamaz.network.TCPConnection;
import ru.ashamaz.network.TCPConnectionListener;

import java.io.IOException;
import java.net.ServerSocket;
import java.util.HashMap;
import java.util.Map;

public class ChatServer implements TCPConnectionListener {
    private final Map<String, TCPConnection> connections = new HashMap<>();

    private ChatServer() {
        System.out.println("Server running...");
        try (ServerSocket serverSocket = new ServerSocket(8189)) {
            while (true) {
                try {
                    new TCPConnection(this, serverSocket.accept());
                } catch (IOException e) {
                    System.out.println("TCPConnection exception: " + e);
                }
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static void main(String[] args) {
        new ChatServer();
    }

    @Override
    public synchronized void onConnectionReady(TCPConnection tcpConnection) {
        connections.put(tcpConnection.toString(), tcpConnection);
        Notification notification = new Notification("connected", tcpConnection.toString(),
                Message.createMessage("Client connected: " + tcpConnection+". the connections count is "+connections.size(), null, null));
        broadcast(notification);
    }

    @Override
    public synchronized void onDisconnect(TCPConnection tcpConnection) {
        connections.remove(tcpConnection.toString());
        Notification notification = new Notification("disconnected", tcpConnection.toString(),
                Message.createMessage("Client disconnected: " + tcpConnection+". the connections count is "+connections.size(), null, null));
        broadcast(notification);
    }

    @Override
    public TCPConnectionListener getInstance() {
        return this;
    }

    @Override
    public synchronized void onReceiveMessage(TCPConnection tcpConnection, Message message) throws IOException {
        TCPConnection to = connections.get(message.getTo());
        if (to != null) to.sendMessage(message);
        else sendToAllConnections(message.getMessage());
    }

    @Override
    public synchronized void onException(TCPConnection tcpConnection, Exception e) {
        System.out.println("TCPConnection exception: " + e);
    }

    private void broadcast(Message m){
        connections.values().forEach(c -> c.sendMessage(m));
    }

    private void sendToAllConnections(String text) {
        Message message = Message.createMessage(text, null, null);
        broadcast(message);
    }

}
