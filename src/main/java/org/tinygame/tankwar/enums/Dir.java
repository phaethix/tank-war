package org.tinygame.tankwar.enums;

import java.util.concurrent.ThreadLocalRandom;

public enum Dir {
    LEFT, UP, RIGHT, DOWN;

    private static final Dir[] VALUES = values();

    public static Dir random() {
        return VALUES[ThreadLocalRandom.current().nextInt(VALUES.length)];
    }
}
