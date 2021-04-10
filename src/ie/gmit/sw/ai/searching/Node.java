package ie.gmit.sw.ai.searching;

import java.util.Stack;

public class Node {
    private final Point point;
    private final Node parent;

    public Node(Point point, Node parent) {
        this.point = point;
        this.parent = parent;
    }

    public Point point() {
        return point;
    }

    public Node parent() {
        return parent;
    }

    /**
     * Follows the parent nodes to get the route from the topmost node to the end.
     *
     * @return A {@link Stack} of nodes in the order they should be travelled.
     */
    public Stack<Node> toRoute() {
        Stack<Node> stack = new Stack<>();

        Node next = this;
        while (next.parent != null) {
            stack.push(next.parent);
            next = next.parent;
        }

        return stack;
    }
}
