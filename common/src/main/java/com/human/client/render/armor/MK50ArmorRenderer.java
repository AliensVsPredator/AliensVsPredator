package com.human.client.render.armor;

import com.human.client.render.layer.MKOuterLayer;
import mod.azure.azurelib.common.render.AzRendererConfig;
import mod.azure.azurelib.common.render.AzRendererPipeline;
import mod.azure.azurelib.common.render.AzRendererPipelineContext;
import mod.azure.azurelib.common.render.armor.AzArmorRenderer;
import mod.azure.azurelib.common.render.armor.AzArmorRendererConfig;
import mod.azure.azurelib.common.render.armor.AzArmorRendererPipeline;
import mod.azure.azurelib.common.render.armor.AzArmorRendererPipelineContext;
import mod.azure.azurelib.core.object.Color;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.DyedItemColor;

import java.util.UUID;

import com.avp.AVPResources;

public class MK50ArmorRenderer extends AzArmorRenderer {

    private static final String NAME = "mk50";

    private static final ResourceLocation MODEL = AVPResources.armorGeoModelLocation(NAME);

    private static final ResourceLocation TEXTURE = AVPResources.armorTextureLocation(NAME + "_inner");

    public MK50ArmorRenderer() {
        super(
            AzArmorRendererConfig.builder(MODEL, TEXTURE)
                .addRenderLayer(new MKOuterLayer())
                .build()
        );
    }

    @Override
    protected AzArmorRendererPipeline createPipeline(AzRendererConfig config) {
        return new AzArmorRendererPipeline(config, this) {

            @Override
            protected AzRendererPipelineContext<UUID, ItemStack> createContext(AzRendererPipeline<UUID, ItemStack> rendererPipeline) {
                return new AzArmorRendererPipelineContext(rendererPipeline) {

                    @Override
                    public Color getRenderColor(ItemStack animatable, float partialTick, int packedLight) {
                        return this.currentStack().is(ItemTags.DYEABLE)
                            ? Color.ofOpaque(
                                DyedItemColor.getOrDefault(this.currentStack(), Color.WHITE.getColor())
                            )
                            : Color.WHITE;
                    }
                };
            }
        };
    }
}
