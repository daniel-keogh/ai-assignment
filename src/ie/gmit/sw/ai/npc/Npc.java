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
    private final int strength = Random.generate(MAX_STRENGTH);
    private final NpcWeapon npcAttack = new NpcWeapon();
    private final char enemyId;
    private final GameModel model;
    private final int[][] modelAsIntArray;
    private final Player player;
    private final NpcBehaviour npcBehaviour;
    private Point target = null;
    private Action currentAction;
    private Stack<Node> route = new Stack<>();

    public Npc(char enemyId, int startRow, int startCol, GameModel model) {
        this.enemyId = enemyId;
        this.model = model;
        this.currentRow = startRow;
        this.currentCol = startCol;
        modelAsIntArray = model.toIntArray();
        player = Player.getInstance();
        npcBehaviour = NpcBehaviour.getInstance();
    }

    @Override
    public void execute() {
        // Decide on an Action to take
        currentAction = npcBehaviour.classify(player.getHealth(), energy, strength);

        if (currentAction == Action.CHASE) {
            updateCurrentPosition();
            searchForPlayer();
        } else {
            target = null; // stop chasing
        }

        updateEnergy();
        attack();
    }

    /**
     * Moves the character to a new position. This will be either the next row/col on their
     * current route, or a random position (if there's no route currently set).
     */
    private void updateCurrentPosition() {
        int temp_row;
        int temp_col;

        if (route.isEmpty()) {
            target = null;

            // Randomly pick a direction up, down, left or right
            temp_row = currentRow;
            temp_col = currentCol;

            if (rand.nextBoolean()) {
                temp_row += rand.nextBoolean() ? 1 : -1;
            } else {
                temp_col += rand.nextBoolean() ? 1 : -1;
            }
        } else {
            System.out.printf("NPC %c chasing the player...\n", enemyId);
            Node next = route.pop();
            temp_row = next.point().row();
            temp_col = next.point().column();
        }

        // Move the character
        if (model.isValidMove(currentRow, currentCol, temp_row, temp_col, enemyId)) {
            model.set(temp_row, temp_col, enemyId);
            model.set(currentRow, currentCol, GameModel.PATH);
            currentCol = temp_col;
            currentRow = temp_row;
        }
    }

    /**
     * Update the NPC's energy, depending on whether they're chasing/resting.
     */
    private void updateEnergy() {
        final int chaseEnergyDelta = 2;
        final int restEnergyDelta = 25;

        if (currentAction == Action.REST) {
            System.out.printf("NPC %c resting...\n", enemyId);
            energy += restEnergyDelta;
            if (energy > MAX_ENERGY) {
                energy = MAX_ENERGY;
            }
        } else {
            energy -= chaseEnergyDelta;
            if (energy < 0) {
                energy = 0;
            }
        }
    }

    /**
     * Search for the player and set the NPC's route to the player's current position.
     */
    private void searchForPlayer() {
        if (target == null) {
            Point playerPos = new Point(player.getCurrentRow(), player.getCurrentCol());
            Point currentPos = new Point(currentRow, currentCol);

            Optional<Node> targetNote = new BFS().search(modelAsIntArray, currentPos, playerPos);

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
        return String.format("[NPC]: (%d, %d), energy=%d, strength=%d", currentRow, currentCol, energy, strength);
    }
}
