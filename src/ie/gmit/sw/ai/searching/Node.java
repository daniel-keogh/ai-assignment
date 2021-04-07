package ie.gmit.sw.ai.searching;

import java.util.Stack;

public record Node(Point point, Node parent) {

    /**
     * Follows the parent nodes to get the route from the top node to the end.
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
