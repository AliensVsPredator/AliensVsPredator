package org.avp.api.common.data.block;

import net.minecraft.tags.BlockTags;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.level.block.FenceBlock;
import net.minecraft.world.level.block.FenceGateBlock;
import net.minecraft.world.level.block.SlabBlock;
import net.minecraft.world.level.block.WallBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.properties.WoodType;

import java.util.HashSet;

import org.avp.api.common.data.loot_table.LootProviders;
import org.avp.api.service.Services;

public class BlockDataUtils {

    public static SingleBlockDataContainer.Holder intoFence(SingleBlockDataContainer.Holder holder) {
        var extendedBlockTags = new HashSet<>(holder.getBlockTagData().blockTags());
        extendedBlockTags.add(BlockTags.FENCES);
        var extendedItemTags = new HashSet<>(holder.getBlockTagData().itemTags());
        extendedItemTags.add(ItemTags.FENCES);
        return new SingleBlockDataContainer(
            () -> new FenceBlock(BlockBehaviour.Properties.ofFullCopy(holder.getHolder().get())),
            holder.getRegistryName() + "_fence",
            new BlockModelData(
                block -> new BlockModelDataType.Fence(holder.getHolder().get(), block),
                holder.getBlockModelData().blockModelRenderType()
            ),
            new BlockTagData(extendedBlockTags, extendedItemTags),
            LootProviders.SELF
        ).withHolder();
    }

    public static SingleBlockDataContainer.Holder intoFenceGate(WoodType woodType, SingleBlockDataContainer.Holder holder) {
        var extendedBlockTags = new HashSet<>(holder.getBlockTagData().blockTags());
        extendedBlockTags.add(BlockTags.FENCE_GATES);
        var extendedItemTags = new HashSet<>(holder.getBlockTagData().itemTags());
        extendedItemTags.add(ItemTags.FENCE_GATES);
        return new SingleBlockDataContainer(
            () -> new FenceGateBlock(woodType, BlockBehaviour.Properties.ofFullCopy(holder.getHolder().get())),
            holder.getRegistryName() + "_fence_gate",
            new BlockModelData(
                block -> new BlockModelDataType.FenceGate(holder.getHolder().get(), block),
                holder.getBlockModelData().blockModelRenderType()
            ),
            new BlockTagData(extendedBlockTags, extendedItemTags),
            LootProviders.SELF
        ).withHolder();
    }

    public static SingleBlockDataContainer.Holder intoSlab(SingleBlockDataContainer.Holder holder) {
        var extendedBlockTags = new HashSet<>(holder.getBlockTagData().blockTags());
        extendedBlockTags.add(BlockTags.SLABS);
        var extendedItemTags = new HashSet<>(holder.getBlockTagData().itemTags());
        extendedItemTags.add(ItemTags.SLABS);
        return new SingleBlockDataContainer(
            () -> new SlabBlock(BlockBehaviour.Properties.ofFullCopy(holder.getHolder().get())),
            holder.getRegistryName() + "_slab",
            new BlockModelData(
                block -> new BlockModelDataType.Slab(holder.getHolder().get(), block),
                holder.getBlockModelData().blockModelRenderType()
            ),
            new BlockTagData(extendedBlockTags, extendedItemTags),
            LootProviders.SLAB
        ).withHolder();
    }

    public static SingleBlockDataContainer.Holder intoStairs(SingleBlockDataContainer.Holder holder) {
        var extendedBlockTags = new HashSet<>(holder.getBlockTagData().blockTags());
        extendedBlockTags.add(BlockTags.STAIRS);
        var extendedItemTags = new HashSet<>(holder.getBlockTagData().itemTags());
        extendedItemTags.add(ItemTags.STAIRS);
        return new SingleBlockDataContainer(
            () -> {
                var parentHolder = holder.getHolder();
                var parentBlock = parentHolder.get();
                var properties = BlockBehaviour.Properties.ofFullCopy(parentBlock);
                return Services.BLOCK_SERVICE.createStairBlock(parentHolder, properties);
            },
            holder.getRegistryName() + "_stairs",
            new BlockModelData(
                block -> new BlockModelDataType.Stair(holder.getHolder().get(), block),
                holder.getBlockModelData().blockModelRenderType()
            ),
            new BlockTagData(extendedBlockTags, extendedItemTags),
            LootProviders.SELF
        ).withHolder();
    }

    public static SingleBlockDataContainer.Holder intoWall(SingleBlockDataContainer.Holder holder) {
        var extendedBlockTags = new HashSet<>(holder.getBlockTagData().blockTags());
        extendedBlockTags.add(BlockTags.WALLS);
        var extendedItemTags = new HashSet<>(holder.getBlockTagData().itemTags());
        extendedItemTags.add(ItemTags.WALLS);
        return new SingleBlockDataContainer(
            () -> new WallBlock(BlockBehaviour.Properties.ofFullCopy(holder.getHolder().get())),
            holder.getRegistryName() + "_wall",
            new BlockModelData(
                block -> new BlockModelDataType.Wall(holder.getHolder().get(), block),
                holder.getBlockModelData().blockModelRenderType()
            ),
            new BlockTagData(extendedBlockTags, extendedItemTags),
            LootProviders.SELF
        ).withHolder();
    }

    private BlockDataUtils() {
        throw new UnsupportedOperationException();
    }
}
