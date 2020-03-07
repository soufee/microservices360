package ru.ashamaz.chat;

import ru.ashamaz.model.*;

import java.io.IOException;
import java.util.Scanner;

/**
 * Game is the main class of a CLient application.
 * First, a Player instance created with limit of chat messages = 10
 * After start we have to enter players name. IT MUST BE UNIQUE!
 * In eternal loop we can chat
 * If we write 'exit', the client disconnects
 * If we write 'init', we start ping-pong play. After 10 messages with 10 responses, the application stops.
 */

public class Game {

    private Game() {
        CommandFactory factory = new CommandFactoryImpl();
        Player player = null;
        try {
            player = new Player(10);
            Scanner scanner = new Scanner(System.in);
            while (true) {
                String s = scanner.nextLine();
                if ("exit".equalsIgnoreCase(s)) {
                    System.out.println("disconnectiong...");
                    player.getConnection().disconnect();
                    System.exit(0);
                } else if ("init".equalsIgnoreCase(s)) {
                    player.getConnection().sendMessage(factory.getRequestCommand(new RqCommand(player.getName(), null)));
                } else {
                    Message m = new Message(s, null, player.getName());
                    player.getConnection().sendMessage(factory.getMessageCommand(m));
                }
            }
        } catch (IOException e) {
            player.onException(player.getConnection(), e);
        }
    }

    public static void main(String[] args) {
        new Game();
    }
}
