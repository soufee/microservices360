package ru.ashamaz.model;

import java.io.Serializable;

/**
 * Message for Commands of type MESSAGE
 * */
public class Message implements Serializable {
    private String message;
    private String from;
    private String to;

    public Message() {

    }

    public Message(String message, String to, String from) {
        this.message = message;
        this.to = to;
        this.from = from;
    }

    public String getMessage() {
        return message;
    }

    public String getFrom() {
        return from;
    }

    public void setFrom(String from) {
        this.from = from;
    }

    public String getTo() {
        return to;
    }
}
