package com.blib.api.client.animation.v1.command;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import com.blib.api.client.animation.v1.command.play_behavior.AzPlayBehavior;
import com.blib.api.client.animation.v1.command.play_behavior.AzPlayBehaviors;
import com.blib.internal.client.animation.AzAnimatorAccessor;
import com.blib.internal.client.animation.dispatch.AzDispatchSide;
import com.blib.internal.client.animation.dispatch.command.action.AzAction;
import com.blib.internal.common.codec.AzListStreamCodec;
import com.blib.internal.service.BLibInternalServices;
import com.blib.mod.BLib;
import com.blib.mod.common.network.packet.S2CBlockEntityDispatchCommandPayload;
import com.blib.mod.common.network.packet.S2CEntityDispatchCommandPayload;
import com.blib.mod.common.network.packet.S2CItemStackDispatchCommandPayload;
import com.blib.mod.common.registry.init.BLibDataComponents;

public record AzCommand(List<AzAction> actions) {

    public static final StreamCodec<FriendlyByteBuf, AzCommand> CODEC = StreamCodec.composite(
        new AzListStreamCodec<>(AzAction.CODEC),
        AzCommand::actions,
        AzCommand::new
    );

    public static AzRootCommandBuilder rootBuilder() {
        return new AzRootCommandBuilder();
    }

    public static AzControllerCommandBuilder controllerBuilder() {
        return new AzControllerCommandBuilder();
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

    public static AzCommand create(String controllerName, String animationName) {
        return create(controllerName, animationName, AzPlayBehaviors.PLAY_ONCE, 0F, 1F, 0F, 0F, 0F, false);
    }

    public static AzCommand create(String controllerName, String animationName, AzPlayBehavior playBehavior) {
        return create(controllerName, animationName, playBehavior, 0F, 1F, 0F, 0F, 0F, false);
    }

    // TODO: Fix transition length overriding transition length on the base create method
    public static AzCommand create(
        String controllerName,
        String animationName,
        AzPlayBehavior playBehavior,
        float startTickOffset,
        float animationSpeed,
        float transitionLength,
        float freezeTickOffset,
        float repeatXTimes,
        boolean isReversing
    ) {
        return controllerBuilder()
            .playSequence(
                controllerName,
                sequenceBuilder -> sequenceBuilder.queue(
                    animationName,
                    props -> props.withPlayBehavior(playBehavior)
                )
            )
            .setFreezeTickOffset(controllerName, freezeTickOffset)
            .setStartTickOffset(controllerName, startTickOffset)
            .setSpeed(controllerName, animationSpeed)
            .setRepeatAmount(controllerName, repeatXTimes)
            .setReverseAnimation(controllerName, isReversing)
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
        float repeatXTimes,
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
            .setRepeatAmount(repeatXTimes)
            .setReverseAnimation(isReversing)
            .build();
    }

    public void sendForEntity(Entity entity) {
        if (entity.level().isClientSide()) {
            dispatchFromClient(entity);
        } else {
            var entityId = entity.getId();
            var payload = new S2CEntityDispatchCommandPayload(entityId, this);
            BLibInternalServices.SERVER_NETWORKING.sendToAllClientsTrackingEntity(entity, payload);
        }
    }

    public void sendForBlockEntity(BlockEntity entity) {
        if (entity.getLevel().isClientSide()) {
            dispatchFromClient(entity);
        } else {
            var entityBlockPos = entity.getBlockPos();
            var payload = new S2CBlockEntityDispatchCommandPayload(entityBlockPos, this);
            BLibInternalServices.SERVER_NETWORKING.sendToAllClientsTrackingChunk((ServerLevel) entity.getLevel(), entityBlockPos, payload);
        }
    }

    public void sendForItem(Entity entity, ItemStack itemStack) {
        if (entity.level().isClientSide()) {
            dispatchFromClient(entity);
        } else {
            var uuid = itemStack.get(BLibDataComponents.AZ_ID.get());

            if (uuid == null) {
                BLib.LOGGER.warn(
                    "Could not find item stack UUID during dispatch. Did you forget to register an identity for the item? Item: {}, Item Stack: {}",
                    itemStack.getItem(),
                    itemStack
                );
                return;
            }

            var payload = new S2CItemStackDispatchCommandPayload(uuid, this);
            BLibInternalServices.SERVER_NETWORKING.sendToAllClientsTrackingEntity(entity, payload);
        }
    }

    private <T> void dispatchFromClient(T animatable) {
        var animator = AzAnimatorAccessor.getOrNull(animatable);

        if (animator != null) {
            actions.forEach(action -> action.handle(AzDispatchSide.CLIENT, animator));
        }
    }
}
