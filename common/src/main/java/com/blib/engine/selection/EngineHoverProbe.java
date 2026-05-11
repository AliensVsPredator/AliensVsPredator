package com.blib.engine.selection;

import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.ProjectileUtil;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Nullable;

import com.blib.engine.jigsaw.JigsawPlacementCursor;
import com.blib.engine.jigsaw.placement.JigsawWorldRaycast;
import com.blib.engine.session.EngineCameraFrame;
import com.blib.engine.session.EngineNavigation;
import com.blib.engine.session.EngineSession;

/**
 * Per-frame "what would be selected if I clicked right now" probe. Mirrors
 * {@link EngineNavigation#performSelectionAt}'s priority (entity vs jigsaw block by closest hit, then generic block as
 * fallback) without mutating {@link SelectionManager}. The {@link com.blib.engine.selection.EngineHoverRenderer} reads
 * {@link #current()} each frame to draw a subtle outline so users have a visual cue for click targeting.
 * <p>
 * Callers (the viewport panel) call {@link #update} each frame when the cursor is over the viewport and {@link #clear}
 * otherwise; the renderer no-ops on null.
 */
@ApiStatus.Internal
public final class EngineHoverProbe {

    /** Max raycast distance for hover detection — matches the click-selection cap. */
    private static final double HOVER_RAYCAST_DISTANCE = 96.0;

    public sealed interface Target {

        record Entity(LivingEntity entity) implements Target {}

        record Block(BlockPos pos) implements Target {}
    }

    private static @Nullable Target current;

    private EngineHoverProbe() {}

    public static @Nullable Target current() {
        return current;
    }

    public static void clear() {
        current = null;
    }

    /**
     * Re-run raycasts and update the cached hover target. Called every frame from {@link ViewportPanel#render} when the
     * cursor is inside the viewport rect. {@code (relX, relY)} are in {@code [0, 1]} viewport-relative coords.
     */
    public static void update(EngineSession session, double relX, double relY) {
        var mc = Minecraft.getInstance();
        if (mc.level == null || mc.player == null) {
            current = null;
            return;
        }

        // Prefer the camera frame's captured matrices over the analytical reconstruction — same accuracy concern as
        // the click-selection raycast. Falls back when no frame is captured (very first render).
        var capturedOrigin = EngineCameraFrame.cameraPosition();
        var origin = capturedOrigin != null ? capturedOrigin : session.cameraPosition();
        Vec3 rayDir = null;
        if (EngineCameraFrame.hasFrame()) {
            rayDir = EngineCameraFrame.cursorRayDirection(relX, relY);
        }
        if (rayDir == null) {
            rayDir = EngineNavigation.cursorRayDirection(session, relX, relY);
        }
        var end = origin.add(
            rayDir.x * HOVER_RAYCAST_DISTANCE,
            rayDir.y * HOVER_RAYCAST_DISTANCE,
            rayDir.z * HOVER_RAYCAST_DISTANCE
        );

        var aabb = new AABB(origin, end).inflate(1.0);
        var entityHit = ProjectileUtil.getEntityHitResult(
            mc.player,
            origin,
            end,
            aabb,
            entity -> !entity.isSpectator() && entity != mc.player && entity instanceof LivingEntity,
            HOVER_RAYCAST_DISTANCE * HOVER_RAYCAST_DISTANCE
        );
        var jigsawTarget = JigsawWorldRaycast.raycastJigsaw(session);
        var blockHit = JigsawPlacementCursor.clipFromCursor(session);

        var haveEntity = entityHit != null && entityHit.getEntity() instanceof LivingEntity;
        var haveJigsaw = jigsawTarget != null;
        var haveBlock = blockHit != null;

        // Priority: entity vs jigsaw by closest hit (matches performSelectionAt). Generic block is a fallback used
        // only when both entity and jigsaw miss — same shape as the ViewportPanel click path's drag-to-pick branch.
        if (haveEntity && haveJigsaw) {
            var entityDistSq = entityHit.getLocation().distanceToSqr(origin);
            var jigsawDistSq = Vec3.atCenterOf(jigsawTarget.worldPos()).distanceToSqr(origin);
            current = entityDistSq <= jigsawDistSq
                ? new Target.Entity((LivingEntity) entityHit.getEntity())
                : new Target.Block(jigsawTarget.worldPos());
        } else if (haveEntity) {
            current = new Target.Entity((LivingEntity) entityHit.getEntity());
        } else if (haveJigsaw) {
            current = new Target.Block(jigsawTarget.worldPos());
        } else if (haveBlock) {
            current = new Target.Block(blockHit.getBlockPos());
        } else {
            current = null;
        }
    }
}
