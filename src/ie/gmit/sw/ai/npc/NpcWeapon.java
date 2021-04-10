package ie.gmit.sw.ai.npc;

import net.sourceforge.jFuzzyLogic.FIS;
import net.sourceforge.jFuzzyLogic.FunctionBlock;
import net.sourceforge.jFuzzyLogic.plot.JFuzzyChart;
import net.sourceforge.jFuzzyLogic.rule.Variable;

/**
 * Denotes an object that can inflict damage upon the player.
 */
public class NpcWeapon {
    private static final String FCL_FILE = "./src/resources/fuzzy/damage.fcl";
    private static final FIS fis;

    static {
        // Load the FCL file
        fis = FIS.load(FCL_FILE, true);

        if (fis == null) {
            System.err.println("[Error] Unable to load FCL file: " + FCL_FILE);
            System.exit(1);
        }
    }

    /**
     * Get the damage to be inflicted on the player using the JFuzzyLogic API.
     *
     * @param strength The NPC's strength.
     * @param energy   The NPC's strength.
     * @return The amount of damage.
     * @throws IllegalArgumentException If strength or energy are out of range.
     */
    public double getDamage(double strength, double energy) {
        if (strength > Npc.MAX_STRENGTH || strength < 0) {
            throw new IllegalArgumentException("strength must be <= " + Npc.MAX_STRENGTH + " and > 0");
        }
        if (energy > Npc.MAX_ENERGY || energy < 0) {
            throw new IllegalArgumentException("energy must be <= " + Npc.MAX_ENERGY + " and > 0");
        }

        FunctionBlock fb = fis.getFunctionBlock("getDamage");

        // JFuzzyChart.get().chart(fb);
        fis.setVariable("strength", strength);
        fis.setVariable("energy", energy);
        fis.evaluate();

        Variable damage = fis.getVariable("damage");
        // JFuzzyChart.get().chart(damage, damage.getDefuzzifier(), true);

        return damage.getValue();
    }
}
