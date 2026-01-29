package com.blib.azurelib.common.animation;

import net.minecraft.client.Minecraft;

import com.blib.azurelib.common.util.client.RenderUtils;

public class AzAnimationTimer {

    private final AzAnimatorConfig config;

    // Remnants from GeoModel.
    private double animTime;

    private double lastGameTickTime;

    private boolean wasPausedLastFrame;

    public AzAnimationTimer(AzAnimatorConfig config) {
        this.config = config;
    }

    public void tick() {
        var minecraft = Minecraft.getInstance();
        var currentRenderTick = RenderUtils.getCurrentTick();

        if (minecraft.isPaused()) {
            if (!wasPausedLastFrame) {
                // If this is the first frame of the game pause time, we need to set a flag.
                this.wasPausedLastFrame = true;
            }

            if (!config.shouldPlayAnimationsWhileGamePaused()) {
                // If we cannot play animations while the game is paused, then return early.
                return;
            }
        }

        // Compute the delta render tick for this frame. This calculation by default does not account for the game
        // pause state, which means that the difference here could be massive by the time the player unpauses.
        var deltaRenderTick = currentRenderTick - lastGameTickTime;

        if (wasPausedLastFrame && !minecraft.isPaused()) {
            // If this is the first frame of the game playtime, we need to set a flag and adjust the deltaRenderTick.
            this.wasPausedLastFrame = false;
            // To account for the deltaRenderTick being massive on exiting the game pause state, we simply set
            // it to 0. This will result in no difference being added to animTime, allowing animations to
            // continue right where it left off.
            deltaRenderTick = 0;
        }

        // Add the deltaRenderTick to animTime. animTime is what controls the progress of animations.
        this.animTime += deltaRenderTick;
        this.lastGameTickTime = currentRenderTick;
    }

    public double getAnimTime() {
        return animTime;
    }
}
