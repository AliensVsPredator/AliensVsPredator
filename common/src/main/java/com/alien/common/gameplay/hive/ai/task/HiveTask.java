package com.alien.common.gameplay.hive.ai.task;

import com.alien.common.gameplay.hive.Hive;

public abstract class HiveTask extends Task {

    protected final Hive hive;

    protected HiveTask(Hive hive) {
        this.hive = hive;
    }

    @Override
    public boolean canRun() {
        return hive.isAlive();
    }
}
