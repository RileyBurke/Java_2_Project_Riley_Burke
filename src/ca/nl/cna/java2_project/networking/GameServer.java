package ca.nl.cna.java2_project.networking;

import ca.nl.cna.java2_project.card_game.Player;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.LinkedList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 *
 */
public class GameServer{
    public static final int NUMBER_PLAYERS = 4;

    /**
     * Main server loop.
     *
     * @param args The command line arguments.
     */
    public static void main(String[] args) throws IOException {

        int portNumber = 4401;
        boolean listening = true;
        LinkedList<Player> playersList = new LinkedList<>();

        LinkedList<Socket> clientSocketList = new LinkedList<>();
        LinkedList<ObjectOutputStream> outputStreamList = new LinkedList<>();
        LinkedList<ObjectInputStream> inputStreamList = new LinkedList<>();

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

        System.out.printf("Connections: %d", clientSocketList.size());

        GameProtocol gameProtocol = new GameProtocol(playersList);

        LinkedList<GameServerThread> gameThreadList = new LinkedList<>();
//        for (int i = 0; i < clientSocketList.size(); i++) {
//            new GameServerThread(gameProtocol, clientSocketList.get(i), playersList.get(i), outputStreamList.get(i), inputStreamList.get(i));
//        }

        ExecutorService executor = Executors.newFixedThreadPool(NUMBER_PLAYERS);

        for (int i = 0; i < NUMBER_PLAYERS; i++) {
            gameThreadList.add (new GameServerThread(gameProtocol, clientSocketList.get(i), playersList.get(i), outputStreamList.get(i), inputStreamList.get(i)));
            executor.execute(gameThreadList.get(i));
        }

        while (listening){
            if (gameProtocol.getGameStatus() == GameProtocol.Status.GAME_OVER) {
                System.out.println("game over");
                for (ObjectOutputStream outputStream: outputStreamList){
                    outputStream.writeObject(gameProtocol.getGameResults());
                }
                break;
            }
        }
    }

    /**
     * Checks the Player's name against a regular expression to ensure validity to enter the game.
     *
     * @param name The player's name to check for validity.
     * @return Boolean value, True if the name matches the regular expression.
     */
    public static boolean isValidName(String name){
        Pattern namePattern = Pattern.compile("^[a-zA-Z]{4,10}$");
        Matcher nameMatcher = namePattern.matcher(name);
        return nameMatcher.matches();
    }
}
