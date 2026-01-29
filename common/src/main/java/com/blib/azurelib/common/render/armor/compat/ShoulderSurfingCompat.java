package com.blib.azurelib.common.render.armor.compat;

import com.github.exopandora.shouldersurfing.api.client.ShoulderSurfing;
import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.Entity;

import java.util.function.Supplier;

import com.blib.api.BLibAPI;

public class ShoulderSurfingCompat {

    private static boolean isLoaded = false;

    public static void init() {
        if (BLibAPI.isModLoaded("shouldersurfing")) {
            isLoaded = true;
        }
    }

    public static boolean isLoaded() {
        return isLoaded;
    }

    public static float getAlpha(Entity currentEntity) {
        Supplier<Float> alphaSupplier;
        var cameraEntity = Minecraft.getInstance().getCameraEntity();
        var cameraEntityRenderer = ShoulderSurfing.getInstance().getCameraEntityRenderer();

        if (cameraEntity == null) {
            return 1.0F;
        }

        if (currentEntity.is(cameraEntity) && cameraEntityRenderer.isRenderingCameraEntity()) {
            alphaSupplier = cameraEntityRenderer::getCameraEntityAlpha;
        } else {
            alphaSupplier = () -> 1.0F;
        }

        return alphaSupplier.get();
    }

    private ShoulderSurfingCompat() { /* NO-OP */}
}
