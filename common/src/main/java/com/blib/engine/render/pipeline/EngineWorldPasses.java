package com.blib.engine.render.pipeline;

import org.jetbrains.annotations.ApiStatus;

import java.util.function.Consumer;

import com.blib.engine.render.entity.EntityGhostRenderer;
import com.blib.engine.render.entity.EntityScaleGizmoRenderer;
import com.blib.engine.render.entity.EntityTranslateGizmoRenderer;
import com.blib.engine.render.hud.EngineSelectionRenderer;
import com.blib.engine.render.jigsaw.JigsawAnchorRenderer;
import com.blib.engine.render.jigsaw.JigsawPlacementWorldRenderer;
import com.blib.engine.render.selection.EngineHoverRenderer;
import com.blib.engine.render.territory.ChunkClaimOverlayRenderer;
import com.blib.engine.render.volume.BlockSelectionScaleGizmoRenderer;
import com.blib.engine.render.volume.BlockSelectionTranslateGizmoRenderer;
import com.blib.engine.render.volume.BlockSelectionWireframeRenderer;
import com.blib.engine.render.volume.MoveBlocksGhostRenderer;
import com.blib.engine.render.volume.MoveBlocksGizmoRenderer;
import com.blib.engine.tool.gizmo.GizmoHoverPass;

/**
 * Single registry of the engine's world-space passes. The mixin entry calls {@link #pipeline()} once and routes the
 * frame through it; reordering or adding a pass is a single edit here instead of search-and-replace across the mixin.
 * <p>
 * Pass order matters and is documented inline. Each pass is a static {@link WorldPass} wrapping the existing renderer
 * by reference — phase-1 of the render-pipeline refactor doesn't change behaviour, it only relocates the ordering
 * decision so it lives in one place that's testable and adjustable.
 */
@ApiStatus.Internal
public final class EngineWorldPasses {

    private static final WorldPassPipeline PIPELINE = buildPipeline();

    private EngineWorldPasses() {}

    public static WorldPassPipeline pipeline() {
        return PIPELINE;
    }

    private static WorldPassPipeline buildPipeline() {
        var p = new WorldPassPipeline();

        // Engine selection visual sits outside the debug-render master gate: engine mode itself is dev-only gated and
        // the visual should always show when a selection exists, regardless of the user's debug toggle.
        p.add(pass("engine_selection", f -> EngineSelectionRenderer.render(f.poseStack(), f.bufferSource(), f.camX(), f.camY(), f.camZ())));

        // Anchor highlight first, then the structure ghost on top — depth-test for the anchor is disabled, so the
        // ghost's depth-tested geometry naturally occludes the parts of the highlight that are behind it.
        p.add(pass("jigsaw_anchor", f -> JigsawAnchorRenderer.render(f.poseStack(), f.camX(), f.camY(), f.camZ())));
        p.add(
            pass(
                "jigsaw_placement",
                f -> JigsawPlacementWorldRenderer.render(f.poseStack(), f.bufferSource(), f.camX(), f.camY(), f.camZ())
            )
        );

        p.add(pass("block_selection_wireframe", f -> BlockSelectionWireframeRenderer.render(f.poseStack(), f.camX(), f.camY(), f.camZ())));
        p.add(pass("chunk_claim_overlay", f -> ChunkClaimOverlayRenderer.render(f.poseStack(), f.camX(), f.camY(), f.camZ())));

        // Hover outline draws last so it lays on top of selection / claim overlays — the "what would I select if I
        // clicked" cue should be visible even when a selection is already drawn nearby.
        p.add(pass("engine_hover", f -> EngineHoverRenderer.render(f.poseStack(), f.camX(), f.camY(), f.camZ())));

        // Hover-state for every gizmo is recomputed here, in a single pass, before any gizmo draws. Subsequent gizmo
        // passes are read-only — they consume the hover field but never write it. This decouples picking from rendering
        // so picking can't silently break when a renderer is skipped (panel offscreen, shader missing, etc.).
        p.add(pass("gizmo_hover_update", f -> GizmoHoverPass.tick()));

        p.add(pass("block_volume_scale_gizmo", f -> BlockSelectionScaleGizmoRenderer.render(f.poseStack(), f.camX(), f.camY(), f.camZ())));
        p.add(
            pass(
                "block_volume_translate_gizmo",
                f -> BlockSelectionTranslateGizmoRenderer.render(f.poseStack(), f.camX(), f.camY(), f.camZ())
            )
        );
        p.add(pass("move_blocks_ghost", f -> MoveBlocksGhostRenderer.render(f.poseStack(), f.camX(), f.camY(), f.camZ())));
        p.add(pass("move_blocks_gizmo", f -> MoveBlocksGizmoRenderer.render(f.poseStack(), f.camX(), f.camY(), f.camZ())));
        p.add(pass("entity_ghost", f -> EntityGhostRenderer.render(f.poseStack(), f.camX(), f.camY(), f.camZ())));
        p.add(pass("entity_translate_gizmo", f -> EntityTranslateGizmoRenderer.render(f.poseStack(), f.camX(), f.camY(), f.camZ())));
        p.add(pass("entity_scale_gizmo", f -> EntityScaleGizmoRenderer.render(f.poseStack(), f.camX(), f.camY(), f.camZ())));

        return p;
    }

    private static WorldPass pass(String id, Consumer<WorldRenderFrame> body) {
        return new WorldPass() {

            @Override
            public String id() {
                return id;
            }

            @Override
            public void render(WorldRenderFrame frame) {
                body.accept(frame);
            }
        };
    }
}
