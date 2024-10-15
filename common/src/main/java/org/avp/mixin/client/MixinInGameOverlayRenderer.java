package org.avp.mixin.client;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.*;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.ScreenEffectRenderer;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import org.avp.common.AVPConstants;
import org.avp.common.AVPResources;
import org.avp.common.data.tag.AVPBlockTags;
import org.joml.Matrix4f;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ScreenEffectRenderer.class)
public class MixinInGameOverlayRenderer {

    private static final ResourceLocation RESIN_OVERLAY_TEXTURE = AVPResources.location("textures/block/resin_webbing.png");

    @Inject(method = {"renderScreenEffect"}, at = {@At("RETURN")})
    private static void renderOverlays(Minecraft client, PoseStack matrices, CallbackInfo ci) {
        if (!AVPConstants.isCreativeSpecPlayer.test(client.player) && client.player.level().getBlockState(client.player.blockPosition()).is(AVPBlockTags.RESIN_WEBBING)) {
            renderOverlay(client, matrices, 1, RESIN_OVERLAY_TEXTURE);
        }
    }

    private static void renderOverlay(Minecraft client, PoseStack matrices, float progress, ResourceLocation resourceLocation) {
        RenderSystem.disableDepthTest();
        RenderSystem.depthMask(false);
        RenderSystem.defaultBlendFunc();
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.setShaderTexture(0, resourceLocation);
        BlockPos blockpos = BlockPos.containing(client.player.getX(), client.player.getEyeY(), client.player.getZ());
        float f = LightTexture.getBrightness(client.player.level().dimensionType(), client.player.level().getMaxLocalRawBrightness(blockpos));
        RenderSystem.enableBlend();
        RenderSystem.setShaderColor(f, f, f, progress);
        float f7 = -client.player.getYRot() / 64.0F;
        float f8 = client.player.getXRot() / 64.0F;
        Matrix4f matrix4f = matrices.last().pose();
        BufferBuilder bufferbuilder = Tesselator.getInstance().getBuilder();
        bufferbuilder.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_TEX);
        bufferbuilder.vertex(matrix4f, -1.0F, -1.0F, -0.5F).uv(4.0F + f7, 4.0F + f8).endVertex();
        bufferbuilder.vertex(matrix4f, 1.0F, -1.0F, -0.5F).uv(0.0F + f7, 4.0F + f8).endVertex();
        bufferbuilder.vertex(matrix4f, 1.0F, 1.0F, -0.5F).uv(0.0F + f7, 0.0F + f8).endVertex();
        bufferbuilder.vertex(matrix4f, -1.0F, 1.0F, -0.5F).uv(4.0F + f7, 0.0F + f8).endVertex();
        BufferUploader.drawWithShader(bufferbuilder.end());
        RenderSystem.depthMask(true);
        RenderSystem.enableDepthTest();
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 0.5F);
        RenderSystem.disableBlend();
    }
}
