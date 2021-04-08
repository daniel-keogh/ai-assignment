package ie.gmit.sw.ai;

import ie.gmit.sw.ai.npc.ChaseBehaviour;
import javafx.application.Application;

/**
 * Main entry point to the application.
 */
public class Runner {
    public static void main(String[] args) {
        ChaseBehaviour.train();
//        Application.launch(GameWindow.class, args);
    }
}