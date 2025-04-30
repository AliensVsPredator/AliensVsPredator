package com.avp.data.model;

import net.minecraft.data.models.model.ModelTemplate;
import net.minecraft.data.models.model.TextureSlot;
import net.minecraft.resources.ResourceLocation;

import java.util.Optional;

import com.avp.AVPResources;

public class AVPModelTemplates {

    public static final ModelTemplate BARS_CAP = createVanilla("iron_bars_cap", "_cap", AVPTextureSlot.BARS, TextureSlot.PARTICLE);

    public static final ModelTemplate BARS_CAP_ALT = createVanilla(
        "iron_bars_cap_alt",
        "_cap_alt",
        AVPTextureSlot.BARS,
        TextureSlot.PARTICLE
    );

    public static final ModelTemplate BARS_POST = createVanilla("iron_bars_post", "_post", AVPTextureSlot.BARS, TextureSlot.PARTICLE);

    public static final ModelTemplate BARS_POST_ENDS = createVanilla(
        "iron_bars_post_ends",
        "_post_ends",
        TextureSlot.EDGE,
        TextureSlot.PARTICLE
    );

    public static final ModelTemplate BARS_SIDE = createVanilla(
        "iron_bars_side",
        "_side",
        AVPTextureSlot.BARS,
        TextureSlot.EDGE,
        TextureSlot.PARTICLE
    );

    public static final ModelTemplate BARS_SIDE_ALT = createVanilla(
        "iron_bars_side_alt",
        "_side_alt",
        AVPTextureSlot.BARS,
        TextureSlot.EDGE,
        TextureSlot.PARTICLE
    );

    public static final ModelTemplate WALL_LOW_SIDE = create("template_wall_side", "_side", TextureSlot.TOP, TextureSlot.WALL);

    public static final ModelTemplate WALL_TALL_SIDE = create("template_wall_side_tall", "_side_tall", TextureSlot.TOP, TextureSlot.WALL);

    public static final ModelTemplate WALL_INVENTORY = create("template_wall_inventory", "_inventory", TextureSlot.TOP, TextureSlot.WALL);

    private static ModelTemplate createVanilla(String string, String string2, TextureSlot... textureSlots) {
        return new ModelTemplate(Optional.of(ResourceLocation.withDefaultNamespace("block/" + string)), Optional.of(string2), textureSlots);
    }

    private static ModelTemplate create(String string, String string2, TextureSlot... textureSlots) {
        return new ModelTemplate(Optional.of(AVPResources.location("block/" + string)), Optional.of(string2), textureSlots);
    }
}
