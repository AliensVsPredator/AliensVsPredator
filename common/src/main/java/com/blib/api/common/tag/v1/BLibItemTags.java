package com.blib.api.common.tag.v1;

import net.minecraft.core.registries.Registries;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;

import com.blib.mod.BLib;

public class BLibItemTags {

    /**
     * @deprecated Use ItemTags.DECORATED_POT_SHERDS, instead.
     */
    @Deprecated(forRemoval = true)
    public static final TagKey<Item> DECORATIVE_POT_SHERDS = create("decorative_pot_sherds");

    /**
     * @deprecated Will be removed in a future version.
     */
    @Deprecated(forRemoval = true)
    public static final TagKey<Item> FIRE_RESISTANT_ARMORS = create("fire_resistant_armors");

    public static final TagKey<Item> IRON_BLOCK_LIKE = create("iron_block_like");

    public static final TagKey<Item> IRON_INGOT_LIKE = create("iron_ingot_like");

    public static final TagKey<Item> MELEE_WEAPONS = create("melee_weapons");

    public static final TagKey<Item> RANGED_WEAPONS = create("ranged_weapons");

    /**
     * @deprecated Will be removed in a future version.
     */
    @Deprecated(forRemoval = true)
    public static final TagKey<Item> WATER_BREATHING_ARMORS = create("water_breathing_armors");

    private static TagKey<Item> create(String path) {
        return BLib.MOD.resources().createTagKey(Registries.ITEM, path);
    }
}
