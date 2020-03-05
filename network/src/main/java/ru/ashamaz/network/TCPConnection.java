package ru.ashamaz.network;

import ru.ashamaz.model.Message;

import java.io.*;
import java.net.Socket;
import java.util.Objects;

public class TCPConnection implements Serializable {
    private final Socket socket;
    private final Thread rxThread;
    private final ObjectInputStream inputStream;
    private final ObjectOutputStream outStream;

    private final TCPConnectionListener eventListener;

    public TCPConnection(final TCPConnectionListener eventListener, String ip, int port) throws IOException {
        this(eventListener, new Socket(ip, port));
    }

    public TCPConnection(final TCPConnectionListener eventListener, Socket socket) throws IOException {
        this.eventListener = eventListener;
        this.socket = socket;
        outStream = new ObjectOutputStream(socket.getOutputStream());
        outStream.flush();
        inputStream = new ObjectInputStream(socket.getInputStream());
        rxThread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    eventListener.onConnectionReady(TCPConnection.this);
                    while (!rxThread.isInterrupted()) {
                        byte[] bytes = (byte[]) inputStream.readObject();
                        ByteArrayInputStream inputStream = new ByteArrayInputStream(bytes);
                        ObjectInputStream objectInputStream = new ObjectInputStream(inputStream);
                        Message message = (Message) objectInputStream.readObject();
                        eventListener.onReceiveMessage(TCPConnection.this, message);
                    }
                } catch (IOException | ClassNotFoundException e) {
                    eventListener.onException(TCPConnection.this, e);
                } finally {
                    eventListener.onDisconnect(TCPConnection.this);
                }
            }
        });
        rxThread.start();
    }

    public synchronized void sendMessage(Message message) {
        byte[] messageByteArray = null;
        try (ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
             ObjectOutputStream outputStream = new ObjectOutputStream(byteArrayOutputStream)
        ) {
            outputStream.writeObject(message);
            outputStream.flush();

            messageByteArray = byteArrayOutputStream.toByteArray();
            outStream.writeObject(Objects.requireNonNull(messageByteArray));
            outStream.flush();
        } catch (Exception e) {
            eventListener.onException(TCPConnection.this, e);
        }
    }

    public synchronized void disconnect() {
        rxThread.interrupt();
        try {
            socket.close();
        } catch (IOException e) {
            eventListener.onException(TCPConnection.this, e);
        }
    }

    @Override
    public String toString() {
        return "TCPConnection: " + socket.getInetAddress() + ": " + socket.getPort();
    }
}
