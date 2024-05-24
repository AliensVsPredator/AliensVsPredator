package org.avp.api.block;

import net.minecraft.world.level.block.state.BlockBehaviour;

public record BlockHolderSetData(
    BlockBehaviour.Properties properties,
    BlockData blockData
) {}
