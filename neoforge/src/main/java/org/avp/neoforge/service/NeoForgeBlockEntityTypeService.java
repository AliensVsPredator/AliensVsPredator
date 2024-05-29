package org.avp.neoforge.service;

import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.registries.DeferredRegister;
import org.avp.api.Holder;
import org.avp.common.AVPConstants;
import org.avp.common.service.BlockEntityTypeService;
import org.avp.neoforge.util.NeoForgeBlockEntityHolder;

import java.util.function.BiFunction;

public class NeoForgeBlockEntityTypeService implements BlockEntityTypeService {

    public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITY_TYPES = DeferredRegister.create(
        BuiltInRegistries.BLOCK_ENTITY_TYPE,
        AVPConstants.MOD_ID
    );

    @Override
    public <T extends BlockEntity> Holder<BlockEntityType<T>> createHolder(
        String registryName,
        BiFunction<BlockPos, BlockState, T> blockEntityFactory,
        Holder<Block> blockHolder
    ) {
        return new NeoForgeBlockEntityHolder<>(
            BLOCK_ENTITY_TYPES,
            registryName,
            () -> BlockEntityType.Builder.of(blockEntityFactory::apply, blockHolder.get()).build(null)
        );
    }

    @Override
    public void register(Holder<BlockEntityType<?>> holder) { /* NO-OP FOR FORGE */ }
}
