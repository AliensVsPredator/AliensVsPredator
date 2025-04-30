package com.avp.common.block;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.level.block.Block;

import java.util.Arrays;
import java.util.Collections;
import java.util.Map;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import com.avp.service.Services;
import net.minecraft.world.level.block.SlabBlock;

// TODO: Rename this once multi-loader migration is finished.
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

    public static final Map<DyeColor, Supplier<Block>> DYE_COLOR_TO_CONCRETE_SLAB =
        Collections.unmodifiableMap(
            Arrays.stream(DyeColor.values())
                .collect(
                    Collectors.toMap(
                        Function.identity(),
                        dyeColor -> register(
                            dyeColor.getName() + "_concrete_slab",
                            () -> new SlabBlock(BlockProperties.DYE_COLOR_TO_CONCRETE_PROPERTIES.get(dyeColor).build())
                        )
                    )
                )
        );

    private static Supplier<Block> register(String id, BlockPropertyBuilder blockPropertyBuilder) {
        return register(id, () -> new Block(blockPropertyBuilder.build()));
    }

    private static Supplier<Block> register(String id, Supplier<Block> blockSupplier) {
        return Services.REGISTRY.register(BuiltInRegistries.BLOCK, id, blockSupplier);
    }

    public static void initialize() {}
}
