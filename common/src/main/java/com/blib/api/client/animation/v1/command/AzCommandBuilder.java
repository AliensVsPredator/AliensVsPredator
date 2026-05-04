package com.blib.api.client.animation.v1.command;

import java.util.ArrayList;
import java.util.List;
import java.util.function.UnaryOperator;

import com.blib.api.client.animation.v1.command.play_behavior.AzPlayBehavior;
import com.blib.api.client.animation.v1.command.policy.AzDispatchMode;
import com.blib.api.client.animation.v1.command.policy.AzDispatchPolicy;
import com.blib.api.client.animation.v1.command.policy.OnBlockedByEndless;
import com.blib.api.client.animation.v1.command.policy.OnPropertiesChanged;
import com.blib.api.client.animation.v1.command.sequence.AzAnimationSequenceBuilder;
import com.blib.api.client.animation.v1.track.AzTrackHandle;
import com.blib.internal.client.animation.dispatch.command.action.AzAction;
import com.blib.internal.client.animation.dispatch.command.action.impl.AzCancelAction;
import com.blib.internal.client.animation.dispatch.command.action.impl.AzPauseAction;
import com.blib.internal.client.animation.dispatch.command.action.impl.AzPlayAnimationSequenceAction;
import com.blib.internal.client.animation.dispatch.command.action.impl.AzResumeAction;
import com.blib.internal.client.animation.dispatch.command.action.impl.AzSetAnimationSpeedAction;
import com.blib.internal.client.animation.dispatch.command.action.impl.AzSetEasingTypeAction;
import com.blib.internal.client.animation.dispatch.command.action.impl.AzSetFreezeTickAction;
import com.blib.internal.client.animation.dispatch.command.action.impl.AzSetReverseAction;
import com.blib.internal.client.animation.dispatch.command.action.impl.AzSetStartTickOffsetAction;
import com.blib.internal.client.animation.dispatch.command.action.impl.AzSetTransitionSpeedAction;
import com.blib.internal.client.animation.dispatch.command.action.impl.AzSkipCurrentAction;
import com.blib.internal.client.animation.easing.AzEasingType;

public class AzCommandBuilder<T> {

    private final List<AzAction<T>> actions;

    private AzDispatchMode dispatchMode;

    private OnBlockedByEndless onBlockedByEndless;

    private OnPropertiesChanged onPropertiesChanged;

    AzCommandBuilder() {
        this.actions = new ArrayList<>();
        this.dispatchMode = null;
        this.onBlockedByEndless = OnBlockedByEndless.APPEND_ANYWAY;
        this.onPropertiesChanged = OnPropertiesChanged.RESTART;
    }

    public AzCommandBuilder<T> dispatchMode(AzDispatchMode mode) {
        this.dispatchMode = mode;
        return this;
    }

    public AzCommandBuilder<T> onBlockedByEndless(OnBlockedByEndless policy) {
        this.onBlockedByEndless = policy;
        return this;
    }

    public AzCommandBuilder<T> onPropertiesChanged(OnPropertiesChanged policy) {
        this.onPropertiesChanged = policy;
        return this;
    }

    public AzCommandBuilder<T> append(AzCommand<T> command) {
        actions.addAll(command.actions());
        return this;
    }

    /**
     * Full cancel: clears the current animation, drains the queue, transitions state machine to STOP.
     */
    public AzCommandBuilder<T> cancel(AzTarget target) {
        actions.add(new AzCancelAction<>(target));
        return this;
    }

    public AzCommandBuilder<T> cancel(AzTrackHandle<? super T> handle) {
        return cancel(AzTarget.track(handle));
    }

    /**
     * Drops the current animation. Queue preserved.
     */
    public AzCommandBuilder<T> skipCurrent(AzTarget target) {
        actions.add(new AzSkipCurrentAction<>(target));
        return this;
    }

    public AzCommandBuilder<T> skipCurrent(AzTrackHandle<? super T> handle) {
        return skipCurrent(AzTarget.track(handle));
    }

    public AzCommandBuilder<T> pause(AzTarget target) {
        actions.add(new AzPauseAction<>(target));
        return this;
    }

    public AzCommandBuilder<T> pause(AzTrackHandle<? super T> handle) {
        return pause(AzTarget.track(handle));
    }

    public AzCommandBuilder<T> resume(AzTarget target) {
        actions.add(new AzResumeAction<>(target));
        return this;
    }

    public AzCommandBuilder<T> resume(AzTrackHandle<? super T> handle) {
        return resume(AzTarget.track(handle));
    }

    public AzCommandBuilder<T> setEasingType(AzTarget target, AzEasingType easingType) {
        actions.add(new AzSetEasingTypeAction<>(target, easingType));
        return this;
    }

    public AzCommandBuilder<T> setEasingType(AzTrackHandle<? super T> handle, AzEasingType easingType) {
        return setEasingType(AzTarget.track(handle), easingType);
    }

    public AzCommandBuilder<T> setSpeed(AzTarget target, double speed) {
        actions.add(new AzSetAnimationSpeedAction<>(target, speed));
        return this;
    }

    public AzCommandBuilder<T> setSpeed(AzTrackHandle<? super T> handle, double speed) {
        return setSpeed(AzTarget.track(handle), speed);
    }

    public AzCommandBuilder<T> setTransitionSpeed(AzTarget target, float transitionSpeed) {
        actions.add(new AzSetTransitionSpeedAction<>(target, transitionSpeed));
        return this;
    }

    public AzCommandBuilder<T> setTransitionSpeed(AzTrackHandle<? super T> handle, float transitionSpeed) {
        return setTransitionSpeed(AzTarget.track(handle), transitionSpeed);
    }

    public AzCommandBuilder<T> setStartTickOffset(AzTarget target, double tickOffset) {
        actions.add(new AzSetStartTickOffsetAction<>(target, tickOffset));
        return this;
    }

    public AzCommandBuilder<T> setStartTickOffset(AzTrackHandle<? super T> handle, double tickOffset) {
        return setStartTickOffset(AzTarget.track(handle), tickOffset);
    }

    public AzCommandBuilder<T> setFreezeTickOffset(AzTarget target, double freezeTickOffset) {
        actions.add(new AzSetFreezeTickAction<>(target, freezeTickOffset));
        return this;
    }

    public AzCommandBuilder<T> setFreezeTickOffset(AzTrackHandle<? super T> handle, double freezeTickOffset) {
        return setFreezeTickOffset(AzTarget.track(handle), freezeTickOffset);
    }

    public AzCommandBuilder<T> setReverseAnimation(AzTarget target, boolean hasReverse) {
        actions.add(new AzSetReverseAction<>(target, hasReverse));
        return this;
    }

    public AzCommandBuilder<T> setReverseAnimation(AzTrackHandle<? super T> handle, boolean hasReverse) {
        return setReverseAnimation(AzTarget.track(handle), hasReverse);
    }

    public AzCommandBuilder<T> play(AzTarget target, String animationName) {
        return playSequence(target, builder -> builder.queue(animationName, properties -> properties));
    }

    public AzCommandBuilder<T> play(AzTrackHandle<? super T> handle, String animationName) {
        return play(AzTarget.track(handle), animationName);
    }

    public AzCommandBuilder<T> play(AzTarget target, String animationName, AzPlayBehavior playBehavior) {
        return playSequence(
            target,
            builder -> builder.queue(animationName, properties -> properties.withPlayBehavior(playBehavior))
        );
    }

    public AzCommandBuilder<T> play(AzTrackHandle<? super T> handle, String animationName, AzPlayBehavior playBehavior) {
        return play(AzTarget.track(handle), animationName, playBehavior);
    }

    public AzCommandBuilder<T> playSequence(
        AzTarget target,
        UnaryOperator<AzAnimationSequenceBuilder> builderUnaryOperator
    ) {
        var sequence = builderUnaryOperator.apply(new AzAnimationSequenceBuilder()).build();
        actions.add(new AzPlayAnimationSequenceAction<>(target, sequence, currentPolicy()));
        return this;
    }

    public AzCommandBuilder<T> playSequence(
        AzTrackHandle<? super T> handle,
        UnaryOperator<AzAnimationSequenceBuilder> builderUnaryOperator
    ) {
        return playSequence(AzTarget.track(handle), builderUnaryOperator);
    }

    public AzCommand<T> build() {
        return new AzCommand<>(List.copyOf(actions));
    }

    /**
     * Materializes the {@link AzDispatchPolicy} for the play action being added. Throws if no dispatch mode has been
     * set — every play action must declare its dispatch intent. Use {@link AzCommand#replay()},
     * {@link AzCommand#idempotent()}, {@link AzCommand#enqueueing()}, or call {@link #dispatchMode(AzDispatchMode)} on
     * the builder.
     */
    private AzDispatchPolicy currentPolicy() {
        if (dispatchMode == null) {
            throw new IllegalStateException(
                "No dispatch mode set on this command builder. Call dispatchMode(...) before adding a "
                    + "play action, or start the builder via AzCommand.replay() / .idempotent() / "
                    + ".enqueueing()."
            );
        }

        return new AzDispatchPolicy(dispatchMode, onBlockedByEndless, onPropertiesChanged);
    }
}
