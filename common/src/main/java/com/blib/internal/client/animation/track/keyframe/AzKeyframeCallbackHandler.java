package com.blib.internal.client.animation.track.keyframe;

import it.unimi.dsi.fastutil.objects.ObjectOpenHashSet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Set;

import com.blib.api.client.animation.v1.keyframe.AzKeyframeCallbacks;
import com.blib.api.client.animation.v1.keyframe.data.KeyFrameData;
import com.blib.api.client.animation.v1.keyframe.event.AzCustomInstructionKeyframeEvent;
import com.blib.api.client.animation.v1.keyframe.event.AzParticleKeyframeEvent;
import com.blib.api.client.animation.v1.keyframe.event.AzSoundKeyframeEvent;
import com.blib.api.client.animation.v1.track.AzAnimationTrack;
import com.blib.internal.client.animation.primitive.AzQueuedAnimation;

// TODO: reduce the boilerplate of the specialized handle functions in this class.
public class AzKeyframeCallbackHandler<T> {

    private static final Logger LOGGER = LoggerFactory.getLogger(AzKeyframeCallbackHandler.class);

    private final AzAnimationTrack<T> animationTrack;

    private final Set<KeyFrameData> executedKeyframes;

    private final AzKeyframeCallbacks<T> keyframeCallbacks;

    public AzKeyframeCallbackHandler(
        AzAnimationTrack<T> animationTrack,
        AzKeyframeCallbacks<T> keyframeCallbacks
    ) {
        this.animationTrack = animationTrack;
        this.executedKeyframes = new ObjectOpenHashSet<>();
        this.keyframeCallbacks = keyframeCallbacks;
    }

    public void handle(T animatable, double adjustedTick) {
        handleSoundKeyframes(animatable, adjustedTick);
        handleParticleKeyframes(animatable, adjustedTick);
        handleCustomKeyframes(animatable, adjustedTick);
    }

    private void handleCustomKeyframes(T animatable, double adjustedTick) {
        var customKeyframeHandler = keyframeCallbacks.customKeyframeHandler();
        var customInstructions = currentAnimation().animation().keyframes().customInstructions();

        for (var keyframeData : customInstructions) {
            if (adjustedTick >= keyframeData.getStartTick() && executedKeyframes.add(keyframeData)) {
                if (customKeyframeHandler == null) {
                    LOGGER.warn(
                        "Custom Instruction Keyframe found for {} -> {}, but no keyframe handler registered",
                        animatable.getClass().getSimpleName(),
                        animationTrack.name()
                    );
                    break;
                }

                customKeyframeHandler.handle(
                    new AzCustomInstructionKeyframeEvent<>(animatable, adjustedTick, animationTrack, keyframeData)
                );
            }
        }
    }

    private void handleParticleKeyframes(T animatable, double adjustedTick) {
        var particleKeyframeHandler = keyframeCallbacks.particleKeyframeHandler();
        var particleInstructions = currentAnimation().animation().keyframes().particles();

        for (var keyframeData : particleInstructions) {
            if (adjustedTick >= keyframeData.getStartTick() && executedKeyframes.add(keyframeData)) {
                if (particleKeyframeHandler == null) {
                    LOGGER.warn(
                        "Particle Keyframe found for {} -> {}, but no keyframe handler registered",
                        animatable.getClass().getSimpleName(),
                        animationTrack.name()
                    );
                    break;
                }

                particleKeyframeHandler.handle(
                    new AzParticleKeyframeEvent<>(animatable, adjustedTick, animationTrack, keyframeData)
                );
            }
        }
    }

    private void handleSoundKeyframes(T animatable, double adjustedTick) {
        var soundKeyframeHandler = keyframeCallbacks.soundKeyframeHandler();
        var soundInstructions = currentAnimation().animation().keyframes().sounds();

        for (var keyframeData : soundInstructions) {
            if (adjustedTick >= keyframeData.getStartTick() && executedKeyframes.add(keyframeData)) {
                if (soundKeyframeHandler == null) {
                    LOGGER.warn(
                        "Sound Keyframe found for {} -> {}, but no keyframe handler registered",
                        animatable.getClass().getSimpleName(),
                        animationTrack.name()
                    );
                    break;
                }

                soundKeyframeHandler.handle(
                    new AzSoundKeyframeEvent<>(animatable, adjustedTick, animationTrack, keyframeData)
                );
            }
        }
    }

    public void reset() {
        executedKeyframes.clear();
    }

    private AzQueuedAnimation currentAnimation() {
        return animationTrack.currentAnimation();
    }
}
