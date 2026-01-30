package com.blib.internal.client.animation.dispatch.command;

import java.util.function.UnaryOperator;

import com.blib.internal.client.animation.dispatch.command.action.impl.root.AzRootCancelAllAction;
import com.blib.internal.client.animation.dispatch.command.action.impl.root.AzRootPlayAnimationSequenceAction;
import com.blib.internal.client.animation.dispatch.command.action.impl.root.AzRootSetAnimationSpeedAction;
import com.blib.internal.client.animation.dispatch.command.action.impl.root.AzRootSetEasingTypeAction;
import com.blib.internal.client.animation.dispatch.command.action.impl.root.AzRootSetFreezeTickAction;
import com.blib.internal.client.animation.dispatch.command.action.impl.root.AzRootSetRepeatTimesAction;
import com.blib.internal.client.animation.dispatch.command.action.impl.root.AzRootSetReverseAction;
import com.blib.internal.client.animation.dispatch.command.action.impl.root.AzRootSetStartTickOffsetAction;
import com.blib.internal.client.animation.dispatch.command.action.impl.root.AzRootSetTransitionSpeedAction;
import com.blib.internal.client.animation.dispatch.command.sequence.AzAnimationSequenceBuilder;
import com.blib.internal.client.animation.easing.AzEasingType;

public class AzRootCommandBuilder extends AzCommandBuilder {

    public AzRootCommandBuilder append(AzCommand command) {
        actions.addAll(command.actions());
        return this;
    }

    public AzRootCommandBuilder cancelAll() {
        actions.add(AzRootCancelAllAction.INSTANCE);
        return this;
    }

    public AzRootCommandBuilder setEasingType(AzEasingType easingType) {
        actions.add(new AzRootSetEasingTypeAction(easingType));
        return this;
    }

    public AzRootCommandBuilder setSpeed(float speed) {
        actions.add(new AzRootSetAnimationSpeedAction(speed));
        return this;
    }

    public AzRootCommandBuilder setTransitionSpeed(float transitionSpeed) {
        actions.add(new AzRootSetTransitionSpeedAction(transitionSpeed));
        return this;
    }

    public AzRootCommandBuilder setStartTickOffset(float tickOffset) {
        actions.add(new AzRootSetStartTickOffsetAction(tickOffset));
        return this;
    }

    public AzRootCommandBuilder setFreezeTickOffset(float freezeTickOffset) {
        actions.add(new AzRootSetFreezeTickAction(freezeTickOffset));
        return this;
    }

    public AzRootCommandBuilder setRepeatAmount(float repeatAmount) {
        actions.add(new AzRootSetRepeatTimesAction(repeatAmount));
        return this;
    }

    public AzRootCommandBuilder setReverseAnimation(boolean hasReverse) {
        actions.add(new AzRootSetReverseAction(hasReverse));
        return this;
    }

    public AzRootCommandBuilder playSequence(
        UnaryOperator<AzAnimationSequenceBuilder> builderUnaryOperator
    ) {
        var sequence = builderUnaryOperator.apply(new AzAnimationSequenceBuilder()).build();
        actions.add(new AzRootPlayAnimationSequenceAction(sequence));
        return this;
    }
}
