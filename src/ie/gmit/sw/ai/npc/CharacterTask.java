package ie.gmit.sw.ai.npc;

import ie.gmit.sw.ai.GameModel;
import ie.gmit.sw.ai.searching.Point;
import javafx.concurrent.Task;

import java.util.Optional;

/**
 * CharacterTask represents a Runnable game character. The character wanders around the
 * game model randomly and can interact with other game characters using
 * implementations of the Command interface that it is composed with.
 */
public class CharacterTask extends Task<Void> {
    private static final int SLEEP_TIME = 300; // Sleep for 300 ms

    private final char enemyID;
    private final GameModel model;
    private final Command cmd;

    private boolean alive = true;

    public CharacterTask(GameModel model, char enemyID, Command cmd) {
        this.model = model;
        this.cmd = cmd;
        this.enemyID = enemyID;
    }

    /**
     * Kills the character.
     */
    public void kilL() {
        alive = false;

        // Remove the character from the view
        Optional<Point> pos = model.getPositionById(enemyID);

        if (pos.isPresent()) {
            Point p = pos.get();
            model.set(p.row(), p.column(), GameModel.PATH);
        }
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