package ie.gmit.sw.ai.npc;

import ie.gmit.sw.ai.GameModel;
import ie.gmit.sw.ai.Player;
import ie.gmit.sw.ai.utils.Maths;
import net.sourceforge.jFuzzyLogic.FIS;
import net.sourceforge.jFuzzyLogic.FunctionBlock;
import net.sourceforge.jFuzzyLogic.plot.JFuzzyChart;
import net.sourceforge.jFuzzyLogic.rule.Variable;

import java.util.Random;

public class Patrol implements Command {

    private static final String FCL_FILE = "./src/resources/fuzzy/patrol.fcl";
    private static final int MAX_ENERGY = 100;
    private static final int MAX_DISTANCE = 100;

    private static final FIS fis;
    private static final Player player;

    private int currentRow;
    private int currentCol;
    private int energy = 100;

    private final char enemyId;
    private final GameModel model;
    private int energyDelta = 5;

    private final Random rand = new Random();

    static {
        fis = FIS.load(FCL_FILE, true);

        if (fis == null) {
            System.err.println("[Error] Unable to load FCL file.");
            System.exit(1);
        }

        player = Player.getInstance();
    }

    public Patrol(char enemyId, GameModel model) {
        this.enemyId = enemyId;
        this.model = model;
    }

    public Patrol(char enemyId, GameModel model, int energyDelta) {
        this(enemyId, model);
        this.energyDelta = energyDelta;
    }

    @Override
    public void execute() {
        if (energy <= 0) {
            energy = MAX_ENERGY;
        } else {
            energy -= energyDelta;
        }

        double distance = Maths.distance(currentRow, currentCol, player.getCurrentRow(), player.getCurrentCol());
        double aggression = getAggression(distance, energy);

        Aggression a = Aggression.valueOf(aggression);
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
