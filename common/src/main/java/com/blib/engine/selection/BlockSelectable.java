package com.blib.engine.selection;

import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Nullable;

/**
 * {@link Selectable} wrapping any single block in the world for the engine inspector. Holds only the {@link BlockPos} —
 * the live {@link BlockState} (and optional {@link net.minecraft.world.level.block.entity.BlockEntity}) is re-read on
 * demand so the inspector always renders authoritative current state, including changes applied by other players or by
 * our own edit-packet roundtrips. Same pattern as {@link JigsawBlockSelectable}, but generalized to any block — jigsaws
 * keep their dedicated form because their inspector exposes pool/target/joint semantics that don't apply to other
 * blocks.
 * <p>
 * Validity collapses to "the chunk is loaded and the block isn't air"; if the user breaks the block or the chunk
 * unloads, {@link SelectionManager} prunes the selection automatically on the next read.
 */
@ApiStatus.Internal
public final class BlockSelectable implements Selectable {

    private static final AABB EMPTY_BOUNDS = new AABB(0, 0, 0, 0, 0, 0);

    private final BlockPos pos;

    public BlockSelectable(BlockPos pos) {
        this.pos = pos.immutable();
    }

    public BlockPos pos() {
        return pos;
    }

    /**
     * Re-read the live {@link BlockState} at this position. Returns {@code null} if the chunk has unloaded since
     * selection — callers (the inspector view) treat null as "show an unloaded note and bail".
     */
    public @Nullable BlockState state() {
        var mc = Minecraft.getInstance();
        if (mc.level == null) {
            return null;
        }
        return mc.level.getBlockState(pos);
    }

    @Override
    public SelectableType type() {
        return SelectableType.BLOCK;
    }

    @Override
    public Component displayName() {
        var state = state();
        if (state == null) {
            return Component.literal("Block @ " + pos.toShortString());
        }
        var id = BuiltInRegistries.BLOCK.getKey(state.getBlock());
        return Component.literal(id + " @ " + pos.toShortString());
    }

    @Override
    public AABB worldBounds() {
        var mc = Minecraft.getInstance();
        if (mc.level == null) {
            return EMPTY_BOUNDS;
        }
        return new AABB(pos);
    }

    @Override
    public boolean isValid() {
        var mc = Minecraft.getInstance();
        if (mc.level == null) {
            return false;
        }
        // Air is the canonical "block was broken" sentinel — drop the selection so the inspector doesn't render an
        // empty property list for a non-existent block.
        return !mc.level.getBlockState(pos).isAir();
    }
}
