package com.avp.client.render.armor;

import mod.azure.azurelib.rewrite.render.armor.AzArmorRenderer;
import mod.azure.azurelib.rewrite.render.armor.AzArmorRendererConfig;
import net.minecraft.core.component.DataComponents;
import net.minecraft.resources.ResourceLocation;

import com.avp.AVPResources;
import net.minecraft.util.FastColor;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.component.DyedItemColor;

public class MK50ArmorRenderer extends AzArmorRenderer {

    private static final String NAME = "mk50";

    private static final ResourceLocation MODEL = AVPResources.armorGeoModelLocation(NAME);

    private static final ResourceLocation TEXTURE = AVPResources.armorTextureLocation(NAME + "_yellow");

    public MK50ArmorRenderer() {
        super(AzArmorRendererConfig.builder($ -> MODEL,
                itemStack ->
                        itemStack != null && itemStack.get(DataComponents.DYED_COLOR) != null ?
                                textureLocation(itemStack.get(DataComponents.DYED_COLOR))
                        : TEXTURE).build());
    }

    public static ResourceLocation textureLocation(DyedItemColor dyedItemColor) {
        return switch (dyedItemColor.rgb()) {
            case 0xFFFFFF -> AVPResources.armorTextureLocation(NAME + "_white");
            case 0xFFA500 -> AVPResources.armorTextureLocation(NAME + "_orange");
            case 0xFF00FF -> AVPResources.armorTextureLocation(NAME + "_magenta");
            case 0xADD8E6 -> AVPResources.armorTextureLocation(NAME + "_light_blue");
            case 0x00FF00 -> AVPResources.armorTextureLocation(NAME + "_lime");
            case 0xFFC0CB -> AVPResources.armorTextureLocation(NAME + "_pink");
            case 0x808080 -> AVPResources.armorTextureLocation(NAME + "_gray");
            case 0xD3D3D3 -> AVPResources.armorTextureLocation(NAME + "_light_gray");
            case 0x00FFFF -> AVPResources.armorTextureLocation(NAME + "_cyan");
            case 0x800080 -> AVPResources.armorTextureLocation(NAME + "_purple");
            case 0x0000FF -> AVPResources.armorTextureLocation(NAME + "_blue");
            case 0xA52A2A -> AVPResources.armorTextureLocation(NAME + "_brown");
            case 0x008000 -> AVPResources.armorTextureLocation(NAME + "_green");
            case 0xFF0000 -> AVPResources.armorTextureLocation(NAME + "_red");
            case 0x000000 -> AVPResources.armorTextureLocation(NAME + "_black");
            default -> TEXTURE;
        };
    }
}
