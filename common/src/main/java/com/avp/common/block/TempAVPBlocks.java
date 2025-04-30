package com.avp.common.block;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.level.block.Block;

import java.util.function.Supplier;

import com.avp.service.Services;

public class TempAVPBlocks {

    public static final Supplier<Block> BAUXITE_ORE = register("bauxite_ore", BlockProperties.BAUXITE_ORE);

    private static Supplier<Block> register(String name, BlockPropertyBuilder blockPropertyBuilder) {
        return Services.REGISTRY.register(BuiltInRegistries.BLOCK, name, () -> new Block(blockPropertyBuilder.build()));
    }

    public static void initialize() {}
}
