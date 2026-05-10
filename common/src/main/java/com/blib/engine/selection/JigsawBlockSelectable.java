package com.blib.engine.selection;

import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.phys.AABB;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Nullable;

import com.blib.engine.jigsaw.placement.JigsawBlockTarget;

/**
 * {@link Selectable} wrapping a placed jigsaw block in the world. Holds only the {@link BlockPos} — the live NBT
 * (name, target, pool, joint) is re-read on demand via {@link #snapshot()} so the inspector always renders the
 * authoritative current state, including changes applied by other players or by our own edit packet roundtrips.
 * <p>
 * Validity collapses to "the block at this position is still a jigsaw"; if the user breaks the block (or the chunk
 * unloads), {@link SelectionManager} prunes us automatically on the next read.
 */
@ApiStatus.Internal
public final class JigsawBlockSelectable implements Selectable {

    private static final AABB EMPTY_BOUNDS = new AABB(0, 0, 0, 0, 0, 0);

    private final BlockPos pos;

    public JigsawBlockSelectable(BlockPos pos) {
        this.pos = pos.immutable();
    }

    public BlockPos pos() {
        return pos;
    }

    /**
     * Re-read the live {@link net.minecraft.world.level.block.entity.JigsawBlockEntity} at this position. Returns
     * {@code null} if the block has been broken / changed type or the chunk has unloaded since selection — callers
     * (the inspector view) treat null as "show an unloaded note and bail".
     */
    public @Nullable JigsawBlockTarget snapshot() {
        var mc = Minecraft.getInstance();
        if (mc.level == null) {
            return null;
        }
        return JigsawBlockTarget.snapshot(mc.level, pos);
    }

    @Override
    public SelectableType type() {
        return SelectableType.BLOCK;
    }

    @Override
    public Component displayName() {
        var snap = snapshot();
        // Show the jigsaw's name only when the user has set something other than vanilla's default "minecraft:empty"
        // — otherwise the header reads "minecraft:empty @ x, y, z" for every fresh jigsaw, which is noise.
        if (snap != null && !"empty".equals(snap.name().getPath())) {
            return Component.literal(snap.name() + " @ " + pos.toShortString());
        }
        return Component.literal("Jigsaw @ " + pos.toShortString());
    }

    @Override
    public AABB worldBounds() {
        var mc = Minecraft.getInstance();
        if (mc.level == null || !mc.level.getBlockState(pos).is(Blocks.JIGSAW)) {
            return EMPTY_BOUNDS;
        }
        return new AABB(pos);
    }

    @Override
    public boolean isValid() {
        var mc = Minecraft.getInstance();
        return mc.level != null && mc.level.getBlockState(pos).is(Blocks.JIGSAW);
    }
}
