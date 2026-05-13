package com.blib.engine.command.api;

import org.jetbrains.annotations.ApiStatus;

import com.blib.engine.command.network.NetworkCommandHandler;

/**
 * Single dispatch point for {@link Command}s emitted by the workspace UI. The bus pattern-matches the intent to the
 * appropriate handler so the UI never holds direct references to packet types or networking facilities.
 */
@ApiStatus.Internal
public final class CommandBus {

    private final NetworkCommandHandler network = new NetworkCommandHandler();

    public void dispatch(Command command) {
        switch (command) {
            case Command.RequestFactionDirectory c -> network.onRequestFactionDirectory(c);
            case Command.UndoAction c -> network.onUndoAction(c);
            case Command.RedoAction c -> network.onRedoAction(c);
            case Command.ReloadProject c -> network.onReloadProject(c);
            case Command.DeleteProject c -> network.onDeleteProject(c);
            case Command.GoapTrack c -> network.onGoapTrack(c);
            case Command.DismemberAllLimbs c -> network.onDismemberAll(c);
            case Command.DismemberLimb c -> network.onDismemberLimb(c);
            case Command.RemoveEntity c -> network.onRemoveEntity(c);
            case Command.DeleteBlockVolume c -> network.onDeleteBlockVolume(c);
            case Command.DeletePlacedPiece c -> network.onDeletePlacedPiece(c);
        }
    }
}
