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

    public static final Supplier<BlockItem> BAUXITE_ORE = register("bauxite_ore", TempAVPBlocks.BAUXITE_ORE);

    private static Supplier<BlockItem> register(String name, Supplier<Block> blockSupplier) {
        return Services.REGISTRY.register(BuiltInRegistries.ITEM, name, () -> new BlockItem(blockSupplier.get(), new Item.Properties()));
    }

    public static void initialize() {}
}
