package ru.ashamaz.server;

import com.google.gson.Gson;
import ru.ashamaz.model.*;
import ru.ashamaz.network.TCPConnection;
import ru.ashamaz.network.TCPConnectionListener;

import java.io.IOException;
import java.net.ServerSocket;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.Map;

public class ChatServer implements TCPConnectionListener {
    private static final int LIMIT = 2;
    private final Map<String, TCPConnection> connections = new HashMap<>();
    protected Map<Commands, Function> commands = new EnumMap<>(Commands.class);
    protected Map<String, String> players = new HashMap<>();
    private Gson gson = new Gson();
    private CommandFactory factory = new CommandFactoryImpl();

    private ChatServer() {
        System.out.println("Server running...");
        commands.put(Commands.REGISTRATION, (value, conn) -> {
            RegistrationRequest request = gson.fromJson(value, RegistrationRequest.class);
            players.put(request.getClientName(), conn.toString());
        });
        commands.put(Commands.REQUEST, (value, conn) -> {
            RqCommand rqCommand = gson.fromJson(value, RqCommand.class);
            if (rqCommand.getResponseBody() == null && rqCommand.getRequestBody() != null && !rqCommand.getRequestBody().isEmpty()) {
               rqCommand.setResponseBody(players.keySet().stream().filter(s->!s.equalsIgnoreCase(rqCommand.getRequestBody())).findFirst().orElse(null));
            }
            connections.get(players.get(rqCommand.getRequestBody())).sendMessage(factory.getRequestCommand(rqCommand));
        });
        commands.put(Commands.MESSAGE, (value, conn) -> {
            Message message = gson.fromJson(value, Message.class);
            if (message.getTo() != null) {
                connections.get(players.get(message.getTo())).sendMessage(factory.getMessageCommand(message));
            } else {
                broadcast(factory.getMessageCommand(message));
            }
        });
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
        if (connections.size() >= LIMIT) {
            Message message = new Message("Impossible to make connection. The Limit is reached", null, null);
            broadcast(factory.getMessageCommand(message));
            tcpConnection.disconnect();
        } else {
            connections.put(tcpConnection.toString(), tcpConnection);
            Message message = new Message ("Client connected: " + tcpConnection + ". the connections count is " + connections.size(), null, null);
            broadcast(factory.getMessageCommand(message));
        }
    }

    @Override
    public synchronized void onDisconnect(TCPConnection tcpConnection) {
        connections.remove(tcpConnection.toString());
        Message message = new Message ("Client disconnected: " + tcpConnection + ". the connections count is " + connections.size(), null, null);
        broadcast(factory.getMessageCommand(message));
    }

    @Override
    public synchronized void onReceiveMessage(TCPConnection tcpConnection, Command command) throws IOException {
        commands.get(command.getType()).invoke(command.getData(), tcpConnection);
    }

    @Override
    public synchronized void onException(TCPConnection tcpConnection, Exception e) {
        System.out.println("TCPConnection exception: " + e);
    }

    private void broadcast(Command command) {
        connections.values().forEach(c -> c.sendMessage(command));
    }

}
