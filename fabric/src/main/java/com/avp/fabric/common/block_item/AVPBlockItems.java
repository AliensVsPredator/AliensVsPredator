package com.avp.fabric.common.block_item;

import mod.azure.azurelib.rewrite.animation.cache.AzIdentityRegistry;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;

import java.util.function.Supplier;

import com.avp.AVPResources;
import com.avp.fabric.common.block.AVPBlocks;

public class AVPBlockItems {

    public static final BlockItem REDSTONE_GENERATOR = register(AVPBlocks.REDSTONE_GENERATOR);

    public static final BlockItem DESK_TERMINAL_BLOCK = register(AVPBlocks.DESK_TERMINAL_BLOCK);

    public static final BlockItem TRIP_MINE_BLOCK = register(AVPBlocks.TRIP_MINE_BLOCK);

    public static final BlockItem RESONATOR_BLOCK = register(AVPBlocks.RESONATOR_BLOCK);

    public static final BlockItem SENTRY_TURRET = register("sentry_turret", SentryTurretBlockItem::new);

    public static final BlockItem NUKE_BLOCK = register(AVPBlocks.NUKE_BLOCK);

    public static final BlockItem LEAD_CHEST = register("lead_chest", LeadChestBlockItem::new);

    public static final BlockItem AMMO_CHEST = register("ammo_chest", AmmoChestBlockItem::new);

    public static final BlockItem RAZOR_WIRE = register(AVPBlocks.RAZOR_WIRE);

    // Metal Block - Slabs and Stairs

    public static final BlockItem INDUSTRIAL_FURNACE_BLOCK = register(AVPBlocks.INDUSTRIAL_FURNACE);

    // Siding - Slabs and Stairs

    // Fastened Siding - Slabs and Stairs

    // Fastened Standing - Slabs and Stairs

    // Plating - Slabs and Stairs

    // Tread - Slabs and Stairs

    // Grate - Slabs and Stairs

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
        AzIdentityRegistry.register(RESONATOR_BLOCK, SENTRY_TURRET);
    }
}
