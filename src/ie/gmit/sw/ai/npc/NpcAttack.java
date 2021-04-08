package ie.gmit.sw.ai.npc;

import net.sourceforge.jFuzzyLogic.FIS;
import net.sourceforge.jFuzzyLogic.FunctionBlock;
import net.sourceforge.jFuzzyLogic.rule.Variable;

public class NpcAttack {
    private static final String FCL_FILE = "./src/resources/fuzzy/damage.fcl";
    private static final FIS fis;
    private static final int MAX_STRENGTH = 100;
    private static final int MAX_ENERGY = 100;

    static {
        fis = FIS.load(FCL_FILE, true);

        if (fis == null) {
            System.err.println("[Error] Unable to load FCL file: " + FCL_FILE);
            System.exit(1);
        }
    }

    public double getDamage(double strength, double energy) {
        if (strength > MAX_STRENGTH || strength < 0) {
            throw new IllegalArgumentException("strength must be <= " + MAX_STRENGTH + " and > 0");
        }
        if (energy > MAX_ENERGY || energy < 0) {
            throw new IllegalArgumentException("energy must be <= " + MAX_ENERGY + " and > 0");
        }

        FunctionBlock fb = fis.getFunctionBlock("getAggression");

//        JFuzzyChart.get().chart(fb);
        fis.setVariable("strength", strength);
        fis.setVariable("energy", energy);
        fis.evaluate();

        Variable damage = fis.getVariable("damage");
//		JFuzzyChart.get().chart(damage, damage.getDefuzzifier(), true);

        return damage.getValue();
    }
}
