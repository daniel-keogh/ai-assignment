package ie.gmit.sw.ai.utils;

import java.util.concurrent.ThreadLocalRandom;

public final class Random {
    private Random() {
    }

    public static int generate(int min, int max) {
        return ThreadLocalRandom.current().nextInt(min, max + 1);
    }

    public static int generate(int max) {
        return generate(0, max);
    }
}
