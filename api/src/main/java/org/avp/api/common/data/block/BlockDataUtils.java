package org.avp.api.common.data.block;

import net.minecraft.tags.BlockTags;
import net.minecraft.world.level.block.SlabBlock;
import net.minecraft.world.level.block.WallBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;
import org.avp.api.common.data.loot_table.LootProviders;
import org.avp.api.service.Services;

import java.util.HashSet;

public class BlockDataUtils {

    public static SingleBlockDataContainer.Holder intoSlab(SingleBlockDataContainer.Holder holder) {
        var extendedBlockTags = new HashSet<>(holder.getBlockTagData().blockTags());
        extendedBlockTags.add(BlockTags.SLABS);
        return new SingleBlockDataContainer(
            () -> new SlabBlock(BlockBehaviour.Properties.ofFullCopy(holder.getHolder().get())),
            holder.getRegistryName() + "_slab",
            new BlockModelData(
                block -> new BlockModelDataType.Slab(holder.getHolder().get(), block),
                holder.getBlockModelData().blockModelRenderType()
            ),
            BlockTagData.ofBlock(extendedBlockTags),
            LootProviders.SLAB
        ).withHolder();
    }

    public static SingleBlockDataContainer.Holder intoStairs(SingleBlockDataContainer.Holder holder) {
        var extendedBlockTags = new HashSet<>(holder.getBlockTagData().blockTags());
        extendedBlockTags.add(BlockTags.STAIRS);
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
            BlockTagData.ofBlock(extendedBlockTags),
            LootProviders.SELF
        ).withHolder();
    }

    public static SingleBlockDataContainer.Holder intoWall(SingleBlockDataContainer.Holder holder) {
        var extendedBlockTags = new HashSet<>(holder.getBlockTagData().blockTags());
        extendedBlockTags.add(BlockTags.WALLS);
        return new SingleBlockDataContainer(
            () -> new WallBlock(BlockBehaviour.Properties.ofFullCopy(holder.getHolder().get())),
            holder.getRegistryName() + "_wall",
            new BlockModelData(
                block -> new BlockModelDataType.Wall(holder.getHolder().get(), block),
                holder.getBlockModelData().blockModelRenderType()
            ),
            BlockTagData.ofBlock(extendedBlockTags),
            LootProviders.SELF
        ).withHolder();
    }

    private BlockDataUtils() {
        throw new UnsupportedOperationException();
    }
}
