package com.blib.internal.client.posteffect;

import com.mojang.blaze3d.pipeline.TextureTarget;
import net.minecraft.client.Minecraft;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Nullable;

/**
 * Owner of the two ping-pong color targets the pipeline reads/writes through. No depth attachment — these are
 * full-screen post-process targets only. Resized in lockstep with the MainTarget by the framebuffer-resize mixin.
 */
@ApiStatus.Internal
public final class BLibPostEffectFramebuffers {

    public static final BLibPostEffectFramebuffers INSTANCE = new BLibPostEffectFramebuffers();

    private @Nullable TextureTarget targetA;

    private @Nullable TextureTarget targetB;

    private int width = -1;

    private int height = -1;

    private BLibPostEffectFramebuffers() {}

    public void ensureSize(int width, int height) {
        if (this.width == width && this.height == height && targetA != null && targetB != null) {
            return;
        }

        destroy();

        this.width = width;
        this.height = height;
        this.targetA = new TextureTarget(width, height, false, Minecraft.ON_OSX);
        this.targetB = new TextureTarget(width, height, false, Minecraft.ON_OSX);
        targetA.setClearColor(0.0F, 0.0F, 0.0F, 0.0F);
        targetB.setClearColor(0.0F, 0.0F, 0.0F, 0.0F);
    }

    public @Nullable TextureTarget targetA() {
        return targetA;
    }

    public @Nullable TextureTarget targetB() {
        return targetB;
    }

    public int width() {
        return width;
    }

    public int height() {
        return height;
    }

    public void resize(int width, int height) {
        ensureSize(width, height);
    }

    public void destroy() {
        if (targetA != null) {
            targetA.destroyBuffers();
            targetA = null;
        }

        if (targetB != null) {
            targetB.destroyBuffers();
            targetB = null;
        }

        width = -1;
        height = -1;
    }
}
