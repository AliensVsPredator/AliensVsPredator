package com.blib.api.client.animation.v1.command;

import java.util.function.UnaryOperator;

import com.blib.api.client.animation.v1.command.play_behavior.AzPlayBehavior;
import com.blib.api.client.animation.v1.command.policy.AzDispatchMode;
import com.blib.api.client.animation.v1.command.policy.OnBlockedByEndless;
import com.blib.api.client.animation.v1.command.policy.OnPropertiesChanged;
import com.blib.api.client.animation.v1.command.sequence.AzAnimationSequenceBuilder;
import com.blib.internal.client.animation.dispatch.command.action.impl.track.AzTrackCancelAction;
import com.blib.internal.client.animation.dispatch.command.action.impl.track.AzTrackPlayAnimationSequenceAction;
import com.blib.internal.client.animation.dispatch.command.action.impl.track.AzTrackSetAnimationSpeedAction;
import com.blib.internal.client.animation.dispatch.command.action.impl.track.AzTrackSetEasingTypeAction;
import com.blib.internal.client.animation.dispatch.command.action.impl.track.AzTrackSetFreezeTickAction;
import com.blib.internal.client.animation.dispatch.command.action.impl.track.AzTrackSetReverseAction;
import com.blib.internal.client.animation.dispatch.command.action.impl.track.AzTrackSetStartTickOffsetAction;
import com.blib.internal.client.animation.dispatch.command.action.impl.track.AzTrackSetTransitionSpeedAction;
import com.blib.internal.client.animation.easing.AzEasingType;

public class AzTrackCommandBuilder extends AzCommandBuilder {

    public AzTrackCommandBuilder append(AzCommand command) {
        actions.addAll(command.actions());
        return this;
    }

    public AzTrackCommandBuilder dispatchMode(AzDispatchMode mode) {
        this.dispatchMode = mode;
        return this;
    }

    public AzTrackCommandBuilder onBlockedByEndless(OnBlockedByEndless policy) {
        this.onBlockedByEndless = policy;
        return this;
    }

    public AzTrackCommandBuilder onPropertiesChanged(OnPropertiesChanged policy) {
        this.onPropertiesChanged = policy;
        return this;
    }

    public AzTrackCommandBuilder setEasingType(String trackName, AzEasingType easingType) {
        actions.add(new AzTrackSetEasingTypeAction(trackName, easingType));
        return this;
    }

    public AzTrackCommandBuilder setSpeed(String trackName, float speed) {
        actions.add(new AzTrackSetAnimationSpeedAction(trackName, speed));
        return this;
    }

    public AzTrackCommandBuilder setTransitionSpeed(String trackName, float transitionSpeed) {
        actions.add(new AzTrackSetTransitionSpeedAction(trackName, transitionSpeed));
        return this;
    }

    public AzTrackCommandBuilder setStartTickOffset(String trackName, float tickOffset) {
        actions.add(new AzTrackSetStartTickOffsetAction(trackName, tickOffset));
        return this;
    }

    public AzTrackCommandBuilder setFreezeTickOffset(String trackName, float freezeTickOffset) {
        actions.add(new AzTrackSetFreezeTickAction(trackName, freezeTickOffset));
        return this;
    }

    public AzTrackCommandBuilder setReverseAnimation(String trackName, boolean hasReverse) {
        actions.add(new AzTrackSetReverseAction(trackName, hasReverse));
        return this;
    }

    public AzTrackCommandBuilder cancel(String trackName) {
        actions.add(new AzTrackCancelAction(trackName));
        return this;
    }

    public AzTrackCommandBuilder play(String trackName, String animationName) {
        return playSequence(trackName, builder -> builder.queue(animationName, properties -> properties));
    }

    public AzTrackCommandBuilder play(String trackName, String animationName, AzPlayBehavior playBehavior) {
        return playSequence(
            trackName,
            builder -> builder.queue(animationName, properties -> properties.withPlayBehavior(playBehavior))
        );
    }

    public AzTrackCommandBuilder playSequence(
        String trackName,
        UnaryOperator<AzAnimationSequenceBuilder> builderUnaryOperator
    ) {
        var sequence = builderUnaryOperator.apply(new AzAnimationSequenceBuilder()).build();
        actions.add(new AzTrackPlayAnimationSequenceAction(trackName, sequence, currentPolicy()));
        return this;
    }
}
