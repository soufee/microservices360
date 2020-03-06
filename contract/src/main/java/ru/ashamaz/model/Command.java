package ru.ashamaz.model;

import java.io.Serializable;

public class Command implements Serializable {
    private Commands type;
    private String data;

    public Command(Commands type, String data) {
        this.type = type;
        this.data = data;
    }

    public Commands getType() {
        return type;
    }

    public void setType(Commands type) {
        this.type = type;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }
}
