package ie.gmit.sw.ai.utils;

public final class Maths {

    private Maths() { }

    /**
     * Get the distance between to points.
     */
    public static double distance(double x1, double y1, double x2, double y2) {
        return Math.sqrt((x2 - x1) * (x2 - x1) + (y2 - y1) * (y2 - y1));
    }
}
