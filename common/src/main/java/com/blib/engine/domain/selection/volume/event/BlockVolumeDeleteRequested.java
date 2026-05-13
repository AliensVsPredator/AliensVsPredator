package com.blib.engine.domain.selection.volume.event;

import net.minecraft.core.BlockPos;
import org.jetbrains.annotations.ApiStatus;

/**
 * Domain event published by {@code BlockSelectionOps.delete} when the user requests deletion of the current volume. The
 * network adapter translates this into a {@code C2SDeleteSelectionPayload}.
 *
 * @param min inclusive minimum block coordinate
 * @param max inclusive maximum block coordinate
 */
@ApiStatus.Internal
public record BlockVolumeDeleteRequested(
    BlockPos min,
    BlockPos max
) {}
