package ie.gmit.sw.ai.npc;

public enum Aggression {
    CALM, ALERT, HIGH;

    public static Aggression valueOf(double value) {
        if (value >= 60) {
            return HIGH;
        } else if (value >= 20 && value < 60) {
            return ALERT;
        }
        return CALM;
    }
}
