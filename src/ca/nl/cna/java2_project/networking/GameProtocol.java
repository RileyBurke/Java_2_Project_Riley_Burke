package ca.nl.cna.java2_project.networking;

import ca.nl.cna.java2_project.card_game.Card;
import ca.nl.cna.java2_project.card_game.CardDeck;
import ca.nl.cna.java2_project.card_game.Player;

import java.io.Serializable;
import java.util.*;

public class GameProtocol implements Serializable {
    private Status gameStatus;
    private final LinkedList<Player> playersList;
    private final LinkedList<Card> currentHand;
    private final HashMap<String, Integer> playerScores;
    private int totalRoundsPlayed;

    public enum Status{
        NEW_HAND, HAND_IN_PROGRESS, GAME_OVER
    }

    public GameProtocol(LinkedList<Player> players) {
        this.gameStatus = Status.HAND_IN_PROGRESS;
        this.playersList = players;
        this.currentHand = new LinkedList<>();;
        this.playerScores = new HashMap<>();
        this.totalRoundsPlayed = players.size();
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


    public void playHand(Card playedCard, String playerName, int roundNumber) {
        while (true) {
            if (gameStatus == Status.HAND_IN_PROGRESS) {
                if (allCardsPlayed()) {
                    break;
                } else if (!currentHand.contains(playedCard) && nextRoundReady(roundNumber)) {
                    if (playerScores.get(playerName) == null) {
                        this.playerScores.put(playerName, 0);
                    }
                    System.out.println(playedCard.getCardValue());
                    currentHand.add(playedCard);
                }
            } else if (gameStatus == Status.NEW_HAND) {
                currentHand.clear();
                gameStatus = Status.HAND_IN_PROGRESS;
            }
        }
    }

    public String resolveHand(int roundNumber, String playerName){
        String roundResults = roundWinner(roundNumber, playerName);
        if(playersList.get(0).getHandSize() == 100/playersList.size()){
            gameStatus = Status.GAME_OVER;
        }else{
            gameStatus = Status.NEW_HAND;
        }
        totalRoundsPlayed++;
        return roundResults;
    }

    public String roundWinner(int roundNumber, String playerName){
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

    public String getGameResults() {
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

    public boolean nextRoundReady(int playerRound){
        return totalRoundsPlayed/playersList.size() == playerRound;
    }

    public void clearCurrentHand(){
        currentHand.clear();
    }
}