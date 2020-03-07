package ru.ashamaz.network;

import ru.ashamaz.model.Command;

import java.io.IOException;

/**
 * Connection listener for process all main actions on socket.
 * <p>
 * It must be implemented by server and client to use the one socket
 */
public interface TCPConnectionListener {

    void onConnectionReady(TCPConnection tcpConnection);

    void onDisconnect(TCPConnection tcpConnection);

    void onReceiveMessage(TCPConnection tcpConnection, Command command) throws IOException;

    void onException(TCPConnection tcpConnection, Exception e);

}
