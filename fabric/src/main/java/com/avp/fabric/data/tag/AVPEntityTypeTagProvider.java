package com.avp.fabric.data.tag;

import com.alien.common.registry.init.AlienEntityTypes;
import com.compat.gigeresque.common.registry.tag.GigEntityTags;
import com.human.common.registry.init.entity_type.HumanEntityTypes;
import com.predator.common.registry.init.PredatorEntityTypes;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricTagProvider;
import net.minecraft.core.HolderLookup;
import net.minecraft.tags.EntityTypeTags;
import net.minecraft.world.entity.EntityType;

import java.util.concurrent.CompletableFuture;

import com.avp.common.registry.tag.AVPEntityTypeTags;
import com.avp.fabric.data.compatibility.stellaris.StellarisConstants;

public class AVPEntityTypeTagProvider extends FabricTagProvider.EntityTypeTagProvider {

    public AVPEntityTypeTagProvider(FabricDataOutput output, CompletableFuture<HolderLookup.Provider> completableFuture) {
        super(output, completableFuture);
    }

    @Override
    protected void addTags(HolderLookup.Provider wrapperLookup) {
        addAberrantAliens();
        addAcidImmune();
        addAdolescents();
        addAliens();
        addAnswersXenomorphCriesForHelp();
        addChestbursters();
        addCrushers();
        addDrones();
        addFacehuggers();
        addHatedByXenomorphs();
        addHiveAliens();
        addHiveLayerSpawns();
        addRunnerHosts();
        addHosts();
        addHumanoids();
        addIrradiatedAliens();
        addNetherAliens();
        addNetherCreatures();
        addNormalAliens();
        addOvomorphs();
        addParasites();
        addPraetorians();
        addPredators();
        addProwlers();
        addQueens();
        addRadiationResistant();
        addRoyalAliens();
        addRoyalXenomorphs();
        addRunners();
        addSpitters();
        addWarriors();
        addXenomorphs();

        addCompatibilityTags();
    }

    private void addHatedByXenomorphs() {
        getOrCreateTagBuilder(AVPEntityTypeTags.HATED_BY_XENOMORPHS)
            .addTag(AVPEntityTypeTags.PREDATORS)
            .add(EntityType.PLAYER)
            // TODO: Add a "humans" tag here that includes the marine.
            .add(HumanEntityTypes.MARINE.get());
    }

    private void addPredators() {
        getOrCreateTagBuilder(AVPEntityTypeTags.PREDATORS)
            .add(PredatorEntityTypes.YAUTJA.get());
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
                HumanEntityTypes.MARINE.get()
            );
    }

    private void addDrones() {
        getOrCreateTagBuilder(AVPEntityTypeTags.DRONES)
            .add(
                AlienEntityTypes.ABERRANT_DRONE.get(),
                AlienEntityTypes.DRONE.get(),
                AlienEntityTypes.IRRADIATED_DRONE.get(),
                AlienEntityTypes.NETHER_DRONE.get()
            );
    }

    private void addProwlers() {
        getOrCreateTagBuilder(AVPEntityTypeTags.PROWLERS)
            .add(
                AlienEntityTypes.ABERRANT_PROWLER.get(),
                AlienEntityTypes.IRRADIATED_PROWLER.get(),
                AlienEntityTypes.NETHER_PROWLER.get(),
                AlienEntityTypes.PROWLER.get()
            );
    }

    private void addRunners() {
        getOrCreateTagBuilder(AVPEntityTypeTags.RUNNERS)
            .add(
                AlienEntityTypes.ABERRANT_RUNNER.get(),
                AlienEntityTypes.IRRADIATED_RUNNER.get(),
                AlienEntityTypes.NETHER_RUNNER.get(),
                AlienEntityTypes.RUNNER.get()
            );
    }

    private void addSpitters() {
        getOrCreateTagBuilder(AVPEntityTypeTags.SPITTERS)
            .add(
                AlienEntityTypes.ABERRANT_SPITTER.get(),
                AlienEntityTypes.NETHER_SPITTER.get(),
                AlienEntityTypes.SPITTER.get()
            );
    }

    private void addWarriors() {
        getOrCreateTagBuilder(AVPEntityTypeTags.WARRIORS)
            .add(
                AlienEntityTypes.ABERRANT_WARRIOR.get(),
                AlienEntityTypes.IRRADIATED_WARRIOR.get(),
                AlienEntityTypes.NETHER_WARRIOR.get(),
                AlienEntityTypes.WARRIOR.get()
            );
    }

    private void addXenomorphs() {
        getOrCreateTagBuilder(AVPEntityTypeTags.XENOMORPHS)
            .addTag(AVPEntityTypeTags.CRUSHERS)
            .addTag(AVPEntityTypeTags.DRONES)
            .addTag(AVPEntityTypeTags.PRAETORIANS)
            .addTag(AVPEntityTypeTags.PROWLERS)
            .addTag(AVPEntityTypeTags.QUEENS)
            .addTag(AVPEntityTypeTags.RUNNERS)
            .addTag(AVPEntityTypeTags.SPITTERS)
            .addTag(AVPEntityTypeTags.WARRIORS);
    }

    private void addAcidImmune() {
        getOrCreateTagBuilder(AVPEntityTypeTags.ACID_IMMUNE)
            .addTag(AVPEntityTypeTags.ALIENS);
    }

    private void addAdolescents() {
        getOrCreateTagBuilder(AVPEntityTypeTags.ADOLESCENTS)
            .add(
                AlienEntityTypes.ABERRANT_ADOLESCENT.get(),
                AlienEntityTypes.ADOLESCENT.get(),
                AlienEntityTypes.NETHER_ADOLESCENT.get(),
                AlienEntityTypes.ROYAL_ABERRANT_ADOLESCENT.get(),
                AlienEntityTypes.ROYAL_ADOLESCENT.get(),
                AlienEntityTypes.ROYAL_NETHER_ADOLESCENT.get()
            );
    }

    private void addAnswersXenomorphCriesForHelp() {
        getOrCreateTagBuilder(AVPEntityTypeTags.ANSWERS_XENOMORPH_CRIES_FOR_HELP)
            .addTag(AVPEntityTypeTags.DRONES)
            .addTag(AVPEntityTypeTags.PROWLERS)
            .addTag(AVPEntityTypeTags.RUNNERS)
            .addTag(AVPEntityTypeTags.SPITTERS)
            .addTag(AVPEntityTypeTags.WARRIORS);
    }

    private void addChestbursters() {
        getOrCreateTagBuilder(AVPEntityTypeTags.CHESTBURSTERS)
            .add(
                AlienEntityTypes.ABERRANT_CHESTBURSTER.get(),
                AlienEntityTypes.CHESTBURSTER.get(),
                AlienEntityTypes.NETHER_CHESTBURSTER.get(),
                AlienEntityTypes.ROYAL_ABERRANT_CHESTBURSTER.get(),
                AlienEntityTypes.ROYAL_CHESTBURSTER.get(),
                AlienEntityTypes.ROYAL_NETHER_CHESTBURSTER.get()
            );
    }

    private void addCrushers() {
        getOrCreateTagBuilder(AVPEntityTypeTags.CRUSHERS)
            .add(
                AlienEntityTypes.ABERRANT_CRUSHER.get(),
                AlienEntityTypes.CRUSHER.get(),
                AlienEntityTypes.IRRADIATED_CRUSHER.get(),
                AlienEntityTypes.NETHER_CRUSHER.get()
            );
    }

    private void addFacehuggers() {
        getOrCreateTagBuilder(AVPEntityTypeTags.FACEHUGGERS)
            .add(
                AlienEntityTypes.ABERRANT_FACEHUGGER.get(),
                AlienEntityTypes.FACEHUGGER.get(),
                AlienEntityTypes.NETHER_FACEHUGGER.get(),
                AlienEntityTypes.ROYAL_ABERRANT_FACEHUGGER.get(),
                AlienEntityTypes.ROYAL_FACEHUGGER.get(),
                AlienEntityTypes.ROYAL_NETHER_FACEHUGGER.get()
            );
    }

    private void addParasites() {
        getOrCreateTagBuilder(AVPEntityTypeTags.PARASITES)
            .addTag(AVPEntityTypeTags.FACEHUGGERS);
    }

    private void addPraetorians() {
        getOrCreateTagBuilder(AVPEntityTypeTags.PRAETORIANS)
            .add(
                AlienEntityTypes.ABERRANT_PRAETORIAN.get(),
                AlienEntityTypes.IRRADIATED_PRAETORIAN.get(),
                AlienEntityTypes.NETHER_PRAETORIAN.get(),
                AlienEntityTypes.PRAETORIAN.get()
            );
    }

    private void addQueens() {
        getOrCreateTagBuilder(AVPEntityTypeTags.QUEENS)
            .add(
                AlienEntityTypes.ABERRANT_QUEEN.get(),
                AlienEntityTypes.IRRADIATED_QUEEN.get(),
                AlienEntityTypes.NETHER_QUEEN.get(),
                AlienEntityTypes.QUEEN.get()
            );
    }

    private void addRoyalAliens() {
        getOrCreateTagBuilder(AVPEntityTypeTags.ROYAL_ALIENS)
            .addTag(AVPEntityTypeTags.ROYAL_XENOMORPHS)
            .add(
                AlienEntityTypes.ROYAL_ABERRANT_ADOLESCENT.get(),
                AlienEntityTypes.ROYAL_ABERRANT_CHESTBURSTER.get(),
                AlienEntityTypes.ROYAL_ABERRANT_FACEHUGGER.get(),
                AlienEntityTypes.ROYAL_ABERRANT_OVOMORPH.get(),
                AlienEntityTypes.ROYAL_ADOLESCENT.get(),
                AlienEntityTypes.ROYAL_CHESTBURSTER.get(),
                AlienEntityTypes.ROYAL_FACEHUGGER.get(),
                AlienEntityTypes.ROYAL_NETHER_ADOLESCENT.get(),
                AlienEntityTypes.ROYAL_NETHER_CHESTBURSTER.get(),
                AlienEntityTypes.ROYAL_NETHER_FACEHUGGER.get(),
                AlienEntityTypes.ROYAL_NETHER_OVOMORPH.get(),
                AlienEntityTypes.ROYAL_OVOMORPH.get()
            );
    }

    private void addRoyalXenomorphs() {
        getOrCreateTagBuilder(AVPEntityTypeTags.ROYAL_XENOMORPHS)
            .addTag(AVPEntityTypeTags.PRAETORIANS)
            .addTag(AVPEntityTypeTags.QUEENS);
    }

    private void addHiveAliens() {
        getOrCreateTagBuilder(AVPEntityTypeTags.HIVE_ALIENS)
            .addTag(AVPEntityTypeTags.XENOMORPHS);
    }

    private void addIrradiatedAliens() {
        getOrCreateTagBuilder(AVPEntityTypeTags.IRRADIATED_ALIENS)
            .add(
                AlienEntityTypes.IRRADIATED_CRUSHER.get(),
                AlienEntityTypes.IRRADIATED_DRONE.get(),
                AlienEntityTypes.IRRADIATED_PRAETORIAN.get(),
                AlienEntityTypes.IRRADIATED_PROWLER.get(),
                AlienEntityTypes.IRRADIATED_QUEEN.get(),
                AlienEntityTypes.IRRADIATED_RUNNER.get(),
                AlienEntityTypes.IRRADIATED_WARRIOR.get()
            );
    }

    private void addAberrantAliens() {
        getOrCreateTagBuilder(AVPEntityTypeTags.ABERRANT_ALIENS)
            .add(
                AlienEntityTypes.ABERRANT_ADOLESCENT.get(),
                AlienEntityTypes.ABERRANT_BOILER.get(),
                AlienEntityTypes.ABERRANT_CHESTBURSTER.get(),
                AlienEntityTypes.ABERRANT_CRUSHER.get(),
                AlienEntityTypes.ABERRANT_DRONE.get(),
                AlienEntityTypes.ABERRANT_FACEHUGGER.get(),
                AlienEntityTypes.ABERRANT_OVOMORPH.get(),
                AlienEntityTypes.ABERRANT_PRAETORIAN.get(),
                AlienEntityTypes.ABERRANT_PROWLER.get(),
                AlienEntityTypes.ABERRANT_QUEEN.get(),
                AlienEntityTypes.ABERRANT_RUNNER.get(),
                AlienEntityTypes.ABERRANT_SPITTER.get(),
                AlienEntityTypes.ABERRANT_WARRIOR.get(),
                AlienEntityTypes.ROYAL_ABERRANT_ADOLESCENT.get(),
                AlienEntityTypes.ROYAL_ABERRANT_CHESTBURSTER.get(),
                AlienEntityTypes.ROYAL_ABERRANT_FACEHUGGER.get(),
                AlienEntityTypes.ROYAL_ABERRANT_OVOMORPH.get()
            );
    }

    private void addNetherAliens() {
        getOrCreateTagBuilder(AVPEntityTypeTags.NETHER_ALIENS)
            .add(
                AlienEntityTypes.NETHER_ADOLESCENT.get(),
                AlienEntityTypes.NETHER_BOILER.get(),
                AlienEntityTypes.NETHER_CHESTBURSTER.get(),
                AlienEntityTypes.NETHER_CRUSHER.get(),
                AlienEntityTypes.NETHER_DRONE.get(),
                AlienEntityTypes.NETHER_FACEHUGGER.get(),
                AlienEntityTypes.NETHER_OVOMORPH.get(),
                AlienEntityTypes.NETHER_PRAETORIAN.get(),
                AlienEntityTypes.NETHER_PROWLER.get(),
                AlienEntityTypes.NETHER_QUEEN.get(),
                AlienEntityTypes.NETHER_RUNNER.get(),
                AlienEntityTypes.NETHER_SPITTER.get(),
                AlienEntityTypes.NETHER_WARRIOR.get(),
                AlienEntityTypes.ROYAL_NETHER_ADOLESCENT.get(),
                AlienEntityTypes.ROYAL_NETHER_CHESTBURSTER.get(),
                AlienEntityTypes.ROYAL_NETHER_OVOMORPH.get(),
                AlienEntityTypes.ROYAL_NETHER_FACEHUGGER.get()
            );
    }

    private void addNormalAliens() {
        getOrCreateTagBuilder(AVPEntityTypeTags.NORMAL_ALIENS)
            .add(
                AlienEntityTypes.ADOLESCENT.get(),
                AlienEntityTypes.BOILER.get(),
                AlienEntityTypes.CHESTBURSTER.get(),
                AlienEntityTypes.CRUSHER.get(),
                AlienEntityTypes.DRONE.get(),
                AlienEntityTypes.FACEHUGGER.get(),
                AlienEntityTypes.OVOMORPH.get(),
                AlienEntityTypes.PRAETORIAN.get(),
                AlienEntityTypes.PROWLER.get(),
                AlienEntityTypes.QUEEN.get(),
                AlienEntityTypes.ROYAL_ADOLESCENT.get(),
                AlienEntityTypes.ROYAL_CHESTBURSTER.get(),
                AlienEntityTypes.ROYAL_FACEHUGGER.get(),
                AlienEntityTypes.ROYAL_OVOMORPH.get(),
                AlienEntityTypes.RUNNER.get(),
                AlienEntityTypes.SPITTER.get(),
                AlienEntityTypes.WARRIOR.get()
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

    private void addRunnerHosts() {
        // NOTE: Llamas are deliberately excluded here.
        getOrCreateTagBuilder(AVPEntityTypeTags.RUNNER_HOSTS)
            .add(
                EntityType.CAMEL,
                EntityType.COW,
                EntityType.DONKEY,
                EntityType.FOX,
                EntityType.GOAT,
                EntityType.HORSE,
                EntityType.MOOSHROOM,
                EntityType.MULE,
                EntityType.PANDA,
                EntityType.PIG,
                EntityType.POLAR_BEAR,
                EntityType.RAVAGER,
                EntityType.SHEEP,
                EntityType.SNIFFER,
                EntityType.WOLF
            );
    }

    private void addHosts() {
        getOrCreateTagBuilder(AVPEntityTypeTags.HOSTS)
            .addTag(AVPEntityTypeTags.RUNNER_HOSTS)
            .addOptionalTag(EntityTypeTags.ILLAGER)
            .add(
                EntityType.LLAMA,
                EntityType.PIGLIN,
                EntityType.PIGLIN_BRUTE,
                EntityType.PLAYER,
                EntityType.TRADER_LLAMA,
                EntityType.VILLAGER,
                EntityType.WANDERING_TRADER,
                EntityType.WITCH,
                HumanEntityTypes.MARINE.get(),
                PredatorEntityTypes.YAUTJA.get()
            );
    }

    private void addOvomorphs() {
        getOrCreateTagBuilder(AVPEntityTypeTags.OVOMORPHS)
            .add(
                AlienEntityTypes.ABERRANT_OVOMORPH.get(),
                AlienEntityTypes.NETHER_OVOMORPH.get(),
                AlienEntityTypes.OVOMORPH.get(),
                AlienEntityTypes.ROYAL_ABERRANT_OVOMORPH.get(),
                AlienEntityTypes.ROYAL_NETHER_OVOMORPH.get(),
                AlienEntityTypes.ROYAL_OVOMORPH.get()
            );
    }

    private void addHiveLayerSpawns() {
        getOrCreateTagBuilder(AVPEntityTypeTags.SPAWNS_IN_HIVE_WARRIOR_LAYER)
            .addTag(AVPEntityTypeTags.PROWLERS)
            .addTag(AVPEntityTypeTags.SPITTERS)
            .addTag(AVPEntityTypeTags.WARRIORS);

        getOrCreateTagBuilder(AVPEntityTypeTags.SPAWNS_IN_HIVE_DRONE_LAYER)
            .addTag(AVPEntityTypeTags.SPAWNS_IN_HIVE_WARRIOR_LAYER)
            .addTag(AVPEntityTypeTags.ADOLESCENTS)
            .addTag(AVPEntityTypeTags.CHESTBURSTERS)
            .addTag(AVPEntityTypeTags.DRONES)
            .addTag(AVPEntityTypeTags.RUNNERS)
            .addTag(AVPEntityTypeTags.OVOMORPHS);

        getOrCreateTagBuilder(AVPEntityTypeTags.SPAWNS_IN_HIVE_PRAETORIAN_LAYER)
            .addTag(AVPEntityTypeTags.SPAWNS_IN_HIVE_DRONE_LAYER)
            .addTag(AVPEntityTypeTags.CRUSHERS)
            .addTag(AVPEntityTypeTags.PRAETORIANS);

        getOrCreateTagBuilder(AVPEntityTypeTags.SPAWNS_IN_HIVE_QUEEN_LAYER)
            .addTag(AVPEntityTypeTags.SPAWNS_IN_HIVE_PRAETORIAN_LAYER)
            .addTag(AVPEntityTypeTags.QUEENS);
    }

    private void addCompatibilityTags() {
        getOrCreateTagBuilder(AVPEntityTypeTags.ACID_IMMUNE)
            .addOptionalTag(GigEntityTags.ACID_RESISTANT);

        getOrCreateTagBuilder(StellarisConstants.NO_OXYGEN_NEEDED)
            .setReplace(false)
            .addTag(AVPEntityTypeTags.ALIENS);
    }
}
