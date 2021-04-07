package ie.gmit.sw.ai;

public final class Player {
    private static final Player instance = new Player();

    private int currentRow;
    private int currentCol;

    private Player() { }

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

    public static Player getInstance() {
        return instance;
    }

    @Override
    public String toString() {
        return String.format("[Player]: (%d, %d)", currentRow, currentCol);
    }
}
