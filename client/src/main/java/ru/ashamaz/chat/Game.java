package ru.ashamaz.chat;

import ru.ashamaz.model.*;

import java.io.IOException;
import java.util.Scanner;

public class Game {

    private Game() {
        Player player = new Player(10);
        CommandFactory factory = new CommandFactoryImpl();
        try {
            player.setConnection(player.createTCPConnection(AbstractClient.IP_ADDR, AbstractClient.PORT));
            Scanner scanner = new Scanner(System.in);
            while (true) {
                String s = scanner.nextLine();
                if ("exit".equalsIgnoreCase(s)) {
                    System.out.println("disconnectiong...");
                    player.getConnection().disconnect();
                    System.exit(0);
                } else if ("init".equalsIgnoreCase(s)) {
                    Command requestCommand = factory.getRequestCommand(new RqCommand(player.getName(), null));
                    player.getConnection().sendMessage(requestCommand);
                } else {
                    Message m = new Message (s, null, player.getName());
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
