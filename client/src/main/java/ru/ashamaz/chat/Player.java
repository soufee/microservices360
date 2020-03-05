package ru.ashamaz.chat;

import ru.ashamaz.model.AbstractClient;
import ru.ashamaz.model.Message;
import ru.ashamaz.model.Notification;
import ru.ashamaz.network.TCPConnection;

import java.io.IOException;

public class Player extends AbstractClient {
    private int limit;

    public Player(int limit) {
        this.limit = limit;
    }

    public int getLimit() {
        return limit;
    }

    public void replyToMessage(Message message) {
        System.out.println("Replying to message "+message.getMessage());
        incrementSentMessageCounter();
        if (countSentMessages >= limit) return;
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
        System.out.println("Message recieved ==> "+message.getMessage());
        if (message instanceof Notification) {
            Notification notification = (Notification) message;
            if ("connected".equals(notification.getNotification())) {
                String[] strings = notification.getAttachedMessage().getMessage().split(" ");
                int connectionLength = Integer.parseInt(strings[strings.length - 1]);
                if ((connectionLength > 1) && !notification.getClient().equals(connection.toString())) {
                    initiateMessage(notification.getClient());
                }
            }
        } else {
            String to = message.getTo();
            String from = message.getFrom();
            if (to != null) connection.sendMessage(message);
            else if (from != null) replyToMessage(message);
            else System.out.println(message.getMessage());
        }

    }


}
