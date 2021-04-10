package ie.gmit.sw.ai.npc;

import ie.gmit.sw.ai.GameModel;
import ie.gmit.sw.ai.Player;
import ie.gmit.sw.ai.searching.BFS;
import ie.gmit.sw.ai.searching.Node;
import ie.gmit.sw.ai.searching.Point;
import ie.gmit.sw.ai.utils.Maths;
import ie.gmit.sw.ai.utils.Random;

import java.util.Optional;
import java.util.Stack;
import java.util.concurrent.ThreadLocalRandom;

/**
 * This class represents an autonomous character.
 */
public class Npc implements Command {

    public static final int MAX_STRENGTH = 100;
    public static final int MAX_ENERGY = 100;

    private static final ThreadLocalRandom rand = ThreadLocalRandom.current();

    private int currentRow;
    private int currentCol;

    private int energy = 100;
    private int energyDelta = 1;
    private final int strength = Random.generate(MAX_STRENGTH);

    private final char enemyId;
    private final GameModel model;
    private final int[][] modelAsIntArray;
    private final Player player;
    private final ChaseBehaviour chaseBehaviour;

    private Point target = null;
    private Action currentAction;
    private Stack<Node> route = new Stack<>();

    private final NpcWeapon npcAttack = new NpcWeapon();

    public Npc(char enemyId, int startRow, int startCol, GameModel model) {
        this.enemyId = enemyId;
        this.model = model;
        this.modelAsIntArray = model.toIntArray();
        this.currentRow = startRow;
        this.currentCol = startCol;
        player = Player.getInstance();
        chaseBehaviour = ChaseBehaviour.getInstance();
    }

    public Npc(char enemyId, int startRow, int startCol, GameModel model, int energyDelta) {
        this(enemyId, startRow, startCol, model);
        this.energyDelta = energyDelta;
    }

    @Override
    public void execute() {
        currentAction = chaseBehaviour.classify(player.getHealth(), energy, strength);
        updateCurrentPosition();
        searchForPlayer();
        updateEnergy();
        attack();
    }

    /**
     * Moves the character to a new position. This will be either the next row/col on their
     * current route or a random position if there's no route currently set.
     */
    private void updateCurrentPosition() {
        int temp_row;
        int temp_col;

        if (route.isEmpty()) {
            target = null;

            //Randomly pick a direction up, down, left or right
            temp_row = currentRow;
            temp_col = currentCol;

            if (rand.nextBoolean()) {
                temp_row += rand.nextBoolean() ? 1 : -1;
            } else {
                temp_col += rand.nextBoolean() ? 1 : -1;
            }
        } else {
            Node next = route.pop();
            temp_row = next.point().row();
            temp_col = next.point().column();
        }

        if (model.isValidMove(currentRow, currentCol, temp_row, temp_col, enemyId)) {
            model.set(temp_row, temp_col, enemyId);
            model.set(currentRow, currentCol, GameModel.PATH);
            currentCol = temp_col;
            currentRow = temp_row;
        }
    }

    /**
     * Update the NPC's energy.
     */
    private void updateEnergy() {
        if (energy <= 0) {
            energy = MAX_ENERGY;
        } else {
            energy -= energyDelta;
        }
    }

    /**
     * Search for the player and set the current route to the player's current position.
     */
    private void searchForPlayer() {
        if (target == null) {
            Point p = new Point(player.getCurrentRow(), player.getCurrentCol());
            Point c = new Point(currentRow, currentCol);

            Optional<Node> targetNote = new BFS().search(modelAsIntArray, c, p);

            if (targetNote.isPresent()) {
                Node node = targetNote.get();
                target = node.point();
                route = node.toRoute();
            }
        }
    }

    /**
     * Attack the player if they are next to the NPC.
     */
    private void attack() {
        double distance = Maths.distance(currentRow, currentCol, player.getCurrentRow(), player.getCurrentCol());

        // If distance is 1, the NPC is beside the player
        if (distance <= 1) {
            int damage = (int) npcAttack.getDamage(strength, energy);
            player.reduceHealth(damage);
            System.out.printf("NPC %c attacking the player (damage: %d)...\n", enemyId, damage);
        }
    }

    @Override
    public String toString() {
        return String.format("[Patrol]: (%d, %d), energy=%d", currentRow, currentCol, energy);
    }
}
