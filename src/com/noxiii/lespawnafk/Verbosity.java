package com.noxiii.lespawnafk;


public enum Verbosity {
    LOW(1),
    NORMAL(2),
    HIGH(3),
    HIGHEST(4),
    EXTREME(5),
    DEBUG(6);

    private int level;

    Verbosity(int lvl) {
        this.level = lvl;
    }

    public boolean exceeds(Verbosity other) {
        if (this.level >= other.level)
            return true;
        return false;
    }
}
