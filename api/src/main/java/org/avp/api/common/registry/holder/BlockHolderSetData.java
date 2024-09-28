package org.avp.api.common.registry.holder;

import net.minecraft.world.level.block.state.BlockBehaviour;

import org.avp.api.common.data.block.OldBlockData;

@Deprecated(forRemoval = true)
public record BlockHolderSetData(
    BlockBehaviour.Properties properties,
    OldBlockData oldBlockData
) {}
