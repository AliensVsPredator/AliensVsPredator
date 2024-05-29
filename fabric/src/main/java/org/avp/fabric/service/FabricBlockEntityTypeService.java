package org.avp.fabric.service;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import org.avp.api.Holder;
import org.avp.common.service.BlockEntityTypeService;

import java.util.function.BiFunction;

public class FabricBlockEntityTypeService implements BlockEntityTypeService {

    @Override
    public <T extends BlockEntity> Holder<BlockEntityType<T>> createHolder(
        String registryName,
        BiFunction<BlockPos, BlockState, T> blockEntityFactory,
        Holder<Block> blockHolder
    ) {
        return new Holder<>(
            registryName,
            () -> BlockEntityType.Builder.of(blockEntityFactory::apply, blockHolder.get()).build()
        );
    }

    @Override
    public void register(Holder<BlockEntityType<?>> holder) {
        Registry.register(BuiltInRegistries.BLOCK_ENTITY_TYPE, holder.getResourceLocation(), holder.get());
    }
}
