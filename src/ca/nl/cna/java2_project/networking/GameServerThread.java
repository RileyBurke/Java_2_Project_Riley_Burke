package ca.nl.cna.java2_project.networking;

import ca.nl.cna.java2_project.card_game.Card;
import ca.nl.cna.java2_project.card_game.Player;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

public class GameServerThread extends Thread {
    GameProtocol gameProtocol;
    Socket playerSocket;
    Player player;
    ObjectOutputStream output;
    ObjectInputStream input;

    public GameServerThread(GameProtocol gameProtocol, Socket playerSocket, Player player, ObjectOutputStream output, ObjectInputStream input) {
        super("GameServerThread - " + player.getName());
        this.gameProtocol = gameProtocol;
        this.playerSocket = playerSocket;
        this.player = player;
        this.output = output;
        this.input = input;
    }

    public void run() {
        System.out.printf("\nRunning: %s", this.getName());

        try{
            output.writeObject(player);
            boolean isCardPlayed = false;
            player.clearHand();
            String handResults = "";
            int roundNumber = 1;

            while (true){
                while (gameProtocol.getGameStatus() == GameProtocol.Status.NEW_HAND && !isCardPlayed ||
                        (gameProtocol.getGameStatus() == GameProtocol.Status.HAND_IN_PROGRESS && !isCardPlayed)) {
                    Card cardPlayed = (Card) input.readObject();
                    while (cardPlayed != null && !isCardPlayed) {
                        System.out.println("ROUND " + roundNumber);
                        player.addCard(cardPlayed);
                        handResults = gameProtocol.playGame(cardPlayed, player.getName(), roundNumber);
                        isCardPlayed = true;
                        output.writeObject(handResults);
                        roundNumber++;
                    }
                }
                while (isCardPlayed && gameProtocol.allCardsPlayed()) {
                    isCardPlayed = false;
                }
                if(gameProtocol.isGameOver()){
                    break;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }


        //Continue until the game is completed

        //TODO Consider sleeping the thread in the game loop for a second to make the game happen in real time
        //But depending on how you do this you may want to sleep somewhere else
    }
}
