package ru.ashamaz.model;

import ru.ashamaz.network.TCPConnection;

/**
 * Functional Interface for processing Commands
 */
@FunctionalInterface
public interface Function {
    void invoke(String o, TCPConnection tcpConnection);
}
