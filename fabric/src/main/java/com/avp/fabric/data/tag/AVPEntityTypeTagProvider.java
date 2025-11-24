package com.avp.fabric.data.tag;

import com.human.common.registry.init.entity_type.HumanEntityTypes;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricTagProvider;
import net.minecraft.core.HolderLookup;
import net.minecraft.tags.EntityTypeTags;
import net.minecraft.world.entity.EntityType;

import java.util.concurrent.CompletableFuture;

import com.avp.common.registry.tag.AVPEntityTypeTags;

public class AVPEntityTypeTagProvider extends FabricTagProvider.EntityTypeTagProvider {

    public AVPEntityTypeTagProvider(FabricDataOutput output, CompletableFuture<HolderLookup.Provider> completableFuture) {
        super(output, completableFuture);
    }

    @Override
    protected void addTags(HolderLookup.Provider wrapperLookup) {
        addHumanoids();
        addNetherCreatures();
        addRadiationResistant();
    }

    private void addHumanoids() {
        getOrCreateTagBuilder(AVPEntityTypeTags.HUMANOIDS)
            .addOptionalTag(EntityTypeTags.ILLAGER)
            .add(
                EntityType.PIGLIN,
                EntityType.PIGLIN_BRUTE,
                EntityType.PLAYER,
                EntityType.VILLAGER,
                EntityType.WANDERING_TRADER,
                EntityType.WITCH,
                HumanEntityTypes.MARINE.get()
            );
    }

    private void addNetherCreatures() {
        getOrCreateTagBuilder(AVPEntityTypeTags.NETHER_CREATURES)
            .add(
                EntityType.HOGLIN,
                EntityType.PIGLIN,
                EntityType.PIGLIN_BRUTE,
                EntityType.STRIDER
            );
    }

    private void addRadiationResistant() {
        getOrCreateTagBuilder(AVPEntityTypeTags.RADIATION_RESISTANT)
            .addOptionalTag(EntityTypeTags.UNDEAD)
            .add(EntityType.CREEPER);
    }
}
