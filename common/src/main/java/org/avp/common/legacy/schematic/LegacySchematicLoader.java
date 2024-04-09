package org.avp.common.legacy.schematic;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtAccounter;
import net.minecraft.nbt.NbtIo;
import net.minecraft.world.level.block.Block;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

import org.avp.common.AVPConstants;

public class LegacySchematicLoader {

    private static final String ADD_BLOCKS = "AddBlocks";

    private static final String ALPHA = "Alpha";

    private static final String BLOCKS = "Blocks";

    private static final String HEIGHT = "Height";

    private static final String LENGTH = "Length";

    private static final String MAPPING = "Mapping";

    private static final String MATERIALS = "Materials";

    private static final String WIDTH = "Width";

    public static Optional<LegacySchematic> loadFromFile(File file, Map<String, Block> resolverMap) {
        try {
            var compoundTag = NbtIo.readCompressed(file.toPath(), NbtAccounter.unlimitedHeap());
            return Optional.of(compoundTag)
                .flatMap(tag -> loadFromCompoundTag(tag, resolverMap));
        } catch (IOException e) {
            AVPConstants.LOGGER.error("An exception occurred while reading a Schematic file!", e);
            return Optional.empty();
        }
    }

    public static Optional<LegacySchematic> loadFromCompoundTag(
        CompoundTag compoundTag,
        Map<String, Block> resolverMap
    ) {
        String materials = compoundTag.getString(MATERIALS);

        if (!Objects.equals(materials, ALPHA)) {
            AVPConstants.LOGGER.warn("Unsupported schematic format: {}", materials);
            return Optional.empty();
        }

        var width = compoundTag.getShort(WIDTH);
        var height = compoundTag.getShort(HEIGHT);
        var length = compoundTag.getShort(LENGTH);

        var blockIds = compoundTag.getByteArray(BLOCKS);
        var addBlocks = compoundTag.getByteArray(ADD_BLOCKS);
        var mappings = compoundTag.getCompound(MAPPING);

        if (mappings.getAllKeys().isEmpty()) {
            AVPConstants.LOGGER.warn("Schematic is missing mappings!");
        }

        var blockIdsToBlockNames = new HashMap<Short, String>();

        for (String key : mappings.getAllKeys()) {
            var blockId = mappings.getShort(key);
            blockIdsToBlockNames.put(blockId, key);
            if (!resolverMap.containsKey(key)) {
                AVPConstants.LOGGER.warn("No resolution found for key {}", key);
            }
        }

        var blockIdData = new short[blockIds.length];

        for (int i = 0; i < blockIds.length; i++) {
            short blockID = (short) (blockIds[i] & 0xff);

            if (addBlocks.length >= (blockIds.length + 1) / 2) {
                boolean lowerNybble = (i & 1) == 0;
                blockID |= (short) (lowerNybble
                    ? ((addBlocks[i >> 1] & 0x0F) << 8)
                    : ((addBlocks[i >> 1] & 0xF0) << 4));
            }

            blockIdData[i] = blockID;
        }

        return Optional.of(
            new LegacySchematic(blockIdData, blockIdsToBlockNames, resolverMap, width, height, length)
        );
    }

    private LegacySchematicLoader() {
        throw new UnsupportedOperationException();
    }
}
