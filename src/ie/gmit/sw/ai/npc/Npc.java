package ie.gmit.sw.ai.npc;

import ie.gmit.sw.ai.GameModel;
import ie.gmit.sw.ai.Player;
import ie.gmit.sw.ai.searching.BFS;
import ie.gmit.sw.ai.searching.Node;
import ie.gmit.sw.ai.searching.Point;
import ie.gmit.sw.ai.utils.Maths;
import net.sourceforge.jFuzzyLogic.FIS;
import net.sourceforge.jFuzzyLogic.FunctionBlock;
import net.sourceforge.jFuzzyLogic.rule.Variable;

import java.util.Optional;
import java.util.Stack;
import java.util.concurrent.ThreadLocalRandom;

public class Npc implements Command {

    private static final int MAX_ENERGY = 100;
    private static final int MAX_DISTANCE = 100;
    private static final ThreadLocalRandom rand = ThreadLocalRandom.current();
    private static final String FCL_FILE = "./src/resources/fuzzy/patrol.fcl";
    private static final FIS fis;

    private int currentRow;
    private int currentCol;
    private int energy = 100;
    private int energyDelta = 5;

    private final char enemyId;
    private final GameModel model;
    private final int[][] modelAsIntArray;
    private final Player player;

    private Point target = null;
    private Stack<Node> route = new Stack<>();

    static {
        fis = FIS.load(FCL_FILE, true);

        if (fis == null) {
            System.err.println("[Error] Unable to load FCL file: " + FCL_FILE);
            System.exit(1);
        }
    }

    public Npc(char enemyId, int startRow, int startCol, GameModel model) {
        this.enemyId = enemyId;
        this.model = model;
        this.modelAsIntArray = model.toIntArray();
        this.currentRow = startRow;
        this.currentCol = startCol;
        player = Player.getInstance();
    }

    public Npc(char enemyId, int startRow, int startCol, GameModel model, int energyDelta) {
        this(enemyId, startRow, startCol, model);
        this.energyDelta = energyDelta;
    }

    @Override
    public void execute() {
        setCurrentPosition();

        if (energy <= 0) {
            energy = MAX_ENERGY;
        } else {
            energy -= energyDelta;
        }

        double distance = Maths.distance(currentRow, currentCol, player.getCurrentRow(), player.getCurrentCol());
        double aggression = getAggression(distance, energy);

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

        Aggression a = Aggression.valueOf(aggression);
    }

    private void setCurrentPosition() {
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

    public double getAggression(double distanceFromPlayer, int energy) {
        if (distanceFromPlayer > MAX_DISTANCE || distanceFromPlayer < 0) {
            throw new IllegalArgumentException("distance must be <= " + MAX_DISTANCE + " and > 0");
        }
        if (energy > MAX_ENERGY || energy < 0) {
            throw new IllegalArgumentException("energy must be <= " + MAX_ENERGY + " and > 0");
        }

        FunctionBlock fb = fis.getFunctionBlock("getAggression");

//        JFuzzyChart.get().chart(fb);
        fis.setVariable("distanceFromPlayer", distanceFromPlayer);
        fis.setVariable("energy", energy);
        fis.evaluate();

        Variable aggression = fis.getVariable("aggression");
//		JFuzzyChart.get().chart(aggression, aggression.getDefuzzifier(), true);

        return aggression.getValue();
    }

    @Override
    public String toString() {
        return String.format("[Patrol]: (%d, %d), energy=%d", currentRow, currentCol, energy);
    }
}
