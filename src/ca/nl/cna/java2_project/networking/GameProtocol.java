package ca.nl.cna.java2_project.networking;

import ca.nl.cna.java2_project.card_game.Card;
import ca.nl.cna.java2_project.card_game.CardDeck;
import ca.nl.cna.java2_project.card_game.Player;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;

public class GameProtocol implements Serializable {
    private Status gameStatus;
    private final LinkedList<Player> playersList;
    private final LinkedList<Card> currentHand;
    private final HashMap<String, Integer> playerScores;
    private Card winningCard;

    public enum Status{
        NEW_HAND, HAND_IN_PROGRESS, HAND_OVER, GAME_OVER
    }

    public GameProtocol(LinkedList<Player> players) {
        this.gameStatus = Status.HAND_IN_PROGRESS;
        this.playersList = players;
        this.currentHand = new LinkedList<>();;
        this.playerScores = new HashMap<>();
        dealCards();
    }


    /**
     * Get the Game Status
     * @return
     */
    public Status getGameStatus() {
        return gameStatus;
    }

    /**
     * Determines if the game is over
     * @return
     */
    public boolean isGameOver(){
        return this.gameStatus == Status.GAME_OVER;
    }


    public String playGame(Card playedCard, String playerName, int roundNumber){
        while (true) {
            if (gameStatus == Status.HAND_IN_PROGRESS) {
                if (allCardsPlayed()) {
                    gameStatus = Status.HAND_OVER;
                } else if (!currentHand.contains(playedCard)){
                    if (playerScores.get(playerName) == null) {
                        this.playerScores.put(playerName, 0);
                    }
                    System.out.println(playedCard.getCardValue());
                    currentHand.add(playedCard);
                }
            }else if (gameStatus == Status.HAND_OVER) {
                String roundResults = roundWinner(roundNumber);
                if(playersList.get(0).getHandSize() == 100/playersList.size()){
                    gameStatus = Status.GAME_OVER;
                }else{
                    gameStatus = Status.NEW_HAND;
                }
                return roundResults;
            }else if (gameStatus == Status.NEW_HAND) {
                currentHand.clear();
                gameStatus = Status.HAND_IN_PROGRESS;
            }else if (gameStatus == Status.GAME_OVER) {
                return "Game over";
            }
        }
    }


    public String roundWinner(int roundNumber){
        StringBuilder handResult = new StringBuilder();
        Player winner = playersList.get(0);
        Collections.sort(currentHand);
        this.winningCard = currentHand.get(playersList.size() - 1);
        for (Player player : this.playersList) {
            if (player.hasCard(winningCard)) {
                winner = player;
                playerScores.put(player.getName(), playerScores.get(player.getName()) + 1);
            }
        }

        handResult.append("Round ").append(roundNumber).append(" results:\n")
                .append(getCurrentHand())
                .append(winner.getName())
                .append(" wins the hand with a ")
                .append(winner.getHand().get(roundNumber - 1).isWildcard() ? "wildcard " : "")
                .append(winner.getHand().get(roundNumber - 1).getCardValue())
                .append("\n");

        return handResult.toString();
    }

    public void dealCards() {
        CardDeck cardDeck = new CardDeck();
        cardDeck.shuffleDeck();

        while (cardDeck.cardsRemaining() >= playersList.size()) {
            for (Player player : playersList) {
                player.drawCard(cardDeck);
            }
        }
    }

    public void printResults() {
        for (Player player: playersList) {   //Fixing the scores from multithreading
            if (playerScores.get(player.getName()) != 0) {
                int fixedScore = playerScores.get(player.getName()) / playersList.size();
                playerScores.put(player.getName(), fixedScore);
            }
        }
        System.out.println("\nScore:");
        for (Player player: playersList) {
            System.out.printf("%s: %d wins%n", player.getName(), playerScores.get(player.getName()) == 0 ? 0 : playerScores.get(player.getName())/playersList.size());
        }
        int mostWins = Collections.max(playerScores.values());
        ArrayList<String> winners = new ArrayList<>();
        for (Player player: playersList) {
            if (playerScores.get(player.getName()) == mostWins){
                winners.add(player.getName());
            }
        }
        if (winners.size() == 1){
            System.out.println(winners.get(0) + " wins the game with " + playerScores.get(winners.get(0)) + " wins!");
        }else{
            System.out.println("Tie game!\n");
            if (winners.size() == 2){ //4 way ties can not happen, only 2 or 3 way.
                System.out.printf("%s and %s both win with %d wins each.", winners.get(0), winners.get(1),
                        playerScores.get(winners.get(0)));
            }else{
                System.out.printf("%s, %s and %s all win with %d wins each.", winners.get(0), winners.get(1),
                        winners.get(2), playerScores.get(winners.get(0)));
            }
        }
    }

    public boolean allCardsPlayed(){
        return playersList.size() == currentHand.size();
    }

    public String getCurrentHand(){
        StringBuilder currentHand = new StringBuilder();
        for (Player player: playersList) {
            currentHand.append(player.getName()).append(" - ").append(player.getTopCardString()).append("\n");
        }
        return currentHand.toString();

    }

}
