package com.blib.mod.common.registry.init;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.level.block.Block;

import com.blib.api.common.registry.v1.BLibHolder;
import com.blib.api.common.registry.v1.BLibRegistry;
import com.blib.mod.BLib;
import com.blib.mod.common.gameplay.block.TickingLightBlock;

public class BLibBlocks {

    private static final BLibRegistry<Block> REGISTRY = BLib.MOD.registries().create(BuiltInRegistries.BLOCK);

    public static final BLibHolder<Block> TICKING_LIGHT = REGISTRY.createHolder("ticking_light", TickingLightBlock::new);

    public static void initialize() {
        REGISTRY.registerAll();
    }
}
