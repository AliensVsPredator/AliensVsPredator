package com.blib.mod.common.network;

import net.minecraft.world.entity.player.Player;
import org.jetbrains.annotations.ApiStatus;

import com.blib.api.common.data_sync.v1.model.DataUser;
import com.blib.azurelib.common.animation.AzAnimatorAccessor;
import com.blib.azurelib.common.animation.cache.AzIdentifiableItemStackAnimatorCache;
import com.blib.azurelib.common.animation.dispatch.AzDispatchSide;
import com.blib.azurelib.common.util.client.ClientUtils;
import com.blib.mod.common.network.packet.S2CBlockEntityDispatchCommandPayload;
import com.blib.mod.common.network.packet.S2CEntityDataSyncPayload;
import com.blib.mod.common.network.packet.S2CEntityDispatchCommandPayload;
import com.blib.mod.common.network.packet.S2CItemStackDispatchCommandPayload;

@ApiStatus.Internal
public final class BLibClientListener {

    public static void handleBlockEntityDispatchCommand(
        S2CBlockEntityDispatchCommandPayload blockEntityDispatchCommandPayload,
        Player player
    ) {
        var blockPos = blockEntityDispatchCommandPayload.blockPos();
        var blockEntity = ClientUtils.getLevel().getBlockEntity(blockPos);

        if (blockEntity == null) {
            return;
        }

        var animator = AzAnimatorAccessor.getOrNull(blockEntity);

        if (animator != null && animator.context().animatable().getBlockPos().equals(blockPos)) {
            var dispatchCommand = blockEntityDispatchCommandPayload.dispatchCommand();
            dispatchCommand.actions().forEach(action -> action.handle(AzDispatchSide.SERVER, animator));
        }
    }

    public static void handleEntityDataSync(S2CEntityDataSyncPayload entityDataSyncPayload, Player player) {
        var targetEntity = player.level().getEntity(entityDataSyncPayload.entityId());

        if (targetEntity == null) {
            return;
        }

        var dataContainer = ((DataUser) targetEntity).getDataContainer();

        entityDataSyncPayload.rawDataSyncMap()
            .rawDataById()
            .forEach(dataContainer::set);
    }

    public static void handleEntityDispatchCommand(S2CEntityDispatchCommandPayload entityDispatchCommandPayload, Player player) {
        var entity = ClientUtils.getLevel().getEntity(entityDispatchCommandPayload.entityId());

        if (entity == null) {
            return;
        }

        var animator = AzAnimatorAccessor.getOrNull(entity);

        if (animator != null) {
            var dispatchCommand = entityDispatchCommandPayload.dispatchCommand();
            dispatchCommand.actions().forEach(action -> action.handle(AzDispatchSide.SERVER, animator));
        }
    }

    public static void handleItemStackDispatchCommand(S2CItemStackDispatchCommandPayload itemStackDispatchCommandPayload, Player player) {
        var animator = AzIdentifiableItemStackAnimatorCache.getInstance().getOrNull(itemStackDispatchCommandPayload.itemStackId());

        if (animator != null) {
            var dispatchCommand = itemStackDispatchCommandPayload.dispatchCommand();
            dispatchCommand.actions().forEach(action -> action.handle(AzDispatchSide.SERVER, animator));
        }
    }

    private BLibClientListener() {
        throw new UnsupportedOperationException();
    }
}
