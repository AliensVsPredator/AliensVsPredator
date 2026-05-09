package com.blib.engine.jigsaw.placement;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;

/**
 * Snap-to-jigsaw resolver: finds the jigsaw block under the cursor, picks a compatible jigsaw in the active template,
 * and computes the placement that makes the two faces mate. Implements spec §2.2 modulo phase-3 simplifications:
 * <ul>
 * <li>Pool-membership check is skipped — the user explicitly picked this template, so we honor that even when its pool
 * name differs from what the anchor would normally accept.</li>
 * <li>Candidate cycling (scroll-wheel through alternative jigsaws in the piece) is deferred to a follow-up; the
 * resolver picks the candidate with the lowest local Y, then lowest X, then lowest Z — a deterministic order that's
 * stable across frames so the preview doesn't jitter as the cursor moves.</li>
 * </ul>
 * Mirror is forced to {@link Mirror#NONE} for snap placements; cycling rotation alone is enough to enumerate the
 * alignment options. Mirror-based alignment (for asymmetric pieces) lands later.
 */
@ApiStatus.Internal
public final class JigsawSnapResolver implements PlacementResolver {

    public static final JigsawSnapResolver INSTANCE = new JigsawSnapResolver();

    private JigsawSnapResolver() {}

    @Override
    public @Nullable Placement resolve(PlacementContext ctx) {
        var anchor = JigsawWorldRaycast.raycastJigsaw(ctx.session());
        if (anchor == null) {
            return null;
        }

        var candidates = JigsawTemplateScanner.jigsawBlocks(ctx.template());
        if (candidates.isEmpty()) {
            return null;
        }

        // Find every (candidate, rotation) pair that aligns with the anchor. Phase 3 picks one deterministically;
        // a later phase will let the user scroll-wheel through this list with live preview updates.
        var compatible = new ArrayList<CompatibleAlignment>();
        for (var candidate : candidates) {
            var rotation = JigsawAlignmentMath.findAlignment(anchor, candidate);
            if (rotation == null) {
                continue;
            }
            compatible.add(new CompatibleAlignment(candidate, rotation));
        }
        if (compatible.isEmpty()) {
            return null;
        }

        // Stable order (lowest Y → lowest X → lowest Z) so the choice doesn't churn between frames as the user
        // moves the cursor over the same anchor. Without sorting we'd inherit whatever order the palette stored
        // them in, which is also deterministic but less predictable for users debugging "why this candidate?"
        compatible.sort((a, b) -> {
            var pa = a.candidate.localPos();
            var pb = b.candidate.localPos();
            if (pa.getY() != pb.getY())
                return Integer.compare(pa.getY(), pb.getY());
            if (pa.getX() != pb.getX())
                return Integer.compare(pa.getX(), pb.getX());
            return Integer.compare(pa.getZ(), pb.getZ());
        });
        var picked = compatible.get(0);

        return computePlacement(picked, anchor);
    }

    /**
     * Given a compatible (candidate, rotation) pair and the anchor, compute the {@code placeInWorld} offset such that
     * the candidate's jigsaw lands at {@code anchor.worldPos + anchor.front} (one block on the anchor's facing side,
     * face-to-face with the anchor).
     */
    private static Placement computePlacement(CompatibleAlignment alignment, JigsawBlockTarget anchor) {
        // Where the candidate's jigsaw block ends up in the world after rotation, before applying the placement
        // offset. transform() with pivot=ZERO is the same math placeInWorld uses, so this matches what the server
        // will produce when it runs the placement packet.
        var rotatedLocal = StructureTemplate.transform(
            alignment.candidate.localPos(),
            Mirror.NONE,
            alignment.rotation,
            BlockPos.ZERO
        );

        // Target world position for the candidate's jigsaw block: one block past the anchor in its facing
        // direction (face-to-face geometry per vanilla JigsawPlacement).
        var targetWorldPos = anchor.worldPos().relative(anchor.front());

        // placeInWorld puts the template's local (0,0,0) at the offset BlockPos. Solving for offset given that the
        // jigsaw block (at rotated-local pos) needs to land at targetWorldPos: offset = targetWorldPos - rotatedLocal.
        var placementAnchor = new BlockPos(
            targetWorldPos.getX() - rotatedLocal.getX(),
            targetWorldPos.getY() - rotatedLocal.getY(),
            targetWorldPos.getZ() - rotatedLocal.getZ()
        );

        return new Placement(placementAnchor, alignment.rotation, Mirror.NONE);
    }

    private record CompatibleAlignment(
        TemplateJigsawInfo candidate,
        net.minecraft.world.level.block.Rotation rotation
    ) {}
}
