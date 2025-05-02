package com.avp.fabric.common.block;

import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockBehaviour;

import com.avp.AVPResources;
import com.avp.common.block.BlockProperties;
import com.avp.common.block.BlockPropertyBuilder;

public class AVPBlocks {

    public static final Block NUKE_BLOCK = register(new NukeBlock(BlockProperties.NUKE.build()), "nuke");

    public static final Block RAZOR_WIRE = register(new RazorWireBlock(BlockProperties.RAZOR_WIRE.build()), "razor_wire");

    public static Block register(BlockPropertyBuilder builder, String id) {
        return register(builder.build(), id);
    }

    public static Block register(BlockBehaviour.Properties properties, String id) {
        return register(new Block(properties), id);
    }

    public static Block register(Block block, String id) {
        return Registry.register(BuiltInRegistries.BLOCK, AVPResources.location(id), block);
    }

    public static void initialize() {}
}
