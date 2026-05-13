package com.blib.mod.common.gameplay.jigsaw;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import org.jetbrains.annotations.ApiStatus;

import java.util.List;
import java.util.UUID;

import com.blib.internal.common.storage.BLibDataStoreManager;
import com.blib.mod.BLib;
import com.blib.mod.common.network.packet.S2CAddPlacedPiecePayload;
import com.blib.mod.common.network.packet.S2CRemovePlacedPiecePayload;
import com.blib.mod.common.network.packet.S2CSyncPlacedPiecesPayload;
import com.blib.mod.common.registry.init.BLibJigsawDataStoreTypes;

/**
 * Server-side fan-out for {@link PlacedPiece} lifecycle changes. The {@link PlacedPieceStore} is authoritative; this
 * class is the bridge that broadcasts deltas to clients so they can keep a mirror for hover / select / context-menu.
 * <p>
 * Engine mode is dev-only and almost always singleplayer in practice, so v1 broadcasts to every player on the level
 * matching the piece's dimension. Filtering by "in engine mode" can be layered on later without changing callers.
 */
@ApiStatus.Internal
public final class PlacedPieceSync {

    private PlacedPieceSync() {}

    /** Called after a piece is added to the store. Broadcasts an add packet to dimension-matched clients. */
    public static void onPieceAdded(ServerLevel level, PlacedPiece piece) {
        var payload = new S2CAddPlacedPiecePayload(piece);
        for (var player : recipients(level)) {
            BLib.MOD.networking().sendToClient(player, payload);
        }
    }

    /** Called after a piece is removed from the store. Broadcasts a removal so clients drop it from their mirror. */
    public static void onPieceRemoved(ServerLevel level, UUID pieceId) {
        var payload = new S2CRemovePlacedPiecePayload(pieceId, level.dimension().location());
        for (var player : recipients(level)) {
            BLib.MOD.networking().sendToClient(player, payload);
        }
    }

    /** Called when a client requests the full set for its current dimension (engine-mode entry). */
    public static void sendFullSyncTo(ServerPlayer player) {
        var level = player.serverLevel();
        var store = BLibDataStoreManager.INSTANCE
            .getLevel(level, BLibJigsawDataStoreTypes.PLACED_PIECES);
        var payload = new S2CSyncPlacedPiecesPayload(level.dimension().location(), List.copyOf(store.all()));
        BLib.MOD.networking().sendToClient(player, payload);
    }

    private static Iterable<ServerPlayer> recipients(ServerLevel level) {
        return level.getServer().getPlayerList().getPlayers();
    }
}
