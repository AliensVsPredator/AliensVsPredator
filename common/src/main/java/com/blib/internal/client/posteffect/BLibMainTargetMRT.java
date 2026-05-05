package com.blib.internal.client.posteffect;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.platform.TextureUtil;
import com.mojang.blaze3d.systems.RenderSystem;
import org.jetbrains.annotations.ApiStatus;
import org.lwjgl.opengl.GL30;

import com.blib.mod.BLib;

/**
 * State + helpers for the MainTarget MRT extension. Owns two auxiliary color attachment texture IDs (entity-mask R8 at
 * attachment 1, entity-lightmap RGBA8 at attachment 2) and the {@code glDrawBuffers} state needed to keep them
 * attached.
 * <p>
 * The mixin on {@code MainTarget.createFrameBuffer} (and the resize path on {@code RenderTarget.createBuffers})
 * delegates to {@link #attach(int, int, int)} after vanilla finishes its own attachment, and to {@link #destroy()} from
 * the mixin on {@code RenderTarget.destroyBuffers}. The clear path mixin calls {@link #clearAuxiliaryAttachments()} so
 * non-entity fragments read 0 from the auxiliaries.
 */
@ApiStatus.Internal
public final class BLibMainTargetMRT {

    private static final int GL_COLOR_ATTACHMENT0 = 36064;

    private static final int GL_COLOR_ATTACHMENT1 = 36065;

    private static final int GL_COLOR_ATTACHMENT2 = 36066;

    private static final int GL_R8 = 33321;

    private static final int GL_RED = 6403;

    private static final int GL_RGBA8 = 32856;

    private static final int GL_RGBA = 6408;

    private static final int GL_UNSIGNED_BYTE = 5121;

    private static final int GL_TEXTURE_2D = 3553;

    private static int entityMaskTextureId = -1;

    private static int entityLightmapTextureId = -1;

    private static int width;

    private static int height;

    private static boolean attached;

    private BLibMainTargetMRT() {
        throw new UnsupportedOperationException();
    }

    public static boolean isAttached() {
        return attached;
    }

    public static int entityMaskTextureId() {
        return entityMaskTextureId;
    }

    public static int entityLightmapTextureId() {
        return entityLightmapTextureId;
    }

    public static int width() {
        return width;
    }

    public static int height() {
        return height;
    }

    /**
     * Allocate the two auxiliary attachments and bind them to the currently-bound framebuffer. Caller is responsible
     * for binding the MainTarget's FBO before calling.
     */
    public static void attach(int frameBufferId, int viewWidth, int viewHeight) {
        if (BLibIrisCompat.isShaderModActive()) {
            return;
        }

        RenderSystem.assertOnRenderThreadOrInit();

        destroy();

        width = viewWidth;
        height = viewHeight;

        entityMaskTextureId = TextureUtil.generateTextureId();
        GlStateManager._bindTexture(entityMaskTextureId);
        GlStateManager._texParameter(GL_TEXTURE_2D, 10241, 9728);
        GlStateManager._texParameter(GL_TEXTURE_2D, 10240, 9728);
        GlStateManager._texParameter(GL_TEXTURE_2D, 10242, 33071);
        GlStateManager._texParameter(GL_TEXTURE_2D, 10243, 33071);
        GlStateManager._texImage2D(GL_TEXTURE_2D, 0, GL_R8, viewWidth, viewHeight, 0, GL_RED, GL_UNSIGNED_BYTE, null);

        entityLightmapTextureId = TextureUtil.generateTextureId();
        GlStateManager._bindTexture(entityLightmapTextureId);
        GlStateManager._texParameter(GL_TEXTURE_2D, 10241, 9728);
        GlStateManager._texParameter(GL_TEXTURE_2D, 10240, 9728);
        GlStateManager._texParameter(GL_TEXTURE_2D, 10242, 33071);
        GlStateManager._texParameter(GL_TEXTURE_2D, 10243, 33071);
        GlStateManager._texImage2D(GL_TEXTURE_2D, 0, GL_RGBA8, viewWidth, viewHeight, 0, GL_RGBA, GL_UNSIGNED_BYTE, null);

        GlStateManager._bindTexture(0);

        GlStateManager._glBindFramebuffer(36160, frameBufferId);
        GlStateManager._glFramebufferTexture2D(36160, GL_COLOR_ATTACHMENT1, GL_TEXTURE_2D, entityMaskTextureId, 0);
        GlStateManager._glFramebufferTexture2D(36160, GL_COLOR_ATTACHMENT2, GL_TEXTURE_2D, entityLightmapTextureId, 0);

        GL30.glDrawBuffers(new int[] { GL_COLOR_ATTACHMENT0, GL_COLOR_ATTACHMENT1, GL_COLOR_ATTACHMENT2 });

        attached = true;

        BLib.LOGGER.debug("[BLib] MRT auxiliary attachments allocated: {}x{}", viewWidth, viewHeight);
    }

    /**
     * Restore the {@code glDrawBuffers} state for the currently-bound framebuffer to {@code [0, 1, 2]} so vanilla
     * entity draws populate the auxiliary attachments. Anything that touches MainTarget's FBO state via
     * {@code glDrawBuffers} (e.g., a fullscreen-quad blit that writes only to attachment 0) MUST call this
     * afterwards to put MainTarget back into MRT mode — otherwise subsequent frames silently drop writes to
     * attachments 1 and 2 and the entity mask "freezes" at whatever was there last.
     */
    public static void restoreDrawBuffers() {
        if (!attached) {
            return;
        }

        GL30.glDrawBuffers(new int[] { GL_COLOR_ATTACHMENT0, GL_COLOR_ATTACHMENT1, GL_COLOR_ATTACHMENT2 });
    }

    /** Clear auxiliary attachments to 0. Caller must have the MainTarget FBO bound. */
    public static void clearAuxiliaryAttachments() {
        if (!attached) {
            return;
        }

        GL30.glClearBufferfv(GL30.GL_COLOR, 1, new float[] { 0.0F, 0.0F, 0.0F, 0.0F });
        GL30.glClearBufferfv(GL30.GL_COLOR, 2, new float[] { 0.0F, 0.0F, 0.0F, 0.0F });
    }

    public static void destroy() {
        if (entityMaskTextureId != -1) {
            TextureUtil.releaseTextureId(entityMaskTextureId);
            entityMaskTextureId = -1;
        }

        if (entityLightmapTextureId != -1) {
            TextureUtil.releaseTextureId(entityLightmapTextureId);
            entityLightmapTextureId = -1;
        }

        attached = false;
        width = 0;
        height = 0;
    }
}
