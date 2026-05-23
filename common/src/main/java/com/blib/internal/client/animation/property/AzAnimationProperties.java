package com.blib.internal.client.animation.property;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;

import com.blib.internal.client.animation.easing.AzEasingType;

public class AzAnimationProperties {

    public static final AzAnimationProperties DEFAULT = new AzAnimationProperties(1D, null, 0F, 0D, 0D, false);

    public static final AzAnimationProperties EMPTY = new AzAnimationProperties(
        null,
        null,
        null,
        null,
        null,
        null
    );

    protected final @Nullable Double animationSpeed;

    protected final @Nullable AzEasingType easingType;

    protected final @Nullable Float transitionLength;

    protected final @Nullable Double startTickOffset;

    protected final @Nullable Double freezeTickOffset;

    protected final @Nullable Boolean isReversing;

    public AzAnimationProperties(
        @Nullable Double animationSpeed,
        @Nullable AzEasingType easingType,
        @Nullable Float transitionLength,
        @Nullable Double startTickOffset,
        @Nullable Double freezeTickOffset,
        @Nullable Boolean isReversing
    ) {
        this.animationSpeed = animationSpeed;
        this.easingType = easingType;
        this.transitionLength = transitionLength;
        this.startTickOffset = startTickOffset;
        this.freezeTickOffset = freezeTickOffset;
        this.isReversing = isReversing;
    }

    public boolean hasAnimationSpeed() {
        return animationSpeed != null;
    }

    public boolean hasEasingType() {
        return easingType != null;
    }

    public boolean hasTransitionLength() {
        return transitionLength != null;
    }

    public boolean hasStartTickOffset() {
        return startTickOffset != null;
    }

    public boolean hasFreezeTickOffset() {
        return freezeTickOffset != null;
    }

    public boolean hasReversing() {
        return isReversing != null;
    }

    public AzAnimationProperties withAnimationSpeed(double animationSpeed) {
        return new AzAnimationProperties(
            animationSpeed,
            easingType,
            transitionLength,
            startTickOffset,
            freezeTickOffset,
            isReversing
        );
    }

    public AzAnimationProperties withEasingType(@NotNull AzEasingType easingType) {
        return new AzAnimationProperties(
            animationSpeed,
            easingType,
            transitionLength,
            startTickOffset,
            freezeTickOffset,
            isReversing
        );
    }

    public AzAnimationProperties withTransitionLength(float transitionLength) {
        return new AzAnimationProperties(
            animationSpeed,
            easingType,
            transitionLength,
            startTickOffset,
            freezeTickOffset,
            isReversing
        );
    }

    public AzAnimationProperties withStartTickOffset(double startTickOffset) {
        return new AzAnimationProperties(
            animationSpeed,
            easingType,
            transitionLength,
            startTickOffset,
            freezeTickOffset,
            isReversing
        );
    }

    public AzAnimationProperties withFreezeTickOffset(double freezeTickOffset) {
        return new AzAnimationProperties(
            animationSpeed,
            easingType,
            transitionLength,
            startTickOffset,
            freezeTickOffset,
            isReversing
        );
    }

    public AzAnimationProperties withShouldReverse(boolean isReversing) {
        return new AzAnimationProperties(
            animationSpeed,
            easingType,
            transitionLength,
            startTickOffset,
            freezeTickOffset,
            isReversing
        );
    }

    public double animationSpeed() {
        return animationSpeed == null ? DEFAULT.animationSpeed() : animationSpeed;
    }

    public AzEasingType easingType() {
        return easingType;
    }

    public float transitionLength() {
        return transitionLength == null ? DEFAULT.transitionLength() : transitionLength;
    }

    public double startTickOffset() {
        return startTickOffset == null ? DEFAULT.startTickOffset() : startTickOffset;
    }

    public double freezeTickOffset() {
        return freezeTickOffset == null ? DEFAULT.freezeTickOffset() : freezeTickOffset;
    }

    public boolean isReversing() {
        return isReversing == null ? DEFAULT.isReversing() : isReversing;
    }

    @Override
    public boolean equals(Object object) {
        if (this == object) {
            return true;
        }

        if (object == null || getClass() != object.getClass()) {
            return false;
        }

        AzAnimationProperties that = (AzAnimationProperties) object;

        return Objects.equals(animationSpeed, that.animationSpeed) && Objects.equals(easingType, that.easingType)
            && Objects.equals(transitionLength, that.transitionLength) && Objects.equals(
                startTickOffset,
                that.startTickOffset
            ) && Objects.equals(freezeTickOffset, that.freezeTickOffset)
            && Objects.equals(isReversing, that.isReversing);
    }

    @Override
    public int hashCode() {
        return Objects.hash(
            animationSpeed,
            easingType,
            transitionLength,
            startTickOffset,
            freezeTickOffset,
            isReversing
        );
    }
}
