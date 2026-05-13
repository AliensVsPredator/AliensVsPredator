package com.blib.engine.domain.selection.volume.event;

import net.minecraft.core.BlockPos;
import org.jetbrains.annotations.ApiStatus;

/**
 * Domain event published by {@code BlockSelectionOps.copy} when the user requests a copy/cut of the current volume. The
 * network adapter ({@code BlockVolumeNetAdapter}) subscribes and translates the event into a
 * {@code C2SCopySelectionPayload}, so the domain layer never imports packet types.
 * <p>
 * {@code min} and {@code max} are inclusive integer block coordinates derived from the volume's AABB.
 *
 * @param min inclusive minimum block coordinate of the volume
 * @param max inclusive maximum block coordinate of the volume
 * @param cut {@code true} for cut (server deletes the source after the copy), {@code false} for plain copy
 */
@ApiStatus.Internal
public record BlockVolumeCopyRequested(
    BlockPos min,
    BlockPos max,
    boolean cut
) {}
