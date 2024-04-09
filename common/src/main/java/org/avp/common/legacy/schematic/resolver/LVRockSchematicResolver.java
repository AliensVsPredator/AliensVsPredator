package org.avp.common.legacy.schematic.resolver;

import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;

import java.util.Map;

public class LVRockSchematicResolver {

    public static final Map<String, Block> RESOLVER_MAP = Map.<String, Block>ofEntries(
        Map.entry("0", Blocks.AIR),
        Map.entry("1", Blocks.WHITE_WOOL) // TODO: LV 426 Stone
    );

    private LVRockSchematicResolver() {
        throw new UnsupportedOperationException();
    }
}
