package com.blib.engine.domain.selection.volume.event;

import net.minecraft.core.BlockPos;
import org.jetbrains.annotations.ApiStatus;

/**
 * Domain event published by {@code BlockSelectionOps.paste} when the user requests a paste of the clipboard contents
 * into the current volume. The network adapter translates this into a {@code C2SPasteFromClipboardPayload}.
 *
 * @param destination block coordinate of the paste's min corner
 */
@ApiStatus.Internal
public record BlockVolumePasteRequested(BlockPos destination) {}
