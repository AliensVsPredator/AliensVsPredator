package com.avp.data.tag;

import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricTagProvider;
import net.minecraft.core.HolderLookup;
import net.minecraft.tags.EntityTypeTags;
import net.minecraft.world.entity.EntityType;

import java.util.concurrent.CompletableFuture;

import com.avp.common.entity.AVPEntityTypeTags;
import com.avp.common.entity.type.AVPEntityTypes;
import com.avp.data.compatibility.gigeresque.GigeresqueConstants;

public class AVPEntityTypeTagProvider extends FabricTagProvider.EntityTypeTagProvider {

    public AVPEntityTypeTagProvider(FabricDataOutput output, CompletableFuture<HolderLookup.Provider> completableFuture) {
        super(output, completableFuture);
    }

    @Override
    protected void addTags(HolderLookup.Provider wrapperLookup) {
        addHosts();
        addIrradiatedAliens();
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
                EntityType.WITCH,
                AVPEntityTypes.MARINE
            );

        getOrCreateTagBuilder(AVPEntityTypeTags.NETHER_CREATURES)
            .add(
                EntityType.HOGLIN,
                EntityType.PIGLIN,
                EntityType.PIGLIN_BRUTE,
                EntityType.STRIDER
            );

        getOrCreateTagBuilder(AVPEntityTypeTags.RADIATION_RESISTANT)
            .addOptionalTag(EntityTypeTags.UNDEAD)
            .addTag(AVPEntityTypeTags.XENOMORPHS)
            .addTag(AVPEntityTypeTags.PREDATORS)
            .add(EntityType.CREEPER);

        getOrCreateTagBuilder(AVPEntityTypeTags.PREDATORS)
            .add(AVPEntityTypes.YAUTJA);

        addCompatibilityTags();
    }

    private void addXenomorphs() {
        getOrCreateTagBuilder(AVPEntityTypeTags.XENOMORPHS)
            .add(
                AVPEntityTypes.ROYAL_CHESTBURSTER,
                AVPEntityTypes.ROYAL_OVAMORPH,
                AVPEntityTypes.ROYAL_FACEHUGGER,
                AVPEntityTypes.ROYAL_ABERRANT_FACEHUGGER,
                AVPEntityTypes.ROYAL_ABERRANT_CHESTBURSTER,
                AVPEntityTypes.ROYAL_ABERRANT_OVAMORPH,
                AVPEntityTypes.ROYAL_NETHER_CHESTBURSTER,
                AVPEntityTypes.ROYAL_NETHER_OVAMORPH,
                AVPEntityTypes.ROYAL_NETHER_FACEHUGGER,
                AVPEntityTypes.IRRADIATED_DRONE,
                AVPEntityTypes.IRRADIATED_PRAETORIAN,
                AVPEntityTypes.IRRADIATED_WARRIOR,
                AVPEntityTypes.ABERRANT_DRONE,
                AVPEntityTypes.ABERRANT_PRAETORIAN,
                AVPEntityTypes.ABERRANT_WARRIOR,
                AVPEntityTypes.ABERRANT_QUEEN,
                AVPEntityTypes.DRONE,
                AVPEntityTypes.NETHER_DRONE,
                AVPEntityTypes.NETHER_PRAETORIAN,
                AVPEntityTypes.NETHER_WARRIOR,
                AVPEntityTypes.NETHER_QUEEN,
                AVPEntityTypes.PRAETORIAN,
                AVPEntityTypes.QUEEN,
                AVPEntityTypes.WARRIOR
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
                AVPEntityTypes.ROYAL_FACEHUGGER,
                AVPEntityTypes.ROYAL_ABERRANT_FACEHUGGER,
                AVPEntityTypes.ROYAL_NETHER_FACEHUGGER,
                AVPEntityTypes.ABERRANT_FACEHUGGER,
                AVPEntityTypes.NETHER_FACEHUGGER,
                AVPEntityTypes.FACEHUGGER
            );
    }

    private void addRoyalAliens() {
        getOrCreateTagBuilder(AVPEntityTypeTags.ROYAL_ALIENS)
            .add(
                AVPEntityTypes.ROYAL_CHESTBURSTER,
                AVPEntityTypes.ROYAL_OVAMORPH,
                AVPEntityTypes.ROYAL_FACEHUGGER,
                AVPEntityTypes.ROYAL_ABERRANT_FACEHUGGER,
                AVPEntityTypes.ROYAL_ABERRANT_CHESTBURSTER,
                AVPEntityTypes.ROYAL_ABERRANT_OVAMORPH,
                AVPEntityTypes.ROYAL_NETHER_CHESTBURSTER,
                AVPEntityTypes.ROYAL_NETHER_OVAMORPH,
                AVPEntityTypes.ROYAL_NETHER_FACEHUGGER,
                AVPEntityTypes.IRRADIATED_DRONE,
                AVPEntityTypes.ABERRANT_PRAETORIAN,
                AVPEntityTypes.CHESTBURSTER_QUEEN,
                AVPEntityTypes.NETHER_PRAETORIAN,
                AVPEntityTypes.PRAETORIAN,
                AVPEntityTypes.QUEEN,
                AVPEntityTypes.ABERRANT_QUEEN,
                AVPEntityTypes.NETHER_QUEEN
            );
    }

    private void addHiveAliens() {
        getOrCreateTagBuilder(AVPEntityTypeTags.HIVE_ALIENS)
            .addTag(AVPEntityTypeTags.XENOMORPHS);
    }

    private void addIrradiatedAliens() {
        getOrCreateTagBuilder(AVPEntityTypeTags.IRRADIATED_ALIENS)
            .add(
                AVPEntityTypes.IRRADIATED_DRONE,
                AVPEntityTypes.IRRADIATED_WARRIOR,
                AVPEntityTypes.IRRADIATED_PRAETORIAN
            );
    }

    private void addAberrantAliens() {
        getOrCreateTagBuilder(AVPEntityTypeTags.ABERRANT_ALIENS)
            .add(
                AVPEntityTypes.ROYAL_ABERRANT_FACEHUGGER,
                AVPEntityTypes.ROYAL_ABERRANT_CHESTBURSTER,
                AVPEntityTypes.ROYAL_ABERRANT_OVAMORPH,
                AVPEntityTypes.ABERRANT_CHESTBURSTER,
                AVPEntityTypes.ABERRANT_DRONE,
                AVPEntityTypes.ABERRANT_FACEHUGGER,
                AVPEntityTypes.ABERRANT_OVAMORPH,
                AVPEntityTypes.ABERRANT_PRAETORIAN,
                AVPEntityTypes.ABERRANT_WARRIOR,
                AVPEntityTypes.ABERRANT_QUEEN
            );
    }

    private void addNetherAliens() {
        getOrCreateTagBuilder(AVPEntityTypeTags.NETHER_ALIENS)
            .add(
                AVPEntityTypes.ROYAL_NETHER_CHESTBURSTER,
                AVPEntityTypes.ROYAL_NETHER_OVAMORPH,
                AVPEntityTypes.ROYAL_NETHER_FACEHUGGER,
                AVPEntityTypes.NETHER_CHESTBURSTER,
                AVPEntityTypes.NETHER_DRONE,
                AVPEntityTypes.NETHER_FACEHUGGER,
                AVPEntityTypes.NETHER_OVAMORPH,
                AVPEntityTypes.NETHER_PRAETORIAN,
                AVPEntityTypes.NETHER_WARRIOR,
                AVPEntityTypes.NETHER_QUEEN
            );
    }

    private void addNormalAliens() {
        getOrCreateTagBuilder(AVPEntityTypeTags.NORMAL_ALIENS)
            .add(
                AVPEntityTypes.CHESTBURSTER,
                AVPEntityTypes.CHESTBURSTER_QUEEN,
                AVPEntityTypes.DRONE,
                AVPEntityTypes.FACEHUGGER,
                AVPEntityTypes.OVAMORPH,
                AVPEntityTypes.PRAETORIAN,
                AVPEntityTypes.QUEEN,
                AVPEntityTypes.WARRIOR
            );
    }

    private void addAliens() {
        getOrCreateTagBuilder(AVPEntityTypeTags.ALIENS)
            .addTag(AVPEntityTypeTags.IRRADIATED_ALIENS)
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
                EntityType.WOLF,
                AVPEntityTypes.MARINE,
                AVPEntityTypes.YAUTJA
            );
    }

    private void addCompatibilityTags() {
        getOrCreateTagBuilder(AVPEntityTypeTags.ACID_IMMUNE)
            .addOptionalTag(GigeresqueConstants.ACID_RESISTANT_ENTITY_TYPE_TAG);
    }
}
