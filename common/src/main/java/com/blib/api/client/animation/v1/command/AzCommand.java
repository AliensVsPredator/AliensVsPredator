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

public record AzCommand<T>(List<AzAction<T>> actions) {

    public AzCommand {
        // Defensive copy: ensures the published action list is immutable and decoupled from any
        // mutable list the caller (typically AzCommandBuilder) may continue to hold.
        actions = List.copyOf(actions);
    }

    private static final Logger LOGGER = LoggerFactory.getLogger(AzCommand.class);

    private static final String SERVER_SIDE_DISPATCH_MESSAGE =
        "AzCommand.dispatch() was called on the server for %s '%s'. "
            + "Animation commands only work client-side. "
            + "Use a data-synced flag or network packet to trigger animations from the server.";

    private static final String SERVER_SIDE_DISPATCH_MESSAGE_LOG = SERVER_SIDE_DISPATCH_MESSAGE.replaceAll("%s", "{}");

    /**
     * Returns a fresh command builder with no dispatch mode set. Useful for set-only commands (e.g. {@code setSpeed}
     * without any play action) or when the caller wants to set the mode explicitly via
     * {@link AzCommandBuilder#dispatchMode(AzDispatchMode)}. Adding a play action to a builder with no mode set throws.
     */
    public static <T> AzCommandBuilder<T> builder() {
        return new AzCommandBuilder<>();
    }

    /**
     * Returns a command builder with {@link AzDispatchMode#REPLAY} pre-set.
     */
    public static <T> AzCommandBuilder<T> replay() {
        return new AzCommandBuilder<T>().dispatchMode(AzDispatchMode.REPLAY);
    }

    /**
     * Returns a command builder with {@link AzDispatchMode#PLAY_IF_NOT_PLAYING} pre-set.
     */
    public static <T> AzCommandBuilder<T> idempotent() {
        return new AzCommandBuilder<T>().dispatchMode(AzDispatchMode.PLAY_IF_NOT_PLAYING);
    }

    /**
     * Returns a command builder with {@link AzDispatchMode#ENQUEUE} pre-set.
     */
    public static <T> AzCommandBuilder<T> enqueueing() {
        return new AzCommandBuilder<T>().dispatchMode(AzDispatchMode.ENQUEUE);
    }

    public static <T> AzCommand<T> compose(Collection<AzCommand<T>> commands) {
        if (commands.isEmpty()) {
            throw new IllegalArgumentException("Attempted to compose an empty collection of commands.");
        } else if (commands.size() == 1) {
            return commands.iterator().next();
        }

        return new AzCommand<>(
            commands.stream()
                .flatMap(command -> command.actions().stream())
                .toList()
        );
    }

    @SafeVarargs
    public static <T> AzCommand<T> compose(AzCommand<T> first, AzCommand<T> second, AzCommand<T>... others) {
        var allCommands = new ArrayList<AzCommand<T>>();

        allCommands.add(first);
        allCommands.add(second);
        Collections.addAll(allCommands, others);

        return compose(allCommands);
    }

    public void dispatchForEntity(T entity) {
        if (entity instanceof Entity mcEntity) {
            validateClientSide(mcEntity.level(), "Entity", entity);
        }
        dispatch(entity);
    }

    public void dispatchForBlockEntity(T blockEntity) {
        if (blockEntity instanceof BlockEntity mcBlockEntity) {
            var level = mcBlockEntity.getLevel();

            if (level != null) {
                validateClientSide(level, "BlockEntity", blockEntity);
            }
        }

        dispatch(blockEntity);
    }

    public void dispatchForItem(Entity entity, T itemStack) {
        if (itemStack instanceof ItemStack stack) {
            validateClientSide(entity.level(), "ItemStack", stack);
        }
        dispatch(itemStack);
    }

    private void dispatch(T animatable) {
        var animator = AzAnimatorAccessor.<Object, T>getOrNull(animatable);

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
