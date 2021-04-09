package ie.gmit.sw.ai;

import ie.gmit.sw.ai.searching.Point;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.ToolBar;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.util.Timer;
import java.util.TimerTask;

/**
 * Main UI for the game.
 */
public class GameWindow extends Application {
    private static final char PLAYER_ID = '1';
    private static final char EXIT_ID = '7';
    private static final int DEFAULT_SIZE = 60;
    private static final int IMAGE_COUNT = 7;

    private Player player;
    private GameView view;
    private GameModel model;
    private int currentRow;
    private int currentCol;
    private double startTime;

    private final Label statusText = new Label();
    private final Label durationText = new Label();
    private final Timer timer = new Timer();

    @Override
    public void start(Stage stage) {
        model = new GameModel(DEFAULT_SIZE); // Create a model
        view = new GameView(model);          // Create a view of the model
        player = Player.getInstance();
        startTime = System.currentTimeMillis();

        stage.setTitle("GMIT - B.Sc. in Computing (Software Development) - AI Assignment 2021");
        stage.setWidth(600);
        stage.setHeight(630);
        stage.setOnCloseRequest((e) -> Platform.exit());

        VBox box = new VBox();
        Scene scene = new Scene(box);
        scene.setOnKeyPressed(this::keyPressed); // Add a key listener
        stage.setScene(scene);

        Sprite[] sprites = getSprites(); // Load the sprites from the res directory
        view.setSprites(sprites);        // Add the sprites to the view
        placePlayer();                   // Add the player
        placeExit();                     // Add the exit

        box.getChildren().add(getToolbar());
        box.getChildren().add(view);

        view.draw(); // Paint the view

        // Display the window
        stage.show();
        stage.centerOnScreen();
    }

    @Override
    public void stop() {
        if (player.getHealth() <= 0) {
            System.out.printf("You died after %.0f seconds.\n", runtime());
        } else {
            System.out.printf("Game ended after %.0f seconds.\n", runtime());
        }
        model.tearDown();   // Shut down the executor service
        timer.cancel();     // Stop the toolbar timer
    }

    private ToolBar getToolbar() {
        ToolBar toolBar = new ToolBar();

        // Add a spacer between the labels
        // https://www.jackrutorial.com/2020/04/how-to-add-space-between-buttons-in-javafx.html
        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        // Schedule the status bar to update every second
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                refreshStatusBar();
            }
        }, 0, 1000);

        toolBar.getItems().add(statusText);
        toolBar.getItems().add(spacer);
        toolBar.getItems().add(durationText);

        return toolBar;
    }

    /**
     * Refreshes the labels in the window's status bar with the player's current health & duration of play.
     */
    private void refreshStatusBar() {
        Platform.runLater(() -> {
            String duration = String.format("Time: %.0f (s)", runtime());
            String health = "Player health: " + player.getHealth();

            if (player.getHealth() <= 0) {
                Platform.exit();
            }

            durationText.setText(duration);
            statusText.setText(health);
        });
    }

    /**
     * Handle key events.
     */
    public void keyPressed(KeyEvent e) {
        KeyCode key = e.getCode();

        if (key == KeyCode.RIGHT && currentCol < DEFAULT_SIZE - 1) {
            if (model.isValidMove(currentRow, currentCol, currentRow, currentCol + 1, PLAYER_ID)) currentCol++;
        } else if (key == KeyCode.LEFT && currentCol > 0) {
            if (model.isValidMove(currentRow, currentCol, currentRow, currentCol - 1, PLAYER_ID)) currentCol--;
        } else if (key == KeyCode.UP && currentRow > 0) {
            if (model.isValidMove(currentRow, currentCol, currentRow - 1, currentCol, PLAYER_ID)) currentRow--;
        } else if (key == KeyCode.DOWN && currentRow < DEFAULT_SIZE - 1) {
            if (model.isValidMove(currentRow, currentCol, currentRow + 1, currentCol, PLAYER_ID)) currentRow++;
        } else if (key == KeyCode.Z) {
            view.toggleZoom();
        } else {
            return;
        }

        updateView();
    }

    /**
     * Place the main player character at a random position.
     */
    private void placePlayer() {
        currentRow = (int) (DEFAULT_SIZE * Math.random());
        currentCol = (int) (DEFAULT_SIZE * Math.random());
        model.set(currentRow, currentCol, PLAYER_ID); // Player is at index 1
        updateView();
    }

    private void placeExit() {
        int exitRow = (int) (DEFAULT_SIZE * Math.random());
        int exitCol = (int) (DEFAULT_SIZE * Math.random());
        model.set(exitRow, exitCol, EXIT_ID);
        view.setExitPoint(new Point(exitRow, exitCol));
    }

    private void updateView() {
        view.setCurrentRow(currentRow);
        view.setCurrentCol(currentCol);
    }

    /**
     * Read in the images from the resources directory as sprites. Each sprite is
     * referenced by its index in the array, e.g. a 3 implies a Pink Enemy...
     * Ideally, the array should be dynamically created from the images...
     */
    private Sprite[] getSprites() {
        Sprite[] sprites = new Sprite[IMAGE_COUNT];
        sprites[0] = new Sprite("Player", "/res/player-0.png", "/res/player-1.png", "/res/player-2.png", "/res/player-3.png", "/res/player-4.png", "/res/player-5.png", "/res/player-6.png", "/res/player-7.png");
        sprites[1] = new Sprite("Red Enemy", "/res/red-0.png", "/res/red-1.png", "/res/red-2.png", "/res/red-3.png", "/res/red-4.png", "/res/red-5.png", "/res/red-6.png", "/res/red-7.png");
        sprites[2] = new Sprite("Pink Enemy", "/res/pink-0.png", "/res/pink-1.png", "/res/pink-2.png", "/res/pink-3.png", "/res/pink-4.png", "/res/pink-5.png", "/res/pink-6.png", "/res/pink-7.png");
        sprites[3] = new Sprite("Blue Enemy", "/res/blue-0.png", "/res/blue-1.png", "/res/blue-2.png", "/res/blue-3.png", "/res/blue-4.png", "/res/blue-5.png", "/res/blue-6.png", "/res/blue-7.png");
        sprites[4] = new Sprite("Red Green Enemy", "/res/gred-0.png", "/res/gred-1.png", "/res/gred-2.png", "/res/gred-3.png", "/res/gred-4.png", "/res/gred-5.png", "/res/gred-6.png", "/res/gred-7.png");
        sprites[5] = new Sprite("Orange Enemy", "/res/orange-0.png", "/res/orange-1.png", "/res/orange-2.png", "/res/orange-3.png", "/res/orange-4.png", "/res/orange-5.png", "/res/orange-6.png", "/res/orange-7.png");
        sprites[6] = new Sprite("Exit", "/res/exit.png");
        return sprites;
    }

    private double runtime() {
        return (System.currentTimeMillis() - startTime) / 1000;
    }
}