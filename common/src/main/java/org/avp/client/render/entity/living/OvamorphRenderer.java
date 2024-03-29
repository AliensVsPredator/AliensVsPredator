package org.avp.client.render.entity.living;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import mod.azure.azurelib.common.api.client.renderer.GeoEntityRenderer;
import mod.azure.azurelib.common.internal.common.cache.object.BakedGeoModel;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;

import org.avp.client.model.entity.living.OvamorphModel;
import org.avp.common.AVPResources;
import org.avp.common.TimeUtilities;
import org.avp.common.entity.living.Ovamorph;

/**
 * @author Boston Vanseghi
 */
public class OvamorphRenderer extends GeoEntityRenderer<Ovamorph> {

    private static final ResourceLocation TEXTURE_HALLOWEEN = AVPResources.entityTextureLocation("ovamorph_halloween");

    public OvamorphRenderer(EntityRendererProvider.Context context) {
        super(context, new OvamorphModel());
    }

    @Override
    public void preRender(
        PoseStack poseStack,
        Ovamorph animatable,
        BakedGeoModel model,
        MultiBufferSource bufferSource,
        VertexConsumer buffer,
        boolean isReRender,
        float partialTick,
        int packedLight,
        int packedOverlay,
        float red,
        float green,
        float blue,
        float alpha
    ) {
        super.preRender(
            poseStack,
            animatable,
            model,
            bufferSource,
            buffer,
            isReRender,
            partialTick,
            packedLight,
            packedOverlay,
            red,
            green,
            blue,
            alpha
        );
        poseStack.scale(1.75F, 1.75F, 1.75F);
    }

    @Override
    public @NotNull ResourceLocation getTextureLocation(@NotNull Ovamorph entity) {
        return TimeUtilities.isHalloween() ? TEXTURE_HALLOWEEN : super.getTextureLocation(entity);
    }
}
