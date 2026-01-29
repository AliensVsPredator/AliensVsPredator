package com.blib.azurelib.core.keyframe.event.data;

import java.util.Objects;

public abstract class KeyFrameData {

    private final double startTick;

    protected KeyFrameData(double startTick) {
        this.startTick = startTick;
    }

    public double getStartTick() {
        return this.startTick;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;

        if (obj == null || getClass() != obj.getClass())
            return false;

        return this.hashCode() == obj.hashCode();
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(this.startTick);
    }
}
