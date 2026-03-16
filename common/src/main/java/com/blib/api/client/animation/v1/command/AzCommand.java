package com.blib.api.client.animation.v1.command;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import com.blib.api.client.animation.v1.command.play_behavior.AzPlayBehavior;
import com.blib.api.client.animation.v1.command.play_behavior.AzPlayBehaviors;
import com.blib.internal.client.animation.AzAnimatorAccessor;
import com.blib.internal.client.animation.dispatch.command.action.AzAction;

public record AzCommand(List<AzAction> actions) {

    public static AzRootCommandBuilder rootBuilder() {
        return new AzRootCommandBuilder();
    }

    public static AzTrackCommandBuilder trackBuilder() {
        return new AzTrackCommandBuilder();
    }

    public static AzCommand compose(Collection<AzCommand> commands) {
        if (commands.isEmpty()) {
            throw new IllegalArgumentException("Attempted to compose an empty collection of commands.");
        } else if (commands.size() == 1) {
            return commands.iterator().next();
        }

        return new AzCommand(
            commands.stream()
                .flatMap(command -> command.actions().stream())
                .toList()
        );
    }

    public static AzCommand compose(AzCommand first, AzCommand second, AzCommand... others) {
        var allCommands = new ArrayList<AzCommand>();

        allCommands.add(first);
        allCommands.add(second);
        Collections.addAll(allCommands, others);

        return compose(allCommands);
    }

    public static AzCommand create(String trackName, String animationName) {
        return create(trackName, animationName, AzPlayBehaviors.PLAY_ONCE, 0F, 1F, 0F, 0F, false);
    }

    public static AzCommand create(String trackName, String animationName, AzPlayBehavior playBehavior) {
        return create(trackName, animationName, playBehavior, 0F, 1F, 0F, 0F, false);
    }

    // TODO: Fix transition length overriding transition length on the base create method
    public static AzCommand create(
        String trackName,
        String animationName,
        AzPlayBehavior playBehavior,
        float startTickOffset,
        float animationSpeed,
        float transitionLength,
        float freezeTickOffset,
        boolean isReversing
    ) {
        return trackBuilder()
            .playSequence(
                trackName,
                sequenceBuilder -> sequenceBuilder.queue(
                    animationName,
                    props -> props.withPlayBehavior(playBehavior)
                )
            )
            .setFreezeTickOffset(trackName, freezeTickOffset)
            .setStartTickOffset(trackName, startTickOffset)
            .setSpeed(trackName, animationSpeed)
            .setReverseAnimation(trackName, isReversing)
            .build();
    }

    // TODO: Fix transition length overriding transition lenght on the base create method
    public static AzCommand createRoot(
        String animationName,
        AzPlayBehavior playBehavior,
        float startTickOffset,
        float animationSpeed,
        float transitionLength,
        float freezeTickOffset,
        boolean isReversing
    ) {
        return rootBuilder()
            .playSequence(
                sequenceBuilder -> sequenceBuilder.queue(
                    animationName,
                    props -> props.withPlayBehavior(playBehavior)
                )
            )
            .setFreezeTickOffset(freezeTickOffset)
            .setTransitionSpeed(transitionLength)
            .setStartTickOffset(startTickOffset)
            .setSpeed(animationSpeed)
            .setReverseAnimation(isReversing)
            .build();
    }

    public <T> void dispatch(T animatable) {
        var animator = AzAnimatorAccessor.getOrNull(animatable);

        if (animator != null) {
            actions.forEach(action -> action.handle(animator));
        }
    }
}
