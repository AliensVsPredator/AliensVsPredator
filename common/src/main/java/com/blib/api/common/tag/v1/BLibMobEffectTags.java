package com.blib.api.common.tag.v1;

import net.minecraft.core.registries.Registries;
import net.minecraft.tags.TagKey;
import net.minecraft.world.effect.MobEffect;

import com.blib.mod.BLib;

public class BLibMobEffectTags {

    /**
     * Mob effects that {@code MilkBucketItem.finishUsingItem} should NOT remove. Vanilla milk wipes every active effect
     * indiscriminately via {@code LivingEntity.removeAllEffects()}; BLib's mixin into that call site snapshots any
     * effect whose holder is in this tag, runs the wipe, and re-applies the snapshot — preserving each tagged effect's
     * original duration, amplifier, ambient flag, and visibility flags.
     * <p>
     * Use cases: stealth-cloak effects (predator vision mud), curse effects that should require a specific
     * counter-item, lore-bound enchantment effects that shouldn't be cured by an unrelated drink. Empty by default —
     * downstream mods add their own effects to the tag.
     */
    public static final TagKey<MobEffect> MILK_IMMUNE = create("milk_immune");

    /**
     * Mob effects that should never emit world particles, regardless of how the effect was applied (commands, splash
     * potions, items that pass {@code visible = true} to the {@code MobEffectInstance} constructor, etc.). BLib's mixin
     * into {@code MobEffectInstance.isVisible} unconditionally returns {@code false} when the holder is in this tag.
     * The {@code showIcon} flag is left alone, so the inventory effect-icon still appears.
     * <p>
     * Use cases: stealth-cloak effects whose purpose is defeated by a visible particle swirl, or any lore-bound effect
     * that should be silent. Empty by default — downstream mods add their own effects to the tag.
     */
    public static final TagKey<MobEffect> NO_PARTICLES = create("no_particles");

    private BLibMobEffectTags() {
        throw new UnsupportedOperationException();
    }

    private static TagKey<MobEffect> create(String path) {
        return BLib.MOD.resources().createTagKey(Registries.MOB_EFFECT, path);
    }
}
