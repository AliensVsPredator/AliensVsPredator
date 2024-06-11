package org.avp.fabric.common.data.tag;

import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricTagProvider;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.world.damagesource.DamageType;
import net.minecraft.world.damagesource.DamageTypes;

import java.util.concurrent.CompletableFuture;

import org.avp.common.game.damage.AVPDamageTypes;
import org.avp.common.data.tag.AVPDamageTypeTags;

public class AVPDamageTypeTagsProvider extends FabricTagProvider<DamageType> {

    public AVPDamageTypeTagsProvider(FabricDataOutput output, CompletableFuture<HolderLookup.Provider> registriesFuture) {
        super(output, Registries.DAMAGE_TYPE, registriesFuture);
    }

    @Override
    protected void addTags(HolderLookup.Provider provider) {
        getOrCreateTagBuilder(AVPDamageTypeTags.IS_BULLET_PROJECTILE)
            .add(AVPDamageTypes.INSTANCE.bullet.location());

        getOrCreateTagBuilder(DamageTypeTags.IS_PROJECTILE)
            .forceAddTag(AVPDamageTypeTags.IS_BULLET_PROJECTILE);

        getOrCreateTagBuilder(AVPDamageTypeTags.IS_PUNCTURING)
            .addOptionalTag(DamageTypeTags.IS_EXPLOSION)
            .addOptionalTag(DamageTypeTags.IS_PROJECTILE)
            .addOptionalTag(AVPDamageTypeTags.IS_BULLET_PROJECTILE)
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
