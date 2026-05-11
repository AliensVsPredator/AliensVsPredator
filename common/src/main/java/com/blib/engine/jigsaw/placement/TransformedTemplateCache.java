package com.blib.engine.jigsaw.placement;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate;
import org.jetbrains.annotations.ApiStatus;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.WeakHashMap;

import com.blib.api.common.worldgen.v1.StructureTemplateAccessor;

/**
 * Per-(template, rotation, mirror) cache of transformed block positions and states. The placement preview and the
 * collision scanner both walk a template's first palette every frame and call {@link StructureTemplate#transform} +
 * {@code state.mirror(...).rotate(...)} on each block — work that is invariant under translation, so the result is
 * stable for the lifetime of the (template, rotation, mirror) tuple. For a 32³ piece this turns ~32k per-frame
 * transform/mirror/rotate calls into a single cache lookup.
 * <p>
 * Each template gets a small inner map of up to 12 entries (4 rotations × 3 mirrors). The outer map is a
 * {@link WeakHashMap} so templates GC'd on datapack reload don't pin the cache; explicit {@link #invalidate} drops
 * everything for callers that want a hard reset on workspace open.
 * <p>
 * Lists exclude {@link Blocks#STRUCTURE_VOID} (vanilla's {@code placeInWorld} skips those positions too) but
 * <em>include</em> air, because the collision scanner treats an air template block over a non-air world block as an
 * overwrite. The preview renderer skips air at iteration time — cheap, and most palettes don't contain air anyway since
 * unauthored cells aren't recorded.
 */
@ApiStatus.Internal
public final class TransformedTemplateCache {

    public record TransformedBlock(
        BlockPos localPos,
        BlockState state
    ) {}

    private record Key(
        Rotation rotation,
        Mirror mirror
    ) {}

    private static final Map<StructureTemplate, Map<Key, List<TransformedBlock>>> CACHE = new WeakHashMap<>();

    private TransformedTemplateCache() {}

    /**
     * Precomputed transformed blocks for the (template, rotation, mirror) tuple — local positions computed with pivot
     * {@link BlockPos#ZERO} (matching what the live placement packet uses), states pre-mirrored then pre-rotated.
     * Callers translate to the placement anchor themselves. Returns an empty list if the template has no palettes or no
     * blocks.
     */
    public static List<TransformedBlock> get(StructureTemplate template, Rotation rotation, Mirror mirror) {
        var inner = CACHE.computeIfAbsent(template, k -> new HashMap<>());
        return inner.computeIfAbsent(new Key(rotation, mirror), k -> build(template, rotation, mirror));
    }

    private static List<TransformedBlock> build(StructureTemplate template, Rotation rotation, Mirror mirror) {
        var palettes = ((StructureTemplateAccessor) template).blib$getPalettes();
        if (palettes == null || palettes.isEmpty()) {
            return List.of();
        }
        var blocks = palettes.get(0).blocks();
        if (blocks.isEmpty()) {
            return List.of();
        }
        var pivot = BlockPos.ZERO;
        var out = new ArrayList<TransformedBlock>(blocks.size());
        for (var info : blocks) {
            var state = info.state();
            if (state.is(Blocks.STRUCTURE_VOID)) {
                continue;
            }
            var transformedPos = StructureTemplate.transform(info.pos(), mirror, rotation, pivot);
            var transformedState = state.mirror(mirror).rotate(rotation);
            out.add(new TransformedBlock(transformedPos, transformedState));
        }
        return List.copyOf(out);
    }

    /** Drop all cached entries. Safe to call from workspace open/close or any other lifecycle hook. */
    public static void invalidate() {
        CACHE.clear();
    }
}
