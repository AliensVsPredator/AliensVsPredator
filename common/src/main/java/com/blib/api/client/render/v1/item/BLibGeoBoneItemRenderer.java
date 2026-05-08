package com.blib.api.client.render.v1.item;

import com.blib.api.client.model.v1.AzBakedModel;
import com.blib.api.client.render.v1.AzRendererPipelineContext;
import com.blib.api.client.render.v1.BLibTransform;
import com.blib.api.client.render.v1.item.model.BLibGeoBoneItemModelRenderer;
import com.blib.api.client.render.v1.item.pipeline.AzItemRendererPipeline;
import com.blib.api.client.render.v1.item.pipeline.AzItemRendererPipelineContext;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.world.item.ItemStack;
import org.joml.Vector3f;

import java.util.UUID;

/**
 * Renders a single named bone subtree of an existing geo model as an item — designed for cases where you
 * want to reuse a mob's geo head (or arm, or some other subtree) as a held item without forking the JSON.
 * <p>
 * Configuration comes from a {@link BLibGeoBoneItemRendererConfig}. The renderer:
 * <ul>
 *   <li>Loads the config's geo model and texture through the standard AzureLib item pipeline.</li>
 *   <li>Walks <em>only</em> the configured target bone's subtree, via a {@link BLibGeoBoneItemModelRenderer}
 *       that overrides the default top-level-bones walk. The ancestor chain (root → … → target) is never
 *       traversed, so its bind-pose translations don't compose into the matrix stack — your tuned transforms
 *       become invariant to anything modelers do above the target bone (waist height, chest width, neck
 *       length, etc.). Only the target bone's own bind pose still flows through.</li>
 *   <li>Freezes animation in every {@link net.minecraft.world.item.ItemDisplayContext} so the bone stays at
 *       bind pose — there's no entity tick to drive an animator anyway.</li>
 *   <li>Anchors the bone's authored {@code pivot} to the pose-stack origin. The bone walk's bind transform
 *       {@code T(+pivot)·R·S·T(-pivot)} is rotation-around-pivot in BLib coords, which leaves the pivot
 *       point invariant; this renderer pre-translates by {@code -pivot/16} after the user transform so the
 *       pivot lands at pose-stack origin (= player's hand for {@code FIRST/THIRD_PERSON_*_HAND}, gui slot
 *       center for {@code GUI}, item-frame center for {@code FIXED}, etc.) when the user transform is
 *       identity. Cubes and descendant bones render at their authored offsets from that pivot, so the
 *       subtree looks identical to how it appears in the source model.</li>
 *   <li>Applies the user's {@link BLibTransform} <em>before</em> the pivot anchor, so a non-zero
 *       {@link BLibTransform#translation()} shifts where the bone pivot lands and a non-zero
 *       {@link BLibTransform#rotation()}/{@link BLibTransform#scale()} sweeps the entire bone subtree
 *       around the pivot. {@link BLibTransform#pivot()} is a manual nudge — an offset added to the bone's
 *       authored pivot when the user wants the rotation center somewhere other than the bone pivot itself.</li>
 *   <li>Applies the config's {@link BLibItemTransforms} (or
 *       {@link BLibGeoBoneItemRendererConfig#blockingTransforms} when
 *       {@link BLibGeoBoneItemRendererConfig#isBlocking} returns true) before bone rendering, letting you
 *       position the bone differently per render context.</li>
 * </ul>
 * <p>
 * Register one instance per shield item via the loader's normal item-renderer hook
 * ({@code BuiltinItemRendererRegistry.register} on Fabric, {@code IClientItemExtensions.getCustomRenderer}
 * on NeoForge), as you would any other {@link AzItemRenderer} subclass.
 */
public class BLibGeoBoneItemRenderer extends AzItemRenderer {

    private final BLibGeoBoneItemRendererConfig config;

    public BLibGeoBoneItemRenderer(BLibGeoBoneItemRendererConfig config) {
        super(buildConfig(config));
        this.config = config;
    }

    public BLibGeoBoneItemRendererConfig geoBoneItemConfig() {
        return config;
    }

    private static AzItemRendererConfig buildConfig(BLibGeoBoneItemRendererConfig config) {
        return AzItemRendererConfig.builder(config.geoModel(), config.texture())
            .setModelRenderer((pipeline, layerRenderer) -> new BLibGeoBoneItemModelRenderer(
                (AzItemRendererPipeline) pipeline,
                layerRenderer,
                config.boneName()
            ))
            .disableAnimationInAllContexts()
            .setPrerenderEntry(context -> {
                applyTransforms(config, context);
                return context;
            })
            .build();
    }

    private static void applyTransforms(BLibGeoBoneItemRendererConfig config, AzRendererPipelineContext<UUID, ItemStack> context) {
        var itemContext = (AzItemRendererPipelineContext) context;
        var displayContext = itemContext.getTransformType();
        var stack = context.animatable();

        BLibTransform transform = null;
        var transformMode = BLibItemTransformMode.IDLE;

        // Short-circuit the blocking predicate when the debug `force-blocking` toggle is on so the user
        // can tune blocking-pose transforms (with gizmos / set-nudge commands) without physically holding
        // RMB. Otherwise the predicate runs as normal — typically `isBlocking` checks the player's use state.
        var blockingActive = config.blockingTransforms() != null
            && (BLibItemTransformOverrides.isForceBlockingEnabled() || config.isBlocking().test(stack));

        if (blockingActive) {
            transform = config.blockingTransforms().getOrNull(displayContext);

            if (transform != null) {
                transformMode = BLibItemTransformMode.BLOCKING;
            }
        }

        if (transform == null) {
            // Cascade: blocking is treated as a per-context override layered on top of idle. Any context the
            // user didn't explicitly set on the blocking transforms falls through to idle, instead of snapping
            // to IDENTITY (which would yank the item to model origin during a block, the previous behavior).
            transform = config.idleTransforms().get(displayContext);
        }

        var pivot = bonePivotInPoseFrame(context.bakedModel(), config.boneName());
        var poseStack = itemContext.poseStack();

        if (BLibItemTransformOverrides.isPivotVisualizationEnabled()) {
            drawPivotDebug(itemContext, transform);
        }

        // Gizmo render + per-frame snapshot for picking. Drawn at the same anchor as the pivot debug —
        // post-translation, post-tuner-pivot, but pre-user-rotation — so the axis arrows/rings stay
        // aligned to the model's pre-rotation frame and don't sweep around as the user adds rotation.
        var tunableId = tunableItemId(config);

        if (tunableId != null && BLibGizmoState.mode() != BLibGizmoMode.OFF) {
            BLibGizmoRenderer.renderAndCapture(itemContext, transform, tunableId, transformMode);
        }

        // Compose `pose = T(translation) · T(user.pivot) · R · S · T(-user.pivot) · T(-bone.pivot/16)`.
        // The trailing `T(-bone.pivot/16)` is the anchor — combined with the bone walk's bind transform
        // `T(+pivot) · R_bone · S_bone · T(-pivot)` the two `T(±pivot/16)` translations cancel, leaving the
        // bone's pivot point landed at the user-transformed pose-stack origin. With identity user transform
        // that's the hand / gui slot / item-frame center; with non-zero translation the pivot moves with it
        // regardless of user rotation/scale (rotation/scale operates around the pivot, not the model origin,
        // because we anchor *after* the rotation/scale on the matrix stack).
        transform.apply(poseStack);
        poseStack.translate(-pivot.x, -pivot.y, -pivot.z);
    }

    /**
     * Returns the {@link BLibTunableItemTransforms#itemId} when the renderer's idle transforms wrap a
     * tuner-aware base, else null. The gizmo only targets items whose author opted in to live tuning, so
     * the input handler can resolve a click back to the right (item, mode, context) override slot.
     */
    private static @org.jetbrains.annotations.Nullable net.minecraft.resources.ResourceLocation tunableItemId(BLibGeoBoneItemRendererConfig config) {
        if (config.idleTransforms() instanceof BLibTunableItemTransforms tunable) {
            return tunable.itemId();
        }

        return null;
    }

    /**
     * Returns the bone's authored pivot point in pose-stack coordinates (BLib's per-16 block convention,
     * with the X-axis flip already applied by the model factory). The bone walk's bind transform
     * {@code T(+pivot) · R · S · T(-pivot)} is rotation-around-pivot in BLib coords, so the pivot point is
     * invariant under the bone walk and ends up at exactly this position in pose-stack frame.
     */
    private static Vector3f bonePivotInPoseFrame(AzBakedModel model, String boneName) {
        var bone = model.getBoneOrNull(boneName);

        if (bone == null) {
            return new Vector3f();
        }

        return new Vector3f(bone.getPivotX() / 16f, bone.getPivotY() / 16f, bone.getPivotZ() / 16f);
    }

    /**
     * Draws a wireframe AABB and a colored XYZ axis tripod at the user's rotation pivot, in the pose-stack
     * frame BEFORE the user's rotation/scale is applied — that's where rotations actually anchor. With
     * default {@link BLibTransform#pivot()} of zero, the debug lands at {@code translation} in the original
     * pose frame, which is also where the bone's pivot ends up after the bone walk; rotating around X tips
     * the head along the red line, Y around the green line, Z around the blue line. Only fires when the
     * tuner's {@code debug pivot} toggle is on.
     */
    private static void drawPivotDebug(AzItemRendererPipelineContext itemContext, BLibTransform transform) {
        var poseStack = itemContext.poseStack();
        poseStack.pushPose();

        poseStack.translate(transform.translation().x, transform.translation().y, transform.translation().z);
        poseStack.translate(transform.pivot().x, transform.pivot().y, transform.pivot().z);

        var buffer = itemContext.multiBufferSource().getBuffer(RenderType.lines());

        // Small wireframe box at pivot — yellow, ~3-pixel sized cube so it stands out without obscuring the
        // model.
        var box = 0.05;
        LevelRenderer.renderLineBox(poseStack, buffer, -box, -box, -box, box, box, box, 1.0F, 1.0F, 0.0F, 1.0F);

        // Axis tripod: full-length lines for the +axes, half-length for - axes (so the user can tell + apart
        // from - direction at a glance).
        var len = 0.25f;
        drawAxisLine(poseStack, buffer, len, 0, 0, 1f, 0f, 0f, 1f);
        drawAxisLine(poseStack, buffer, -len * 0.5f, 0, 0, 1f, 0f, 0f, 0.4f);
        drawAxisLine(poseStack, buffer, 0, len, 0, 0f, 1f, 0f, 1f);
        drawAxisLine(poseStack, buffer, 0, -len * 0.5f, 0, 0f, 1f, 0f, 0.4f);
        drawAxisLine(poseStack, buffer, 0, 0, len, 0f, 0f, 1f, 1f);
        drawAxisLine(poseStack, buffer, 0, 0, -len * 0.5f, 0f, 0f, 1f, 0.4f);

        poseStack.popPose();
    }

    private static void drawAxisLine(PoseStack poseStack, VertexConsumer buffer, float x, float y, float z, float r, float g, float b, float a) {
        var pose = poseStack.last();
        var nx = x == 0 ? 0 : Math.signum(x);
        var ny = y == 0 ? 0 : Math.signum(y);
        var nz = z == 0 ? 0 : Math.signum(z);

        buffer.addVertex(pose.pose(), 0, 0, 0).setColor(r, g, b, a).setNormal(pose, nx, ny, nz);
        buffer.addVertex(pose.pose(), x, y, z).setColor(r, g, b, a).setNormal(pose, nx, ny, nz);
    }
}
