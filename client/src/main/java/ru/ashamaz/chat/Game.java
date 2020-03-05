package ru.ashamaz.chat;

import ru.ashamaz.model.AbstractClient;
import ru.ashamaz.model.Message;

import java.io.IOException;
import java.util.Scanner;

public class Game {

    private Game() {
        Player player = new Player(10);
        try {
            player.setConnection(player.createTCPConnection(AbstractClient.IP_ADDR, AbstractClient.PORT));
            Scanner scanner = new Scanner(System.in);
            while (true){
                String s = scanner.nextLine();
                if ("exit".equalsIgnoreCase(s)) {
                    System.out.println("disconnectiong...");
                    player.getConnection().disconnect();
                    System.exit(0);
                } else {
                    Message m = Message.createMessage(s, null, player.getName());
                    player.getConnection().sendMessage(m);
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
