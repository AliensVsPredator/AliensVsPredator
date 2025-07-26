package com.lib.common.gameplay.entity.manager;

import com.alien.common.gameplay.entity.living.alien.xenomorph.queen.Queen;
import com.lib.common.gameplay.NBTSerializable;
import com.lib.common.network.DataAccessor;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.PathfinderMob;

public class CrawlingManager implements NBTSerializable {

    private static final String NBT_CRAWLING = "crawling";

    private final PathfinderMob entity;

    private final DataAccessor<Boolean> isCrawling;

    public CrawlingManager(PathfinderMob entity, DataAccessor<Boolean> isCrawling) {
        this.entity = entity;
        this.isCrawling = isCrawling;
    }

    public void tick() {
        if (entity.level().isClientSide) {
            return;
        }

        if (entity instanceof Queen) {
            return;
        }

        tryToCrawl();
    }

    public boolean isCrawling() {
        return isCrawling.get();
    }

    private void tryToCrawl() {
        var blockPosition = entity.blockPosition();
        var level = entity.level();
        var navigation = entity.getNavigation();

        if (level.isClientSide) {
            return;
        }

        var path = navigation.getPath();

        var isTight = isTightSpace(blockPosition);

        if (path != null && path.getNextNodeIndex() < path.getNodeCount()) {
            var previousNode = path.getPreviousNode();
            isTight = isTight || previousNode != null && isTightSpace(previousNode.asBlockPos());
            var nextNode = path.getNextNode();
            isTight = isTight || isTightSpace(nextNode.asBlockPos());
        }

        isCrawling.set(isTight);
    }

    private boolean isTightSpace(BlockPos blockPos) {
        var level = entity.level();
        var above = blockPos.above();
        var aboveState = level.getBlockState(above);
        return !aboveState.isAir() && aboveState.entityCanStandOn(entity.level(), blockPos, entity);
    }

    @Override
    public void load(CompoundTag compoundTag) {
        if (compoundTag.contains(NBT_CRAWLING)) {
            isCrawling.set(compoundTag.getBoolean(NBT_CRAWLING));
        }
    }

    @Override
    public void save(CompoundTag compoundTag) {
        compoundTag.putBoolean(NBT_CRAWLING, isCrawling.get());
    }
}
