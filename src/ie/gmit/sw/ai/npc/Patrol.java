package ie.gmit.sw.ai.npc;

import ie.gmit.sw.ai.GameModel;
import net.sourceforge.jFuzzyLogic.FIS;
import net.sourceforge.jFuzzyLogic.FunctionBlock;
import net.sourceforge.jFuzzyLogic.plot.JFuzzyChart;
import net.sourceforge.jFuzzyLogic.rule.Variable;

public class Patrol implements Command {

    private static final String FCL_FILE = "./src/resources/fuzzy/patrol.fcl";

    private static final FIS fis;

    private char enemyId;
    private GameModel model;

    static {
        fis = FIS.load(FCL_FILE, true);

        if (fis == null) {
            System.err.println("[Error] Unable to load FCL file.");
            System.exit(1);
        }
    }

    public Patrol(char enemyId, GameModel model) {
        this.enemyId = enemyId;
        this.model = model;
    }

    @Override
    public void execute() {
        System.out.println("Patrolling");
    }

    public double getAggression(double distanceFromPlayer, int energy) {
//        if (distanceFromPlayer > 10) {
//            throw new IllegalArgumentException();
//        }
//        if (energy > 10) {
//            throw new IllegalArgumentException();
//        }

        FunctionBlock fb = fis.getFunctionBlock("getAggression");

        JFuzzyChart.get().chart(fb);
		fis.setVariable("distanceFromPlayer", distanceFromPlayer);
		fis.setVariable("energy", energy);
		fis.evaluate();

		Variable aggression = fis.getVariable("aggression");
		JFuzzyChart.get().chart(aggression, aggression.getDefuzzifier(), true);

		return aggression.getValue();
    }
}
