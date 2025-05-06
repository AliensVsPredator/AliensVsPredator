package com.avp.fabric.data.tag;

import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricTagProvider;
import net.minecraft.core.HolderLookup;
import net.minecraft.tags.EntityTypeTags;
import net.minecraft.world.entity.EntityType;

import java.util.concurrent.CompletableFuture;

import com.avp.common.entity.AVPEntityTypeTags;
import com.avp.common.entity.type.AVPEntityTypes;
import com.avp.fabric.data.compatibility.gigeresque.GigeresqueConstants;
import com.avp.fabric.data.compatibility.stellaris.StellarisConstants;

public class AVPEntityTypeTagProvider extends FabricTagProvider.EntityTypeTagProvider {

    public AVPEntityTypeTagProvider(FabricDataOutput output, CompletableFuture<HolderLookup.Provider> completableFuture) {
        super(output, completableFuture);
    }

    @Override
    protected void addTags(HolderLookup.Provider wrapperLookup) {
        addAberrantAliens();
        addAcidImmune();
        addAliens();
        addAnimals();
        addHatedByXenomorphs();
        addHiveAliens();
        addHosts();
        addHumanoids();
        addIrradiatedAliens();
        addNetherAliens();
        addNetherCreatures();
        addNormalAliens();
        addOvamorphs();
        addParasites();
        addPredators();
        addRadiationResistant();
        addRemovableVanillaSpawns();
        addRoyalAliens();
        addXenomorphs();

        addCompatibilityTags();
    }

    private void addHatedByXenomorphs() {
        getOrCreateTagBuilder(AVPEntityTypeTags.HATED_BY_XENOMORPHS)
            .addTag(AVPEntityTypeTags.PREDATORS)
            .add(EntityType.PLAYER)
            // TODO: Add a "humans" tag here that includes the marine.
            .add(AVPEntityTypes.MARINE.get());
    }

    private void addPredators() {
        getOrCreateTagBuilder(AVPEntityTypeTags.PREDATORS)
            .add(AVPEntityTypes.YAUTJA.get());
    }

    private void addRadiationResistant() {
        getOrCreateTagBuilder(AVPEntityTypeTags.RADIATION_RESISTANT)
            .addOptionalTag(EntityTypeTags.UNDEAD)
            .addTag(AVPEntityTypeTags.XENOMORPHS)
            .addTag(AVPEntityTypeTags.PREDATORS)
            .add(EntityType.CREEPER);
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
                AVPEntityTypes.MARINE.get()
            );
    }

    private void addAnimals() {
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
    }

    private void addXenomorphs() {
        getOrCreateTagBuilder(AVPEntityTypeTags.XENOMORPHS)
            .add(
                AVPEntityTypes.IRRADIATED_DRONE.get(),
                AVPEntityTypes.IRRADIATED_PRAETORIAN.get(),
                AVPEntityTypes.IRRADIATED_QUEEN.get(),
                AVPEntityTypes.IRRADIATED_WARRIOR.get(),
                AVPEntityTypes.ABERRANT_DRONE.get(),
                AVPEntityTypes.ABERRANT_PRAETORIAN.get(),
                AVPEntityTypes.ABERRANT_WARRIOR.get(),
                AVPEntityTypes.ABERRANT_QUEEN.get(),
                AVPEntityTypes.DRONE.get(),
                AVPEntityTypes.NETHER_DRONE.get(),
                AVPEntityTypes.NETHER_PRAETORIAN.get(),
                AVPEntityTypes.NETHER_WARRIOR.get(),
                AVPEntityTypes.NETHER_QUEEN.get(),
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
                AVPEntityTypes.FACEHUGGER.get(),
                AVPEntityTypes.NETHER_FACEHUGGER.get(),
                AVPEntityTypes.ROYAL_ABERRANT_FACEHUGGER.get(),
                AVPEntityTypes.ROYAL_FACEHUGGER.get(),
                AVPEntityTypes.ROYAL_NETHER_FACEHUGGER.get()
            );
    }

    private void addRoyalAliens() {
        getOrCreateTagBuilder(AVPEntityTypeTags.ROYAL_ALIENS)
            .add(
                AVPEntityTypes.ABERRANT_PRAETORIAN.get(),
                AVPEntityTypes.ABERRANT_QUEEN.get(),
                AVPEntityTypes.IRRADIATED_PRAETORIAN.get(),
                AVPEntityTypes.IRRADIATED_QUEEN.get(),
                AVPEntityTypes.NETHER_PRAETORIAN.get(),
                AVPEntityTypes.NETHER_QUEEN.get(),
                AVPEntityTypes.PRAETORIAN.get(),
                AVPEntityTypes.QUEEN.get(),
                AVPEntityTypes.ROYAL_ABERRANT_CHESTBURSTER.get(),
                AVPEntityTypes.ROYAL_ABERRANT_FACEHUGGER.get(),
                AVPEntityTypes.ROYAL_ABERRANT_OVAMORPH.get(),
                AVPEntityTypes.ROYAL_CHESTBURSTER.get(),
                AVPEntityTypes.ROYAL_FACEHUGGER.get(),
                AVPEntityTypes.ROYAL_NETHER_CHESTBURSTER.get(),
                AVPEntityTypes.ROYAL_NETHER_FACEHUGGER.get(),
                AVPEntityTypes.ROYAL_NETHER_OVAMORPH.get(),
                AVPEntityTypes.ROYAL_OVAMORPH.get()
            );
    }

    private void addHiveAliens() {
        getOrCreateTagBuilder(AVPEntityTypeTags.HIVE_ALIENS)
            .addTag(AVPEntityTypeTags.XENOMORPHS);
    }

    private void addIrradiatedAliens() {
        getOrCreateTagBuilder(AVPEntityTypeTags.IRRADIATED_ALIENS)
            .add(
                AVPEntityTypes.IRRADIATED_DRONE.get(),
                AVPEntityTypes.IRRADIATED_PRAETORIAN.get(),
                AVPEntityTypes.IRRADIATED_QUEEN.get(),
                AVPEntityTypes.IRRADIATED_WARRIOR.get()
            );
    }

    private void addAberrantAliens() {
        getOrCreateTagBuilder(AVPEntityTypeTags.ABERRANT_ALIENS)
            .add(
                AVPEntityTypes.ABERRANT_CHESTBURSTER.get(),
                AVPEntityTypes.ABERRANT_DRONE.get(),
                AVPEntityTypes.ABERRANT_FACEHUGGER.get(),
                AVPEntityTypes.ABERRANT_OVAMORPH.get(),
                AVPEntityTypes.ABERRANT_PRAETORIAN.get(),
                AVPEntityTypes.ABERRANT_QUEEN.get(),
                AVPEntityTypes.ABERRANT_WARRIOR.get(),
                AVPEntityTypes.ROYAL_ABERRANT_FACEHUGGER.get(),
                AVPEntityTypes.ROYAL_ABERRANT_CHESTBURSTER.get(),
                AVPEntityTypes.ROYAL_ABERRANT_OVAMORPH.get()
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
                AVPEntityTypes.NETHER_QUEEN.get(),
                AVPEntityTypes.NETHER_WARRIOR.get(),
                AVPEntityTypes.ROYAL_NETHER_CHESTBURSTER.get(),
                AVPEntityTypes.ROYAL_NETHER_OVAMORPH.get(),
                AVPEntityTypes.ROYAL_NETHER_FACEHUGGER.get()
            );
    }

    private void addNormalAliens() {
        getOrCreateTagBuilder(AVPEntityTypeTags.NORMAL_ALIENS)
            .add(
                AVPEntityTypes.CHESTBURSTER.get(),
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
            .addTag(AVPEntityTypeTags.IRRADIATED_ALIENS)
            .addTag(AVPEntityTypeTags.ABERRANT_ALIENS)
            .addTag(AVPEntityTypeTags.NORMAL_ALIENS)
            .addTag(AVPEntityTypeTags.NETHER_ALIENS)
            .addTag(AVPEntityTypeTags.ROYAL_ALIENS);
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
                EntityType.WOLF,
                AVPEntityTypes.MARINE.get(),
                AVPEntityTypes.YAUTJA.get()
            );
    }

    private void addOvamorphs() {
        getOrCreateTagBuilder(AVPEntityTypeTags.OVAMORPHS)
            .add(
                AVPEntityTypes.ABERRANT_OVAMORPH.get(),
                AVPEntityTypes.NETHER_OVAMORPH.get(),
                AVPEntityTypes.OVAMORPH.get(),
                AVPEntityTypes.ROYAL_ABERRANT_OVAMORPH.get(),
                AVPEntityTypes.ROYAL_NETHER_OVAMORPH.get(),
                AVPEntityTypes.ROYAL_OVAMORPH.get()
            );
    }

    private void addCompatibilityTags() {
        getOrCreateTagBuilder(AVPEntityTypeTags.ACID_IMMUNE)
            .addOptionalTag(GigeresqueConstants.ACID_RESISTANT_ENTITIES);

        getOrCreateTagBuilder(StellarisConstants.NO_OXYGEN_NEEDED)
            .setReplace(false)
            .addTag(AVPEntityTypeTags.ALIENS);
    }
}
