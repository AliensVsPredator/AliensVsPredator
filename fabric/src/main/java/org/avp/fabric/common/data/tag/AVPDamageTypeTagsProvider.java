package org.avp.fabric.common.data.tag;

import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricTagProvider;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.damagesource.DamageType;
import net.minecraft.world.damagesource.DamageTypes;
import org.avp.common.tag.AVPDamageTypeTags;

import java.util.concurrent.CompletableFuture;

public class AVPDamageTypeTagsProvider extends FabricTagProvider<DamageType> {

    public AVPDamageTypeTagsProvider(FabricDataOutput output, CompletableFuture<HolderLookup.Provider> registriesFuture) {
        super(output, Registries.DAMAGE_TYPE, registriesFuture);
    }

    @Override
    protected void addTags(HolderLookup.Provider provider) {
        getOrCreateTagBuilder(AVPDamageTypeTags.PUNCTURE)
            .add(DamageTypes.ARROW)
            .add(DamageTypes.CACTUS)
            .add(DamageTypes.EXPLOSION)
            .add(DamageTypes.FALLING_ANVIL)
            .add(DamageTypes.FALLING_BLOCK)
            .add(DamageTypes.FALLING_STALACTITE)
            .add(DamageTypes.MOB_PROJECTILE)
            .add(DamageTypes.STALAGMITE)
            .add(DamageTypes.THORNS)
            .add(DamageTypes.TRIDENT);
    }
}
