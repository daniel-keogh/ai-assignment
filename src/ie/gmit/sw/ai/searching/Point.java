package ie.gmit.sw.ai.searching;

/**
 * Denotes a single point on the grid.
 */
public class Point {
    private final int row;
    private final int column;

    public Point(int row, int column) {
        this.row = row;
        this.column = column;
    }

    public int row() {
        return row;
    }

    public int column() {
        return column;
    }
}
