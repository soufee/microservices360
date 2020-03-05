package ru.ashamaz.chat;

import ru.ashamaz.model.AbstractClient;

import java.io.IOException;

public class Game {

    public Game() {
        Player player = new Player(10);
        try {
            player.setConnection(player.createTCPConnection(AbstractClient.IP_ADDR, AbstractClient.PORT));
            while (true) {
                if (player.getCountSentMessages() >= 10) {
                    break;
                }
                System.exit(0);
            }
        } catch (IOException e) {
            player.onException(player.getConnection(), e);
        }
    }

    public static void main(String[] args) {
        new Game();
    }
}
