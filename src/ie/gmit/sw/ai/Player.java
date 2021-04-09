package ie.gmit.sw.ai;

import ie.gmit.sw.ai.searching.Point;

/**
 * Singleton object that provides a handle on the Player's current state.
 */
public final class Player {
    private static final Player instance = new Player();

    public static final int STARTING_HEALTH = 200;

    private int currentRow;
    private int currentCol;
    private int health = STARTING_HEALTH;

    private Player() {
    }

    public static Player getInstance() {
        return instance;
    }

    public int getCurrentRow() {
        return currentRow;
    }

    public void setCurrentRow(int currentRow) {
        this.currentRow = currentRow;
    }

    public int getCurrentCol() {
        return currentCol;
    }

    public void setCurrentCol(int currentCol) {
        this.currentCol = currentCol;
    }

    public Point getPosition() {
        return new Point(currentRow, currentCol);
    }

    public int getHealth() {
        return health;
    }

    public void setHealth(int health) {
        this.health = Math.max(health, 0);
    }

    public void reduceHealth(int amount) {
        setHealth(health - amount);
    }

    @Override
    public String toString() {
        return String.format("[Player]: (%d, %d)", currentRow, currentCol);
    }
}
