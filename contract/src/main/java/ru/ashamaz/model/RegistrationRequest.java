package ru.ashamaz.model;

import java.io.Serializable;

/**
 * Entity for Command types REGISTER and UNREGISTER
 */
public class RegistrationRequest implements Serializable {
    private String clientName;

    public RegistrationRequest() {
    }

    public RegistrationRequest(String clientName) {
        this.clientName = clientName;
    }

    public String getClientName() {
        return clientName;
    }

    public void setClientName(String clientName) {
        this.clientName = clientName;
    }

}
