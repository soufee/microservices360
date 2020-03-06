package ru.ashamaz.model;

import java.io.Serializable;

public class RegistrationRequest implements Serializable {
    private String clientName;
    private String tcpConnection;

    public RegistrationRequest() {
    }

    public RegistrationRequest(String clientName, String tcpConnection) {
        this.clientName = clientName;
        this.tcpConnection = tcpConnection;
    }

    public String getClientName() {
        return clientName;
    }

    public void setClientName(String clientName) {
        this.clientName = clientName;
    }

    public String getTcpConnection() {
        return tcpConnection;
    }

    public void setTcpConnection(String tcpConnection) {
        this.tcpConnection = tcpConnection;
    }
}
