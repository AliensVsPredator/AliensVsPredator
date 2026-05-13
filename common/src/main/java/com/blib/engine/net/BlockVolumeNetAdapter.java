package com.blib.engine.net;

import net.minecraft.client.Minecraft;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Nullable;

import com.blib.engine.domain.selection.volume.event.BlockVolumeCopyRequested;
import com.blib.engine.domain.selection.volume.event.BlockVolumeDeleteRequested;
import com.blib.engine.domain.selection.volume.event.BlockVolumePasteRequested;
import com.blib.engine.runtime.EventBus;
import com.blib.mod.BLib;
import com.blib.mod.common.network.packet.C2SCopySelectionPayload;
import com.blib.mod.common.network.packet.C2SDeleteSelectionPayload;
import com.blib.mod.common.network.packet.C2SPasteFromClipboardPayload;

/**
 * Network adapter for the block-volume domain events. Subscribes to the volume domain's request events and translates
 * each into the matching {@code C2S*Payload} packet. This is the only place that imports both
 * {@code com.blib.engine.domain.selection.volume.event.*} and {@code com.blib.mod.common.network.packet.*} — the domain
 * layer publishes events; the network adapter does the mapping.
 * <p>
 * Installed once per session from {@link com.blib.engine.session.EngineMode#enter}; subscriptions are dropped when the
 * {@code EventBus} is cleared on session exit.
 */
@ApiStatus.Internal
public final class BlockVolumeNetAdapter {

    private BlockVolumeNetAdapter() {}

    public static void install() {
        var bus = EventBus.get();
        bus.subscribe(BlockVolumeCopyRequested.class, BlockVolumeNetAdapter::onCopy);
        bus.subscribe(BlockVolumePasteRequested.class, BlockVolumeNetAdapter::onPaste);
        bus.subscribe(BlockVolumeDeleteRequested.class, BlockVolumeNetAdapter::onDelete);
    }

    private static void onCopy(BlockVolumeCopyRequested e) {
        var dim = currentDimension();
        if (dim == null) {
            return;
        }
        BLib.MOD.networking().sendToServer(new C2SCopySelectionPayload(e.min(), e.max(), e.cut(), dim));
    }

    private static void onPaste(BlockVolumePasteRequested e) {
        var dim = currentDimension();
        if (dim == null) {
            return;
        }
        BLib.MOD.networking().sendToServer(new C2SPasteFromClipboardPayload(e.destination(), dim));
    }

    private static void onDelete(BlockVolumeDeleteRequested e) {
        var dim = currentDimension();
        if (dim == null) {
            return;
        }
        BLib.MOD.networking().sendToServer(new C2SDeleteSelectionPayload(e.min(), e.max(), dim));
    }

    private static @Nullable ResourceLocation currentDimension() {
        var mc = Minecraft.getInstance();
        var player = mc.player;
        if (player == null) {
            return null;
        }
        return player.level().dimension().location();
    }
}
