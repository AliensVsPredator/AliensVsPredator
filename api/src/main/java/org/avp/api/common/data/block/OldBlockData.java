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

@Deprecated(forRemoval = true)
public record OldBlockData(
    String registryName,
    BlockModelData blockModelData,
    BlockTagData blockTagData,
    Function<Block, LootTable.Builder> lootProvider
) {

    @Deprecated(forRemoval = true)
    public OldBlockData(String registryName, BlockModelData blockModelData, BlockTagData blockTagData) {
        this(registryName, blockModelData, blockTagData, LootProviders.SELF);
    }

    @Deprecated(forRemoval = true)
    public OldBlockData withRegistryName(String registryName) {
        return new OldBlockData(registryName, blockModelData, blockTagData, lootProvider);
    }

    @Deprecated(forRemoval = true)
    public OldBlockData withPrefixRegistryName(String prefix) {
        return new OldBlockData(prefix + registryName, blockModelData, blockTagData, lootProvider);
    }

    @Deprecated(forRemoval = true)
    public OldBlockData withSuffixRegistryName(String suffix) {
        return new OldBlockData(registryName + suffix, blockModelData, blockTagData, lootProvider);
    }

    @Deprecated(forRemoval = true)
    public static OldBlockData toSlab(BLHolder<Block> parentHolder, BlockBehaviour.Properties properties, OldBlockData oldBlockData) {
        var blockTags = new HashSet<>(oldBlockData.blockTagData().blockTags());
        blockTags.add(BlockTags.SLABS);
        var itemTags = new HashSet<>(oldBlockData.blockTagData().itemTags());
        itemTags.add(ItemTags.SLABS);

        return new OldBlockData(
            oldBlockData.registryName() + "_slab",
            BlockModelData.slab(parentHolder, properties),
            new BlockTagData(blockTags, itemTags),
            LootProviders.SLAB
        );
    }

    @Deprecated(forRemoval = true)
    public static OldBlockData toStairs(BLHolder<Block> parentHolder, BlockBehaviour.Properties properties, OldBlockData oldBlockData) {
        var blockTags = new HashSet<>(oldBlockData.blockTagData().blockTags());
        blockTags.add(BlockTags.STAIRS);
        var itemTags = new HashSet<>(oldBlockData.blockTagData().itemTags());
        itemTags.add(ItemTags.STAIRS);

        return new OldBlockData(
            oldBlockData.registryName() + "_stairs",
            BlockModelData.stairs(parentHolder, properties),
            new BlockTagData(blockTags, itemTags)
        );
    }

    @Deprecated(forRemoval = true)
    public static OldBlockData toWall(BLHolder<Block> parentHolder, BlockBehaviour.Properties properties, OldBlockData oldBlockData) {
        var blockTags = new HashSet<>(oldBlockData.blockTagData().blockTags());
        blockTags.add(BlockTags.WALLS);
        var itemTags = new HashSet<>(oldBlockData.blockTagData().itemTags());
        itemTags.add(ItemTags.WALLS);

        return new OldBlockData(
            oldBlockData.registryName() + "_wall",
            BlockModelData.wall(parentHolder, properties),
            new BlockTagData(blockTags, itemTags)
        );
    }
}
