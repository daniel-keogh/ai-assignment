package ie.gmit.sw.ai.searching;

import java.util.LinkedList;
import java.util.Queue;

public class BFS {
    public int rowSize;
    public int colSize;

    private static final int[] rowNum = {-1, 0, 0, 1};
    private static final int[] colNum = {0, -1, 1, 0};

    public BFS(int rowSize, int colSize) {
        this.rowSize = rowSize;
        this.colSize = colSize;
    }

    public Node search(int[][] mat, Point src, Point dest) {
        if (mat[src.row()][src.column()] != 1 || mat[dest.row()][dest.column()] != 1) {
            return null;
        }

        boolean[][] visited = new boolean[rowSize][colSize];

        // Mark the source cell as visited
        visited[src.row()][src.column()] = true;

        // Create a queue for BFS
        Queue<Node> q = new LinkedList<>();

        // Distance of source cell is 0
        Node s = new Node(src, null);
        q.add(s); // Enqueue source cell

        // Do a BFS starting from source cell
        while (!q.isEmpty()) {
            Node curr = q.peek();
            Point pt = curr.point();

            // If we have reached the destination cell,
            // we are done
            if (pt.row() == dest.row() && pt.column() == dest.column())
                return curr;

            // Otherwise dequeue the front cell
            // in the queue and enqueue
            // its adjacent cells
            q.remove();

            for (int i = 0; i < 4; i++) {
                int row = pt.row() + rowNum[i];
                int col = pt.column() + colNum[i];

                // if adjacent cell is valid, has path
                // and not visited yet, enqueue it.
                if (isValid(row, col) && mat[row][col] == 1 && !visited[row][col]) {
                    // mark cell as visited and enqueue it
                    visited[row][col] = true;
                    q.add(new Node(new Point(row, col), curr));
                }
            }
        }

        return null;
    }

    public boolean isValid(int row, int col) {
        return (row >= 0) && (row < rowSize) && (col >= 0) && (col < colSize);
    }
}
