package com.blib.internal.client.animation.property;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;

import com.blib.api.client.animation.v1.command.play_behavior.AzPlayBehavior;
import com.blib.api.client.animation.v1.command.play_behavior.AzPlayBehaviors;
import com.blib.internal.client.animation.easing.AzEasingType;
import com.blib.internal.client.animation.easing.AzEasingTypes;

public class AzAnimationStageProperties extends AzAnimationProperties {

    public static final AzAnimationStageProperties DEFAULT = new AzAnimationStageProperties(
        1D,
        AzEasingTypes.NONE,
        AzPlayBehaviors.PLAY_ONCE,
        0F,
        0D,
        0D,
        false
    );

    public static final AzAnimationStageProperties EMPTY = new AzAnimationStageProperties(
        null,
        null,
        null,
        null,
        null,
        null,
        null
    );

    private AzPlayBehavior playBehavior;

    public AzAnimationStageProperties(
        @Nullable Double animationSpeed,
        @Nullable AzEasingType easingType,
        @Nullable AzPlayBehavior playBehavior,
        @Nullable Float transitionLength,
        @Nullable Double startTickOffset,
        @Nullable Double freezeTickOffset,
        @Nullable Boolean isReversing
    ) {
        super(
            animationSpeed,
            easingType,
            transitionLength,
            startTickOffset,
            freezeTickOffset,
            isReversing
        );
        this.playBehavior = playBehavior;
        this.startTickOffset = startTickOffset;
    }

    public boolean hasPlayBehavior() {
        return playBehavior != null;
    }

    @Override
    public AzAnimationStageProperties withAnimationSpeed(double animationSpeed) {
        this.animationSpeed = animationSpeed;
        return new AzAnimationStageProperties(
            animationSpeed,
            easingType,
            playBehavior,
            transitionLength,
            startTickOffset,
            freezeTickOffset,
            isReversing
        );
    }

    @Override
    public AzAnimationStageProperties withEasingType(@NotNull AzEasingType easingType) {
        this.easingType = easingType;
        return new AzAnimationStageProperties(
            animationSpeed,
            easingType,
            playBehavior,
            transitionLength,
            startTickOffset,
            freezeTickOffset,
            isReversing
        );
    }

    public AzAnimationStageProperties withPlayBehavior(@NotNull AzPlayBehavior playBehavior) {
        this.playBehavior = playBehavior;
        return new AzAnimationStageProperties(
            animationSpeed,
            easingType,
            playBehavior,
            transitionLength,
            startTickOffset,
            freezeTickOffset,
            isReversing
        );
    }

    @Override
    public AzAnimationStageProperties withTransitionLength(float transitionLength) {
        this.transitionLength = transitionLength;
        return new AzAnimationStageProperties(
            animationSpeed,
            easingType,
            playBehavior,
            transitionLength,
            startTickOffset,
            freezeTickOffset,
            isReversing
        );
    }

    @Override
    public AzAnimationStageProperties withStartTickOffset(double startTickOffset) {
        this.startTickOffset = startTickOffset;
        return new AzAnimationStageProperties(
            animationSpeed,
            easingType,
            playBehavior,
            transitionLength,
            startTickOffset,
            freezeTickOffset,
            isReversing
        );
    }

    @Override
    public AzAnimationStageProperties withFreezeTickOffset(double freezeTickOffset) {
        this.freezeTickOffset = freezeTickOffset;
        return new AzAnimationStageProperties(
            animationSpeed,
            easingType,
            playBehavior,
            transitionLength,
            startTickOffset,
            freezeTickOffset,
            isReversing
        );
    }

    @Override
    public AzAnimationStageProperties withShouldReverse(boolean isReversing) {
        this.isReversing = isReversing;
        return new AzAnimationStageProperties(
            animationSpeed,
            easingType,
            playBehavior,
            transitionLength,
            startTickOffset,
            freezeTickOffset,
            isReversing
        );
    }

    public AzPlayBehavior playBehavior() {
        return playBehavior == null ? DEFAULT.playBehavior() : playBehavior;
    }

    @Override
    public boolean equals(Object object) {
        if (this == object) {
            return true;
        }

        if (object == null || getClass() != object.getClass()) {
            return false;
        }

        if (!super.equals(object)) {
            return false;
        }

        AzAnimationStageProperties that = (AzAnimationStageProperties) object;

        return Objects.equals(playBehavior, that.playBehavior) && super.equals(object);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), playBehavior);
    }
}
