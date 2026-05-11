package com.blib.engine.selection;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.ProjectileUtil;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;

import com.blib.engine.jigsaw.ClientPlacedPieceRegistry;
import com.blib.engine.jigsaw.JigsawPlacementCursor;
import com.blib.engine.session.EngineCameraFrame;
import com.blib.engine.session.EngineInteractionRange;
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

    public sealed interface Target {

        record Entity(LivingEntity entity) implements Target {}

        record Block(BlockPos pos) implements Target {}

        record Piece(UUID id) implements Target {}
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
     * Re-run raycasts and update the cached hover target. Called every frame from
     * {@link com.blib.engine.ui.ViewportPanel#render} when the cursor is inside the viewport rect. {@code (relX, relY)}
     * are in {@code [0, 1]} viewport-relative coords.
     */
    public static void update(EngineSession session, double relX, double relY) {
        var mc = Minecraft.getInstance();
        if (mc.level == null || mc.player == null) {
            current = null;
            return;
        }
        // Suppress hover entirely while the user has a piece armed for placement. The placement ghost preview is the
        // affordance in that mode; layering a block/piece hover outline on top adds visual noise the user can't act
        // on (LMB always places, RMB is a no-op in place mode).
        if (com.blib.engine.jigsaw.JigsawPieceSelection.selectedId() != null) {
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
            rayDir.x * EngineInteractionRange.MAX,
            rayDir.y * EngineInteractionRange.MAX,
            rayDir.z * EngineInteractionRange.MAX
        );

        var aabb = new AABB(origin, end).inflate(1.0);
        var entityHit = ProjectileUtil.getEntityHitResult(
            mc.player,
            origin,
            end,
            aabb,
            entity -> !entity.isSpectator() && entity != mc.player && entity instanceof LivingEntity,
            EngineInteractionRange.MAX_SQR
        );
        var blockHit = JigsawPlacementCursor.clipFromCursor(session);
        // Ctrl held = "give me the block under the piece" fallthrough — drill through the piece tier and pick whatever
        // block is at the cursor. Alt was the original choice but conflicts with the Linux window-manager's
        // alt+drag-to-move gesture, so Ctrl is the portable substitute.
        var ctrlHeld = Screen.hasControlDown();
        var pieceHit = ctrlHeld ? null : ClientPlacedPieceRegistry.raycast(origin, rayDir, EngineInteractionRange.MAX);

        var haveEntity = entityHit != null && entityHit.getEntity() instanceof LivingEntity;
        var havePiece = pieceHit != null;
        var haveBlock = blockHit != null;

        // Priority: entity vs block by closest hit (matches performSelectionAt). Placed-piece beats any block (jigsaw
        // or generic) when the cursor is inside its AABB — jigsaw blocks aren't given priority over pieces; they're
        // just blocks with a richer inspector. Alt drops the piece tier so the user can drill through to the block.
        if (haveEntity && haveBlock) {
            var entityDistSq = entityHit.getLocation().distanceToSqr(origin);
            var blockDistSq = Vec3.atCenterOf(blockHit.getBlockPos()).distanceToSqr(origin);
            if (entityDistSq <= blockDistSq) {
                current = new Target.Entity((LivingEntity) entityHit.getEntity());
            } else if (havePiece) {
                current = new Target.Piece(pieceHit.pieceId());
            } else {
                current = new Target.Block(blockHit.getBlockPos());
            }
        } else if (haveEntity) {
            current = new Target.Entity((LivingEntity) entityHit.getEntity());
        } else if (havePiece) {
            current = new Target.Piece(pieceHit.pieceId());
        } else if (haveBlock) {
            current = new Target.Block(blockHit.getBlockPos());
        } else {
            current = null;
        }
    }
}
