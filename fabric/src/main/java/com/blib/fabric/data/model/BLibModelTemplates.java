package com.blib.fabric.data.model;

import com.blib.BLib;
import net.minecraft.data.models.model.ModelTemplate;
import net.minecraft.data.models.model.TextureSlot;
import net.minecraft.resources.ResourceLocation;

import java.util.Optional;

public class BLibModelTemplates {

    public static final ModelTemplate BARS_CAP = createVanilla("iron_bars_cap", "_cap", BLibTextureSlot.BARS, TextureSlot.PARTICLE);

    public static final ModelTemplate BARS_CAP_ALT = createVanilla(
        "iron_bars_cap_alt",
        "_cap_alt",
        BLibTextureSlot.BARS,
        TextureSlot.PARTICLE
    );

    public static final ModelTemplate BARS_POST = createVanilla("iron_bars_post", "_post", BLibTextureSlot.BARS, TextureSlot.PARTICLE);

    public static final ModelTemplate BARS_POST_ENDS = createVanilla(
        "iron_bars_post_ends",
        "_post_ends",
        TextureSlot.EDGE,
        TextureSlot.PARTICLE
    );

    public static final ModelTemplate BARS_SIDE = createVanilla(
        "iron_bars_side",
        "_side",
        BLibTextureSlot.BARS,
        TextureSlot.EDGE,
        TextureSlot.PARTICLE
    );

    public static final ModelTemplate BARS_SIDE_ALT = createVanilla(
        "iron_bars_side_alt",
        "_side_alt",
        BLibTextureSlot.BARS,
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
        return new ModelTemplate(Optional.of(BLib.MOD.createResourceLocation("block/" + string)), Optional.of(string2), textureSlots);
    }
}
