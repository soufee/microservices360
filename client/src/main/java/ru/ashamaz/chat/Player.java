package ru.ashamaz.chat;

import com.google.gson.Gson;
import ru.ashamaz.model.*;
import ru.ashamaz.network.TCPConnection;

import java.io.IOException;
import java.util.Scanner;

/**
 * PLayer - is a main entity with business logic of microservice 'client'
 * It extends AbstractClient from 'contract' library
 * Initially, we create socket connection and complete commands map to process commands with the connection.
 */

public class Player extends AbstractClient {
    private int limit;
    private String name;
    private Gson gson = new Gson();

    public Player(int limit) throws IOException {
        this.limit = limit;
        Scanner scanner = new Scanner(System.in);
        System.out.println("Enter your name...");
        name = scanner.nextLine();
        setConnection(createTCPConnection(IP_ADDR, PORT));
        commands.put(Commands.MESSAGE, (value, conn) -> processMessage(parseMessage(value)));
        commands.put(Commands.REQUEST, (value, conn) -> processRequest(parseRequest(value)));
        commands.put(Commands.REGISTER, (value, conn) -> printMessage("Not supported"));
        commands.put(Commands.UNREGISTER, (value, conn) -> printMessage("Not supported"));
    }

    /**
     * Method for process Commands of type REQUEST
     * When the Game sends REQUEST type of command, it means that ping-pong starts. The first message to another player is 'init message'
     */

    private void processRequest(RqCommand request) {
        if (request.getResponseBody() != null) {
            Command init = factory.getMessageCommand(new Message("init message", request.getResponseBody(), name));
            initPingPong(init);
        } else {
            onException(connection, new IllegalStateException("Empty response body has been gotten. Maybe the second player is not registered"));
        }
    }

    /**
     * Play starts
     */
    private void initPingPong(Command init) {
        printMessage(">> 0");
        incrementSentMessageCounter();
        connection.sendMessage(init);
    }

    /**
     * Method for process Commands of type MESSAGE
     * if the limit is reached, the method returns. Else, it replies to got message
     */

    private void processMessage(Message message) {
        printMessage("<< " + message.getMessage());
        if (name.equals(message.getTo()) && !isEmpty(message.getFrom())) {
            if (countSentMessages >= limit) {
                connection.disconnect();
                return;
            }
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

    /**
     * For replying to a message, we concat got message with countSentMessages
     */
    private void replyToMessage(Message message) {
        if (countSentMessages >= limit) {
            connection.disconnect();
            return;
        }
        incrementSentMessageCounter();
        String text = message.getMessage() + " " + countSentMessages;
        printMessage(">> " + text);
        connection.sendMessage(factory.getMessageCommand(new Message(text, message.getFrom(), name)));
        if (countSentMessages >= limit) connection.disconnect();
    }

    /**
     * TCP connection created
     */
    @Override
    public TCPConnection createTCPConnection(String ip, int port) throws IOException {
        return new TCPConnection(this, IP_ADDR, PORT);
    }

    @Override
    protected void printMessage(String msg) {
        System.out.println(msg);
    }

    /**
     * overriden method works on command recieved.
     * We get lambda function from commands map and invoke it
     */

    @Override
    public void onReceiveMessage(TCPConnection tcpConnection, Command command) {
        commands.get(command.getType()).invoke(command.getData(), tcpConnection);
    }
}
