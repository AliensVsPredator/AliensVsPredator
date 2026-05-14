package com.blib.engine.command.network;

import org.jetbrains.annotations.ApiStatus;

import com.blib.engine.command.api.Command;
import com.blib.mod.BLib;
import com.blib.mod.common.network.packet.C2SDeletePlacedPiecePayload;
import com.blib.mod.common.network.packet.C2SDeleteProjectPayload;
import com.blib.mod.common.network.packet.C2SDeleteSelectionPayload;
import com.blib.mod.common.network.packet.C2SDismemberAllLimbsPayload;
import com.blib.mod.common.network.packet.C2SDismemberLimbPayload;
import com.blib.mod.common.network.packet.C2SRedoActionPayload;
import com.blib.mod.common.network.packet.C2SReloadProjectPayload;
import com.blib.mod.common.network.packet.C2SRemoveEntityPayload;
import com.blib.mod.common.network.packet.C2SRequestFactionDirectoryPayload;
import com.blib.mod.common.network.packet.C2SUndoActionPayload;

/**
 * Translates editor {@link Command}s into C2S packets. The only class in the command/ layer that imports the network
 * package — everything upstream of it (UI, domain, controllers) sees only intent records.
 */
@ApiStatus.Internal
public final class NetworkCommandHandler {

    public void onRequestFactionDirectory(Command.RequestFactionDirectory ignored) {
        BLib.MOD.networking().sendToServer(C2SRequestFactionDirectoryPayload.INSTANCE);
    }

    public void onUndoAction(Command.UndoAction ignored) {
        BLib.MOD.networking().sendToServer(C2SUndoActionPayload.INSTANCE);
    }

    public void onRedoAction(Command.RedoAction ignored) {
        BLib.MOD.networking().sendToServer(C2SRedoActionPayload.INSTANCE);
    }

    public void onReloadProject(Command.ReloadProject cmd) {
        BLib.MOD.networking().sendToServer(new C2SReloadProjectPayload(cmd.projectName()));
    }

    public void onDeleteProject(Command.DeleteProject cmd) {
        BLib.MOD.networking().sendToServer(new C2SDeleteProjectPayload(cmd.projectName()));
    }

    public void onDismemberAll(Command.DismemberAllLimbs cmd) {
        BLib.MOD.networking().sendToServer(new C2SDismemberAllLimbsPayload(cmd.entityId()));
    }

    public void onDismemberLimb(Command.DismemberLimb cmd) {
        BLib.MOD.networking().sendToServer(new C2SDismemberLimbPayload(cmd.entityId(), cmd.limbId()));
    }

    public void onRemoveEntity(Command.RemoveEntity cmd) {
        BLib.MOD.networking().sendToServer(new C2SRemoveEntityPayload(cmd.entityId()));
    }

    public void onDeleteBlockVolume(Command.DeleteBlockVolume cmd) {
        BLib.MOD.networking().sendToServer(new C2SDeleteSelectionPayload(cmd.cornerA(), cmd.cornerB(), cmd.dimensionId()));
    }

    public void onDeletePlacedPiece(Command.DeletePlacedPiece cmd) {
        BLib.MOD.networking().sendToServer(new C2SDeletePlacedPiecePayload(cmd.pieceId()));
    }
}
