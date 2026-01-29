package com.blib.azurelib.core.keyframe.event.data;

import java.util.Objects;

public class SoundKeyframeData extends KeyFrameData {

    private final String sound;

    public SoundKeyframeData(Double startTick, String sound) {
        super(startTick);

        this.sound = sound;
    }

    public String getSound() {
        return this.sound;
    }

    @Override
    public int hashCode() {
        return Objects.hash(getStartTick(), this.sound);
    }
}
