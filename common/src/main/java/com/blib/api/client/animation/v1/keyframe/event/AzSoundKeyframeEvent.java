package com.blib.api.client.animation.v1.keyframe.event;

import com.blib.api.client.animation.v1.keyframe.data.SoundKeyframeData;
import com.blib.api.client.animation.v1.track.AzAnimationTrack;

public class AzSoundKeyframeEvent<T> extends AzKeyframeEvent<T, SoundKeyframeData> {

    public AzSoundKeyframeEvent(
        T entity,
        double animationTick,
        AzAnimationTrack<T> track,
        SoundKeyframeData keyframeData
    ) {
        super(entity, animationTick, track, keyframeData);
    }
}
