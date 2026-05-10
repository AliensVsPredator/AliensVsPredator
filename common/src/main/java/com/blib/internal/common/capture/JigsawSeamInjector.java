package com.blib.internal.common.capture;

import net.minecraft.core.BlockPos;
import net.minecraft.core.FrontAndTop;
import net.minecraft.core.Vec3i;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.IntTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.JigsawBlock;
import org.jetbrains.annotations.ApiStatus;

/**
 * Mutates a {@link net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate}'s saved NBT to add
 * jigsaw connector blocks at the boundary faces shared with neighboring sub-pieces. Each seam pair gets a unique
 * deterministic name so vanilla's jigsaw resolver pairs them correctly during assembly — no randomness, no swapped
 * pieces.
 * <p>
 * NBT mutation rather than {@link net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate} field
 * manipulation: vanilla's palette structure is private + immutable (records of immutable lists) and a write accessor
 * would need a fresh mixin every time the structure changes. The on-disk format ({@code palette} + {@code
 * blocks}) is much more stable across MC versions, so we manipulate it directly here.
 * <p>
 * Seam-block placement: one jigsaw at the geometric center of each face shared with a neighbor. For an X-cut at
 * boundary between sub-piece (i, j, k) and (i+1, j, k):
 * <ul>
 * <li>In (i, j, k): jigsaw at template-local {@code (sizeX-1, ⌊sizeY/2⌋, ⌊sizeZ/2⌋)}, front=EAST.</li>
 * <li>In (i+1, j, k): jigsaw at template-local {@code (0, ⌊sizeY/2⌋, ⌊sizeZ/2⌋)}, front=WEST.</li>
 * </ul>
 * Names are paired so each side's {@code target} matches the other's {@code name}.
 */
@ApiStatus.Internal
public final class JigsawSeamInjector {

    private JigsawSeamInjector() {}

    /**
     * Append seam jigsaw blocks to the saved {@code templateNbt}. {@code (i, j, k)} is the sub-piece's grid position;
     * {@code (xCells, yCells, zCells)} is the full grid extent. {@code cellSize} is the sub-piece's bounding-box size;
     * we use it to compute face centers in template-local coordinates.
     * <p>
     * For each axis-direction with a neighbor (e.g. {@code i+1 < xCells} → +X neighbor), one jigsaw is placed. No
     * jigsaws are added at exterior faces of the overall capture volume — those are open seams the user can connect to
     * manually if they want to extend the structure later.
     */
    public static void inject(
        CompoundTag templateNbt,
        int i,
        int j,
        int k,
        int xCells,
        int yCells,
        int zCells,
        Vec3i cellSize,
        String captureName,
        ResourceLocation poolId
    ) {
        var palette = ensurePalette(templateNbt);
        var blocks = ensureBlocks(templateNbt);

        var cx = cellSize.getX() / 2;
        var cy = cellSize.getY() / 2;
        var cz = cellSize.getZ() / 2;

        // +X seam (we are i, neighbor is i+1)
        if (i + 1 < xCells) {
            addSeam(
                palette,
                blocks,
                new BlockPos(cellSize.getX() - 1, cy, cz),
                FrontAndTop.EAST_UP,
                seamName(captureName, i, j, k, "xp"),
                seamName(captureName, i, j, k, "xn"),
                poolId
            );
        }
        // -X seam (we are i, neighbor is i-1)
        if (i > 0) {
            addSeam(
                palette,
                blocks,
                new BlockPos(0, cy, cz),
                FrontAndTop.WEST_UP,
                seamName(captureName, i - 1, j, k, "xn"),
                seamName(captureName, i - 1, j, k, "xp"),
                poolId
            );
        }
        // +Y seam
        if (j + 1 < yCells) {
            addSeam(
                palette,
                blocks,
                new BlockPos(cx, cellSize.getY() - 1, cz),
                FrontAndTop.UP_SOUTH,
                seamName(captureName, i, j, k, "yp"),
                seamName(captureName, i, j, k, "yn"),
                poolId
            );
        }
        // -Y seam
        if (j > 0) {
            addSeam(
                palette,
                blocks,
                new BlockPos(cx, 0, cz),
                FrontAndTop.DOWN_SOUTH,
                seamName(captureName, i, j - 1, k, "yn"),
                seamName(captureName, i, j - 1, k, "yp"),
                poolId
            );
        }
        // +Z seam
        if (k + 1 < zCells) {
            addSeam(
                palette,
                blocks,
                new BlockPos(cx, cy, cellSize.getZ() - 1),
                FrontAndTop.SOUTH_UP,
                seamName(captureName, i, j, k, "zp"),
                seamName(captureName, i, j, k, "zn"),
                poolId
            );
        }
        // -Z seam
        if (k > 0) {
            addSeam(
                palette,
                blocks,
                new BlockPos(cx, cy, 0),
                FrontAndTop.NORTH_UP,
                seamName(captureName, i, j, k - 1, "zn"),
                seamName(captureName, i, j, k - 1, "zp"),
                poolId
            );
        }
    }

    /**
     * The seam name canonicalizes on the *lower-coord* sub-piece so the +/- side of a cut share a stable identifier.
     * For an X-cut between (i, j, k) and (i+1, j, k), both sides are labeled {@code <captureName>_seam_<i>_<j>_<k>_xp}
     * (positive side, on (i, j, k)) and {@code _xn} (negative side, on (i+1, j, k)). The (i, j, k) coordinates always
     * refer to the lower-coord cell of the pair so + and − shares match.
     */
    private static String seamName(String captureName, int li, int lj, int lk, String suffix) {
        return captureName + "_seam_" + li + "_" + lj + "_" + lk + "_" + suffix;
    }

    private static void addSeam(
        ListTag palette,
        ListTag blocks,
        BlockPos pos,
        FrontAndTop orientation,
        String name,
        String target,
        ResourceLocation poolId
    ) {
        var paletteIdx = getOrAddJigsawPaletteEntry(palette, orientation);

        var blockTag = new CompoundTag();
        var posList = new ListTag();
        posList.add(IntTag.valueOf(pos.getX()));
        posList.add(IntTag.valueOf(pos.getY()));
        posList.add(IntTag.valueOf(pos.getZ()));
        blockTag.put("pos", posList);
        blockTag.putInt("state", paletteIdx);

        var nbt = new CompoundTag();
        nbt.putString("name", asResourcePath(name));
        nbt.putString("target", asResourcePath(target));
        nbt.putString("pool", poolId.toString());
        nbt.putString("joint", "aligned");
        nbt.putString("final_state", "minecraft:air");
        nbt.putInt("selection_priority", 0);
        nbt.putInt("placement_priority", 0);
        blockTag.put("nbt", nbt);

        blocks.add(blockTag);
    }

    /**
     * Find an existing jigsaw block-state palette entry that matches {@code orientation}, or append a fresh one.
     * Different seams need different orientations (face-direction varies), so multiple jigsaw entries can coexist.
     */
    private static int getOrAddJigsawPaletteEntry(ListTag palette, FrontAndTop orientation) {
        var state = Blocks.JIGSAW.defaultBlockState().setValue(JigsawBlock.ORIENTATION, orientation);
        var serialized = NbtUtils.writeBlockState(state);
        for (var i = 0; i < palette.size(); i++) {
            if (palette.getCompound(i).equals(serialized)) {
                return i;
            }
        }
        palette.add(serialized);
        return palette.size() - 1;
    }

    /**
     * Vanilla's jigsaw {@code name} / {@code target} fields are stored as resource-location strings. To keep the
     * captureName + suffix scheme valid we prefix with {@code "minecraft:"} so they parse — vanilla doesn't actually
     * use the namespace meaningfully here, just expects a valid ResourceLocation.
     */
    private static String asResourcePath(String label) {
        return "minecraft:" + label;
    }

    /**
     * Get the {@code blocks} ListTag, creating it if missing. The vanilla save() method always emits this, but
     * defensively handle the case where the NBT is freshly constructed.
     */
    private static ListTag ensureBlocks(CompoundTag templateNbt) {
        if (templateNbt.contains("blocks", Tag.TAG_LIST)) {
            return templateNbt.getList("blocks", Tag.TAG_COMPOUND);
        }
        var list = new ListTag();
        templateNbt.put("blocks", list);
        return list;
    }

    /**
     * Get the {@code palette} ListTag, creating it if missing. Vanilla's save method emits {@code palette} for the
     * single-variant case (which fillFromWorld always produces) and {@code palettes} for the multi-variant case (which
     * we never produce). We only read/write {@code palette}.
     */
    private static ListTag ensurePalette(CompoundTag templateNbt) {
        if (templateNbt.contains("palette", Tag.TAG_LIST)) {
            return templateNbt.getList("palette", Tag.TAG_COMPOUND);
        }
        var list = new ListTag();
        templateNbt.put("palette", list);
        return list;
    }
}
