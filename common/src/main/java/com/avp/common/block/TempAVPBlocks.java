package com.avp.common.block;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.level.block.Block;

import java.util.function.Supplier;

import com.avp.service.Services;

public class TempAVPBlocks {

    // FIXME: Change this to new RadiatedBlock once RadiatedBlock is migrated.
    public static final Supplier<Block> AUTUNITE_ORE = register("autunite_ore", () -> new Block(BlockProperties.AUTUNITE_ORE.build()));

    public static final Supplier<Block> BAUXITE_ORE = register("bauxite_ore", BlockProperties.BAUXITE_ORE);

    public static final Supplier<Block> DEEPSLATE_TITANIUM_ORE = register("deepslate_titanium_ore", BlockProperties.DEEPSLATE_TITANIUM_ORE);

    public static final Supplier<Block> DEEPSLATE_ZINC_ORE = register("deepslate_zinc_ore", BlockProperties.DEEPSLATE_ZINC_ORE);

    public static final Supplier<Block> GALENA_ORE = register("galena_ore", BlockProperties.GALENA_ORE);

    public static final Supplier<Block> LITHIUM_ORE = register("lithium_ore", () -> new LithiumBlock(BlockProperties.LITHIUM_ORE.build()));

    public static final Supplier<Block> MONAZITE_ORE = register("monazite_ore", BlockProperties.MONAZITE_ORE);

    public static final Supplier<Block> ZINC_ORE = register("zinc_ore", BlockProperties.ZINC_ORE);

    private static Supplier<Block> register(String id, BlockPropertyBuilder blockPropertyBuilder) {
        return register(id, () -> new Block(blockPropertyBuilder.build()));
    }

    private static Supplier<Block> register(String id, Supplier<Block> blockSupplier) {
        return Services.REGISTRY.register(BuiltInRegistries.BLOCK, id, blockSupplier);
    }

    public static void initialize() {}
}
