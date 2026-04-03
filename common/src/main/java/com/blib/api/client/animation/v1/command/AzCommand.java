package com.blib.api.client.animation.v1.command;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import com.blib.api.BLibAPI;
import com.blib.api.client.animation.v1.command.play_behavior.AzPlayBehavior;
import com.blib.api.client.animation.v1.command.play_behavior.AzPlayBehaviors;
import com.blib.internal.client.animation.AzAnimatorAccessor;
import com.blib.internal.client.animation.dispatch.command.action.AzAction;

public record AzCommand(List<AzAction> actions) {

    private static final Logger LOGGER = LoggerFactory.getLogger(AzCommand.class);

    private static final String SERVER_SIDE_DISPATCH_MESSAGE =
        "AzCommand.dispatch() was called on the server for %s '%s'. "
            + "Animation commands only work client-side. "
            + "Use a data-synced flag or network packet to trigger animations from the server.";

    private static final String SERVER_SIDE_DISPATCH_MESSAGE_LOG = SERVER_SIDE_DISPATCH_MESSAGE.replaceAll("%s", "{}");

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

    public void dispatchForEntity(Entity entity) {
        validateClientSide(entity.level(), "Entity", entity);
        dispatch(entity);
    }

    public void dispatchForBlockEntity(BlockEntity blockEntity) {
        var level = blockEntity.getLevel();

        if (level != null) {
            validateClientSide(level, "BlockEntity", blockEntity);
        }

        dispatch(blockEntity);
    }

    public void dispatchForItem(Entity entity, ItemStack itemStack) {
        validateClientSide(entity.level(), "ItemStack", itemStack);
        dispatch(itemStack);
    }

    private <T> void dispatch(T animatable) {
        var animator = AzAnimatorAccessor.getOrNull(animatable);

        if (animator != null) {
            actions.forEach(action -> action.handle(animator));
        }
    }

    private void validateClientSide(Level level, String type, Object animatable) {
        if (level.isClientSide()) {
            return;
        }

        if (BLibAPI.isDevelopmentEnvironment()) {
            throw new IllegalStateException(SERVER_SIDE_DISPATCH_MESSAGE.formatted(type, animatable));
        }

        LOGGER.warn(SERVER_SIDE_DISPATCH_MESSAGE_LOG, type, animatable);
    }
}
