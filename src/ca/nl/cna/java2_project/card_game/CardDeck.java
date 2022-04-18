package ca.nl.cna.java2_project.card_game;

import java.io.PrintStream;
import java.util.*;

/**
 * Class representing a deck of cards for use in the One Hundreds game.
 */
public class CardDeck {
    ArrayList<Card> deck;

    /**
     * Initializes a deck. Calls the generateDeck method to build it.
     */
    public CardDeck(){
        this.deck = generateDeck();
    }

    /**
     * Creates a deck of 100 cards with a random 4 cards being assigned wildcards.
     *
     * @return Deck of cards.
     */
    public ArrayList<Card> generateDeck(){
        Random random = new Random();
        ArrayList<Card> deck = new ArrayList<>();
        Set<Integer> wildcards = new HashSet<>();
        while(wildcards.size() < 4){
            wildcards.add(random.nextInt(100) + 1);
        }
        for(int i = 1; i <= 100; i++){
            deck.add(new Card(i, wildcards.contains(i)));
        }
        Collections.shuffle(deck);
        return deck;
    }

    /**
     * Shuffles the deck.
     */
    public void shuffleDeck(){
        Collections.shuffle(deck);
    }

    /**
     * Prints the cards in the deck to the designated print stream.
     *
     * @param printStream Which print stream to be outputted to.
     */
    public void printDeck(PrintStream printStream){
        for (Card card: deck) {
            printStream.println(card.getCardValue() + (card.isWildcard() ? " - Wildcard" : ""));
        }
    }

    /**
     * Gets the number of cards left in the deck.
     *
     * @return The number of cards left in the deck.
     */
    public int cardsRemaining(){
        return deck.size();
    }

    /**
     * Grabs a card from the top of the deck and removes it, returning that card.
     *
     * @return Card from the deck.
     */
    public Card draw(){
        Card card = deck.get(0);
        deck.remove(0);
        return card;
    }
}
