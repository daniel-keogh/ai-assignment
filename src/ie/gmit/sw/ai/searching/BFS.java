package ie.gmit.sw.ai.searching;

import java.util.LinkedList;
import java.util.Optional;
import java.util.Queue;

/**
 * This class performs a Breath-First Search on a 2D array / matrix.
 * This implementation is adapted from the solution for finding the shortest path in a binary maze
 * by PrinciRaj1992, linked below.
 *
 * @see <a href="https://www.geeksforgeeks.org/shortest-path-in-a-binary-maze/">https://www.geeksforgeeks.org/shortest-path-in-a-binary-maze/</a>
 */
public final class BFS {
    public int rowSize;
    public int colSize;

    private static final int[] rowNum = {-1, 0, 0, 1};
    private static final int[] colNum = {0, -1, 1, 0};

    /**
     * Performs a BFS on the given 2D matrix.
     *
     * @param matrix The search matrix
     * @param src    The point to begin searching from
     * @param dest   The point to search for
     * @return The found Node if it exists
     * @throws IllegalArgumentException if the matrix is empty
     */
    public Optional<Node> search(int[][] matrix, Point src, Point dest) {
        if (matrix.length == 0 || matrix[0].length == 0) {
            throw new IllegalArgumentException("matrix cannot be empty");
        } else {
            rowSize = matrix.length;
            colSize = matrix[0].length;
        }

        if (matrix[src.row()][src.column()] != 1 || matrix[dest.row()][dest.column()] != 1) {
            return Optional.empty();
        }

        // Create a queue for BFS
        Queue<Node> q = new LinkedList<>();

        boolean[][] visited = new boolean[rowSize][colSize];
        visited[src.row()][src.column()] = true;

        // Enqueue source cell as a Node
        Node s = new Node(src, null);
        q.add(s);

        // Do a BFS starting from source cell
        while (!q.isEmpty()) {
            Node current = q.peek();
            Point point = current.point();

            // Check if we have reached the destination cell
            if (point.row() == dest.row() && point.column() == dest.column()) {
                return Optional.of(current);
            }

            // Otherwise dequeue the front cell in the queue and enqueue its adjacent cells
            q.remove();

            for (int i = 0; i < 4; i++) {
                int row = point.row() + rowNum[i];
                int col = point.column() + colNum[i];

                // if adjacent cell is valid, has a path and is not visited yet, enqueue it
                if (isValid(row, col) && matrix[row][col] == 1 && !visited[row][col]) {
                    visited[row][col] = true;
                    q.add(new Node(new Point(row, col), current));
                }
            }
        }

        return Optional.empty();
    }

    /**
     * Checks whether given cell is valid or not.
     */
    private boolean isValid(int row, int col) {
        return (row >= 0) && (row < rowSize) && (col >= 0) && (col < colSize);
    }
}
