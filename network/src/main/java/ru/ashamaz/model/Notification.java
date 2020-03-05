package ru.ashamaz.model;

public class Notification extends Message{
   private String notification;
   private String client;
   private Message message;

    public Notification(String notification, String client, Message message) {
        this.notification = notification;
        this.client = client;
        this.message = message;
    }

    public String getNotification() {
        return notification;
    }

    public void setNotification(String notification) {
        this.notification = notification;
    }

    public String getClient() {
        return client;
    }

    public void setClient(String client) {
        this.client = client;
    }

    public Message getAttachedMessage() {
        return message;
    }

    public void setAttachedMessage(Message message) {
        this.message = message;
    }
}
