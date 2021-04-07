package ie.gmit.sw.ai.utils;

import ie.gmit.sw.ai.GameModel;

public class ModelUtils {
    private ModelUtils() {
    }

    public static int[][] toIntArray(char[][] model) {
        int[][] matrix = new int[60][60];

        for (int i = 0; i < model.length; i++) {
            for (int j = 0; j < model[i].length; j++) {
                if (model[i][j] == GameModel.HEDGE) {
                    matrix[i][j] = 0;
                } else {
                    matrix[i][j] = 1;
                }
            }
        }

        return matrix;
    }
}
