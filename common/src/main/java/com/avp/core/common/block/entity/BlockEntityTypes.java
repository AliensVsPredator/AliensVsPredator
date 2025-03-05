package com.avp.core.common.block.entity;

import com.avp.core.platform.service.Services;
import net.minecraft.Util;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.util.datafix.fixes.References;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;

import com.avp.core.AVPResources;
import com.avp.core.common.block.AVPBlocks;
import com.avp.core.common.block.entity.resin_node.ResinNodeBlockEntity;

import java.util.Objects;
import java.util.function.Supplier;

public class BlockEntityTypes {

    public static final Supplier<BlockEntityType<ResinNodeBlockEntity>> RESIN_NODE = register(
        "resin_node",
        () -> BlockEntityType.Builder.of(ResinNodeBlockEntity::new, AVPBlocks.NETHER_RESIN_NODE.get(), AVPBlocks.RESIN_NODE.get())
    );

    private static <T extends BlockEntity> Supplier<BlockEntityType<T>> register(String id, Supplier<BlockEntityType.Builder<T>> builderSupplier) {
        return Services.REGISTRY.register(BuiltInRegistries.BLOCK_ENTITY_TYPE, id, () -> {
            var type = Util.fetchChoiceType(References.BLOCK_ENTITY, id);
            return builderSupplier.get().build(type);
        });
    }

    public static void initialize() {}
}
