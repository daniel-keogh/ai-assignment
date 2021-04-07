package ie.gmit.sw.ai;

import java.util.concurrent.ThreadLocalRandom;

import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.util.Duration;

public class GameView extends Canvas {
    public static final int DEFAULT_VIEW_SIZE = 600;
    private static final int OFFSET = 48; // The number 0 is ASCII 48
    private static final ThreadLocalRandom rand = ThreadLocalRandom.current();

    private static final Color PLAYER_COLOUR = Color.YELLOW;
    private static final Color BACKGROUND_COLOUR = Color.GREY;
    private static final Color HEDGE_COLOUR = Color.GREEN;
    private final Color[] reds = {Color.SALMON, Color.CRIMSON, Color.RED}; // Animate enemy "dots" to make them easier to see

    private final int cellPadding = 2;
    private final GameModel model;
    private final Player player;

    private int currentRow;
    private int currentCol;
    private boolean zoom = false;
    private Sprite[] sprites;

    public GameView(GameModel model) {
        super(DEFAULT_VIEW_SIZE, DEFAULT_VIEW_SIZE);
        this.model = model;
        this.player = Player.getInstance();

        Timeline timeline = new Timeline(new KeyFrame(Duration.millis(300), e -> draw()));
        timeline.setCycleCount(Animation.INDEFINITE);
        timeline.play();
    }

    public void setCurrentRow(int row) {
        if (row < cellPadding) {
            currentRow = cellPadding;
        } else {
            currentRow = Math.min(row, (model.size() - 1) - cellPadding);
        }
        player.setCurrentRow(currentRow);
    }

    public void setCurrentCol(int col) {
        if (col < cellPadding) {
            currentCol = cellPadding;
        } else {
            currentCol = Math.min(col, (model.size() - 1) - cellPadding);
        }
        player.setCurrentCol(currentCol);
    }

    public void draw() {
        GraphicsContext g = super.getGraphicsContext2D();

        int cellspan = zoom ? model.size() : 5;  // 5x5 default view
        final int size = DEFAULT_VIEW_SIZE / cellspan;

        g.setFill(BACKGROUND_COLOUR);
        g.fillRect(0, 0, GameView.DEFAULT_VIEW_SIZE, GameView.DEFAULT_VIEW_SIZE);

        for (int row = 0; row < cellspan; row++) {
            for (int col = 0; col < cellspan; col++) {
                int x1 = col * size;
                int y1 = row * size;

                char ch = model.get(row, col);
                if (zoom) {
                    if (ch > '0') {
                        g.setFill(row == currentRow && col == currentCol ? PLAYER_COLOUR : reds[rand.nextInt(reds.length)]);
                        g.fillRect(x1, y1, size, size);
                    }
                } else {
                    ch = model.get(currentRow - cellPadding + row, currentCol - cellPadding + col);
                }

                int imageIndex = ch;
                imageIndex -= OFFSET;

                if (imageIndex < 0) {
                    g.setFill(BACKGROUND_COLOUR); // Empty cell
                    g.fillRect(x1, y1, size, size);
                } else if (imageIndex == 0) {
                    g.setFill(HEDGE_COLOUR); // Hedge is 0
                    g.fillRect(x1, y1, size, size);
                } else {
                    g.drawImage(sprites[imageIndex - 1].getNext(), x1, y1);
                }
            }
        }
    }

    public void toggleZoom() {
        zoom = !zoom;
    }

    public void setSprites(Sprite[] sprites) {
        this.sprites = sprites;
    }
}