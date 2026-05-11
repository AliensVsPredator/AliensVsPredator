package com.blib.engine.blockselection;

import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import org.jetbrains.annotations.ApiStatus;

import com.blib.internal.common.clipboard.BlockClipboardEngine;
import com.blib.mod.BLib;
import com.blib.mod.common.network.packet.C2SCopySelectionPayload;
import com.blib.mod.common.network.packet.C2SDeleteSelectionPayload;
import com.blib.mod.common.network.packet.C2SPasteFromClipboardPayload;

/**
 * Shared op-fire helpers for the clipboard / delete operations on the current {@link BlockSelection}. Used by both the
 * Selection panel's buttons and the workspace screen's keyboard shortcuts so the call site duplication stays at three
 * lines per op (the can-do guard + the AABB read) rather than spreading across two files.
 * <p>
 * All methods are no-ops when their preconditions aren't met (no AABB / volume too big / empty clipboard) — callers can
 * fire blindly without re-checking guards. UI affordance (button enabled/disabled) reads {@link #canCopy} /
 * {@link #canPaste} / {@link #canDelete} for the same gating logic.
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
        var minX = (int) Math.floor(aabb.minX);
        var minY = (int) Math.floor(aabb.minY);
        var minZ = (int) Math.floor(aabb.minZ);
        var maxX = (int) Math.floor(aabb.maxX) - 1;
        var maxY = (int) Math.floor(aabb.maxY) - 1;
        var maxZ = (int) Math.floor(aabb.maxZ) - 1;
        var mc = Minecraft.getInstance();
        if (mc.player == null) {
            return;
        }
        var dim = mc.player.level().dimension().location();
        BLib.MOD.networking()
            .sendToServer(new C2SCopySelectionPayload(new BlockPos(minX, minY, minZ), new BlockPos(maxX, maxY, maxZ), cut, dim));
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
        var mc = Minecraft.getInstance();
        if (mc.player == null) {
            return;
        }
        var dim = mc.player.level().dimension().location();
        BLib.MOD.networking().sendToServer(new C2SPasteFromClipboardPayload(dest, dim));

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
        var minX = (int) Math.floor(aabb.minX);
        var minY = (int) Math.floor(aabb.minY);
        var minZ = (int) Math.floor(aabb.minZ);
        var maxX = (int) Math.floor(aabb.maxX) - 1;
        var maxY = (int) Math.floor(aabb.maxY) - 1;
        var maxZ = (int) Math.floor(aabb.maxZ) - 1;
        var mc = Minecraft.getInstance();
        if (mc.player == null) {
            return;
        }
        var dim = mc.player.level().dimension().location();
        BLib.MOD.networking()
            .sendToServer(new C2SDeleteSelectionPayload(new BlockPos(minX, minY, minZ), new BlockPos(maxX, maxY, maxZ), dim));
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
