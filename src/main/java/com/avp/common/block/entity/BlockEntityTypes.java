package com.avp.common.block.entity;

import net.minecraft.Util;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.util.datafix.fixes.References;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;

import com.avp.AVPResources;
import com.avp.common.block.AVPBlocks;
import com.avp.common.block.entity.resin_node.ResinNodeBlockEntity;

public class BlockEntityTypes {

    public static final BlockEntityType<ResinNodeBlockEntity> RESIN_NODE = register(
        "resin_node",
        BlockEntityType.Builder.of(ResinNodeBlockEntity::new, AVPBlocks.NETHER_RESIN_NODE, AVPBlocks.RESIN_NODE)
    );

    private static <T extends BlockEntity> BlockEntityType<T> register(String id, BlockEntityType.Builder<T> builder) {
        var type = Util.fetchChoiceType(References.BLOCK_ENTITY, id);
        var resourceLocation = AVPResources.location(id);
        return Registry.register(BuiltInRegistries.BLOCK_ENTITY_TYPE, resourceLocation, builder.build(type));
    }

    public static void initialize() {}
}
