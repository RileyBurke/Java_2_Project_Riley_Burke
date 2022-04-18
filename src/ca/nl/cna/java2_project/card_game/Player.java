package ca.nl.cna.java2_project.card_game;

import java.io.Serializable;
import java.util.LinkedList;
import java.util.StringJoiner;

/**
 * Class representing a Player playing a game of One Hundreds.
 */
public class Player implements Serializable {
    private String name;
    private final LinkedList<Card> hand;

    /**
     * Initializes a Player with their name and an empty LinkedList for their hand.
     *
     * @param name The player's name.
     */
    public Player(String name){
        this.name = name;
        this.hand = new LinkedList<>();
    }

    /**
     * Gets the player's name.
     *
     * @return The player's name.
     */
    public String getName() {
        return name;
    }

    /**
     * Allows changes to the player name.
     *
     * @param name The player's name.
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Prints all cards contained in the player's hand with their value and wildcard status.
     */
    public void printHand(){
        StringJoiner cardJoiner = new StringJoiner(", ");

        for (Card card: this.hand) {
            cardJoiner.add((card.getCardValue() + (card.isWildcard() ? " - Wildcard" : "")));
        }
        String hand = cardJoiner.toString();
        System.out.println(hand);
    }

    /**
     * Adds a card to the player's hand from the given deck.
     *
     * @param deck The deck of cards used in a game of One Hundreds.
     */
    public void drawCard(CardDeck deck){
        hand.add(deck.draw());
    }

    /**
     * Gets the top card from the player's hand.
     *
     * @return The top card from the player's hand.
     */
    public Card revealCard(){
        return hand.get(0);
    }

    public Card revealCard(int i){
        return hand.get(i);
    }

    public String getTopCardString(){
        return hand.get(hand.size() - 1).getCardValue() + (hand.get(hand.size() - 1).isWildcard() ? " - Wildcard" : "");
    }

    /**
     * Checks if a player's hand contains a specific Card.
     *
     * @param card Card to be checked for in the player's hand.
     * @return Boolean value. True if the hand contains the card, false otherwise.
     */
    public boolean hasCard(Card card){
        return hand.contains(card);
    }

    /**
     * Checks if the Player's hand has any cards left in it.
     *
     * @return Boolean value, true if hand has any cards in it.
     */
    public boolean hadCardsRemaining(){
        return hand.size() > 0;
    }

    /**
     * Removes the top card from the player's hand.
     */
    public void discardCard(){
        hand.remove();
    }

    /**
     * Returns the size of the player's hand.
     *
     * @return The size of the player's hand.
     */
    public int getHandSize(){
        return hand.size();
    }

    /**
     * Empties the hand of all cards.
     */
    public void clearHand(){
        hand.clear();
    }

    /**
     * Adds a specified Card to the Player's hand.
     *
     * @param card A Card object to be put into the hand.
     */
    public void addCard(Card card){
        hand.add(card);
    }

    /**
     * Get the players hand.
     *
     * @return LinkedList of all Cards in the player's hand.
     */
    public LinkedList<Card> getHand() {
        return hand;
    }
}
