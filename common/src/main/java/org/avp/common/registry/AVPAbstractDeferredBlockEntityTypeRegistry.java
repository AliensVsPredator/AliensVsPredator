package org.avp.common.registry;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import org.avp.api.Holder;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BiFunction;

public abstract class AVPAbstractDeferredBlockEntityTypeRegistry {

    protected final List<Holder<? extends BlockEntityType<?>>> entries;

    protected AVPAbstractDeferredBlockEntityTypeRegistry() {
        entries = new ArrayList<>();
    }

    protected abstract <T extends BlockEntity> Holder<BlockEntityType<T>> createHolder(
        String registryName,
        BiFunction<BlockPos, BlockState, T> blockEntityFactory,
        Holder<Block> blockHolder
    );

    public abstract void register();

    public List<Holder<? extends BlockEntityType<?>>> getEntries() {
        return entries;
    }
}
