package ru.ashamaz.network;

import ru.ashamaz.model.Command;

import java.io.IOException;

public interface TCPConnectionListener {

    void onConnectionReady(TCPConnection tcpConnection);

    void onDisconnect(TCPConnection tcpConnection);

    void onReceiveMessage(TCPConnection tcpConnection, Command command) throws IOException;

    void onException(TCPConnection tcpConnection, Exception e);

}
