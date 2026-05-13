package com.blib.engine.domain.selection.volume;

import net.minecraft.core.BlockPos;
import org.jetbrains.annotations.ApiStatus;

import com.blib.engine.domain.selection.volume.event.BlockVolumeCopyRequested;
import com.blib.engine.domain.selection.volume.event.BlockVolumeDeleteRequested;
import com.blib.engine.domain.selection.volume.event.BlockVolumePasteRequested;
import com.blib.engine.runtime.EventBus;
import com.blib.internal.common.clipboard.BlockClipboardEngine;

/**
 * Shared op-fire helpers for the clipboard / delete operations on the current {@link BlockSelection}. Used by both the
 * Selection panel's buttons and the workspace screen's keyboard shortcuts so the call site duplication stays at three
 * lines per op (the can-do guard + the AABB read) rather than spreading across two files.
 * <p>
 * All methods are no-ops when their preconditions aren't met (no AABB / volume too big / empty clipboard) — callers can
 * fire blindly without re-checking guards. UI affordance (button enabled/disabled) reads {@link #canCopy} /
 * {@link #canPaste} / {@link #canDelete} for the same gating logic.
 * <p>
 * Networking is delegated: each op publishes a domain event on the {@link EventBus}, and the network adapter
 * ({@code BlockVolumeNetAdapter}) translates the event into a packet. The domain layer never imports packet types,
 * which keeps it possible to drive these ops from tests or alternate transports without touching the domain.
 */
@ApiStatus.Internal
public final class BlockSelectionOps {

    private BlockSelectionOps() {}

    public static boolean canCopy() {
        return inVolumeCap();
    }

    public static boolean canPaste() {
        return BlockSelectionClipboard.hasContents() && !BlockSelection.aabb().isEmpty();
    }

    public static boolean canDelete() {
        return inVolumeCap();
    }

    public static void copy(boolean cut) {
        if (!canCopy()) {
            return;
        }
        var aabb = BlockSelection.aabb().orElseThrow();
        var min = new BlockPos((int) Math.floor(aabb.minX), (int) Math.floor(aabb.minY), (int) Math.floor(aabb.minZ));
        var max = new BlockPos((int) Math.floor(aabb.maxX) - 1, (int) Math.floor(aabb.maxY) - 1, (int) Math.floor(aabb.maxZ) - 1);
        EventBus.get().publish(new BlockVolumeCopyRequested(min, max, cut));
        if (cut) {
            // Cut = copy + delete; clearing the AABB matches what delete() does and avoids leaving a wireframe over
            // empty air. Plain copy intentionally keeps the AABB so the user can see what they captured.
            BlockSelection.clear();
        }
    }

    /**
     * Paste at the current AABB's min corner, optimistically resizing the AABB to wrap the pasted volume. The server
     * doesn't reply on success (the world state is its own confirmation), so the AABB shift happens client-side at send
     * time — reasonable since the destination + size are deterministic and the server's only failure modes (empty
     * clipboard, dimension miss) are blocked by the {@link #canPaste} guard.
     */
    public static void paste() {
        if (!canPaste()) {
            return;
        }
        var aabb = BlockSelection.aabb().orElseThrow();
        var dest = new BlockPos((int) Math.floor(aabb.minX), (int) Math.floor(aabb.minY), (int) Math.floor(aabb.minZ));
        EventBus.get().publish(new BlockVolumePasteRequested(dest));

        // Optimistic AABB shift to wrap the pasted volume.
        var sx = BlockSelectionClipboard.sizeX();
        var sy = BlockSelectionClipboard.sizeY();
        var sz = BlockSelectionClipboard.sizeZ();
        BlockSelection.setBounds(dest, new BlockPos(dest.getX() + sx - 1, dest.getY() + sy - 1, dest.getZ() + sz - 1));
    }

    public static void delete() {
        if (!canDelete()) {
            return;
        }
        var aabb = BlockSelection.aabb().orElseThrow();
        var min = new BlockPos((int) Math.floor(aabb.minX), (int) Math.floor(aabb.minY), (int) Math.floor(aabb.minZ));
        var max = new BlockPos((int) Math.floor(aabb.maxX) - 1, (int) Math.floor(aabb.maxY) - 1, (int) Math.floor(aabb.maxZ) - 1);
        EventBus.get().publish(new BlockVolumeDeleteRequested(min, max));
        // The volume's blocks are about to be gone; leaving the AABB wireframe floating over empty space confuses
        // users into thinking the operation didn't apply. Optimistic clear matches what paste() does for AABB shift.
        BlockSelection.clear();
    }

    private static boolean inVolumeCap() {
        var aabb = BlockSelection.aabb();
        if (aabb.isEmpty()) {
            return false;
        }
        var box = aabb.get();
        var volume = (long) (box.maxX - box.minX) * (long) (box.maxY - box.minY) * (long) (box.maxZ - box.minZ);
        return volume > 0 && volume <= BlockClipboardEngine.MAX_TOTAL_VOLUME;
    }
}
