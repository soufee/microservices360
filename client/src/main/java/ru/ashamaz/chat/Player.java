package ru.ashamaz.chat;

import com.google.gson.Gson;
import ru.ashamaz.model.*;
import ru.ashamaz.network.TCPConnection;

import java.io.IOException;
import java.util.Scanner;

public class Player extends AbstractClient {
    private int limit;
    private String name;
    private Gson gson = new Gson();

    public Player(int limit) {
        this.limit = limit;
        Scanner scanner = new Scanner(System.in);
        System.out.println("Enter your name...");
        name = scanner.nextLine();
        commands.put(Commands.MESSAGE, value -> processMessage(parseMessage(value)));
        commands.put(Commands.REQUEST, value -> processRequest(parseRequest(value)));
        commands.put(Commands.REGISTRATION, value -> System.out.println("Not supported"));
    }

    private void processRequest(RqCommand request) {
        if (request.getResponseBody() != null) {
            Command init = factory.getMessageCommand(new Message("ping", request.getResponseBody(), name));
            initPingPong(init);
        } else {
            onException(connection, new IllegalStateException("Empty response body has been gotten"));
        }
    }

    private void initPingPong(Command init) {
        connection.sendMessage(init);
    }

    private void processMessage(Message message) {
        printMessage("direct message " + message.getMessage());
        if (name.equals(message.getTo()) && !isEmpty(message.getFrom())) {
            replyToMessage(message);
        }
    }

    private boolean isEmpty(String string) {
        if (string == null || "".equals(string) || "null".equals(string)) return true;
        else return false;
    }

    private Message parseMessage(String value) {
        return gson.fromJson(value, Message.class);
    }

    private RqCommand parseRequest(String value) {
        return gson.fromJson(value, RqCommand.class);
    }

    @Override
    public String getName() {
        return name;
    }

    public int getLimit() {
        return limit;
    }

    public void replyToMessage(Message message) {
        System.out.println("Replying to message " + message.getMessage());
        incrementSentMessageCounter();
        if (countSentMessages >= limit) {
            connection.disconnect();
            return;
        }
        Message m = Message.createMessage(message.getMessage() + " " + countSentMessages, message.getFrom(), connection.toString());
        Command messageCommand = factory.getMessageCommand(m);
        connection.sendMessage(messageCommand);
    }

    @Override
    public TCPConnection createTCPConnection(String ip, int port) throws IOException {
        return new TCPConnection(this, IP_ADDR, PORT);
    }

    @Override
    protected void printMessage(String msg) {
        System.out.println(msg);
    }


    @Override
    public void onReceiveMessage(TCPConnection tcpConnection, Command command) {
        commands.get(command.getType()).invoke(command.getData());
    }
}
