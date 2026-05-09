package com.blib.engine.jigsaw.placement;

import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.entity.JigsawBlockEntity;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Nullable;

/**
 * Pure-function alignment math: given an anchor jigsaw in the world and a candidate jigsaw in template-local space,
 * find the {@link Rotation} that makes the candidate face mate with the anchor face.
 * <p>
 * The math mirrors vanilla's {@link net.minecraft.world.level.block.JigsawBlock#canAttach}: two jigsaws connect when
 * their fronts face each other ({@code anchor.front == candidate.front.opposite}) and — for {@code ALIGNED} joints —
 * their tops also match. {@code ROLLABLE} joints don't constrain top, so any of the four rotations satisfying the
 * front-condition is a valid alignment (we return the smallest one by ordinal; later phases can let the user cycle
 * through all four).
 * <p>
 * Returns {@code null} when no rotation works — typically because the candidate's front is on a different axis than the
 * anchor's (horizontal vs vertical), since rotation around the vertical axis can't bridge those.
 */
@ApiStatus.Internal
public final class JigsawAlignmentMath {

    private JigsawAlignmentMath() {}

    public static @Nullable Rotation findAlignment(JigsawBlockTarget anchor, TemplateJigsawInfo candidate) {
        // Name match is the hard prerequisite: the anchor's `target` (what it wants to connect to) must equal the
        // candidate's `name` (its identity). No rotation can fix a name mismatch.
        if (!anchor.target().equals(candidate.name())) {
            return null;
        }

        var desiredFront = anchor.front().getOpposite();
        for (var rotation : Rotation.values()) {
            var rotatedFront = rotation.rotate(candidate.frontLocal());
            if (rotatedFront != desiredFront) {
                continue;
            }

            // ROLLABLE joints don't constrain top — we accept the first front-matching rotation. ALIGNED joints
            // additionally require the rotated top to match the anchor's top. Vanilla uses the *anchor's* joint
            // type as the gate (see JigsawBlock.canAttach), not the candidate's, so we follow that convention.
            if (anchor.joint() == JigsawBlockEntity.JointType.ROLLABLE) {
                return rotation;
            }
            var rotatedTop = rotation.rotate(candidate.topLocal());
            if (rotatedTop == anchor.top()) {
                return rotation;
            }
        }
        return null;
    }
}
