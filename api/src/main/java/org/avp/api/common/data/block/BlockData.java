package org.avp.api.common.data.block;

import net.minecraft.tags.BlockTags;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.storage.loot.LootTable;

import java.util.HashSet;
import java.util.function.Function;

import org.avp.api.common.data.loot_table.LootProviders;
import org.avp.api.common.registry.holder.BLHolder;

public record BlockData(
    String registryName,
    BlockModelData blockModelData,
    BlockTagData blockTagData,
    Function<Block, LootTable.Builder> lootProvider
) {

    public BlockData(String registryName, BlockModelData blockModelData, BlockTagData blockTagData) {
        this(registryName, blockModelData, blockTagData, LootProviders.SELF);
    }

    public BlockData withRegistryName(String registryName) {
        return new BlockData(registryName, blockModelData, blockTagData, lootProvider);
    }

    public BlockData withPrefixRegistryName(String prefix) {
        return new BlockData(prefix + registryName, blockModelData, blockTagData, lootProvider);
    }

    public BlockData withSuffixRegistryName(String suffix) {
        return new BlockData(registryName + suffix, blockModelData, blockTagData, lootProvider);
    }

    public static BlockData toSlab(BLHolder<Block> parentHolder, BlockBehaviour.Properties properties, BlockData blockData) {
        var blockTags = new HashSet<>(blockData.blockTagData().blockTags());
        blockTags.add(BlockTags.SLABS);
        var itemTags = new HashSet<>(blockData.blockTagData().itemTags());
        itemTags.add(ItemTags.SLABS);

        return new BlockData(
            blockData.registryName() + "_slab",
            BlockModelData.slab(parentHolder, properties),
            new BlockTagData(blockTags, itemTags),
            LootProviders.SLAB
        );
    }

    public static BlockData toStairs(BLHolder<Block> parentHolder, BlockBehaviour.Properties properties, BlockData blockData) {
        var blockTags = new HashSet<>(blockData.blockTagData().blockTags());
        blockTags.add(BlockTags.STAIRS);
        var itemTags = new HashSet<>(blockData.blockTagData().itemTags());
        itemTags.add(ItemTags.STAIRS);

        return new BlockData(
            blockData.registryName() + "_stairs",
            BlockModelData.stairs(parentHolder, properties),
            new BlockTagData(blockTags, itemTags)
        );
    }

    public static BlockData toWall(BLHolder<Block> parentHolder, BlockBehaviour.Properties properties, BlockData blockData) {
        var blockTags = new HashSet<>(blockData.blockTagData().blockTags());
        blockTags.add(BlockTags.WALLS);
        var itemTags = new HashSet<>(blockData.blockTagData().itemTags());
        itemTags.add(ItemTags.WALLS);

        return new BlockData(
            blockData.registryName() + "_wall",
            BlockModelData.wall(parentHolder, properties),
            new BlockTagData(blockTags, itemTags)
        );
    }
}
