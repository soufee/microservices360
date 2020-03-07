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
import java.util.Objects;

/**
 * The main Server class
 * Implements TCPConnectionListener
 * Contains maps of all connections, pplayers with names and commands, that it can process
 */
public class Server implements TCPConnectionListener {
    private static final int LIMIT = 2;
    private final Map<String, TCPConnection> connections = new HashMap<>();
    private Map<Commands, Function> commands = new EnumMap<>(Commands.class);
    private Map<String, String> players = new HashMap<>();
    private Gson gson = new Gson();
    private CommandFactory factory = new CommandFactoryImpl();

    private Server() {
        System.out.println("Server running...");
        commands.put(Commands.REGISTER, (value, conn) -> {
            RegistrationRequest request = gson.fromJson(value, RegistrationRequest.class);
            players.put(request.getClientName(), conn.toString());
        });
        commands.put(Commands.UNREGISTER, (value, conn) -> {
            RegistrationRequest request = gson.fromJson(value, RegistrationRequest.class);
            players.remove(request.getClientName(), conn.toString());
        });
        commands.put(Commands.REQUEST, (value, conn) -> {
            RqCommand rqCommand = gson.fromJson(value, RqCommand.class);
            if (rqCommand.getResponseBody() == null && rqCommand.getRequestBody() != null && !rqCommand.getRequestBody().isEmpty()) {
                rqCommand.setResponseBody(players.keySet().stream().filter(s -> !s.equalsIgnoreCase(rqCommand.getRequestBody())).findFirst().orElse(null));
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
        new Server();
    }

    /**
     * Server has a limit of connections. We dont need more than two players. If the limit reached, we can't connect anymore
     */
    @Override
    public synchronized void onConnectionReady(TCPConnection tcpConnection) {
        if (connections.size() >= LIMIT) {
            Message message = new Message("Impossible to make connection. The Limit is reached", null, null);
            broadcast(factory.getMessageCommand(message));
            tcpConnection.disconnect();
        } else {
            connections.put(tcpConnection.toString(), tcpConnection);
            Message message = new Message("Client connected: " + tcpConnection + ". the connections count is " + connections.size(), null, null);
            broadcast(factory.getMessageCommand(message));
        }
    }

    /**
     * on client disconnect, we must delete the player and connection from maps. When all players disconnected, the server closes
     */
    @Override
    public synchronized void onDisconnect(TCPConnection tcpConnection) {
        Map.Entry<String, TCPConnection> record = connections.entrySet().stream().filter(c -> c.getValue().equals(tcpConnection)).findAny().orElse(null);
        String userName = "";
        if (record != null) {
            Map.Entry<String, String> entry = players.entrySet().stream().filter(p -> p.getValue().equals(record.getKey())).findAny().orElse(null);
            userName = entry != null ? entry.getKey() : "";
        }
        connections.remove(tcpConnection.toString());
        Message message = new Message("Client disconnected: " + userName + ". the connections count is " + connections.size(), null, null);
        broadcast(factory.getMessageCommand(message));
        if (connections.size() == 0) System.exit(0);
    }

    /**
     * When server gets command, we get from enummap a specified function and invoke it
     */
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
