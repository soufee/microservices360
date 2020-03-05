package ru.ashamaz.network;

import ru.ashamaz.model.Message;

import java.io.IOException;

public interface TCPConnectionListener {

    void onConnectionReady(TCPConnection tcpConnection);

    void onDisconnect(TCPConnection tcpConnection);

    void onReceiveMessage(TCPConnection tcpConnection, Message message) throws IOException;

    void onException(TCPConnection tcpConnection, Exception e);

    TCPConnectionListener getInstance();
}
