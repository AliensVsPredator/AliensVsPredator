package org.avp.common.legacy.schematic.resolver;

import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;

import java.util.Map;

import org.avp.common.registry.block.AVPUnidentifiedBlockRegistry;

public class LVRockSchematicResolver {

    public static final Map<String, Block> RESOLVER_MAP = Map.<String, Block>ofEntries(
        Map.entry("0", Blocks.AIR),
        Map.entry("1", AVPUnidentifiedBlockRegistry.INSTANCE.rock.get())
    );

    private LVRockSchematicResolver() {
        throw new UnsupportedOperationException();
    }
}
