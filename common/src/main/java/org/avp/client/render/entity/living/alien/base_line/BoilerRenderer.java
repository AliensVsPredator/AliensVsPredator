package org.avp.client.render.entity.living.alien.base_line;

import mod.azure.azurelib.common.api.client.renderer.GeoEntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;

import org.avp.client.model.entity.living.alien.base_line.BoilerModel;
import org.avp.common.game.entity.living.alien.base_line.Boiler;

public class BoilerRenderer extends GeoEntityRenderer<Boiler> {

    public BoilerRenderer(EntityRendererProvider.Context context) {
        super(context, new BoilerModel());
        this.withScale(0.9F);
    }

    @Override
    public @NotNull Vec3 getRenderOffset(Boiler boiler, float partialTicks) {
        var shakeIntensity = 0.0025;
        return new Vec3(boiler.getRandom().nextGaussian() * shakeIntensity, 0.0, boiler.getRandom().nextGaussian() * shakeIntensity);
    }
}
