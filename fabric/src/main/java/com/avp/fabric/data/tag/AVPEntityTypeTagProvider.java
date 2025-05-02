package com.avp.fabric.data.tag;

import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricTagProvider;
import net.minecraft.core.HolderLookup;
import net.minecraft.tags.EntityTypeTags;
import net.minecraft.world.entity.EntityType;

import java.util.concurrent.CompletableFuture;

import com.avp.common.entity.AVPEntityTypeTags;
import com.avp.common.entity.type.TempAVPEntityTypes;
import com.avp.fabric.data.compatibility.gigeresque.GigeresqueConstants;
import com.avp.fabric.data.compatibility.stellaris.StellarisConstants;

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
        addOvamorphs();
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
                TempAVPEntityTypes.MARINE.get()
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
            .add(TempAVPEntityTypes.YAUTJA.get());

        addCompatibilityTags();
    }

    private void addXenomorphs() {
        getOrCreateTagBuilder(AVPEntityTypeTags.XENOMORPHS)
            .add(
                TempAVPEntityTypes.IRRADIATED_DRONE.get(),
                TempAVPEntityTypes.IRRADIATED_PRAETORIAN.get(),
                TempAVPEntityTypes.IRRADIATED_QUEEN.get(),
                TempAVPEntityTypes.IRRADIATED_WARRIOR.get(),
                TempAVPEntityTypes.ABERRANT_DRONE.get(),
                TempAVPEntityTypes.ABERRANT_PRAETORIAN.get(),
                TempAVPEntityTypes.ABERRANT_WARRIOR.get(),
                TempAVPEntityTypes.ABERRANT_QUEEN.get(),
                TempAVPEntityTypes.DRONE.get(),
                TempAVPEntityTypes.NETHER_DRONE.get(),
                TempAVPEntityTypes.NETHER_PRAETORIAN.get(),
                TempAVPEntityTypes.NETHER_WARRIOR.get(),
                TempAVPEntityTypes.NETHER_QUEEN.get(),
                TempAVPEntityTypes.PRAETORIAN.get(),
                TempAVPEntityTypes.QUEEN.get(),
                TempAVPEntityTypes.WARRIOR.get()
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
                TempAVPEntityTypes.ABERRANT_FACEHUGGER.get(),
                TempAVPEntityTypes.FACEHUGGER.get(),
                TempAVPEntityTypes.NETHER_FACEHUGGER.get(),
                TempAVPEntityTypes.ROYAL_ABERRANT_FACEHUGGER.get(),
                TempAVPEntityTypes.ROYAL_FACEHUGGER.get(),
                TempAVPEntityTypes.ROYAL_NETHER_FACEHUGGER.get()
            );
    }

    private void addRoyalAliens() {
        getOrCreateTagBuilder(AVPEntityTypeTags.ROYAL_ALIENS)
            .add(
                TempAVPEntityTypes.ABERRANT_PRAETORIAN.get(),
                TempAVPEntityTypes.ABERRANT_QUEEN.get(),
                TempAVPEntityTypes.IRRADIATED_PRAETORIAN.get(),
                TempAVPEntityTypes.IRRADIATED_QUEEN.get(),
                TempAVPEntityTypes.NETHER_PRAETORIAN.get(),
                TempAVPEntityTypes.NETHER_QUEEN.get(),
                TempAVPEntityTypes.PRAETORIAN.get(),
                TempAVPEntityTypes.QUEEN.get(),
                TempAVPEntityTypes.ROYAL_ABERRANT_CHESTBURSTER.get(),
                TempAVPEntityTypes.ROYAL_ABERRANT_FACEHUGGER.get(),
                TempAVPEntityTypes.ROYAL_ABERRANT_OVAMORPH.get(),
                TempAVPEntityTypes.ROYAL_CHESTBURSTER.get(),
                TempAVPEntityTypes.ROYAL_FACEHUGGER.get(),
                TempAVPEntityTypes.ROYAL_NETHER_CHESTBURSTER.get(),
                TempAVPEntityTypes.ROYAL_NETHER_FACEHUGGER.get(),
                TempAVPEntityTypes.ROYAL_NETHER_OVAMORPH.get(),
                TempAVPEntityTypes.ROYAL_OVAMORPH.get()
            );
    }

    private void addHiveAliens() {
        getOrCreateTagBuilder(AVPEntityTypeTags.HIVE_ALIENS)
            .addTag(AVPEntityTypeTags.XENOMORPHS);
    }

    private void addIrradiatedAliens() {
        getOrCreateTagBuilder(AVPEntityTypeTags.IRRADIATED_ALIENS)
            .add(
                TempAVPEntityTypes.IRRADIATED_DRONE.get(),
                TempAVPEntityTypes.IRRADIATED_PRAETORIAN.get(),
                TempAVPEntityTypes.IRRADIATED_QUEEN.get(),
                TempAVPEntityTypes.IRRADIATED_WARRIOR.get()
            );
    }

    private void addAberrantAliens() {
        getOrCreateTagBuilder(AVPEntityTypeTags.ABERRANT_ALIENS)
            .add(
                TempAVPEntityTypes.ABERRANT_CHESTBURSTER.get(),
                TempAVPEntityTypes.ABERRANT_DRONE.get(),
                TempAVPEntityTypes.ABERRANT_FACEHUGGER.get(),
                TempAVPEntityTypes.ABERRANT_OVAMORPH.get(),
                TempAVPEntityTypes.ABERRANT_PRAETORIAN.get(),
                TempAVPEntityTypes.ABERRANT_QUEEN.get(),
                TempAVPEntityTypes.ABERRANT_WARRIOR.get(),
                TempAVPEntityTypes.ROYAL_ABERRANT_FACEHUGGER.get(),
                TempAVPEntityTypes.ROYAL_ABERRANT_CHESTBURSTER.get(),
                TempAVPEntityTypes.ROYAL_ABERRANT_OVAMORPH.get()
            );
    }

    private void addNetherAliens() {
        getOrCreateTagBuilder(AVPEntityTypeTags.NETHER_ALIENS)
            .add(
                TempAVPEntityTypes.NETHER_CHESTBURSTER.get(),
                TempAVPEntityTypes.NETHER_DRONE.get(),
                TempAVPEntityTypes.NETHER_FACEHUGGER.get(),
                TempAVPEntityTypes.NETHER_OVAMORPH.get(),
                TempAVPEntityTypes.NETHER_PRAETORIAN.get(),
                TempAVPEntityTypes.NETHER_QUEEN.get(),
                TempAVPEntityTypes.NETHER_WARRIOR.get(),
                TempAVPEntityTypes.ROYAL_NETHER_CHESTBURSTER.get(),
                TempAVPEntityTypes.ROYAL_NETHER_OVAMORPH.get(),
                TempAVPEntityTypes.ROYAL_NETHER_FACEHUGGER.get()
            );
    }

    private void addNormalAliens() {
        getOrCreateTagBuilder(AVPEntityTypeTags.NORMAL_ALIENS)
            .add(
                TempAVPEntityTypes.CHESTBURSTER.get(),
                TempAVPEntityTypes.DRONE.get(),
                TempAVPEntityTypes.FACEHUGGER.get(),
                TempAVPEntityTypes.OVAMORPH.get(),
                TempAVPEntityTypes.PRAETORIAN.get(),
                TempAVPEntityTypes.QUEEN.get(),
                TempAVPEntityTypes.WARRIOR.get()
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
                TempAVPEntityTypes.MARINE.get(),
                TempAVPEntityTypes.YAUTJA.get()
            );
    }

    private void addOvamorphs() {
        getOrCreateTagBuilder(AVPEntityTypeTags.OVAMORPHS)
            .add(
                TempAVPEntityTypes.ABERRANT_OVAMORPH.get(),
                TempAVPEntityTypes.NETHER_OVAMORPH.get(),
                TempAVPEntityTypes.OVAMORPH.get(),
                TempAVPEntityTypes.ROYAL_ABERRANT_OVAMORPH.get(),
                TempAVPEntityTypes.ROYAL_NETHER_OVAMORPH.get(),
                TempAVPEntityTypes.ROYAL_OVAMORPH.get()
            );
    }

    private void addCompatibilityTags() {
        getOrCreateTagBuilder(AVPEntityTypeTags.ACID_IMMUNE)
            .addOptionalTag(GigeresqueConstants.ACID_RESISTANT_ENTITY_TYPE_TAG);

        getOrCreateTagBuilder(StellarisConstants.NO_OXYGEN_NEEDED)
            .setReplace(false)
            .addTag(AVPEntityTypeTags.ALIENS);
    }
}
