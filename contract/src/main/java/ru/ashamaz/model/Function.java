package ru.ashamaz.model;

import ru.ashamaz.network.TCPConnection;

public interface Function {
    void invoke(String o, TCPConnection tcpConnection);
}
