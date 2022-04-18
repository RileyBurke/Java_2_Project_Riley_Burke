package ca.nl.cna.java2_project.networking;

import ca.nl.cna.java2_project.card_game.Card;
import ca.nl.cna.java2_project.card_game.Player;

import java.io.*;
import java.net.Socket;
import java.net.UnknownHostException;

public class GameClient{

    public static void main(String[] args) {
        Thread thread = Thread.currentThread();

        String hostName = "localhost";
        int portNumber = 4401;

        try (Socket gameSocket = new Socket(hostName, portNumber);
             ObjectOutputStream output = new ObjectOutputStream(gameSocket.getOutputStream());
             ObjectInputStream input = new ObjectInputStream(gameSocket.getInputStream());
             BufferedReader br = new BufferedReader(new InputStreamReader(System.in))
        ) {
            System.out.println("Welcome to One Hundreds!");
            boolean validName = false;
            do {
                System.out.print("Please enter a name: ");
                String name = br.readLine();
                output.writeObject(new Player(name));
                if (input.readObject().equals(true)){
                    System.out.println(input.readObject());
                    validName = true;
                }else{
                    System.out.println(input.readObject());
                }
            }while (!validName);


            Player player = (Player) input.readObject();
            player.printHand();

            boolean cardSent = false;
            String winnerString = "";
            String playAgain = "y";

            do {
                if (player.hadCardsRemaining()){
                    if (!cardSent){
                        Card card = player.revealCard();
                        System.out.printf("You play a %d%s.%n", card.getCardValue(), card.isWildcard() ? " - Wildcard" : "");
                        output.writeObject(card);
                        cardSent = true;
                    }
                    winnerString = (String) input.readObject();
                    if (winnerString != null && cardSent){
                        System.out.println(winnerString);
                        player.discardCard();
                        cardSent = false;
                    }
                }else{
                    System.out.println("GAME OVER\n");
                    String finalResults = (String) input.readObject();
                    System.out.println(finalResults);
                    System.out.println("Play again (y/n)?: ");
                    playAgain = br.readLine();
                    if (playAgain.equalsIgnoreCase("y")){
                        output.writeObject(true);
                    }else{
                        output.writeObject(false);
                    }
                }
            }while(playAgain.equalsIgnoreCase("y"));

        }
        catch (UnknownHostException e) {
            System.err.println("Don't know about host " + hostName);
            System.exit(1);
        } catch (IOException e) {
            System.err.println("Couldn't get I/O for the connection to " + hostName);
            e.printStackTrace();
            System.exit(1);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }


    }
}