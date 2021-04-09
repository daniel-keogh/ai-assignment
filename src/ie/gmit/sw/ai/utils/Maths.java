package ie.gmit.sw.ai.utils;

import ie.gmit.sw.ai.searching.Point;

public final class Maths {

    private Maths() {
    }

    /**
     * Gets the distance between two points.
     */
    public static double distance(double x1, double y1, double x2, double y2) {
        return Math.sqrt((x2 - x1) * (x2 - x1) + (y2 - y1) * (y2 - y1));
    }

    /**
     * Gets the distance between two {@link Point}s.
     */
    public static double distance(Point a, Point b) {
        return Math.sqrt((a.row() - b.row()) * (a.row() - b.row()) + (a.column() - b.column()) * (a.column() - b.column()));
    }
}
