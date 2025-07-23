package com.alien.common.gameplay.level.gameevent.listener;

import com.alien.common.data.AlienVariantTypes;
import com.alien.common.gameplay.block.entity.resin.vent.ResinVentBlockEntity;
import com.lib.common.gameplay.util.spatial.block.BlockPosUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.level.gameevent.GameEventListener;
import net.minecraft.world.level.gameevent.PositionSource;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;

import com.avp.common.registry.tag.AVPBlockTags;
import com.avp.common.registry.tag.AVPEntityTypeTags;

public class CryForHelpListener implements GameEventListener {

    private static final int MAXIMUM_SUMMONED_XENOMORPHS_PER_HIVE = 60;

    private final PositionSource positionSource;

    public CryForHelpListener(PositionSource positionSource) {
        this.positionSource = positionSource;
    }

    @Override
    public @NotNull PositionSource getListenerSource() {
        return positionSource;
    }

    @Override
    public @NotNull GameEventListener.DeliveryMode getDeliveryMode() {
        return DeliveryMode.BY_DISTANCE;
    }

    @Override
    public int getListenerRadius() {
        return 16;
    }

    @Override
    public boolean handleGameEvent(
        @NotNull ServerLevel serverLevel,
        @NotNull Holder<GameEvent> holder,
        @NotNull GameEvent.Context context,
        @NotNull Vec3 vec3
    ) {
        var sourceEntity = context.sourceEntity();

        if (
            sourceEntity == null
                // If there is no alien variant type for given source entity
                // OR if there is a cry for help event type mismatch...
                || AlienVariantTypes.getFor(sourceEntity)
                    .isNoneOr(alienVariantType -> !holder.is(alienVariantType.cryForHelpEvent().getHolder()))
        ) {
            // Then ignore the event.
            return false;
        }

        var blockPos = positionSource.getPosition(serverLevel)
            .map(BlockPos::containing)
            .orElse(null);

        if (blockPos == null) {
            return false;
        }

        var blockEntity = serverLevel.getBlockEntity(blockPos);

        if (
            !(blockEntity instanceof ResinVentBlockEntity resinVentBlockEntity)
                || resinVentBlockEntity.getAlienSpawnCooldown().isActive()
        ) {
            return false;
        }

        var hive = resinVentBlockEntity.getHive();

        if (hive == null || hive.getMembershipManager().getLoadedMembers().size() >= MAXIMUM_SUMMONED_XENOMORPHS_PER_HIVE) {
            return false;
        }

        var basePos = resinVentBlockEntity.getBlockPos();
        var freeSpaces = BlockPosUtil.getNeighborsMatching(serverLevel, basePos, blockState -> blockState.is(AVPBlockTags.RESIN_WEBS));

        var spawnPos = freeSpaces.isEmpty()
            ? null
            : freeSpaces.get(sourceEntity.getRandom().nextInt(freeSpaces.size()));

        if (spawnPos == null) {
            return false;
        }

        var reserveManager = hive.getReserveManager();
        var availableEntityTypes = reserveManager.getAvailableEntityTypes()
            .stream()
            .filter(entityType -> entityType.is(AVPEntityTypeTags.ANSWERS_XENOMORPH_CRIES_FOR_HELP))
            .toList();

        var randomSummonType = availableEntityTypes.isEmpty()
            ? null
            : availableEntityTypes.get(sourceEntity.getRandom().nextInt(availableEntityTypes.size()));

        if (randomSummonType == null || !reserveManager.canSpawn(randomSummonType)) {
            return false;
        }

        var summonedAlien = randomSummonType.spawn(serverLevel, spawnPos, MobSpawnType.MOB_SUMMONED);

        if (summonedAlien != null) {
            resinVentBlockEntity.getAlienSpawnCooldown().reset();
            reserveManager.add(randomSummonType, -1);

            if (sourceEntity instanceof Mob sourceMob && summonedAlien instanceof Mob summonedAlienMob) {
                summonedAlienMob.setTarget(sourceMob.getTarget());
            }

            return true;
        }

        return false;
    }
}
