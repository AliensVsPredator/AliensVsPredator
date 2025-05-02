package com.avp.fabric.common.block_item;

import mod.azure.azurelib.rewrite.animation.cache.AzIdentityRegistry;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;

import java.util.function.Supplier;

import com.avp.AVPResources;
import com.avp.common.item.TempAVPBlockItems;
import com.avp.fabric.common.block.AVPBlocks;

public class AVPBlockItems {

    public static final BlockItem NUKE_BLOCK = register(AVPBlocks.NUKE_BLOCK);

    @Deprecated
    public static BlockItem register(Block block) {
        return register(new Item.Properties(), block);
    }

    @Deprecated
    public static BlockItem register(Item.Properties properties, Block block) {
        return register(properties, block, BuiltInRegistries.BLOCK.getKey(block).getPath());
    }

    @Deprecated
    public static BlockItem register(Item.Properties properties, Block block, String id) {
        var resourceLocation = AVPResources.location(id);
        var blockItem = new BlockItem(block, properties);

        return Registry.register(BuiltInRegistries.ITEM, resourceLocation, blockItem);
    }

    @Deprecated
    public static BlockItem registerCustomBlockItem(BlockItem blockItem, Block block) {
        var resourceLocation = AVPResources.location(BuiltInRegistries.BLOCK.getKey(block).getPath());

        return Registry.register(BuiltInRegistries.ITEM, resourceLocation, blockItem);
    }

    public static BlockItem register(String id, Supplier<BlockItem> blockItemSupplier) {
        var resourceLocation = AVPResources.location(id);

        return Registry.register(BuiltInRegistries.ITEM, resourceLocation, blockItemSupplier.get());
    }

    public static void initialize() {
        // FIXME:
        AzIdentityRegistry.register(TempAVPBlockItems.RESONATOR_BLOCK.get(), TempAVPBlockItems.SENTRY_TURRET.get());
    }
}
