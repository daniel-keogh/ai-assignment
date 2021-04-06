package ie.gmit.sw.ai.npc;

/**
 * Use implementations of this functional interface to specify
 * how a computer controlled game character should behave.
 */
@FunctionalInterface
public interface Command {
    void execute();
}