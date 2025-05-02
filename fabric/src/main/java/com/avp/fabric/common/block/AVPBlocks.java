package com.avp.fabric.common.block;

import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.level.block.Block;

import com.avp.AVPResources;
import com.avp.common.block.BlockProperties;

@Deprecated
public class AVPBlocks {

    public static final Block NUKE_BLOCK = register(new NukeBlock(BlockProperties.NUKE.build()), "nuke");

    public static Block register(Block block, String id) {
        return Registry.register(BuiltInRegistries.BLOCK, AVPResources.location(id), block);
    }

    public static void initialize() {}
}
