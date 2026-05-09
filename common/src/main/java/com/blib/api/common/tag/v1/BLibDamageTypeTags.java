package com.blib.api.common.tag.v1;

import net.minecraft.core.registries.Registries;
import net.minecraft.tags.TagKey;
import net.minecraft.world.damagesource.DamageType;

import com.blib.mod.BLib;

public class BLibDamageTypeTags {

    /**
     * Damage types that should skip armor reduction WITHOUT also gaining shield bypass. Vanilla's
     * {@code minecraft:bypasses_shield} tag includes {@code #minecraft:bypasses_armor} as a sub-tag value, so any
     * damage type tagged into vanilla's {@code bypasses_armor} automatically also bypasses shields — meaning there's no
     * vanilla-tag-only way to express "this attack tears through armor but a shield can still stop it."
     * <p>
     * BLib's {@code MixinLivingEntity_BypassesArmorOnly} hooks {@code LivingEntity.getDamageAfterArmorAbsorb} and
     * returns the unreduced damage when the source is in this tag, mirroring what vanilla's {@code bypasses_armor} tag
     * does — but purely through this BLib-owned tag, so the inclusion in {@code bypasses_shield} doesn't transitively
     * pick it up. Tag your damage type into this instead of (or in addition to, if you also want vanilla-armor-bypass
     * semantics elsewhere) vanilla's {@code bypasses_armor}.
     */
    public static final TagKey<DamageType> BYPASSES_ARMOR_ONLY = create("bypasses_armor_only");

    private BLibDamageTypeTags() {
        throw new UnsupportedOperationException();
    }

    private static TagKey<DamageType> create(String path) {
        return BLib.MOD.resources().createTagKey(Registries.DAMAGE_TYPE, path);
    }
}
