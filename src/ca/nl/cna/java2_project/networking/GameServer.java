package ca.nl.cna.java2_project.networking;

import ca.nl.cna.java2_project.card_game.Player;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.LinkedList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class GameServer{
    //TODO change to 4 players on completion
    public static final int NUMBER_PLAYERS = 2;

    /**
     * Main server loop
     * @param args
     */
    public static void main(String[] args) {

        int portNumber = 4401;
        boolean listening = true;
        LinkedList<Player> playersList = new LinkedList<>();


        //Track the connections
        LinkedList<Socket> clientSocketList = new LinkedList<>();
        LinkedList<ObjectOutputStream> outputStreamList = new LinkedList<>();
        LinkedList<ObjectInputStream> inputStreamList = new LinkedList<>();

        //Wait until we have all the connections we need
        try (ServerSocket serverSocket = new ServerSocket(portNumber)) {
            while (listening & clientSocketList.size() < NUMBER_PLAYERS) {
                Socket socket = serverSocket.accept();
                ObjectOutputStream output = new ObjectOutputStream(socket.getOutputStream());
                ObjectInputStream input = new ObjectInputStream(socket.getInputStream());
                clientSocketList.add(socket);
                outputStreamList.add(output);
                inputStreamList.add(input);

                boolean validName = false;
                do {
                    Player player = (Player) input.readObject();
                    if (isValidName(player.getName())) {
                        playersList.add(player);
                        output.writeObject(true);
                        output.writeObject(player.getName() + " added to game.");
                        validName = true;
                    } else {
                        output.writeObject(false);
                        output.writeObject("Invalid name, please try again (4-10 characters).");
                    }
                }while (!validName);
            }
        } catch (IOException e) {
            System.err.println("Could not listen on port " + portNumber);
            System.exit(-1);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }

        //Debug - be sure you got the connections you wanted
        System.out.printf("Connections: %d", clientSocketList.size());

        //Start the game
        GameProtocol gameProtocol = new GameProtocol(playersList);

        //Create the threads
        for (int i = 0; i < clientSocketList.size(); i++) {
            new GameServerThread(gameProtocol, clientSocketList.get(i), playersList.get(i), outputStreamList.get(i), inputStreamList.get(i)).start();
        }

        //TODO do a while on the game loop
        System.out.println("Back here again?");
        while (!gameProtocol.isGameOver()){
            if (gameProtocol.getGameStatus() == GameProtocol.Status.GAME_OVER) {
                System.out.println("game over");
            }
        }

        //TODO print the results
        gameProtocol.printResults();
    }

    public static boolean isValidName(String name){
        Pattern namePattern = Pattern.compile("^[a-zA-Z]{4,10}$");
        Matcher nameMatcher = namePattern.matcher(name);
        return nameMatcher.matches();
    }
}
