package ru.ashamaz.chat;

import ru.ashamaz.model.AbstractClient;
import ru.ashamaz.model.Message;
import ru.ashamaz.network.TCPConnection;

import java.io.IOException;
import java.util.Scanner;

public class Player extends AbstractClient {
    private int limit;
    private String name;

    public Player(int limit) {
        this.limit = limit;
        Scanner scanner = new Scanner(System.in);
        System.out.println("Enter your name...");
        name = scanner.nextLine();
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
        connection.sendMessage(m);
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
    public void onReceiveMessage(TCPConnection tcpConnection, Message message) {
        System.out.println("Message recieved ==> " + message.getMessage());

        String to = message.getTo();
        String from = message.getFrom();
        if (to != null) connection.sendMessage(message);
        else if (from != null) replyToMessage(message);
        else System.out.println(message.getMessage());


    }


}
