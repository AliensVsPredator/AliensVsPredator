package com.avp.fabric.data.tag;

import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricTagProvider;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.world.damagesource.DamageType;
import net.minecraft.world.damagesource.DamageTypes;

import java.util.concurrent.CompletableFuture;

import com.avp.fabric.common.damage.AVPDamageTypes;
import com.avp.common.damage.AVPDamageTypesTags;

public class AVPDamageTypeTagProvider extends FabricTagProvider<DamageType> {

    public AVPDamageTypeTagProvider(FabricDataOutput output, CompletableFuture<HolderLookup.Provider> registriesFuture) {
        super(output, Registries.DAMAGE_TYPE, registriesFuture);
    }

    @Override
    protected void addTags(HolderLookup.Provider wrapperLookup) {
        getOrCreateTagBuilder(DamageTypeTags.AVOIDS_GUARDIAN_THORNS)
            .add(
                AVPDamageTypes.BULLET,
                AVPDamageTypes.FLAMETHROW
            );

        getOrCreateTagBuilder(DamageTypeTags.BYPASSES_ARMOR)
            .add(
                AVPDamageTypes.RADIATION
            );

        getOrCreateTagBuilder(DamageTypeTags.BYPASSES_WOLF_ARMOR)
            .add(
                AVPDamageTypes.RADIATION
            );

        getOrCreateTagBuilder(DamageTypeTags.BYPASSES_ENCHANTMENTS)
            .add(
                AVPDamageTypes.RADIATION
            );

        getOrCreateTagBuilder(DamageTypeTags.BYPASSES_RESISTANCE)
            .add(
                AVPDamageTypes.RADIATION
            );

        getOrCreateTagBuilder(DamageTypeTags.IS_PROJECTILE)
            .add(
                AVPDamageTypes.BULLET,
                AVPDamageTypes.FLAMETHROW
            );

        getOrCreateTagBuilder(DamageTypeTags.IS_FIRE)
            .add(
                AVPDamageTypes.FLAMETHROW
            );

        getOrCreateTagBuilder(DamageTypeTags.NO_KNOCKBACK)
            .add(
                AVPDamageTypes.ACID,
                AVPDamageTypes.BULLET,
                AVPDamageTypes.FLAMETHROW,
                AVPDamageTypes.RADIATION,
                AVPDamageTypes.RAZOR_WIRE,
                AVPDamageTypes.SMOTHERING
            );

        // All missing damage types here were excluded on purpose. Yes, including lava. Lava melts machinery.
        getOrCreateTagBuilder(AVPDamageTypesTags.DOES_NOT_HURT_SENTRY_TURRETS)
            .add(
                DamageTypes.FREEZE,
                DamageTypes.CACTUS,
                DamageTypes.CAMPFIRE,
                DamageTypes.CRAMMING,
                DamageTypes.DROWN,
                DamageTypes.FALL,
                DamageTypes.HOT_FLOOR,
                DamageTypes.IN_FIRE,
                DamageTypes.IN_WALL,
                DamageTypes.FIREWORKS,
                DamageTypes.ON_FIRE,
                AVPDamageTypes.RADIATION,
                AVPDamageTypes.RAZOR_WIRE,
                DamageTypes.STARVE,
                DamageTypes.STING,
                DamageTypes.SWEET_BERRY_BUSH,
                DamageTypes.THORNS,
                DamageTypes.WIND_CHARGE,
                DamageTypes.WITHER,
                AVPDamageTypes.SMOTHERING
            );
    }
}
