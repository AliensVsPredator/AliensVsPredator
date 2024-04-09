package org.avp.common.legacy.schematic;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;

import java.util.Map;

import org.avp.common.AVPConstants;

public class LegacySchematic {

    private final short[] blockIdData;

    private final Map<Short, String> blockIdsToBlockNames;

    private final Map<String, Block> resolverMap;

    private final int width;

    private final int height;

    private final int length;

    public LegacySchematic(
        short[] blockIdData,
        Map<Short, String> blockIdsToBlockNames,
        Map<String, Block> resolverMap,
        int width,
        int height,
        int length
    ) {
        this.blockIdData = blockIdData;
        this.blockIdsToBlockNames = blockIdsToBlockNames;
        this.resolverMap = resolverMap;
        this.width = width;
        this.height = height;
        this.length = length;
    }

    public void generateInLevel(Level level, BlockPos startingBlockPos, boolean skipAir) {
        AVPConstants.LOGGER.debug("Generating schematic at pos {}", startingBlockPos);
        var offsetWidth = width - 1;
        var offsetHeight = height - 1;
        var offsetLength = length - 1;
        var bounds = BlockPos.betweenClosed(0, 0, 0, offsetWidth, offsetHeight, offsetLength);

        // Variables for debugging purposes.
        var total = offsetWidth * offsetHeight * offsetLength;
        var successfulBlockCount = 0;
        var now = System.currentTimeMillis();

        for (BlockPos blockPos : bounds) {
            var index = blockPos.getX() + (blockPos.getY() * length + blockPos.getZ()) * width;

            short blockId;

            try {
                blockId = blockIdData[index];
            } catch (ArrayIndexOutOfBoundsException e) {
                AVPConstants.LOGGER.error("Block ID index out of bounds. Index: {}, BlockPos: {}", index, blockPos);
                continue;
            }

            var blockName = blockIdsToBlockNames.get(blockId);
            var resolvedBlock = resolverMap.get(blockName);

            if (blockName == null || resolvedBlock == null) {
                AVPConstants.LOGGER.warn("Null block name or block: {}, {}", blockName, resolvedBlock);
                continue;
            }

            if (skipAir && resolvedBlock == Blocks.AIR) {
                continue;
            }
            var offsetBlockPos = blockPos.offset(startingBlockPos);
            var result = level.setBlock(offsetBlockPos, resolvedBlock.defaultBlockState(), 2);

            if (!result) {
                AVPConstants.LOGGER.warn("Server level could not set block: {}, {}", offsetBlockPos, resolvedBlock);
            } else {
                successfulBlockCount++;
            }
        }

        var timeTakenInMillis = System.currentTimeMillis() - now;

        AVPConstants.LOGGER.debug(
            "{}/{} blocks were successfully placed in {}ms.",
            successfulBlockCount,
            total,
            timeTakenInMillis
        );
    }
}
