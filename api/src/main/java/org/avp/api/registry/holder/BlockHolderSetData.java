package org.avp.api.registry.holder;

import net.minecraft.world.level.block.state.BlockBehaviour;
import org.avp.api.data.block.BlockData;

public record BlockHolderSetData(
    BlockBehaviour.Properties properties,
    BlockData blockData
) {}
