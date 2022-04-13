package ca.nl.cna.java2_project.test_classes;

import ca.nl.cna.java2_project.networking.GameProtocol;

public class GameProtocolTester {

    public static void main(String[] args) {
        System.out.println("Demonstrating ENUM simplicity!");

        GameProtocol.Status gameStatus = GameProtocol.Status.NOT_STARTED;

        if(gameStatus.name().equals(GameProtocol.Status.NOT_STARTED)){
            System.out.println("Game not Started!");
        }

        gameStatus = GameProtocol.Status.GAME_OVER;

        if(gameStatus == GameProtocol.Status.GAME_OVER){
            System.out.println("Does this work? Game over!");
        }

        System.out.println("See how easy ENUMs are!");
    }
}
