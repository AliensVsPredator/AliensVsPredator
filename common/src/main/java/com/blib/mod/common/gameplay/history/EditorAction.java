package com.blib.mod.common.gameplay.history;

import net.minecraft.server.MinecraftServer;
import org.jetbrains.annotations.ApiStatus;

import com.blib.mod.common.network.packet.ActionDescriptor;

/**
 * Root of the engine-mode action-history hierarchy. Every user gesture that mutates engine state (block placement,
 * entity edit, tag/pool JSON write, faction edit, etc.) produces one {@code EditorAction} which {@link ActionHistory}
 * stores so the gesture can be reversed (undo) or re-applied (redo).
 * <p>
 * Implementations carry both the pre-state (used by {@link #revert}) and the post-state (used by {@link #redo}). The
 * snapshot pattern keeps actions self-contained — the action knows exactly what it changed and can put the world back
 * regardless of subsequent server state. The trade-off is memory: see {@link #estimatedBytes} and the dual-cap eviction
 * policy in {@link ActionHistory}.
 * <p>
 * Variants split by scope: {@link WorldAction} affects a specific level (filtered on undo so a cross-dimension player
 * doesn't accidentally restore unrelated state); {@code ProjectAction} (added in a later step) affects on-disk project
 * metadata.
 */
@ApiStatus.Internal
public sealed interface EditorAction permits WorldAction, ProjectAction, FactionEdit {

    /** Short human-readable label shown in the Action Stack panel. e.g. {@code "Place jigsaw piece blib:foo"}. */
    String description();

    /** Wall-clock time the action was pushed; used for {@code "12s ago"} display in the panel. */
    long timestamp();

    /** Stable string discriminator for the descriptor packet — drives panel icons and grouping. */
    String typeId();

    /** Approximate retained-bytes cost. {@link ActionHistory} sums these for soft-byte eviction. */
    long estimatedBytes();

    /** Roll the world back to the pre-action state. Called by Ctrl+Z / Edit → Undo. */
    void revert(MinecraftServer server);

    /** Re-apply the action after a {@link #revert}. Called by Ctrl+Y / Edit → Redo. */
    void redo(MinecraftServer server);

    /**
     * Flatten this action into the wire-friendly descriptor the panel renders. Default extracts {@code dim} from
     * {@link WorldAction} variants — extends to {@code projectName} when {@code ProjectAction} variants ship.
     */
    default ActionDescriptor toDescriptor() {
        var dim = (this instanceof WorldAction wa) ? wa.dimension().location() : null;
        var proj = (this instanceof ProjectAction pa) ? pa.projectName() : null;
        return new ActionDescriptor(typeId(), description(), timestamp(), dim, proj);
    }
}
