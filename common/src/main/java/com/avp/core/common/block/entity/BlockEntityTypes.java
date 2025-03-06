package com.avp.core.common.block.entity;

import com.avp.core.platform.service.Services;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;

import com.avp.core.common.block.AVPBlocks;
import com.avp.core.common.block.entity.resin_node.ResinNodeBlockEntity;

import java.util.function.Supplier;

public class BlockEntityTypes {

    public static final Supplier<BlockEntityType<ResinNodeBlockEntity>> RESIN_NODE = register(
        "resin_node",
        () -> BlockEntityType.Builder.of(ResinNodeBlockEntity::new, AVPBlocks.NETHER_RESIN_NODE.get(), AVPBlocks.RESIN_NODE.get()).build(null)
    );

    private static <T extends BlockEntity> Supplier<BlockEntityType<T>> register(String id, Supplier<BlockEntityType<T>> builderSupplier) {
        return Services.REGISTRY.registerBlockEntity(id, builderSupplier);
    }

    public static void initialize() {}
}
