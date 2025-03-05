package com.avp.data.tag;

import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricTagProvider;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.world.damagesource.DamageType;

import java.util.concurrent.CompletableFuture;

import com.avp.common.damage.AVPDamageTypes;

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
                AVPDamageTypes.RAZOR_WIRE
            );
    }
}
