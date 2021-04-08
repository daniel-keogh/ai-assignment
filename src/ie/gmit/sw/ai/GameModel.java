package ie.gmit.sw.ai;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Optional;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadLocalRandom;

import ie.gmit.sw.ai.npc.CharacterTask;
import ie.gmit.sw.ai.npc.Npc;
import ie.gmit.sw.ai.searching.Point;
import javafx.concurrent.Task;

public class GameModel {
    private static final int MAX_CHARACTERS = 10;
    private final ThreadLocalRandom rand = ThreadLocalRandom.current();
    private final char[][] model;

    public static final char HEDGE = '\u0030'; // \u0030 = 0x30 = 0 (base 10) = A hedge
    public static final char PATH = '\u0020';

    private final ExecutorService exec = Executors.newFixedThreadPool(MAX_CHARACTERS, e -> {
        Thread t = new Thread(e);
        t.setDaemon(true);
        return t;
    });

    public GameModel(int dimension) {
        model = new char[dimension][dimension];
        init();
        carve();
        addGameCharacters();
    }

    public void tearDown() {
        exec.shutdownNow();
    }

    /**
     * Initialises the game model by creating an n row m array filled with hedge
     */
    private void init() {
        for (char[] chars : model) {
            Arrays.fill(chars, HEDGE);
        }
    }

    /**
     * Carve paths through the hedge to create passages.
     */
    public void carve() {
        for (int row = 0; row < model.length; row++) {
            for (int col = 0; col < model[row].length - 1; col++) {
                if (row == 0) {
                    model[row][col + 1] = PATH;
                } else if (col == model.length - 1) {
                    model[row - 1][col] = PATH;
                } else if (rand.nextBoolean()) {
                    model[row][col + 1] = PATH;
                } else {
                    model[row - 1][col] = PATH;
                }
            }
        }
    }

    private void addGameCharacters() {
        Collection<Task<Void>> tasks = new ArrayList<>();
        addGameCharacter(tasks, '\u0032', '0', 1); // 2 is a Red Enemy, 0 is a hedge
//        addGameCharacter(tasks, '\u0033', '0', MAX_CHARACTERS / 5); // 3 is a Pink Enemy, 0 is a hedge
//        addGameCharacter(tasks, '\u0034', '0', MAX_CHARACTERS / 5); // 4 is a Blue Enemy, 0 is a hedge
//        addGameCharacter(tasks, '\u0035', '0', MAX_CHARACTERS / 5); // 5 is a Red Green Enemy, 0 is a hedge
//        addGameCharacter(tasks, '\u0036', '0', MAX_CHARACTERS / 5); // 6 is a Orange Enemy, 0 is a hedge
        tasks.forEach(exec::execute);
    }

    private void addGameCharacter(Collection<Task<Void>> tasks, char enemyID, char replace, int number) {
        int counter = 0;
        while (counter < number) {
            int row = rand.nextInt(model.length);
            int col = rand.nextInt(model[0].length);

            if (model[row][col] == replace) {
                model[row][col] = enemyID;

                Npc npc = new Npc(enemyID, row, col, this);
                tasks.add(new CharacterTask(this, enemyID, npc));

                counter++;
            }
        }
    }

    public boolean isValidMove(int fromRow, int fromCol, int toRow, int toCol, char character) {
        if (toRow <= this.size() - 1 && toCol <= this.size() - 1 && this.get(toRow, toCol) == ' ') {
            this.set(fromRow, fromCol, '\u0020');
            this.set(toRow, toCol, character);
            return true;
        } else {
            return false; // Can't move
        }
    }

    public char[][] getModel() {
        return this.model;
    }

    public char get(int row, int col) {
        return this.model[row][col];
    }

    public void set(int row, int col, char c) {
        this.model[row][col] = c;
    }

    public int size() {
        return this.model.length;
    }

    /**
     * Converts the game model from a 2D char array to a 2D int array,
     * where 1 is a path and 0 is a hedge.
     *
     * @return The model as a 2D array of integers
     */
    public int[][] toIntArray() {
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

    public Optional<Point> getPositionById(char id) {
        for (int i = 0; i < model.length; i++) {
            for (int j = 0; j < model[i].length; j++) {
                if (model[i][j] == id) {
                    return Optional.of(new Point(i, j));
                }
            }
        }
        return Optional.empty();
    }
}