package com.blib.api.common.tag.v1;

import net.minecraft.core.registries.Registries;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.EntityType;

import com.blib.mod.BLib;

public class BLibEntityTypeTags {

    public static final TagKey<EntityType<?>> HUMANOIDS = create("humanoids");

    public static final TagKey<EntityType<?>> NETHER_CREATURES = create("nether_creatures");

    /**
     * Entity types that should be visible in BLib's thermal vision post-effect. Entities not in this tag are
     * skipped during the gbuffer pass while thermal is active — their pixels show the cold world heat behind
     * them, so they appear "invisible" in IR.
     * <p>
     * Empty by default — modders/datapacks add the entity types they want detectable. Common case: warm-blooded
     * mobs (cows, sheep, players, villagers, hostile mobs) but not e.g. silverfish, endermites, or constructed/
     * non-biological entities.
     */
    public static final TagKey<EntityType<?>> THERMAL_VISIBLE = create("thermal_visible");

    /**
     * Entity types that read as "naturally hot" in thermal vision — every bone's effective block-light coord is
     * floored at maximum, so the entity always reads as if lit by a full-intensity light source regardless of its
     * actual world lighting. Combined with the warm-color heuristic (lava/magma/blaze textures are red/orange),
     * the result is the entity displays as bright orange/red/white in IR.
     * <p>
     * Membership implies {@link #THERMAL_VISIBLE} effectively — but they should still be added to both tags
     * explicitly. An entity in THERMAL_HOT but not in THERMAL_VISIBLE will be skipped from rendering entirely.
     */
    public static final TagKey<EntityType<?>> THERMAL_HOT = create("thermal_hot");

    private static TagKey<EntityType<?>> create(String path) {
        return BLib.MOD.resources().createTagKey(Registries.ENTITY_TYPE, path);
    }
}
