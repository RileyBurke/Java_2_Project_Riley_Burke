package ca.nl.cna.java2_project.card_game;

import java.util.*;

/**
 * Class in which games of One Hundreds are played.
 */
public class OneHundreds {

    /**
     * Main method in which games of One Hundreds are played.
     *
     * @param args The command line arguments.
     */
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        System.out.println("One Hundreds\n");

        ArrayList<Player> playerList = new ArrayList<>();
        HashMap<String, Integer> playerScores = new HashMap<>();
        boolean isInvalidNumberOfPlayers = false;
        int numberOfPlayers;

        do {
            System.out.print("Enter number of players (2-4): ");

            try {
                numberOfPlayers = scanner.nextInt();
            }catch (InputMismatchException inputMismatchException){
                numberOfPlayers = 0;
            }

            scanner.nextLine();
            if (2 <= numberOfPlayers && numberOfPlayers <= 4) {
                for (int i = 1; i <= numberOfPlayers; i++) {
                    System.out.print("Enter player " + i + " name: ");
                    String playerName = scanner.nextLine();
                    playerList.add(new Player(playerName));
                    playerScores.put(playerName, 0);
                    isInvalidNumberOfPlayers = false;
                }
                System.out.println();
            } else {
                System.out.println("Invalid number of players. Please try again. \n");
                isInvalidNumberOfPlayers = true;
            }
        }while (isInvalidNumberOfPlayers);

        CardDeck cardDeck = new CardDeck();
        cardDeck.shuffleDeck();

        while(cardDeck.cardsRemaining() >= playerList.size()){  //Draw all cards
            for (Player player : playerList){
                player.drawCard(cardDeck);
            }
        }

        playGame(playerList, playerScores);

        System.out.printf("Remaining cards in deck (%d)%s%n", cardDeck.cardsRemaining(), cardDeck.cardsRemaining() > 0 ? ":" : "");
        cardDeck.printDeck(System.out);

        printScore(playerList, playerScores);
    }


    /**
     * Plays each hand of the game.
     *
     * @param playerList ArrayList of all Players playing the game.
     * @param playerScores HashMap keeping track of the score of the game.
     */
    public static void playGame(ArrayList<Player> playerList, HashMap<String, Integer> playerScores){

        ArrayList<Card> cardsPlayed = new ArrayList<>();
        for(int turn = 0; turn < 100/playerList.size(); turn++){  //Entire game
            for (Player player: playerList) { //One play
                Card card = player.revealCard();
                System.out.printf("%s: %d%s%n", player.getName(), card.getCardValue(), card.isWildcard() ? " - Wildcard" : "");
                cardsPlayed.add(card);
            }
            Collections.sort(cardsPlayed);
            Card winningCard = cardsPlayed.get(playerList.size() - 1);
            cardsPlayed.clear();

            for (Player player: playerList) {
                if (player.hasCard(winningCard)){
                    playerScores.put(player.getName(), playerScores.get(player.getName()) + 1);
                    System.out.printf("%s wins the hand with a %s%d.%n%n", player.getName(), winningCard.isWildcard() ? "wildcard " : "", winningCard.getCardValue() );
                }
                player.discardCard();
            }
        }
    }


    /**
     * Prints the results of the hands and selects a winner.
     *
     * @param playerList ArrayList of all Players playing the game.
     * @param playerScores HashMap keeping track of the score of the game.
     */
    public static void printScore(ArrayList<Player> playerList, HashMap<String, Integer> playerScores){
        System.out.println("\nScore:");
        for (Player player: playerList) {
            System.out.printf("%s: %d wins%n", player.getName(), playerScores.get(player.getName()));
        }
        int mostWins = Collections.max(playerScores.values());
        ArrayList<String> winners = new ArrayList<>();
        for (Player player: playerList) {
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
}
