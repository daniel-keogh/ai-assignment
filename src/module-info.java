module gmit.software {
    requires javafx.base;
    requires javafx.graphics;
    requires transitive javafx.controls;

    requires jFuzzyLogic;
    requires encog.core;
    
    exports ie.gmit.sw.ai;
    exports ie.gmit.sw.ai.npc;
    exports ie.gmit.sw.ai.searching;
    exports ie.gmit.sw.ai.utils;
}