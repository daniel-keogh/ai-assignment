package ie.gmit.sw.ai.utils;

import java.util.Arrays;

public class NNUtils {

    private NNUtils() {
    }

    /**
     * Normalize the input variables to values within the range [lower..upper]
     */
    public static double[][] normalize(double[][] matrix, double lower, double upper) {
        double[][] normalized = new double[matrix.length][];
        
        for (int row = 0; row < matrix.length; row++) {
            // Feed each row to the overloaded method
            normalized[row] = normalize(matrix[row], lower, upper);
        }

        return normalized;
    }

    /**
     * Normalize the input variables to values within the range [lower..upper]
     */
    public static double[] normalize(double[] vector, double lower, double upper) {
        double[] normalized = new double[vector.length];
        double max = Arrays.stream(vector).max().getAsDouble();
        double min = Arrays.stream(vector).min().getAsDouble();

        for (int i = 0; i < normalized.length; i++) {
            normalized[i] = (vector[i] - min) * (upper - lower) / (max - min) + lower;
        }

        return normalized;
    }
}
