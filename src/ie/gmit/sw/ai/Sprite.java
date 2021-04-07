package ie.gmit.sw.ai;

import javafx.scene.image.Image;

import java.util.Objects;

/**
 * Sprite container for the game.
 */
public class Sprite {
    private final String name;     // The name of this sprite
    private final Image[] frames;  // The set of image frames to animate
    private int index;             // Initial starting index in array

    public Sprite(String name, String... images) throws Exception {
        this.name = name;
        this.index = 0;                          // Initialise the starting index to zero
        this.frames = new Image[images.length];  // Initialise the image frames

        // Read in each image as a BufferedImage
        for (int i = 0; i < images.length; i++){
            frames[i] = new Image(Objects.requireNonNull(getClass().getResource(images[i])).toString());
        }
    }

    /**
     * Returns the next image frame.
     */
    public Image getNext() {
        int idx = index;
        if (index < frames.length - 1){
            index++;
        } else {
            index = 0; // Circle back to the start of the array
        }
        return frames[idx];
    }

    public String getName() {
        return this.name;
    }
}