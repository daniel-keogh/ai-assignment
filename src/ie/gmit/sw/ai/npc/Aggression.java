package ie.gmit.sw.ai.npc;

public enum Aggression {
    CALM, ALERT, HIGH;

    public static Aggression valueOf(double value) {
        if (value >= 70) {
            return HIGH;
        } else if (value >= 40 && value < 70) {
            return ALERT;
        }
        return CALM;
    }
}
