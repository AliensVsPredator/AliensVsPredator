package com.blib.engine.render.modeler;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.BufferUploader;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.Tesselator;
import com.mojang.blaze3d.vertex.VertexFormat;
import com.mojang.math.Axis;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.util.Mth;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.ApiStatus;
import org.joml.Matrix4f;
import org.joml.Vector3f;

import com.blib.api.client.render.v1.BLibTransform;
import com.blib.api.client.render.v1.item.BLibItemTransformMode;
import com.blib.engine.gizmo.BLibItemTransformOverrides;
import com.blib.engine.modeler.Selection;
import com.blib.engine.modeler.gizmo.ModelerGizmoState;
import com.blib.engine.modeler.item.ModelerItemSession;

/**
 * Renders an {@link ItemStack} for the {@link ModelerItemSession}'s editing context through vanilla's actual
 * {@code ItemRenderer.renderStatic} path. Same render code as in-game, same display transform applied, same
 * {@link BLibItemTransformOverrides} consulted — so the modeler viewport's preview is guaranteed identical to what the
 * player sees in-game.
 * <p>
 * On top of the item itself the renderer also draws a per-context backdrop so the user reads the pose relative to what
 * they'd see in the game world:
 * <ul>
 * <li>{@code GUI} — a darkened inventory-slot quad behind the item.</li>
 * <li>{@code GROUND} — a grass block beneath the item, plus vanilla's age-driven Y spin + sine Y-bob.</li>
 * <li>{@code FIXED} — an oak-planks slab behind the item (item-frame mockup).</li>
 * <li>{@code FIXED} with {@code wallFixedActive} — a cobblestone wall behind the item.</li>
 * </ul>
 * Backdrops sit in the same pose-stack frame as the item so the modeler camera orbits item + backdrop together.
 * Animation (spin + bob) is applied AFTER backdrop emission so the ground stays put while the item rotates.
 */
@ApiStatus.Internal
public final class ModelerItemPreviewRenderer {

    private ModelerItemPreviewRenderer() {}

    /** Half-edge of the GUI slot backdrop quad in pose-stack units (1 unit ≈ 1 block). */
    private static final float SLOT_HALF = 0.55f;

    /** Y position of the grass block top surface beneath the item. */
    private static final float GROUND_TOP_Y = -0.5f;

    /**
     * Render the item for {@code session.editingContext}. Caller has already set up the modeler camera's projection and
     * modelview matrices; this method opens its own {@link PoseStack} for the local item transforms and reuses
     * {@link Minecraft#renderBuffers}'s buffer source.
     */
    public static void render(ModelerItemSession session) {
        var renderContext = session.editingContext;
        var mc = Minecraft.getInstance();
        var item = BuiltInRegistries.ITEM.get(session.itemId);
        if (item == null) {
            return;
        }
        var stack = new ItemStack(item);
        if (stack.isEmpty()) {
            return;
        }

        var bufferSource = mc.renderBuffers().bufferSource();
        var poseStack = new PoseStack();
        var packedLight = LightTexture.FULL_BRIGHT;
        boolean wallFixed = renderContext == ItemDisplayContext.FIXED && session.wallFixedActive;

        // Sync the shim bone with the active BLibTransform — drives the modeler gizmos against the item-transform.
        // During a gizmo drag the shim's fields are the source of truth (the user is mutating them via the gizmo),
        // so we propagate shim → override; otherwise the override is authoritative (Inspector edits / command tuner)
        // and we propagate override → shim. The gizmo target is set on the scene so ModelerGizmoRenderer picks the
        // shim instead of scene.selection without confusing the inspector / outliner.
        syncShimBone(session, wallFixed);
        var scene = com.blib.engine.modeler.ModelerScene.get();
        scene.gizmoTargetSelection = new Selection.BoneSelection(session.gizmoShimBone);

        // GUI orientation isn't handled here anymore — ModelerRenderer's GUI-preview branch sets up an
        // orthographic +Y-up projection plus a screen-pixel modelview so model +Y already appears up on screen.
        // The previous local-PoseStack scale(1, -1, 1) double-flipped against the (then perspective) camera and
        // misled tuning; trust the projection/modelview instead. Other contexts (GROUND, FIXED, FIRST/THIRD_*,
        // HEAD) keep the perspective orbital camera and don't need a static orientation tweak.

        renderBackdrop(poseStack, packedLight, renderContext, wallFixed);

        // Animation transform — only meaningful for GROUND; everywhere else this is a no-op. Applied after the
        // backdrop so the ground block doesn't spin along with the item.
        applyAnimationTransform(poseStack, renderContext);

        boolean forceBlocking = session.mode == BLibItemTransformMode.BLOCKING;
        boolean priorForceBlocking = BLibItemTransformOverrides.isForceBlockingEnabled();
        boolean priorWall = BLibItemTransformOverrides.isRenderAsWallBlock();
        BLibItemTransformOverrides.setForceBlockingEnabled(forceBlocking);
        BLibItemTransformOverrides.setRenderAsWallBlock(wallFixed);
        try {
            RenderSystem.enableBlend();
            RenderSystem.defaultBlendFunc();
            mc.getItemRenderer()
                .renderStatic(
                    stack,
                    renderContext,
                    packedLight,
                    OverlayTexture.NO_OVERLAY,
                    poseStack,
                    bufferSource,
                    mc.level,
                    0
                );
            bufferSource.endBatch();
        } finally {
            BLibItemTransformOverrides.setForceBlockingEnabled(priorForceBlocking);
            BLibItemTransformOverrides.setRenderAsWallBlock(priorWall);
        }
    }

    /**
     * GROUND-only spin + bob mirroring vanilla's {@code ItemEntityRenderer}. Drives time off {@link System#nanoTime} so
     * the animation runs without a loaded world / tick clock.
     */
    private static void applyAnimationTransform(PoseStack poseStack, ItemDisplayContext context) {
        if (context == ItemDisplayContext.GROUND) {
            // Vanilla (1.21.1): spin = age/20 rad/sec at 20 tps → 1 rad/sec; bob = sin(age/10)*0.1+0.1 → freq 2
            // rad/sec.
            var seconds = (System.nanoTime() % 1_000_000_000_000L) / 1_000_000_000f;
            var bob = Mth.sin(seconds * 2f) * 0.1f + 0.1f;
            poseStack.translate(0f, bob, 0f);
            poseStack.mulPose(Axis.YP.rotation(seconds));
        }
    }

    private static void renderBackdrop(PoseStack poseStack, int packedLight, ItemDisplayContext context, boolean wallFixed) {
        if (context == ItemDisplayContext.GUI) {
            emitGuiSlotBackdrop(poseStack);
        } else if (context == ItemDisplayContext.GROUND) {
            emitGroundBlockBackdrop(poseStack, packedLight);
        } else if (context == ItemDisplayContext.FIXED && wallFixed) {
            emitWallBackdrop(poseStack, packedLight);
        } else if (context == ItemDisplayContext.FIXED) {
            emitFrameBackdrop(poseStack, packedLight);
        }
    }

    /**
     * Inventory-slot mockup behind the item: a dark outer bevel + a slightly lighter inset, drawn as two colored quads
     * in the item's XY plane via the position-color shader (raw {@link Tesselator}). Sits at small +Z so the item
     * (which renders near z = 0 after the GUI flip) reads on top of the slot.
     */
    private static void emitGuiSlotBackdrop(PoseStack poseStack) {
        var matrix = poseStack.last().pose();
        RenderSystem.setShader(GameRenderer::getPositionColorShader);
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        var buffer = Tesselator.getInstance().begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_COLOR);
        var z = 0.5f;
        // Outer slot frame — darker.
        addQuad(buffer, matrix, -SLOT_HALF, -SLOT_HALF, z, SLOT_HALF, SLOT_HALF, z, 0x40, 0x40, 0x48, 0xE0);
        // Inset interior — lighter so the bevel reads.
        var inset = SLOT_HALF - 0.06f;
        addQuad(buffer, matrix, -inset, -inset, z - 0.001f, inset, inset, z - 0.001f, 0x6E, 0x6E, 0x76, 0xE0);
        var built = buffer.build();
        if (built != null) {
            BufferUploader.drawWithShader(built);
        }
    }

    /** Grass block beneath the item — vanilla model + texture via {@code BlockRenderDispatcher}. */
    private static void emitGroundBlockBackdrop(PoseStack poseStack, int packedLight) {
        poseStack.pushPose();
        // Center a 1×1×1 block so its top sits at GROUND_TOP_Y. renderSingleBlock expects pose-stack origin at the
        // block's min corner.
        poseStack.translate(-0.5f, GROUND_TOP_Y - 1f, -0.5f);
        renderBlock(Blocks.GRASS_BLOCK.defaultBlockState(), poseStack, packedLight);
        poseStack.popPose();
    }

    /** Thin oak-planks slab behind the item — item-frame backdrop. */
    private static void emitFrameBackdrop(PoseStack poseStack, int packedLight) {
        poseStack.pushPose();
        // Z-scale flattens the block to ~0.1 unit thick; positioned just behind where the item sits.
        poseStack.translate(-0.5f, -0.5f, 0.55f);
        poseStack.scale(1f, 1f, 0.1f);
        renderBlock(Blocks.OAK_PLANKS.defaultBlockState(), poseStack, packedLight);
        poseStack.popPose();
    }

    /** Full cobblestone block behind the item — the wall a trophy/head would be mounted on. */
    private static void emitWallBackdrop(PoseStack poseStack, int packedLight) {
        poseStack.pushPose();
        poseStack.translate(-0.5f, -0.5f, 0.5f);
        renderBlock(Blocks.COBBLESTONE.defaultBlockState(), poseStack, packedLight);
        poseStack.popPose();
    }

    private static void renderBlock(BlockState state, PoseStack poseStack, int packedLight) {
        var mc = Minecraft.getInstance();
        var bufferSource = mc.renderBuffers().bufferSource();
        mc.getBlockRenderer().renderSingleBlock(state, poseStack, bufferSource, packedLight, OverlayTexture.NO_OVERLAY);
    }

    /**
     * Bidirectional sync between the BLibItemTransformOverrides entry for the session's active (mode, context) and the
     * shim bone the modeler gizmo targets. During a drag the shim has just been mutated by the gizmo, so its fields are
     * authoritative and we write them back to the overrides (so BLib's renderer picks them up on the next frame).
     * Otherwise the overrides are authoritative (Inspector edits land there) and we copy them into the shim so the
     * gizmo handles render at the correct pose.
     */
    private static void syncShimBone(ModelerItemSession session, boolean wallFixed) {
        var shim = session.gizmoShimBone;
        // editingContext drives both what's rendered and where gizmo drags land — one picker, one slot, no chance of
        // dragging a transform that lives in a context the user can't see.
        var activeContext = session.editingContext;
        var drag = ModelerGizmoState.drag();
        boolean dragTargetsShim = drag != null && drag.isBoneDrag() && drag.startSnapshot() != null && drag.startSnapshot().bone() == shim;
        if (dragTargetsShim) {
            // Direct field copy — BLibTransform and ModelerBone share Z-Y-X intrinsic rotation order, so the shim's
            // vec3s map 1:1 to the transform's. One convention across entity-model editing and item-config editing.
            var updated = new BLibTransform(
                new Vector3f((float) shim.position.x, (float) shim.position.y, (float) shim.position.z),
                new Vector3f((float) shim.rotation.x, (float) shim.rotation.y, (float) shim.rotation.z),
                new Vector3f((float) shim.scale.x, (float) shim.scale.y, (float) shim.scale.z),
                new Vector3f((float) shim.pivot.x, (float) shim.pivot.y, (float) shim.pivot.z)
            );
            if (wallFixed) {
                BLibItemTransformOverrides.setWallFixed(session.itemId, session.mode, updated);
            } else {
                BLibItemTransformOverrides.set(session.itemId, session.mode, activeContext, updated);
            }
            return;
        }
        var current = wallFixed
            ? BLibItemTransformOverrides.getEffectiveWallFixed(session.itemId, session.mode)
            : BLibItemTransformOverrides.getEffective(session.itemId, session.mode, activeContext);
        shim.position = new Vec3(current.translation().x, current.translation().y, current.translation().z);
        shim.rotation = new Vec3(current.rotation().x, current.rotation().y, current.rotation().z);
        shim.scale = new Vec3(current.scale().x, current.scale().y, current.scale().z);
        shim.pivot = new Vec3(current.pivot().x, current.pivot().y, current.pivot().z);
    }

    private static void addQuad(
        com.mojang.blaze3d.vertex.BufferBuilder buffer,
        Matrix4f matrix,
        float x0,
        float y0,
        float z0,
        float x1,
        float y1,
        float z1,
        int r,
        int g,
        int b,
        int a
    ) {
        buffer.addVertex(matrix, x0, y0, z0).setColor(r, g, b, a);
        buffer.addVertex(matrix, x0, y1, z0).setColor(r, g, b, a);
        buffer.addVertex(matrix, x1, y1, z1).setColor(r, g, b, a);
        buffer.addVertex(matrix, x1, y0, z1).setColor(r, g, b, a);
    }
}
