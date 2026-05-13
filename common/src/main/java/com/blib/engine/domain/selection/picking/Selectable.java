package com.blib.engine.domain.selection.picking;

import net.minecraft.network.chat.Component;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Nullable;

/**
 * Anything the engine workspace can select for inspection. Implementations carry whatever state the inspector needs to
 * display (entity reference, block pos, limb id, etc.) and expose a small surface for the framework: a type
 * discriminator, a display name for the inspector header, and a world AABB for the selection-highlight renderer.
 * <p>
 * Implementations are expected to be lightweight wrappers that hold weak references to live world objects so a stale
 * {@code Selectable} doesn't pin chunks / entities in memory after they unload. Stale lookups should return sentinel
 * data ({@link Component#empty} display name, zero-volume AABB) rather than throw.
 */
@ApiStatus.Internal
public interface Selectable {

    SelectableType type();

    Component displayName();

    /**
     * World-space bounds for the selection highlight. Implementations may return a zero-volume AABB if the target has
     * unloaded between selection and render — the highlight just won't draw.
     */
    AABB worldBounds();

    /**
     * Where gizmos / pivot indicators anchor. Defaults to the AABB center, which is right for entities and blocks but
     * might be overridden for objects with a meaningful authored origin (e.g. a jigsaw piece's structure-block
     * position).
     */
    default @Nullable Vec3 pivot() {
        var bounds = worldBounds();
        return new Vec3((bounds.minX + bounds.maxX) / 2.0, (bounds.minY + bounds.maxY) / 2.0, (bounds.minZ + bounds.maxZ) / 2.0);
    }

    /**
     * True when the wrapped target is still valid (entity alive, block loaded, etc.). The selection manager prunes
     * stale entries on read.
     */
    boolean isValid();
}
