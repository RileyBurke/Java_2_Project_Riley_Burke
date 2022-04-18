package ca.nl.cna.java2_project.networking;

import ca.nl.cna.java2_project.card_game.Card;
import ca.nl.cna.java2_project.card_game.Player;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

/**
 *
 */
public class GameServerThread implements Runnable {
    GameProtocol gameProtocol;
    Socket playerSocket;
    Player player;
    ObjectOutputStream output;
    ObjectInputStream input;

    /**
     *
     *
     * @param gameProtocol
     * @param playerSocket
     * @param player
     * @param output
     * @param input
     */
    public GameServerThread(GameProtocol gameProtocol, Socket playerSocket, Player player, ObjectOutputStream output, ObjectInputStream input) {
//        super("GameServerThread - " + player.getName());
        this.gameProtocol = gameProtocol;
        this.playerSocket = playerSocket;
        this.player = player;
        this.output = output;
        this.input = input;
    }

    /**
     *
     */
    public synchronized void run() {
//        System.out.printf("\nRunning: %s", this.getName());

        try{
            output.writeObject(player);
            boolean isCardPlayed = false;
            player.clearHand();
            String handResults = "";
            int roundNumber = 1;
            boolean gameInProgress = true;
            boolean waiting = false;

            while (gameInProgress){
//                while (gameProtocol.getGameStatus() == GameProtocol.Status.NEW_HAND && !isCardPlayed ||
//                        (gameProtocol.getGameStatus() == GameProtocol.Status.HAND_IN_PROGRESS && !isCardPlayed)) {
//                    Card cardPlayed = (Card) input.readObject();
//                    while (cardPlayed != null && !isCardPlayed) {
//                        System.out.println("ROUND " + roundNumber);
//                        player.addCard(cardPlayed);
//                        gameProtocol.playHand(cardPlayed, player.getName(), roundNumber);
//                        handResults = gameProtocol.resolveHand(roundNumber, player.getName());
//                        isCardPlayed = true;
//                        output.writeObject(handResults);
//                        roundNumber++;
//                    }
//                }
//                while (isCardPlayed && !gameProtocol.allCardsPlayed()){
//
//                }
//                while (isCardPlayed && gameProtocol.allCardsPlayed()) {
//                    isCardPlayed = false;
//                }
                if (!isCardPlayed) {
                    Card cardPlayed = (Card) input.readObject();
                    while (cardPlayed != null && !isCardPlayed) {
                        System.out.println("ROUND " + roundNumber);
                        player.addCard(cardPlayed);
                        gameProtocol.playHand(cardPlayed, player.getName(), roundNumber);
                        isCardPlayed = true;
                    }
                }
                if (gameProtocol.allCardsPlayed()){
                    handResults = gameProtocol.resolveHand(roundNumber, player.getName());
                    output.writeObject(handResults);
                    output.flush();
                    roundNumber++;
                    isCardPlayed = false;
                    gameProtocol.setPlayerReady();
                    waiting = true;
                }
                while (waiting) {
                    if (gameProtocol.allPlayersReady()) {
                        gameProtocol.clearCurrentHand();
                        gameProtocol.resetPlayersReady();
                    }
                    if (gameProtocol.currentHandEmpty()){
                        waiting = false;
                    }
                }
                if (gameProtocol.isGameOver()) {
                    System.out.println("game over");
                    output.writeObject(gameProtocol.getGameResults());
                    boolean rematch = (boolean) input.readObject();
                    gameProtocol.attemptRematch(rematch);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }
}