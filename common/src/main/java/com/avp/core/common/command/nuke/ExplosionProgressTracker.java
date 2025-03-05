package com.avp.core.common.command.nuke;

public class ExplosionProgressTracker {

    private int blocksDestroyed;

    private long timeStarted;

    private long timeTaken;

    public void incrementBlockDestroyCounter() {
        blocksDestroyed++;
    }

    public void startTimer() {
        timeStarted = System.currentTimeMillis();
    }

    public void stopTimer() {
        timeTaken = System.currentTimeMillis() - timeStarted;
        timeStarted = 0;
    }

    public int blocksDestroyed() {
        return blocksDestroyed;
    }

    public long timeTaken() {
        return timeTaken;
    }
}
