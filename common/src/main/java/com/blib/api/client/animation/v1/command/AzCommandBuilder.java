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

public class AzCommandBuilder {

    private final List<AzAction> actions;

    private AzDispatchMode dispatchMode;

    private OnBlockedByEndless onBlockedByEndless;

    private OnPropertiesChanged onPropertiesChanged;

    AzCommandBuilder() {
        this.actions = new ArrayList<>();
        this.dispatchMode = null;
        this.onBlockedByEndless = OnBlockedByEndless.APPEND_ANYWAY;
        this.onPropertiesChanged = OnPropertiesChanged.RESTART;
    }

    public AzCommandBuilder dispatchMode(AzDispatchMode mode) {
        this.dispatchMode = mode;
        return this;
    }

    public AzCommandBuilder onBlockedByEndless(OnBlockedByEndless policy) {
        this.onBlockedByEndless = policy;
        return this;
    }

    public AzCommandBuilder onPropertiesChanged(OnPropertiesChanged policy) {
        this.onPropertiesChanged = policy;
        return this;
    }

    public AzCommandBuilder append(AzCommand command) {
        actions.addAll(command.actions());
        return this;
    }

    /**
     * Full cancel: clears the current animation, drains the queue, and transitions the state
     * machine to STOP. The track ends up silent and stays silent until something dispatches a new
     * play action.
     */
    public AzCommandBuilder cancel(AzTarget target) {
        actions.add(new AzCancelAction(target));
        return this;
    }

    /**
     * Drops the current animation. The queue is preserved — on the next state machine tick, the
     * next queued animation begins playing. If the queue is empty, the track auto-stops.
     */
    public AzCommandBuilder skipCurrent(AzTarget target) {
        actions.add(new AzSkipCurrentAction(target));
        return this;
    }

    /**
     * Freezes the current animation at its current frame. No-op on tracks that are stopped or
     * already paused.
     */
    public AzCommandBuilder pause(AzTarget target) {
        actions.add(new AzPauseAction(target));
        return this;
    }

    /**
     * Unfreezes a paused animation. No-op on tracks that are not currently paused.
     */
    public AzCommandBuilder resume(AzTarget target) {
        actions.add(new AzResumeAction(target));
        return this;
    }

    public AzCommandBuilder setSpeed(AzTarget target, double speed) {
        actions.add(new AzSetAnimationSpeedAction(target, speed));
        return this;
    }

    public AzCommandBuilder setEasingType(AzTarget target, AzEasingType easingType) {
        actions.add(new AzSetEasingTypeAction(target, easingType));
        return this;
    }

    public AzCommandBuilder setTransitionSpeed(AzTarget target, float transitionSpeed) {
        actions.add(new AzSetTransitionSpeedAction(target, transitionSpeed));
        return this;
    }

    public AzCommandBuilder setStartTickOffset(AzTarget target, double tickOffset) {
        actions.add(new AzSetStartTickOffsetAction(target, tickOffset));
        return this;
    }

    public AzCommandBuilder setFreezeTickOffset(AzTarget target, double freezeTickOffset) {
        actions.add(new AzSetFreezeTickAction(target, freezeTickOffset));
        return this;
    }

    public AzCommandBuilder setReverseAnimation(AzTarget target, boolean hasReverse) {
        actions.add(new AzSetReverseAction(target, hasReverse));
        return this;
    }

    public AzCommandBuilder play(AzTarget target, String animationName) {
        return playSequence(target, builder -> builder.queue(animationName, properties -> properties));
    }

    public AzCommandBuilder play(AzTarget target, String animationName, AzPlayBehavior playBehavior) {
        return playSequence(
            target,
            builder -> builder.queue(animationName, properties -> properties.withPlayBehavior(playBehavior))
        );
    }

    public AzCommandBuilder playSequence(
        AzTarget target,
        UnaryOperator<AzAnimationSequenceBuilder> builderUnaryOperator
    ) {
        var sequence = builderUnaryOperator.apply(new AzAnimationSequenceBuilder()).build();
        actions.add(new AzPlayAnimationSequenceAction(target, sequence, currentPolicy()));
        return this;
    }

    public AzCommand build() {
        return new AzCommand(List.copyOf(actions));
    }

    /**
     * Materializes the {@link AzDispatchPolicy} for the play action being added. Throws if no
     * dispatch mode has been set — every play action must declare its dispatch intent. Use
     * {@link AzCommand#replay()}, {@link AzCommand#idempotent()}, {@link AzCommand#enqueueing()},
     * or call {@link #dispatchMode(AzDispatchMode)} on the builder.
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
