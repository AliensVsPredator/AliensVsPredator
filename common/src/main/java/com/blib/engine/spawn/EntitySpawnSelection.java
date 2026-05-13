package com.blib.engine.spawn;

import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Nullable;

import com.blib.engine.runtime.EventBus;
import com.blib.engine.runtime.tool.ActiveTool;
import com.blib.engine.runtime.tool.ToolChangedEvent;
import com.blib.engine.runtime.tool.ToolStateMachine;

/**
 * Mutable singleton holding the user's currently-armed entity type for the engine's spawn-on-click flow. Mirrors
 * {@link com.blib.engine.jigsaw.JigsawPieceSelection}: when non-null, a viewport LMB triggers a spawn instead of a
 * selection / placement, and the cursor swaps to a crosshair while the cursor is over the viewport.
 * <p>
 * Mutually exclusive with the other armed tools: arming an entity activates {@link ActiveTool#ENTITY_SPAWN} via
 * {@link ToolStateMachine}, which broadcasts a {@code ToolChangedEvent} that disarms the others (jigsaw piece, block
 * volume, claim paint). Replaces the prior cross-singleton {@code clear()} cascade.
 * <p>
 * Cleared automatically on {@link com.blib.engine.session.EngineMode#exit()} so a stale selection doesn't bleed into
 * the next session.
 */
@ApiStatus.Internal
public final class EntitySpawnSelection {

    private static @Nullable ResourceLocation selectedTypeId;

    private EntitySpawnSelection() {}

    public static @Nullable ResourceLocation selectedTypeId() {
        return selectedTypeId;
    }

    public static boolean hasSelection() {
        return selectedTypeId != null;
    }

    /**
     * Arm the given entity type for spawning. Activates {@link ActiveTool#ENTITY_SPAWN}; the resulting tool-changed
     * broadcast disarms other tools so the LMB-place dispatch is unambiguous (the viewport's mouseClicked checks one or
     * the other, never both).
     */
    public static void select(ResourceLocation typeId) {
        selectedTypeId = typeId;
        ToolStateMachine.get().activate(ActiveTool.ENTITY_SPAWN);
    }

    public static void clear() {
        selectedTypeId = null;
    }

    /**
     * Install the subscriber that disarms this tool when any other tool becomes active. Called once per session from
     * {@link com.blib.engine.session.EngineMode#enter}.
     */
    public static void installToolListener() {
        EventBus.get().subscribe(ToolChangedEvent.class, e -> {
            if (e.current() != ActiveTool.ENTITY_SPAWN) {
                clear();
            }
        });
    }
}
