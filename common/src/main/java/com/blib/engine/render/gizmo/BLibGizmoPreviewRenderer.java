package com.blib.engine.render.gizmo;

import com.mojang.blaze3d.platform.Lighting;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.math.Axis;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.ApiStatus;

import com.blib.engine.gizmo.BLibGizmoMode;
import com.blib.engine.gizmo.BLibGizmoState;
import com.blib.engine.gizmo.BLibItemTransformOverrides;

/**
 * Re-renders the actively-tuned tunable held items in the top corners of the screen with oversized gizmos, so the user
 * can interact with translate/rotate/scale handles without the actual first-person item being tucked into the
 * bottom-corner slot (where translate handles in particular project off-screen and become unclickable).
 * <p>
 * Layout:
 * <ul>
 * <li>Main hand item rendered with {@code FIRST_PERSON_RIGHT_HAND} → top-right corner. (Holds for the default
 * right-handed setting; for left-handed players the actual hand display context is swapped by vanilla, so the corner
 * that matches the hand's screen-edge stays correct.)</li>
 * <li>Off hand item rendered with {@code FIRST_PERSON_LEFT_HAND} → top-left corner.</li>
 * </ul>
 * <p>
 * Translation behavior: while the preview is rendering, {@link BLibGizmoState#isPreviewRender()} is true, which causes
 * {@link com.blib.api.client.render.v1.item.BLibGeoBoneItemRenderer} to strip the user's translation. The preview
 * therefore stays anchored to its corner regardless of how much the user has translated the actual world item — the
 * whole point: a translation drag would otherwise pull the preview off-screen, defeating the feature. The actual
 * translation field IS still updated on drag (writes go through {@link com.blib.engine.gizmo.BLibGizmoInput#updateDrag}
 * → override map → world render picks it up next frame), so the user sees their world-space item move while the preview
 * stays put.
 * <p>
 * Rotation/scale changes show in the preview faithfully — the preview re-renders each frame against the current
 * override values.
 */
@ApiStatus.Internal
public final class BLibGizmoPreviewRenderer {

    /**
     * Pose-stack scale applied to the preview item. Tuned so the head/sword-sized geo items fill ~80% of the preview
     * tile. Kick up if items render too small at your DPI; the gizmo handles scale with this value via
     * {@link BLibGizmoState#setHandleScaleMultiplier}.
     */
    private static final float PREVIEW_RENDER_SCALE = 80f;

    /**
     * Pose-stack-local size of each gizmo handle when rendering inside the preview. Read by {@link BLibGizmoRenderer}
     * as the absolute scale (not a multiplier on a depth-based base — the depth-based formula doesn't apply in HUD
     * coords). With {@link #PREVIEW_RENDER_SCALE} = 80 and a value of 0.7, each translate arrow / scale handle is ~56
     * GUI pixels long, comfortably visible against the ~150 px-wide preview tile without being so big the handles
     * overlap each other.
     */
    private static final float PREVIEW_GIZMO_HANDLE_BOOST = 0.7f;

    /** Pixel offset of the preview anchor from each screen edge — same on all 4 sides for visual balance. */
    private static final int PREVIEW_MARGIN_PX = 110;

    private BLibGizmoPreviewRenderer() {
        throw new UnsupportedOperationException();
    }

    /**
     * Render hook — called every HUD frame from the loader-specific HUD callback. No-ops fast when the gizmo is off,
     * the player isn't first-person, or no held item is tunable, so it's safe to register unconditionally.
     */
    public static void render(GuiGraphics guiGraphics, float partialTick) {
        if (BLibGizmoState.mode() == BLibGizmoMode.OFF) {
            return;
        }

        var mc = Minecraft.getInstance();
        var player = mc.player;

        if (player == null) {
            return;
        }

        // Preview only matters in first-person — third-person doesn't render FIRST_PERSON_*_HAND items at
        // all, so the preview would be the only thing on screen reflecting changes and the user couldn't
        // see how their drags are affecting the actual hand item until they switch back.
        if (!mc.options.getCameraType().isFirstPerson()) {
            return;
        }

        int screenWidth = guiGraphics.guiWidth();

        // Right-hand preview (top-right). Drawn first so a left-hand draw landing in unexpected territory
        // doesn't visually overlay it — neither corner overlaps in normal layout, but defensive ordering
        // costs nothing.
        renderHandPreview(
            guiGraphics,
            mc,
            player,
            partialTick,
            player.getMainHandItem(),
            ItemDisplayContext.FIRST_PERSON_RIGHT_HAND,
            screenWidth - PREVIEW_MARGIN_PX,
            PREVIEW_MARGIN_PX
        );

        renderHandPreview(
            guiGraphics,
            mc,
            player,
            partialTick,
            player.getOffhandItem(),
            ItemDisplayContext.FIRST_PERSON_LEFT_HAND,
            PREVIEW_MARGIN_PX,
            PREVIEW_MARGIN_PX
        );
    }

    private static void renderHandPreview(
        GuiGraphics guiGraphics,
        Minecraft mc,
        LocalPlayer player,
        float partialTick,
        ItemStack stack,
        ItemDisplayContext context,
        int anchorX,
        int anchorY
    ) {
        if (stack.isEmpty()) {
            return;
        }

        var itemId = BuiltInRegistries.ITEM.getKey(stack.getItem());

        // Only preview items that have opted in to live tuning. Everything else has nothing for the
        // gizmo to act on, so there's no point reserving screen space for them.
        if (!BLibItemTransformOverrides.tunableItemIds().contains(itemId)) {
            return;
        }

        var poseStack = guiGraphics.pose();
        poseStack.pushPose();

        // Translate to the corner anchor. The Z translation pushes the item in front of the HUD chrome
        // so it draws on top of the hotbar/health bars; matches the depth GuiGraphics uses for floating
        // tooltip-style items.
        poseStack.translate(anchorX, anchorY, 150f);
        // GUI y points down; flipping the y scale puts the item right-side up. Z is left positive so
        // depth tests behave the same way as in world render.
        poseStack.scale(PREVIEW_RENDER_SCALE, -PREVIEW_RENDER_SCALE, PREVIEW_RENDER_SCALE);

        // Default-orient the model so the user sees a useful 3/4 view rather than whatever the item's
        // FPV transform happens to be. The user's rotation override stacks on top of this — note this
        // is the *preview* render's view rotation, NOT a tweak to the user's transform. Drag-rotate
        // feedback still reads off the user's transform.rotation, applied after this view rotation by
        // the geo bone renderer's transform.apply().
        poseStack.mulPose(Axis.XP.rotationDegrees(15f));
        poseStack.mulPose(Axis.YP.rotationDegrees(-25f));

        // 3D lighting — FPV transforms assume a 3D-lit item; flat-item lighting (used for hotbar icons)
        // would make rotated items look uniformly bright with no shading.
        Lighting.setupFor3DItems();
        RenderSystem.enableDepthTest();

        BLibGizmoState.setPreviewRender(true);
        BLibGizmoState.setHandleScaleMultiplier(PREVIEW_GIZMO_HANDLE_BOOST);

        var bufferSource = mc.renderBuffers().bufferSource();

        try {
            mc.getItemRenderer()
                .renderStatic(
                    stack,
                    context,
                    LightTexture.FULL_BRIGHT,
                    OverlayTexture.NO_OVERLAY,
                    poseStack,
                    bufferSource,
                    mc.level,
                    player.getId()
                );

            bufferSource.endBatch();
        } finally {
            BLibGizmoState.setPreviewRender(false);
            BLibGizmoState.setHandleScaleMultiplier(1f);
            Lighting.setupFor3DItems();
            poseStack.popPose();
        }
    }
}
