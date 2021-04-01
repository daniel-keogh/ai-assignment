package ie.gmit.sw.ai;

import javafx.application.Application;
import net.sourceforge.jFuzzyLogic.FIS;

public class Runner {
    private static final String FCL_FILE = "./src/resources/fuzzy/game.fcl";

    public static void main(String[] args) {
//        FIS fis = FIS.load(FCL_FILE, true);

//        if (fis == null) {
//            System.err.println("[Error] Unable to load FCL file.");
//            System.exit(1);
//        }

        Application.launch(GameWindow.class, args);
    }
}