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
import com.blib.api.client.animation.v1.command.policy.AzDispatchMode;
import com.blib.internal.client.animation.AzAnimatorAccessor;
import com.blib.internal.client.animation.dispatch.command.action.AzAction;

public record AzCommand(List<AzAction> actions) {

    private static final Logger LOGGER = LoggerFactory.getLogger(AzCommand.class);

    private static final String SERVER_SIDE_DISPATCH_MESSAGE =
        "AzCommand.dispatch() was called on the server for %s '%s'. "
            + "Animation commands only work client-side. "
            + "Use a data-synced flag or network packet to trigger animations from the server.";

    private static final String SERVER_SIDE_DISPATCH_MESSAGE_LOG = SERVER_SIDE_DISPATCH_MESSAGE.replaceAll("%s", "{}");

    /**
     * Returns a fresh command builder with no dispatch mode set. Useful for set-only commands
     * (e.g. {@code setSpeed} without any play action) or when the caller wants to set the mode
     * explicitly via {@link AzCommandBuilder#dispatchMode(AzDispatchMode)}. Adding a play action
     * to a builder with no mode set throws.
     */
    public static AzCommandBuilder builder() {
        return new AzCommandBuilder();
    }

    /**
     * Returns a command builder with {@link AzDispatchMode#REPLAY} pre-set. Subsequent
     * {@code playSequence} / {@code play} calls produce actions that always restart the dispatched
     * animation from frame 0.
     */
    public static AzCommandBuilder replay() {
        return new AzCommandBuilder().dispatchMode(AzDispatchMode.REPLAY);
    }

    /**
     * Returns a command builder with {@link AzDispatchMode#PLAY_IF_NOT_PLAYING} pre-set. Subsequent
     * {@code playSequence} / {@code play} calls produce actions that no-op if the dispatched
     * sequence is already the active one, and otherwise replay it.
     */
    public static AzCommandBuilder idempotent() {
        return new AzCommandBuilder().dispatchMode(AzDispatchMode.PLAY_IF_NOT_PLAYING);
    }

    /**
     * Returns a command builder with {@link AzDispatchMode#ENQUEUE} pre-set. Subsequent
     * {@code playSequence} / {@code play} calls produce actions that append to the queue rather
     * than interrupting the current animation.
     */
    public static AzCommandBuilder enqueueing() {
        return new AzCommandBuilder().dispatchMode(AzDispatchMode.ENQUEUE);
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
