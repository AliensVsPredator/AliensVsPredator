package com.avp.common.hive.ai.task;

import com.avp.common.hive.Hive;

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
