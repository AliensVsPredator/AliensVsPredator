package com.human.client.render.entity;

import com.human.common.gameplay.entity.nuke.PrimedNuke;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.block.BlockRenderDispatcher;
import net.minecraft.client.renderer.culling.Frustum;
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
    public void render(
        T nukeEntity,
        float partialTicks,
        float animationProgress,
        PoseStack poseStack,
        MultiBufferSource bufferSource,
        int lightLevel
    ) {
        if (nukeEntity instanceof PrimedNuke primedNuke) {
            poseStack.pushPose();
            poseStack.mulPose(Axis.YP.rotationDegrees(-90.0F));
            poseStack.mulPose(Axis.YP.rotationDegrees(90.0F));

            // Render the Nuke Block with alternating white "flashes" when the fuse is running
            var shouldFlash = primedNuke.getFuse() / 5 % 2 == 0;
            TntMinecartRenderer.renderWhiteSolidBlock(
                this.blockRenderer,
                primedNuke.getBlockState(),
                poseStack,
                bufferSource,
                lightLevel,
                shouldFlash
            );

            poseStack.popPose();
        }
        super.render(nukeEntity, partialTicks, animationProgress, poseStack, bufferSource, lightLevel);
    }

    @Override
    public boolean shouldRender(T livingEntity, Frustum camera, double camX, double camY, double camZ) {
        return true;
    }

    @SuppressWarnings("deprecation")
    @Override
    public @NotNull ResourceLocation getTextureLocation(@NotNull T entity) {
        return TextureAtlas.LOCATION_BLOCKS;
    }
}
