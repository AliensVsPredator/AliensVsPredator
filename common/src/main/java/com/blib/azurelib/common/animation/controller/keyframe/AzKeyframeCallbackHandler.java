package com.blib.azurelib.common.animation.controller.keyframe;

import it.unimi.dsi.fastutil.objects.ObjectOpenHashSet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Set;

import com.blib.azurelib.common.animation.controller.AzAnimationController;
import com.blib.azurelib.common.animation.event.AzCustomInstructionKeyframeEvent;
import com.blib.azurelib.common.animation.event.AzParticleKeyframeEvent;
import com.blib.azurelib.common.animation.event.AzSoundKeyframeEvent;
import com.blib.azurelib.common.animation.primitive.AzQueuedAnimation;
import com.blib.azurelib.core.keyframe.event.data.KeyFrameData;

// TODO: reduce the boilerplate of the specialized handle functions in this class.
public class AzKeyframeCallbackHandler<T> {

    private static final Logger LOGGER = LoggerFactory.getLogger(AzKeyframeCallbackHandler.class);

    private final AzAnimationController<T> animationController;

    private final Set<KeyFrameData> executedKeyframes;

    private final AzKeyframeCallbacks<T> keyframeCallbacks;

    public AzKeyframeCallbackHandler(
        AzAnimationController<T> animationController,
        AzKeyframeCallbacks<T> keyframeCallbacks
    ) {
        this.animationController = animationController;
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
                        animationController.name()
                    );
                    break;
                }

                customKeyframeHandler.handle(
                    new AzCustomInstructionKeyframeEvent<>(animatable, adjustedTick, animationController, keyframeData)
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
                        animationController.name()
                    );
                    break;
                }

                particleKeyframeHandler.handle(
                    new AzParticleKeyframeEvent<>(animatable, adjustedTick, animationController, keyframeData)
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
                        animationController.name()
                    );
                    break;
                }

                soundKeyframeHandler.handle(
                    new AzSoundKeyframeEvent<>(animatable, adjustedTick, animationController, keyframeData)
                );
            }
        }
    }

    public void reset() {
        executedKeyframes.clear();
    }

    private AzQueuedAnimation currentAnimation() {
        return animationController.currentAnimation();
    }
}
