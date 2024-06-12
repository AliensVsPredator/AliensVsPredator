package org.avp.api.common.registry.holder;

import net.minecraft.world.level.block.state.BlockBehaviour;
import org.avp.api.common.data.block.BlockData;

public record BlockHolderSetData(
    BlockBehaviour.Properties properties,
    BlockData blockData
) {}
