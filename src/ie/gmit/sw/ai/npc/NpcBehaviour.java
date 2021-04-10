package ie.gmit.sw.ai.npc;

import ie.gmit.sw.ai.utils.NNUtils;
import org.encog.Encog;
import org.encog.engine.network.activation.*;
import org.encog.ml.data.MLData;
import org.encog.ml.data.MLDataPair;
import org.encog.ml.data.MLDataSet;
import org.encog.ml.data.basic.BasicMLData;
import org.encog.ml.data.basic.BasicMLDataSet;
import org.encog.neural.networks.BasicNetwork;
import org.encog.neural.networks.layers.BasicLayer;
import org.encog.neural.networks.training.propagation.resilient.ResilientPropagation;
import org.encog.util.obj.SerializeObject;

import java.io.File;
import java.io.IOException;

/**
 * NPC Neural Network controller.
 * <p>
 * <p>
 * Inputs:
 * <hr/>
 * <ol>
 *     <li>Player Health</li>
 *     <li>NPC Energy</li>
 *     <li>NPC Strength</li>
 * </ol>
 * <p>
 * Outputs:
 * <hr/>
 * <ol>
 *     <li>Rest</li>
 *     <li>Chase</li>
 * </ol>
 */
public class NpcBehaviour {

    private static final String FILE_NAME = "./resources/neural/npc_model.bin";
    private static final double MIN_ERROR = 0.12;

    private static NpcBehaviour instance;

    // 1. Player health
    // 2. Energy
    // 3. Strength
    private final double[][] data = {
            {200, 100, 100}, {150, 80, 80}, {100, 60, 60},
            {50, 40, 40}, {200, 20, 20}, {150, 100, 20},
            {100, 80, 40}, {50, 60, 60}, {200, 40, 80},
            {150, 20, 100}, {100, 40, 100}, {50, 60, 80},
            {200, 80, 60}, {150, 100, 40}, {100, 80, 20},
            {50, 60, 20}, {200, 40, 40}, {150, 20, 60},
            {50, 100, 80}, {75, 80, 100}, {75, 50, 50},
            {75, 25, 25}, {25, 15, 75}, {25, 10, 25},
    };

    // 1. Rest
    // 2. Chase
    private final double[][] expected = {
            {0.0, 1.0}, {0.0, 1.0}, {0.0, 1.0},
            {1.0, 0.0}, {1.0, 0.0}, {0.0, 1.0},
            {0.0, 1.0}, {0.0, 1.0}, {1.0, 0.0},
            {1.0, 0.0}, {0.0, 1.0}, {0.0, 1.0},
            {0.0, 1.0}, {0.0, 1.0}, {0.0, 1.0},
            {0.0, 1.0}, {1.0, 0.0}, {1.0, 0.0},
            {0.0, 1.0}, {0.0, 1.0}, {0.0, 1.0},
            {1.0, 0.0}, {0.0, 1.0}, {1.0, 0.0},
    };

    private BasicNetwork network;

    private NpcBehaviour() {
        try {
            // try to load a pre-existing neural network
            network = load();
        } catch (Exception e) {
            System.err.println("[Error]: Failed to load pre-existing network");
            network = null;
        }
    }

    public static NpcBehaviour getInstance() {
        if (instance == null) {
            instance = new NpcBehaviour();
        }
        return instance;
    }

    /**
     * Check if a network exists. If false it will have to be trained.
     */
    public boolean networkExists() {
        return network != null;
    }

    /**
     * Train and then save the neural network.
     *
     * @throws IOException If unable to save the serialised model.
     */
    public void train(boolean save) throws IOException {
        train();
        if (save) save();
    }

    /**
     * Trains the neural network.
     */
    public void train() {
        //----------------------------------------------------
        // Step 1: Declare Network Topology
        //----------------------------------------------------
        System.out.println("[Info] Creating neural network topology...");

        network = new BasicNetwork();
        network.addLayer(new BasicLayer(null, true, 3));
        network.addLayer(new BasicLayer(new ActivationSigmoid(), true, 2));
        network.addLayer(new BasicLayer(new ActivationSigmoid(), false, 2));
        network.getStructure().finalizeStructure();
        network.reset();

        //----------------------------------------------------
        // Step 2: Create the training data set
        //----------------------------------------------------
        System.out.println("[Info] Creating training set...");

        double[][] normData = NNUtils.normalize(data, 0, 1);
        double[][] normExpected = NNUtils.normalize(expected, 0, 1);
        MLDataSet trainingSet = new BasicMLDataSet(normData, normExpected);

        //----------------------------------------------------
        // Step 3: Train the NN
        //----------------------------------------------------
        System.out.println("[Info] Training the network...");
        ResilientPropagation train = new ResilientPropagation(network, trainingSet);

        int epoch = 1;
        do {
            train.iteration();
            System.out.println("Epoch #" + epoch + " Error:" + train.getError());
            epoch++;
        } while (train.getError() > MIN_ERROR);
        train.finishTraining();

        System.out.println("[Info] training complete in " + epoch + " epochs with error=" + train.getError());

        //----------------------------------------------------
        // Step 4: Test the NN
        //----------------------------------------------------
        System.out.println("[Info] Testing the network...");

        int correct = 0;
        int total = 0;

        for (MLDataPair pair : trainingSet) {
            MLData output = network.compute(pair.getInput());

            int y = (int) Math.round(output.getData(0));
            int yd = (int) pair.getIdeal().getData(0);

            if (y == yd) correct++;

            double health = pair.getInput().getData(0);
            double energy = pair.getInput().getData(1);
            double strength = pair.getInput().getData(2);

            System.out.printf("%.2f, %.2f, %.2f [Y=%d, Yd=%d]\n", health, energy, strength, y, yd);

            total++;
        }

        //----------------------------------------------------
        // Step 5: Shutdown the NN
        //----------------------------------------------------
        System.out.println("[Info] Shutting down...");
        Encog.getInstance().shutdown();

        System.out.printf("[Info] Accuracy: %.2f%%\n", ((double) correct / total) * 100);
    }

    /**
     * Use the NN to classify the given inputs and determine what {@link Action} the NPC should take.
     *
     * @param playerHealth The amount of health the player has.
     * @param energy       The amount of energy the NPC has.
     * @param strength     How strong the NPC is.
     * @return The action the NPC ought to take.
     */
    public Action classify(double playerHealth, double energy, double strength) {

        double[] norm = NNUtils.normalize(new double[]{playerHealth, energy, strength}, 0, 1);

        MLData data = new BasicMLData(norm);

        return switch (network.classify(data)) {
            case 0 -> Action.REST;
            case 1 -> Action.CHASE;
            default -> throw new IllegalStateException("Invalid ML classification");
        };
    }

    /**
     * Save the model to the disk.
     */
    private void save() throws IOException {
        SerializeObject.save(new File(FILE_NAME), network);
    }

    /**
     * Loads a pre-existing model from the disk.
     */
    private BasicNetwork load() throws IOException, ClassNotFoundException {
        return (BasicNetwork) SerializeObject.load(new File(FILE_NAME));
    }
}
