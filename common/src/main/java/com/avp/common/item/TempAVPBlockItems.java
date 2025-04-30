package com.avp.common.item;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;

import java.util.function.Supplier;

import com.avp.common.block.TempAVPBlocks;
import com.avp.service.Services;

// TODO: Rename this once multi-loader migration is finished.
public class TempAVPBlockItems {

    public static final Supplier<BlockItem> AUTUNITE_ORE = register("autunite_ore", TempAVPBlocks.AUTUNITE_ORE);

    public static final Supplier<BlockItem> BAUXITE_ORE = register("bauxite_ore", TempAVPBlocks.BAUXITE_ORE);

    public static final Supplier<BlockItem> DEEPSLATE_TITANIUM_ORE = register(
        "deepslate_titanium_ore",
        TempAVPBlocks.DEEPSLATE_TITANIUM_ORE
    );

    public static final Supplier<BlockItem> DEEPSLATE_ZINC_ORE = register("deepslate_zinc_ore", TempAVPBlocks.DEEPSLATE_ZINC_ORE);

    public static final Supplier<BlockItem> GALENA_ORE = register("galena_ore", TempAVPBlocks.GALENA_ORE);

    public static final Supplier<BlockItem> LITHIUM_ORE = register("lithium_ore", TempAVPBlocks.LITHIUM_ORE);

    public static final Supplier<BlockItem> MONAZITE_ORE = register("monazite_ore", TempAVPBlocks.MONAZITE_ORE);

    public static final Supplier<BlockItem> ZINC_ORE = register("zinc_ore", TempAVPBlocks.ZINC_ORE);

    private static Supplier<BlockItem> register(String id, Supplier<Block> blockSupplier) {
        return Services.REGISTRY.register(BuiltInRegistries.ITEM, id, () -> new BlockItem(blockSupplier.get(), new Item.Properties()));
    }

    public static void initialize() {}
}
