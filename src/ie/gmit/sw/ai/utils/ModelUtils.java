package ie.gmit.sw.ai.utils;

import ie.gmit.sw.ai.GameModel;

public class ModelUtils {
    private ModelUtils() {
    }

    /**
     * Converts the game model from a 2D char array to a 2D int array,
     * where 1 is a path and 0 is a hedge.
     *
     * @param model The game model
     * @return The model as a 2D array of integers
     * @throws IllegalArgumentException if the model is empty
     */
    public static int[][] toIntArray(char[][] model) {
        if (model.length == 0 || model[0].length == 0) {
            throw new IllegalArgumentException("model cannot be empty");
        }

        int[][] matrix = new int[model.length][model[0].length];

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
