package ie.gmit.sw.ai.npc;

import ie.gmit.sw.ai.GameModel;
import javafx.concurrent.Task;

/**
 * CharacterTask represents a Runnable game character. The character wanders around the
 * game model and can interact with other game characters using
 * implementations of the Command interface that it is composed with.
 */
public class CharacterTask extends Task<Void> {
    private static final int SLEEP_TIME = 300; // Sleep for 300 ms

    private final GameModel model;
    private final Command cmd;

    private boolean alive = true;

    public CharacterTask(GameModel model, Command cmd) {
        this.model = model;
        this.cmd = cmd;
    }

    /**
     * Kills the character.
     */
    public void kill() {
        alive = false;
    }

    /**
     * This Task will remain alive until the call() method returns. This
     * cannot happen as long as the loop control variable "alive" is set
     * to true. You can set this value to false to "kill" the game
     * character if necessary.
     */
    @Override
    public Void call() throws Exception {
        while (alive) {
            Thread.sleep(SLEEP_TIME);

            synchronized (model) {
                cmd.execute();
            }
        }

        return null;
    }
}