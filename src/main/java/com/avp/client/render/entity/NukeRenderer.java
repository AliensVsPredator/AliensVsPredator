package com.avp.client.render.entity;

import com.avp.common.block.entity.NukeBE;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.block.BlockRenderDispatcher;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.TntMinecartRenderer;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import org.jetbrains.annotations.NotNull;

public class NukeRenderer<T extends Entity> extends EntityRenderer<T> {
    private final BlockRenderDispatcher blockRenderer;

    public NukeRenderer(EntityRendererProvider.Context context) {
        super(context);
        this.blockRenderer = context.getBlockRenderDispatcher();
    }

    @Override
    public void render(T nukeEntity, float partialTicks, float animationProgress, PoseStack poseStack, MultiBufferSource bufferSource, int lightLevel) {
        if (nukeEntity instanceof NukeBE nukeBE) {
            poseStack.pushPose();
            poseStack.mulPose(Axis.YP.rotationDegrees(-90.0F));
            poseStack.mulPose(Axis.YP.rotationDegrees(90.0F));

            // Render the Nuke Block with alternating white "flashes" when the fuse is running
            var shouldFlash = nukeBE.getFuse() / 5 % 2 == 0;
            TntMinecartRenderer.renderWhiteSolidBlock(
                    this.blockRenderer,
                    nukeBE.getBlockState(),
                    poseStack,
                    bufferSource,
                    lightLevel,
                    shouldFlash
            );

            poseStack.popPose();
        }
        super.render(nukeEntity, partialTicks, animationProgress, poseStack, bufferSource, lightLevel);
    }

    @SuppressWarnings("deprecation")
    @Override
    public @NotNull ResourceLocation getTextureLocation(@NotNull T entity) {
        return TextureAtlas.LOCATION_BLOCKS;
    }
}
