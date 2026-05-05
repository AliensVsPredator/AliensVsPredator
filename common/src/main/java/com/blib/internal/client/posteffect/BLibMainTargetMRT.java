package com.blib.internal.client.posteffect;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.platform.TextureUtil;
import com.mojang.blaze3d.systems.RenderSystem;
import org.jetbrains.annotations.ApiStatus;
import org.lwjgl.opengl.GL30;

import com.blib.mod.BLib;

/**
 * State + helpers for the MainTarget MRT extension. Owns six auxiliary color attachment texture IDs (entity-mask R8 at
 * attachment 1, entity-lightmap RGBA8 at attachment 2, entity-normal RGBA8 at attachment 3, entity-thermal-data RGBA8
 * at attachment 4, entity-specular RGBA8 at attachment 5, entity-material-id R8 at attachment 6) and the
 * {@code glDrawBuffers} state needed to keep them attached.
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

    private static final int GL_COLOR_ATTACHMENT3 = 36067;

    private static final int GL_COLOR_ATTACHMENT4 = 36068;

    private static final int GL_COLOR_ATTACHMENT5 = 36069;

    private static final int GL_COLOR_ATTACHMENT6 = 36070;

    private static final int GL_R8 = 33321;

    private static final int GL_RED = 6403;

    private static final int GL_RGBA8 = 32856;

    private static final int GL_RGBA = 6408;

    private static final int GL_UNSIGNED_BYTE = 5121;

    private static final int GL_TEXTURE_2D = 3553;

    private static int entityMaskTextureId = -1;

    private static int entityLightmapTextureId = -1;

    private static int entityNormalTextureId = -1;

    private static int entityThermalDataTextureId = -1;

    private static int entitySpecularTextureId = -1;

    private static int entityMaterialIdTextureId = -1;

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

    public static int entityNormalTextureId() {
        return entityNormalTextureId;
    }

    public static int entityThermalDataTextureId() {
        return entityThermalDataTextureId;
    }

    public static int entitySpecularTextureId() {
        return entitySpecularTextureId;
    }

    public static int entityMaterialIdTextureId() {
        return entityMaterialIdTextureId;
    }

    public static int width() {
        return width;
    }

    public static int height() {
        return height;
    }

    /**
     * Allocate the six auxiliary attachments and bind them to the currently-bound framebuffer. Caller is responsible
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

        entityNormalTextureId = TextureUtil.generateTextureId();
        GlStateManager._bindTexture(entityNormalTextureId);
        GlStateManager._texParameter(GL_TEXTURE_2D, 10241, 9728);
        GlStateManager._texParameter(GL_TEXTURE_2D, 10240, 9728);
        GlStateManager._texParameter(GL_TEXTURE_2D, 10242, 33071);
        GlStateManager._texParameter(GL_TEXTURE_2D, 10243, 33071);
        GlStateManager._texImage2D(GL_TEXTURE_2D, 0, GL_RGBA8, viewWidth, viewHeight, 0, GL_RGBA, GL_UNSIGNED_BYTE, null);

        entityThermalDataTextureId = TextureUtil.generateTextureId();
        GlStateManager._bindTexture(entityThermalDataTextureId);
        GlStateManager._texParameter(GL_TEXTURE_2D, 10241, 9728);
        GlStateManager._texParameter(GL_TEXTURE_2D, 10240, 9728);
        GlStateManager._texParameter(GL_TEXTURE_2D, 10242, 33071);
        GlStateManager._texParameter(GL_TEXTURE_2D, 10243, 33071);
        GlStateManager._texImage2D(GL_TEXTURE_2D, 0, GL_RGBA8, viewWidth, viewHeight, 0, GL_RGBA, GL_UNSIGNED_BYTE, null);

        entitySpecularTextureId = TextureUtil.generateTextureId();
        GlStateManager._bindTexture(entitySpecularTextureId);
        GlStateManager._texParameter(GL_TEXTURE_2D, 10241, 9728);
        GlStateManager._texParameter(GL_TEXTURE_2D, 10240, 9728);
        GlStateManager._texParameter(GL_TEXTURE_2D, 10242, 33071);
        GlStateManager._texParameter(GL_TEXTURE_2D, 10243, 33071);
        GlStateManager._texImage2D(GL_TEXTURE_2D, 0, GL_RGBA8, viewWidth, viewHeight, 0, GL_RGBA, GL_UNSIGNED_BYTE, null);

        entityMaterialIdTextureId = TextureUtil.generateTextureId();
        GlStateManager._bindTexture(entityMaterialIdTextureId);
        GlStateManager._texParameter(GL_TEXTURE_2D, 10241, 9728);
        GlStateManager._texParameter(GL_TEXTURE_2D, 10240, 9728);
        GlStateManager._texParameter(GL_TEXTURE_2D, 10242, 33071);
        GlStateManager._texParameter(GL_TEXTURE_2D, 10243, 33071);
        GlStateManager._texImage2D(GL_TEXTURE_2D, 0, GL_R8, viewWidth, viewHeight, 0, GL_RED, GL_UNSIGNED_BYTE, null);

        GlStateManager._bindTexture(0);

        GlStateManager._glBindFramebuffer(36160, frameBufferId);
        GlStateManager._glFramebufferTexture2D(36160, GL_COLOR_ATTACHMENT1, GL_TEXTURE_2D, entityMaskTextureId, 0);
        GlStateManager._glFramebufferTexture2D(36160, GL_COLOR_ATTACHMENT2, GL_TEXTURE_2D, entityLightmapTextureId, 0);
        GlStateManager._glFramebufferTexture2D(36160, GL_COLOR_ATTACHMENT3, GL_TEXTURE_2D, entityNormalTextureId, 0);
        GlStateManager._glFramebufferTexture2D(36160, GL_COLOR_ATTACHMENT4, GL_TEXTURE_2D, entityThermalDataTextureId, 0);
        GlStateManager._glFramebufferTexture2D(36160, GL_COLOR_ATTACHMENT5, GL_TEXTURE_2D, entitySpecularTextureId, 0);
        GlStateManager._glFramebufferTexture2D(36160, GL_COLOR_ATTACHMENT6, GL_TEXTURE_2D, entityMaterialIdTextureId, 0);

        GL30.glDrawBuffers(
            new int[] {
                GL_COLOR_ATTACHMENT0,
                GL_COLOR_ATTACHMENT1,
                GL_COLOR_ATTACHMENT2,
                GL_COLOR_ATTACHMENT3,
                GL_COLOR_ATTACHMENT4,
                GL_COLOR_ATTACHMENT5,
                GL_COLOR_ATTACHMENT6
            }
        );

        attached = true;

        BLib.LOGGER.debug("[BLib] MRT auxiliary attachments allocated: {}x{}", viewWidth, viewHeight);
    }

    /**
     * Restore the {@code glDrawBuffers} state for the currently-bound framebuffer to {@code [0, 1, 2, 3, 4, 5, 6]} so
     * vanilla entity draws populate the auxiliary attachments. Anything that touches MainTarget's FBO state via
     * {@code glDrawBuffers} (e.g., a fullscreen-quad blit that writes only to attachment 0) MUST call this afterwards
     * to put MainTarget back into MRT mode — otherwise subsequent frames silently drop writes to attachments 1-6 and
     * the auxiliary entity data "freezes" at whatever was there last.
     */
    public static void restoreDrawBuffers() {
        if (!attached) {
            return;
        }

        GL30.glDrawBuffers(
            new int[] {
                GL_COLOR_ATTACHMENT0,
                GL_COLOR_ATTACHMENT1,
                GL_COLOR_ATTACHMENT2,
                GL_COLOR_ATTACHMENT3,
                GL_COLOR_ATTACHMENT4,
                GL_COLOR_ATTACHMENT5,
                GL_COLOR_ATTACHMENT6
            }
        );
    }

    /** Clear auxiliary attachments to 0. Caller must have the MainTarget FBO bound. */
    public static void clearAuxiliaryAttachments() {
        if (!attached) {
            return;
        }

        GL30.glClearBufferfv(GL30.GL_COLOR, 1, new float[] { 0.0F, 0.0F, 0.0F, 0.0F });
        GL30.glClearBufferfv(GL30.GL_COLOR, 2, new float[] { 0.0F, 0.0F, 0.0F, 0.0F });
        GL30.glClearBufferfv(GL30.GL_COLOR, 3, new float[] { 0.0F, 0.0F, 0.0F, 0.0F });
        GL30.glClearBufferfv(GL30.GL_COLOR, 4, new float[] { 0.0F, 0.0F, 0.0F, 0.0F });
        GL30.glClearBufferfv(GL30.GL_COLOR, 5, new float[] { 0.0F, 0.0F, 0.0F, 0.0F });
        GL30.glClearBufferfv(GL30.GL_COLOR, 6, new float[] { 0.0F, 0.0F, 0.0F, 0.0F });
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

        if (entityNormalTextureId != -1) {
            TextureUtil.releaseTextureId(entityNormalTextureId);
            entityNormalTextureId = -1;
        }

        if (entityThermalDataTextureId != -1) {
            TextureUtil.releaseTextureId(entityThermalDataTextureId);
            entityThermalDataTextureId = -1;
        }

        if (entitySpecularTextureId != -1) {
            TextureUtil.releaseTextureId(entitySpecularTextureId);
            entitySpecularTextureId = -1;
        }

        if (entityMaterialIdTextureId != -1) {
            TextureUtil.releaseTextureId(entityMaterialIdTextureId);
            entityMaterialIdTextureId = -1;
        }

        attached = false;
        width = 0;
        height = 0;
    }
}
