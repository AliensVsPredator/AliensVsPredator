package com.avp.fabric.data.tag;

import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricTagProvider;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.world.damagesource.DamageType;
import net.minecraft.world.damagesource.DamageTypes;

import java.util.concurrent.CompletableFuture;

import com.avp.common.registry.key.AVPDamageTypeKeys;
import com.avp.common.registry.tag.AVPDamageTypesTags;

public class AVPDamageTypeTagProvider extends FabricTagProvider<DamageType> {

    public AVPDamageTypeTagProvider(FabricDataOutput output, CompletableFuture<HolderLookup.Provider> registriesFuture) {
        super(output, Registries.DAMAGE_TYPE, registriesFuture);
    }

    @Override
    protected void addTags(HolderLookup.Provider wrapperLookup) {
        getOrCreateTagBuilder(DamageTypeTags.AVOIDS_GUARDIAN_THORNS)
            .add(
                AVPDamageTypeKeys.BULLET,
                AVPDamageTypeKeys.FLAMETHROW
            );

        getOrCreateTagBuilder(DamageTypeTags.BYPASSES_ARMOR)
            .add(
                AVPDamageTypeKeys.CHESTBURSTING,
                AVPDamageTypeKeys.RADIATION
            );

        getOrCreateTagBuilder(DamageTypeTags.BYPASSES_ENCHANTMENTS)
            .add(
                AVPDamageTypeKeys.CHESTBURSTING,
                AVPDamageTypeKeys.RADIATION
            );

        getOrCreateTagBuilder(DamageTypeTags.BYPASSES_INVULNERABILITY)
            .add(
                AVPDamageTypeKeys.CHESTBURSTING
            );

        getOrCreateTagBuilder(DamageTypeTags.BYPASSES_RESISTANCE)
            .add(
                AVPDamageTypeKeys.CHESTBURSTING,
                AVPDamageTypeKeys.RADIATION
            );

        getOrCreateTagBuilder(DamageTypeTags.BYPASSES_SHIELD)
            .add(
                AVPDamageTypeKeys.CHESTBURSTING
            );

        getOrCreateTagBuilder(DamageTypeTags.BYPASSES_WOLF_ARMOR)
            .add(
                AVPDamageTypeKeys.CHESTBURSTING,
                AVPDamageTypeKeys.RADIATION
            );

        getOrCreateTagBuilder(DamageTypeTags.IS_PROJECTILE)
            .add(
                AVPDamageTypeKeys.BULLET,
                AVPDamageTypeKeys.FLAMETHROW
            );

        getOrCreateTagBuilder(DamageTypeTags.IS_FIRE)
            .add(
                AVPDamageTypeKeys.FLAMETHROW
            );

        getOrCreateTagBuilder(DamageTypeTags.NO_KNOCKBACK)
            .add(
                AVPDamageTypeKeys.ACID,
                AVPDamageTypeKeys.BULLET,
                AVPDamageTypeKeys.CHESTBURSTING,
                AVPDamageTypeKeys.FLAMETHROW,
                AVPDamageTypeKeys.RADIATION,
                AVPDamageTypeKeys.RAZOR_WIRE,
                AVPDamageTypeKeys.SMOTHERING
            );

        getOrCreateTagBuilder(AVPDamageTypesTags.DOES_NOT_HURT_ALIENS)
            .add(
                AVPDamageTypeKeys.ACID,
                DamageTypes.DROWN,
                DamageTypes.FREEZE,
                DamageTypes.IN_WALL
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
                AVPDamageTypeKeys.RADIATION,
                AVPDamageTypeKeys.RAZOR_WIRE,
                DamageTypes.STARVE,
                DamageTypes.STING,
                DamageTypes.SWEET_BERRY_BUSH,
                DamageTypes.THORNS,
                DamageTypes.WIND_CHARGE,
                DamageTypes.WITHER,
                AVPDamageTypeKeys.SMOTHERING
            );
    }
}
