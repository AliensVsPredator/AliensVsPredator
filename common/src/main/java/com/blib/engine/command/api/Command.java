package com.blib.engine.command.api;

import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.ApiStatus;

import java.util.UUID;

/**
 * Editor-level intent that the UI emits in response to a user gesture. Handlers translate the intent into side-effects
 * — network packets, modal screen swaps, in-process state mutation — so the UI never has to import packet types,
 * networking, or domain mutators directly.
 * <p>
 * Adding a new intent: add a permitted record below, then update the relevant handler (network / local / modal) to
 * route it.
 */
@ApiStatus.Internal
public sealed interface Command {

    record RequestFactionDirectory() implements Command {}

    record UndoAction() implements Command {}

    record RedoAction() implements Command {}

    record ReloadProject(String projectName) implements Command {}

    record DeleteProject(String projectName) implements Command {}

    record DismemberAllLimbs(int entityId) implements Command {}

    record DismemberLimb(
        int entityId,
        ResourceLocation limbId
    ) implements Command {}

    record RemoveEntity(int entityId) implements Command {}

    record DeleteBlockVolume(
        BlockPos cornerA,
        BlockPos cornerB,
        ResourceLocation dimensionId
    ) implements Command {}

    record DeletePlacedPiece(UUID pieceId) implements Command {}
}
