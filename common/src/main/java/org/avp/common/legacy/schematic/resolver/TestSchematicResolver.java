package org.avp.common.legacy.schematic.resolver;

import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;

import java.util.Map;

import org.avp.common.block.AVPIndustrialBlocks;

public class TestSchematicResolver {

    public static final Map<String, Block> RESOLVER_MAP = Map.ofEntries(
        Map.entry("avp:industrialglass", AVPIndustrialBlocks.INSTANCE.glass.get()),
        Map.entry("avp:industrialwall", AVPIndustrialBlocks.INSTANCE.wall.get()),
        Map.entry("avp:industrialwall2", AVPIndustrialBlocks.INSTANCE.wallHazard.get()),
        Map.entry("minecraft:air", Blocks.AIR),

        // WOOL PLACEHOLDERS
        Map.entry("avp:ceilinggrill", Blocks.BLACK_WOOL),
        Map.entry("avp:cryostasistube", Blocks.BLUE_WOOL),
        Map.entry("avp:generator", Blocks.BROWN_WOOL),
        Map.entry("avp:industrialwall.corner", Blocks.CYAN_WOOL),
        Map.entry("avp:industrialwall.slope", Blocks.GRAY_WOOL),
        Map.entry("avp:industrialwall2.corner", Blocks.GREEN_WOOL),
        Map.entry("avp:industrialwall2.slope", Blocks.LIGHT_BLUE_WOOL),
        Map.entry("avp:engineershipwall2.invertedcorner", Blocks.LIGHT_GRAY_WOOL),
        Map.entry("avp:metalpanel1", Blocks.LIME_WOOL),
        Map.entry("avp:powerline", Blocks.MAGENTA_WOOL),

        // CONCRETE PLACEHOLDERS
        Map.entry("avp:hiveresin", Blocks.BLACK_CONCRETE)
    );

    private TestSchematicResolver() {
        throw new UnsupportedOperationException();
    }
}
