package org.avp.common.data.loot_table.block;

import net.minecraft.world.level.block.Block;

import org.avp.api.block.drop.BlockDrops;
import org.avp.api.block.drop.impl.ItemBlockDrop;
import org.avp.api.block.drop.impl.OtherBlockDrop;
import org.avp.api.block.drop.key.BlockDropKeys;
import org.avp.common.AVPConstants;
import org.avp.common.block.AVPBlocks;
import org.avp.common.data.loot_table.AbstractAVPBlockLootTableProvider;

public class AVPBlockLootTableProvider extends AbstractAVPBlockLootTableProvider {

    @Override
    public void generate() {
        AVPBlocks.getEntries().forEach(tuple -> {
            var block = tuple.first().get();
            var data = tuple.second();
            var drop = data.getDrop();
            var key = drop.getBlockDropKey();

            if (drop == BlockDrops.SELF) {
                dropSelf(block);
            } else if (drop instanceof OtherBlockDrop otherBlockDrop) {
                handleOtherBlockDrop(otherBlockDrop, block);
            } else if (drop instanceof ItemBlockDrop itemBlockDrop) {
                handleItemBlockDrop(itemBlockDrop, block);
            } else {
                AVPConstants.LOGGER.warn("[2] Unhandled block drop: Block: {}, Drop: {}, Key: {}", block, drop, key);
            }
        });
    }

    private void handleOtherBlockDrop(OtherBlockDrop otherBlockDrop, Block block) {
        var key = otherBlockDrop.getBlockDropKey();
        var otherBlock = otherBlockDrop.getOtherSupplier().get();

        if (key == BlockDropKeys.LEAF) {
            add(block, leafBlock -> this.createLeavesDrops(leafBlock, otherBlock));
        } else if (key == BlockDropKeys.OTHER) {
            dropOther(block, otherBlockDrop.getOtherSupplier().get());
        } else if (key == BlockDropKeys.ORE) {
            add(block, oreBlock -> createOreDrop(oreBlock, otherBlock.asItem()));
        } else if (key == BlockDropKeys.SILK_TOUCH_ONLY) {
            add(block, silkableBlock -> this.createSingleItemTableWithSilkTouch(silkableBlock, otherBlock));
        } else {
            AVPConstants.LOGGER.warn(
                "[0] Unhandled block drop: Block: {}, Drop: {}, Key: {}",
                block,
                otherBlockDrop,
                key
            );
        }
    }

    private void handleItemBlockDrop(ItemBlockDrop itemBlockDrop, Block block) {
        var key = itemBlockDrop.getBlockDropKey();

        if (key == BlockDropKeys.ORE) {
            add(block, oreBlock -> createOreDrop(oreBlock, itemBlockDrop.getOtherSupplier().get()));
        } else {
            AVPConstants.LOGGER.warn(
                "[1] Unhandled block drop: Block: {}, Drop: {}, Key: {}",
                block,
                itemBlockDrop,
                key
            );
        }
    }
}
