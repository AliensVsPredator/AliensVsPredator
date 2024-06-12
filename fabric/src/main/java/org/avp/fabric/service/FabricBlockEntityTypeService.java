package org.avp.fabric.service;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

import java.util.function.BiFunction;

import org.avp.api.common.registry.holder.BLHolder;
import org.avp.api.service.BlockEntityTypeService;
import org.avp.common.registry.holder.AVPHolder;

public class FabricBlockEntityTypeService implements BlockEntityTypeService {

    @Override
    public <T extends BlockEntity> BLHolder<BlockEntityType<T>> createHolder(
        String registryName,
        BiFunction<BlockPos, BlockState, T> blockEntityFactory,
        BLHolder<Block> blockHolder
    ) {
        return new AVPHolder<>(
            registryName,
            () -> BlockEntityType.Builder.of(blockEntityFactory::apply, blockHolder.get()).build()
        );
    }

    @Override
    public void register(BLHolder<BlockEntityType<?>> holder) {
        Registry.register(BuiltInRegistries.BLOCK_ENTITY_TYPE, holder.getResourceLocation(), holder.get());
    }
}
