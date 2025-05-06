package com.avp.client.render.item.gun.muzzled;

import mod.azure.azurelib.rewrite.render.item.AzItemRenderer;
import mod.azure.azurelib.rewrite.render.item.AzItemRendererConfig;
import mod.azure.azurelib.rewrite.render.layer.AzAutoGlowingLayer;

import java.util.List;
import java.util.function.UnaryOperator;

import com.avp.AVPResources;
import com.avp.common.item.GunItem;

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
                    var itemStack = context.animatable();

                    if (!(itemStack.getItem() instanceof GunItem gunItem)) {
                        return context;
                    }

                    muzzleFlashBoneNames.forEach(muzzleFlashBoneName -> {
                        var maybeBone = context.bakedModel().getBoneOrNull(muzzleFlashBoneName);

                        if (maybeBone != null) {
                            maybeBone.setHidden(!gunItem.isFiring);
                        }
                    });

                    return context;
                })
                .build()
        );
    }
}
