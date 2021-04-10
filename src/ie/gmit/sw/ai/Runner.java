package ie.gmit.sw.ai;

import ie.gmit.sw.ai.npc.NpcBehaviour;
import javafx.application.Application;

import java.io.IOException;

/**
 * Main entry point to the application.
 */
public class Runner {
    public static void main(String[] args) {
        NpcBehaviour npcBehaviour = NpcBehaviour.getInstance();

        // Train the model if it doesn't exist already
        if (!npcBehaviour.networkExists()) {
            try {
                npcBehaviour.train(true);
            } catch (IOException e) {
                System.err.println("[Error]: Failed to save neural network");
                e.printStackTrace();
            }
        }

        Application.launch(GameWindow.class, args);
    }
}