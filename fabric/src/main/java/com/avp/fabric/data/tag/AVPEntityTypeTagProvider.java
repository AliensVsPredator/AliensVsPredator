package com.avp.fabric.data.tag;

import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricTagProvider;
import net.minecraft.core.HolderLookup;
import net.minecraft.tags.EntityTypeTags;
import net.minecraft.world.entity.EntityType;

import java.util.concurrent.CompletableFuture;

import com.avp.core.common.entity.AVPEntityTypeTags;
import com.avp.core.common.entity.type.AVPEntityTypes;
import com.avp.fabric.data.compatibility.gigeresque.GigeresqueConstants;

public class AVPEntityTypeTagProvider extends FabricTagProvider.EntityTypeTagProvider {

    public AVPEntityTypeTagProvider(FabricDataOutput output, CompletableFuture<HolderLookup.Provider> completableFuture) {
        super(output, completableFuture);
    }

    @Override
    protected void addTags(HolderLookup.Provider wrapperLookup) {
        addHosts();
        addAberrantAliens();
        addNetherAliens();
        addNormalAliens();
        addAliens();
        addHiveAliens();
        addRoyalAliens();
        addParasites();
        addXenomorphs();
        addAcidImmune();
        addRemovableVanillaSpawns();

        getOrCreateTagBuilder(AVPEntityTypeTags.ANIMALS)
            .add(
                EntityType.CAMEL,
                EntityType.COW,
                EntityType.DONKEY,
                EntityType.FOX,
                EntityType.GOAT,
                EntityType.HORSE,
                EntityType.LLAMA,
                EntityType.MOOSHROOM,
                EntityType.MULE,
                EntityType.PANDA,
                EntityType.PIG,
                EntityType.POLAR_BEAR,
                EntityType.RAVAGER,
                EntityType.SHEEP,
                EntityType.SNIFFER,
                EntityType.TRADER_LLAMA,
                EntityType.WOLF
            );

        getOrCreateTagBuilder(AVPEntityTypeTags.HUMANOIDS)
            .addOptionalTag(EntityTypeTags.ILLAGER)
            .add(
                EntityType.PIGLIN,
                EntityType.PIGLIN_BRUTE,
                EntityType.PLAYER,
                EntityType.VILLAGER,
                EntityType.WANDERING_TRADER,
                EntityType.WITCH
            );

        getOrCreateTagBuilder(AVPEntityTypeTags.NETHER_CREATURES)
            .add(
                EntityType.HOGLIN,
                EntityType.PIGLIN,
                EntityType.PIGLIN_BRUTE,
                EntityType.STRIDER
            );

        addCompatibilityTags();
    }

    private void addXenomorphs() {
        getOrCreateTagBuilder(AVPEntityTypeTags.XENOMORPHS)
            .add(
                AVPEntityTypes.ABERRANT_DRONE.get(),
                AVPEntityTypes.ABERRANT_PRAETORIAN.get(),
                AVPEntityTypes.ABERRANT_WARRIOR.get(),
                AVPEntityTypes.DRONE.get(),
                AVPEntityTypes.NETHER_DRONE.get(),
                AVPEntityTypes.NETHER_PRAETORIAN.get(),
                AVPEntityTypes.NETHER_WARRIOR.get(),
                AVPEntityTypes.PRAETORIAN.get(),
                AVPEntityTypes.QUEEN.get(),
                AVPEntityTypes.WARRIOR.get()
            );
    }

    private void addRemovableVanillaSpawns() {
        getOrCreateTagBuilder(AVPEntityTypeTags.REMOVE_VANILLA_SPAWNS)
            .add(
                EntityType.CREEPER,
                EntityType.HUSK,
                EntityType.SKELETON,
                EntityType.SPIDER,
                EntityType.WITCH,
                EntityType.ZOMBIE,
                EntityType.ZOMBIE_VILLAGER
            );
    }

    private void addAcidImmune() {
        getOrCreateTagBuilder(AVPEntityTypeTags.ACID_IMMUNE)
            .addTag(AVPEntityTypeTags.ALIENS);
    }

    private void addParasites() {
        getOrCreateTagBuilder(AVPEntityTypeTags.PARASITES)
            .add(
                AVPEntityTypes.ABERRANT_FACEHUGGER.get(),
                AVPEntityTypes.NETHER_FACEHUGGER.get(),
                AVPEntityTypes.FACEHUGGER.get()
            );
    }

    private void addRoyalAliens() {
        getOrCreateTagBuilder(AVPEntityTypeTags.ROYAL_ALIENS)
            .add(
                AVPEntityTypes.ABERRANT_PRAETORIAN.get(),
                AVPEntityTypes.CHESTBURSTER_QUEEN.get(),
                AVPEntityTypes.NETHER_PRAETORIAN.get(),
                AVPEntityTypes.PRAETORIAN.get(),
                AVPEntityTypes.QUEEN.get()
            );
    }

    private void addHiveAliens() {
        getOrCreateTagBuilder(AVPEntityTypeTags.HIVE_ALIENS)
            .addTag(AVPEntityTypeTags.XENOMORPHS);
    }

    private void addAberrantAliens() {
        getOrCreateTagBuilder(AVPEntityTypeTags.ABERRANT_ALIENS)
            .add(
                AVPEntityTypes.ABERRANT_CHESTBURSTER.get(),
                AVPEntityTypes.ABERRANT_DRONE.get(),
                AVPEntityTypes.ABERRANT_FACEHUGGER.get(),
                AVPEntityTypes.ABERRANT_OVAMORPH.get(),
                AVPEntityTypes.ABERRANT_PRAETORIAN.get(),
                AVPEntityTypes.ABERRANT_WARRIOR.get()
            );
    }

    private void addNetherAliens() {
        getOrCreateTagBuilder(AVPEntityTypeTags.NETHER_ALIENS)
            .add(
                AVPEntityTypes.NETHER_CHESTBURSTER.get(),
                AVPEntityTypes.NETHER_DRONE.get(),
                AVPEntityTypes.NETHER_FACEHUGGER.get(),
                AVPEntityTypes.NETHER_OVAMORPH.get(),
                AVPEntityTypes.NETHER_PRAETORIAN.get(),
                AVPEntityTypes.NETHER_WARRIOR.get()
            );
    }

    private void addNormalAliens() {
        getOrCreateTagBuilder(AVPEntityTypeTags.NORMAL_ALIENS)
            .add(
                AVPEntityTypes.CHESTBURSTER.get(),
                AVPEntityTypes.CHESTBURSTER_QUEEN.get(),
                AVPEntityTypes.DRONE.get(),
                AVPEntityTypes.FACEHUGGER.get(),
                AVPEntityTypes.OVAMORPH.get(),
                AVPEntityTypes.PRAETORIAN.get(),
                AVPEntityTypes.QUEEN.get(),
                AVPEntityTypes.WARRIOR.get()
            );
    }

    private void addAliens() {
        getOrCreateTagBuilder(AVPEntityTypeTags.ALIENS)
            .addTag(AVPEntityTypeTags.ABERRANT_ALIENS)
            .addTag(AVPEntityTypeTags.NORMAL_ALIENS)
            .addTag(AVPEntityTypeTags.NETHER_ALIENS);
    }

    private void addHosts() {
        getOrCreateTagBuilder(AVPEntityTypeTags.HOSTS)
            .addOptionalTag(EntityTypeTags.ILLAGER)
            .add(
                EntityType.CAMEL,
                EntityType.COW,
                EntityType.DONKEY,
                EntityType.FOX,
                EntityType.GOAT,
                EntityType.HORSE,
                EntityType.LLAMA,
                EntityType.MOOSHROOM,
                EntityType.MULE,
                EntityType.PANDA,
                EntityType.PIG,
                EntityType.PIGLIN,
                EntityType.PIGLIN_BRUTE,
                EntityType.PLAYER,
                EntityType.POLAR_BEAR,
                EntityType.RAVAGER,
                EntityType.SHEEP,
                EntityType.SNIFFER,
                EntityType.TRADER_LLAMA,
                EntityType.VILLAGER,
                EntityType.WANDERING_TRADER,
                EntityType.WITCH,
                EntityType.WOLF
            );
    }

    private void addCompatibilityTags() {
        getOrCreateTagBuilder(AVPEntityTypeTags.ACID_IMMUNE)
            .addOptionalTag(GigeresqueConstants.ACID_RESISTANT_ENTITY_TYPE_TAG);
    }
}
