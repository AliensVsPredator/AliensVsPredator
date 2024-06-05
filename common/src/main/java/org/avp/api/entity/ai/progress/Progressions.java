package org.avp.api.entity.ai.progress;

public class Progressions {
    // Generic
    public static final ProgressKey MOVE_TO_TARGET = new ProgressKey("move_to_target");
    public static final ProgressKey RIDE_TARGET = new ProgressKey("ride_target");

    private Progressions() {
        throw new UnsupportedOperationException();
    }
}
