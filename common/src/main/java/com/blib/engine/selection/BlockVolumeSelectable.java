package com.blib.engine.selection;

import net.minecraft.network.chat.Component;
import net.minecraft.world.phys.AABB;
import org.jetbrains.annotations.ApiStatus;

import com.blib.engine.blockselection.BlockSelection;

/**
 * {@link Selectable} wrapper for the engine's block-volume selection (the AABB defined by {@link BlockSelection}'s two
 * corners). Holds no state of its own — it's a thin façade over the {@code BlockSelection} singleton so the inspector
 * and selection-highlight renderer see one cohesive "selected volume" target while the corner state lives where it
 * always has.
 * <p>
 * Drag-to-pick installs an instance of this in {@link SelectionManager}; subsequent drags update the underlying
 * {@code BlockSelection} and the same instance keeps reporting fresh values via the static delegation.
 */
@ApiStatus.Internal
public final class BlockVolumeSelectable implements Selectable {

    private static final AABB EMPTY_BOUNDS = new AABB(0, 0, 0, 0, 0, 0);

    public BlockVolumeSelectable() {}

    @Override
    public SelectableType type() {
        return SelectableType.BLOCK_VOLUME;
    }

    @Override
    public Component displayName() {
        var aabb = BlockSelection.aabb();
        if (aabb.isEmpty()) {
            return Component.literal("Selection (empty)");
        }
        var box = aabb.get();
        var sx = (int) (box.maxX - box.minX);
        var sy = (int) (box.maxY - box.minY);
        var sz = (int) (box.maxZ - box.minZ);
        var minX = (int) Math.floor(box.minX);
        var minY = (int) Math.floor(box.minY);
        var minZ = (int) Math.floor(box.minZ);
        return Component.literal("Selection " + sx + "×" + sy + "×" + sz + " @ " + minX + ", " + minY + ", " + minZ);
    }

    @Override
    public AABB worldBounds() {
        return BlockSelection.aabb().orElse(EMPTY_BOUNDS);
    }

    @Override
    public boolean isValid() {
        return !BlockSelection.aabb().isEmpty();
    }
}
