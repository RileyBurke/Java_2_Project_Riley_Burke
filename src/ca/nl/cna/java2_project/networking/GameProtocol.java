package ca.nl.cna.java2_project.networking;

import ca.nl.cna.java2_project.card_game.Card;
import ca.nl.cna.java2_project.card_game.CardDeck;
import ca.nl.cna.java2_project.card_game.Player;

import java.io.Serializable;
import java.util.*;

/**
 *
 */
public class GameProtocol implements Serializable {
    private Status gameStatus;
    private final LinkedList<Player> playersList;
    private final LinkedList<Card> currentHand;
    private final HashMap<String, Integer> playerScores;
    private int totalRoundsPlayed;
    private LinkedList<Boolean> playerRematch;
    private int playersReady;

    public enum Status{
        NEW_HAND, HAND_IN_PROGRESS, GAME_OVER
    }

    /**
     * @param players
     */
    public GameProtocol(LinkedList<Player> players) {
        this.gameStatus = Status.HAND_IN_PROGRESS;
        this.playersList = players;
        this.currentHand = new LinkedList<>();;
        this.playerScores = new HashMap<>();
        this.playerRematch = new LinkedList<>();
        this.totalRoundsPlayed = players.size();
        this.playersReady = 0;
        dealCards();
    }


    /**
     * Get the status of the game.
     *
     * @return The status of the game.
     */
    public synchronized Status getGameStatus() {
        return gameStatus;
    }

    /**
     * Checks if the game is over.
     *
     * @return True if the game is over. False otherwise.
     */
    public synchronized boolean isGameOver(){
        return this.gameStatus == Status.GAME_OVER;
    }


    /**
     * Plays a hand of One Hundreds.
     *
     * @param playedCard Card player plays for the hand.
     * @param playerName Name of the player.
     * @param roundNumber The number of the current round of hands.
     */
    public synchronized void playHand(Card playedCard, String playerName, int roundNumber) {
//        while (gameStatus != Status.GAME_OVER) {
//            if (gameStatus == Status.HAND_IN_PROGRESS) {
//                if (!currentHand.contains(playedCard)) {
//                    if (playerScores.get(playerName) == null) {
//                        this.playerScores.put(playerName, 0);
//                    }
//                    System.out.println(playedCard.getCardValue());
//                    System.out.println("ADD CARD");
//                    currentHand.add(playedCard);
//                }
//            } else if (gameStatus == Status.NEW_HAND) {
//                System.out.println("CLEAR HAND");
//                currentHand.clear();
//                gameStatus = Status.HAND_IN_PROGRESS;
//            }
//        }


            if (!currentHand.contains(playedCard)) {
                if (playerScores.get(playerName) == null) {
                    this.playerScores.put(playerName, 0);
                }
                System.out.println(playedCard.getCardValue());
                System.out.println("ADD CARD");
                currentHand.add(playedCard);
            }
    }

    /**
     *
     *
     * @param roundNumber The number of the current round of hands.
     * @param playerName Name of the player.
     * @return Results of the hand, who played what cards and who won the hand.
     */
    public synchronized String resolveHand(int roundNumber, String playerName){

        if(playersList.get(0).getHandSize() == 100/playersList.size()){
            gameStatus = Status.GAME_OVER;
        }
        return roundWinner(roundNumber, playerName);

    }

    /**
     * Checks for the winning card and assigns score. Returns String of results.
     *
     * @param roundNumber The number of the current round of hands.
     * @param playerName Name of the player.
     * @return Results of the hand.
     */
    public synchronized String roundWinner(int roundNumber, String playerName){
        StringBuilder handResult = new StringBuilder();
        Player winner = playersList.get(0);
        Collections.sort(currentHand);
        Card winningCard = currentHand.get(playersList.size() - 1);
        for (Player player : this.playersList) {
            if (player.hasCard(winningCard)) {
                winner = player;
                if (playerName.equals(winner.getName())) {
                    playerScores.put(playerName, playerScores.get(playerName) + 1);
                }
            }
        }

        handResult.append("Round ").append(roundNumber).append(" results:\n")
                .append(getCurrentHand())
                .append(winner.getName())
                .append(" wins the hand with a ")
                .append(winningCard.isWildcard() ? "wildcard " : "")
                .append(winningCard.getCardValue())
                .append("\n");

        System.out.println("RETURN RESULTS");
        return handResult.toString();
    }

    /**
     * Deals the deck across the Player hands.
     */
    public synchronized void dealCards() {
        CardDeck cardDeck = new CardDeck();
        cardDeck.shuffleDeck();

        while (cardDeck.cardsRemaining() >= playersList.size()) {
            for (Player player : playersList) {
                player.drawCard(cardDeck);
            }
        }
    }

    /**
     * Totals the results of the game and returns the results to the player.
     *
     * @return The end results of the game.
     */
    public synchronized String getGameResults() {
        StringBuilder gameResults = new StringBuilder();
        gameResults.append("Score:\n");
        for (Player player: playersList) {
            gameResults.append(player.getName())
                    .append(": ")
                    .append(playerScores.get(player.getName()) == 0 ? 0 : playerScores.get(player.getName()))
                    .append("\n");
        }

        int mostWins = Collections.max(playerScores.values());
        ArrayList<String> winners = new ArrayList<>();
        for (Player player: playersList) {
            if (playerScores.get(player.getName()) == mostWins){
                winners.add(player.getName());
            }
        }

        gameResults.append(winners.get(0)).append(" wins the game with ").append(playerScores.get(winners.get(0))).append(" wins!\n");
        return gameResults.toString();
    }

    /**
     * Checks if all players have played a card in the current hand.
     *
     * @return Boolean value True if all player have played a card, false otherwise.
     */
    public synchronized boolean allCardsPlayed(){
        return playersList.size() == currentHand.size();
    }

    /**
     * Returns a String of all cards in the current hand.
     *
     * @return A String representing all cards in the current hand.
     */
    public synchronized String getCurrentHand(){
        StringBuilder currentHand = new StringBuilder();
        for (Player player: playersList) {
            currentHand.append(player.getName()).append(" - ").append(player.getTopCardString()).append("\n");
        }
        return currentHand.toString();
    }



    /**
     *
     *
     * @param playerRound The number of the current round of hands.
     * @return
     */
    public synchronized boolean nextRoundReady(int playerRound){
        int playersReady = 0;
        for (Player player : playersList){
            if (player.getHandSize() <= playerRound){
                playersReady++;
            }
        }
        return playersReady == playersList.size();
    }

    /**
     * Clears the current hand.
     */
    public synchronized void clearCurrentHand(){
        currentHand.clear();
    }

    /**
     * Adds boolean value of whether the player accepts the rematch or not to a list.
     *
     * @param rematch True if player accepts rematch, false otherwise.
     */
    public synchronized void attemptRematch(boolean rematch){
        playerRematch.add(rematch);
    }

    /**
     * Checks if all players want to play again.
     *
     * @return True if all players accept the rematch, false otherwise.
     */
    public boolean rematchAccepted(){
        return playerRematch.size() == playersList.size();
    }

    public synchronized void setPlayerReady(){
        this.playersReady++;
    }

    public synchronized boolean allPlayersReady(){
        return playersList.size() == playersReady;
    }

    public synchronized void resetPlayersReady(){
        this.playersReady = 0;
    }

    public synchronized boolean currentHandEmpty(){
        return currentHand.size() == 0;
    }
}