package org.avp.neoforge.service;

import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.registries.DeferredRegister;
import org.avp.api.registry.holder.BLHolder;
import org.avp.common.AVPConstants;
import org.avp.api.service.BlockEntityTypeService;
import org.avp.neoforge.util.NeoForgeBlockEntityHolder;

import java.util.function.BiFunction;

public class NeoForgeBlockEntityTypeService implements BlockEntityTypeService {

    public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITY_TYPES = DeferredRegister.create(
        BuiltInRegistries.BLOCK_ENTITY_TYPE,
        AVPConstants.MOD_ID
    );

    @Override
    public <T extends BlockEntity> BLHolder<BlockEntityType<T>> createHolder(
        String registryName,
        BiFunction<BlockPos, BlockState, T> blockEntityFactory,
        BLHolder<Block> blockHolder
    ) {
        return new NeoForgeBlockEntityHolder<>(
            BLOCK_ENTITY_TYPES,
            registryName,
            () -> BlockEntityType.Builder.of(blockEntityFactory::apply, blockHolder.get()).build(null)
        );
    }

    @Override
    public void register(BLHolder<BlockEntityType<?>> holder) { /* NO-OP FOR FORGE */ }
}
