package com.human.client.render.item.gun.muzzled;

import com.human.common.registry.init.HumanDataComponents;
import mod.azure.azurelib.common.render.item.AzItemRenderer;
import mod.azure.azurelib.common.render.item.AzItemRendererConfig;
import mod.azure.azurelib.common.render.layer.AzAutoGlowingLayer;

import java.util.List;
import java.util.function.UnaryOperator;

import com.avp.AVPResources;

public abstract class MuzzledGunItemRenderer extends AzItemRenderer {

    private static final List<String> DEFAULT_MUZZLE_FLASH_BONE_LIST = List.of("gFlash");

    protected MuzzledGunItemRenderer(String name, UnaryOperator<AzItemRendererConfig.Builder> configBuilderUnaryOperator) {
        this(name, DEFAULT_MUZZLE_FLASH_BONE_LIST, configBuilderUnaryOperator);
    }

    protected MuzzledGunItemRenderer(
        String name,
        List<String> muzzleFlashBoneNames,
        UnaryOperator<AzItemRendererConfig.Builder> configBuilderUnaryOperator
    ) {
        super(
            configBuilderUnaryOperator.apply(
                AzItemRendererConfig.builder(
                    AVPResources.itemGeoModelLocation(name),
                    AVPResources.itemTextureLocation(name)
                )
            )
                .addRenderLayer(new AzAutoGlowingLayer<>())
                .useNewOffset(true)
                .setPrerenderEntry(context -> {
                    var isFiring = context.animatable().get(HumanDataComponents.IS_FIRING.get());

                    muzzleFlashBoneNames.forEach(muzzleFlashBoneName -> {
                        var maybeBone = context.bakedModel().getBoneOrNull(muzzleFlashBoneName);

                        if (maybeBone != null) {
                            // The null check here is deliberate for backwards compatibility.
                            maybeBone.setHidden(isFiring == null || !isFiring);
                        }
                    });

                    return context;
                })
                .build()
        );
    }
}
