package ca.nl.cna.java2_project.card_game;

import java.io.Serializable;

/**
 * Class representing a card for use in a deck of cards for playing One Hundreds.
 */
public class Card implements Comparable<Card>, Serializable {
    private final int cardValue;
    private boolean wildcard;

    /**
     * Initializes a card with its number value and wildcard status.
     *
     * @param cardValue The integer value of the card (1-100).
     * @param wildcard The wildcard status of a card.
     */
    public Card(int cardValue, boolean wildcard){
        this.cardValue = cardValue;
        this.wildcard = wildcard;
    }

    /**
     * Gets the number value of the card.
     *
     * @return The integer value of the card (1-100).
     */
    public int getCardValue() {
        return cardValue;
    }

    /**
     * Returns a boolean value based on whether the card is a wildcard or not.
     *
     * @return The wildcard status of a card.
     */
    public boolean isWildcard() {
        return wildcard;
    }

    /**
     * Allows changes to the wildcard status of a Card.
     *
     * @param wildcard The wildcard status of a card.
     */
    public void setWildcard(boolean wildcard) {
        this.wildcard = wildcard;
    }

    /**
     * Method used to compare cards with one another for sorting and determining a winner.
     *
     * @param otherCard Another Card to be compared against.
     * @return An integer value based upon the games winning criteria.
     */
    @Override
    public int compareTo(Card otherCard) {
        if (wildcard && otherCard.isWildcard()){        //Lower card value wins
            if (cardValue < otherCard.getCardValue()){
                return 1;
            }else{
                return -1;
            }
        }else if(wildcard && !otherCard.isWildcard()){  //This card wins
            return 1;
        }else if(!wildcard && otherCard.isWildcard()){  //Other card wins
            return -1;
        }else{                                          //Higher card value wins
            if (cardValue > otherCard.getCardValue()){
                return 1;
            }else{
                return -1;
            }
        }
    }

    /**
     * Allows Card objects to be printed as a String.
     *
     * @return A string representation of the Card object.
     */
    public String toString(){
        return this.cardValue + (this.isWildcard() ? ": Wildcard" : "");
    }
}
