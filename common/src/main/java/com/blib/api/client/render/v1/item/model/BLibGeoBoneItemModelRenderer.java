package com.blib.api.client.render.v1.item.model;

import com.blib.api.client.render.v1.AzLayerRenderer;
import com.blib.api.client.render.v1.AzRendererPipelineContext;
import com.blib.api.client.render.v1.item.pipeline.AzItemRendererPipeline;
import net.minecraft.world.item.ItemStack;
import org.joml.Matrix4f;

import java.util.UUID;

/**
 * Item-side model renderer that walks only one named bone subtree of the underlying geo, instead of the
 * standard top-level-bones-plus-recursion walk. Used by
 * {@link com.blib.api.client.render.v1.item.BLibGeoBoneItemRenderer} to render a mob's head (or arm, or
 * other subtree) as a held item.
 * <p>
 * Replacing the walk at the {@code render} level — rather than skipping ancestors via visibility flags during
 * a full walk — means the ancestor bones' bind-pose translations never compose into the matrix stack. The
 * net effect is that consumer-tuned item transforms become invariant to anything modelers do above the
 * target bone in the hierarchy (waist height, chest width, neck length, etc.). Only the target bone's own
 * bind pose still flows through, which is rare to be edited when adjusting body proportions.
 * <p>
 * If the target bone name doesn't resolve in the baked model, nothing is rendered (silent no-op rather than
 * an error, since the model may legitimately not contain the bone in some rare baked-model edge cases).
 */
public class BLibGeoBoneItemModelRenderer extends AzItemModelRenderer {

    private final String targetBoneName;

    public BLibGeoBoneItemModelRenderer(
        AzItemRendererPipeline itemRendererPipeline,
        AzLayerRenderer<UUID, ItemStack> layerRenderer,
        String targetBoneName
    ) {
        super(itemRendererPipeline, layerRenderer);
        this.targetBoneName = targetBoneName;
    }

    @Override
    public void render(AzRendererPipelineContext<UUID, ItemStack> context, boolean isReRender) {
        if (!isReRender) {
            var animator = itemRendererPipeline.getRenderer().getAnimator();

            if (animator != null) {
                handleAnimation(animator, context.animatable(), context.partialTick());
            }
        }

        var poseStack = context.poseStack();
        itemRendererPipeline.setModelRenderTranslations(new Matrix4f(poseStack.last().pose()));

        // Replace AzModelRenderer.render's top-level-bone walk with a single-bone walk on the configured
        // target. The ancestor chain (root → ... → target) is never traversed, so its bind-pose translations
        // can't compose into the matrix stack — that's the whole point of this subclass.
        var bakedModel = context.bakedModel();
        var target = bakedModel.getBoneOrNull(targetBoneName);

        itemRendererPipeline.updateAnimatedTextureFrame(context.animatable());

        if (target != null) {
            renderRecursively(context, target, isReRender);
        }

        itemRendererPipeline.config().renderEntry(context);
    }
}
