package com.alien.common.registry.init;

import com.alien.common.gameplay.entity.acid.Acid;
import com.alien.common.gameplay.entity.living.alien.Alien;
import com.alien.common.gameplay.entity.living.alien.chestburster.Chestburster;
import com.alien.common.gameplay.entity.living.alien.ovomorph.Ovomorph;
import com.alien.common.gameplay.entity.living.alien.parasite.facehugger.Facehugger;
import com.alien.common.gameplay.entity.living.alien.xenomorph.drone.Drone;
import com.alien.common.gameplay.entity.living.alien.xenomorph.praetorian.Praetorian;
import com.alien.common.gameplay.entity.living.alien.xenomorph.prowler.Prowler;
import com.alien.common.gameplay.entity.living.alien.xenomorph.queen.Queen;
import com.alien.common.gameplay.entity.living.alien.xenomorph.runner.Runner;
import com.alien.common.gameplay.entity.living.alien.xenomorph.warrior.Warrior;
import com.lib.common.gameplay.gene.GeneKeys;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.level.Level;

import java.util.function.BiFunction;

import com.avp.common.registry.AVPDeferredHolder;
import com.avp.common.registry.init.entity_type.AVPEntityTypes;
import com.avp.service.Services;

public class AlienEntityTypes {

    public static final AVPDeferredHolder<EntityType<Chestburster>> ABERRANT_CHESTBURSTER = AVPEntityTypes.register(
        "aberrant_chestburster",
        EntityType.Builder.<Chestburster>of(
            (type, level) -> aberrantFactory(Chestburster::new, type, level),
            AVPEntityTypes.ALIEN_CATEGORY
        )
            .sized(0.35f, 0.35f)
    );

    public static final AVPDeferredHolder<EntityType<Drone>> ABERRANT_DRONE = AVPEntityTypes.register(
        "aberrant_drone",
        EntityType.Builder.<Drone>of(
            (type, level) -> aberrantFactory(Drone::new, type, level),
            AVPEntityTypes.ALIEN_CATEGORY
        )
            .sized(0.8f, 1.98f)
    );

    public static final AVPDeferredHolder<EntityType<Facehugger>> ABERRANT_FACEHUGGER = AVPEntityTypes.register(
        "aberrant_facehugger",
        EntityType.Builder.<Facehugger>of(
            (type, level) -> aberrantFactory(Facehugger::new, type, level),
            AVPEntityTypes.ALIEN_CATEGORY
        )
            .sized(0.8f, 0.25f)
    );

    public static final AVPDeferredHolder<EntityType<Ovomorph>> ABERRANT_OVOMORPH = AVPEntityTypes.register(
        // TODO: Change this to "ovomorph" with 0.2.0.
        "aberrant_ovamorph",
        EntityType.Builder.<Ovomorph>of(
            (type, level) -> aberrantFactory(Ovomorph::new, type, level),
            AVPEntityTypes.ALIEN_CATEGORY
        )
            .sized(0.65f, 0.8f)
    );

    public static final AVPDeferredHolder<EntityType<Praetorian>> ABERRANT_PRAETORIAN = AVPEntityTypes.register(
        "aberrant_praetorian",
        EntityType.Builder.<Praetorian>of(
            (type, level) -> aberrantFactory(Praetorian::new, type, level),
            AVPEntityTypes.ALIEN_CATEGORY
        )
            .sized(0.98f, 3.98f)
    );

    public static final AVPDeferredHolder<EntityType<Prowler>> ABERRANT_PROWLER = AVPEntityTypes.register(
        "aberrant_prowler",
        EntityType.Builder.of(Prowler::new, AVPEntityTypes.ALIEN_CATEGORY)
            .sized(0.8f, 0.98f)
    );

    public static final AVPDeferredHolder<EntityType<Queen>> ABERRANT_QUEEN = AVPEntityTypes.register(
        "aberrant_queen",
        EntityType.Builder.<Queen>of(
            (type, level) -> aberrantFactory(Queen::new, type, level),
            AVPEntityTypes.ALIEN_CATEGORY
        )
            .sized(1.98f, 3.98f)
    );

    public static final AVPDeferredHolder<EntityType<Runner>> ABERRANT_RUNNER = AVPEntityTypes.register(
        "aberrant_runner",
        EntityType.Builder.of(Runner::new, AVPEntityTypes.ALIEN_CATEGORY)
            .sized(0.8f, 0.98f)
    );

    public static final AVPDeferredHolder<EntityType<Warrior>> ABERRANT_WARRIOR = AVPEntityTypes.register(
        "aberrant_warrior",
        EntityType.Builder.<Warrior>of(
            (type, level) -> aberrantFactory(Warrior::new, type, level),
            AVPEntityTypes.ALIEN_CATEGORY
        )
            .sized(0.8f, 1.98f)
    );

    public static final AVPDeferredHolder<EntityType<Acid>> ACID = AVPEntityTypes.register(
        "acid",
        EntityType.Builder.of(Acid::new, MobCategory.MISC)
            .sized(0.66F, 0.05F)
    );

    public static final AVPDeferredHolder<EntityType<Chestburster>> CHESTBURSTER = AVPEntityTypes.register(
        "chestburster",
        EntityType.Builder.of(Chestburster::new, AVPEntityTypes.ALIEN_CATEGORY)
            .sized(0.35f, 0.35f)
    );

    public static final AVPDeferredHolder<EntityType<Drone>> DRONE = AVPEntityTypes.register(
        "drone",
        EntityType.Builder.of(Drone::new, AVPEntityTypes.ALIEN_CATEGORY)
            .sized(0.8f, 1.98f)
    );

    public static final AVPDeferredHolder<EntityType<Facehugger>> FACEHUGGER = AVPEntityTypes.register(
        "facehugger",
        EntityType.Builder.of(Facehugger::new, AVPEntityTypes.ALIEN_CATEGORY)
            .sized(0.8f, 0.25f)
    );

    public static final AVPDeferredHolder<EntityType<Drone>> IRRADIATED_DRONE = AVPEntityTypes.register(
        "irradiated_drone",
        EntityType.Builder.of(Drone::new, AVPEntityTypes.ALIEN_CATEGORY)
            .sized(0.8f, 1.98f)
    );

    public static final AVPDeferredHolder<EntityType<Praetorian>> IRRADIATED_PRAETORIAN = AVPEntityTypes.register(
        "irradiated_praetorian",
        EntityType.Builder.<Praetorian>of(Praetorian::new, AVPEntityTypes.ALIEN_CATEGORY)
            .sized(0.98f, 3.98f)
    );

    public static final AVPDeferredHolder<EntityType<Prowler>> IRRADIATED_PROWLER = AVPEntityTypes.register(
        "irradiated_prowler",
        EntityType.Builder.of(Prowler::new, AVPEntityTypes.ALIEN_CATEGORY)
            .sized(0.8f, 0.98f)
    );

    public static final AVPDeferredHolder<EntityType<Queen>> IRRADIATED_QUEEN = AVPEntityTypes.register(
        "irradiated_queen",
        EntityType.Builder.of(Queen::new, AVPEntityTypes.ALIEN_CATEGORY)
            .sized(1.98f, 3.98f)
    );

    public static final AVPDeferredHolder<EntityType<Runner>> IRRADIATED_RUNNER = AVPEntityTypes.register(
        "irradiated_runner",
        EntityType.Builder.of(Runner::new, AVPEntityTypes.ALIEN_CATEGORY)
            .sized(0.8f, 0.98f)
    );

    public static final AVPDeferredHolder<EntityType<Warrior>> IRRADIATED_WARRIOR = AVPEntityTypes.register(
        "irradiated_warrior",
        EntityType.Builder.of(Warrior::new, AVPEntityTypes.ALIEN_CATEGORY)
            .sized(0.8f, 1.98f)
    );

    public static final AVPDeferredHolder<EntityType<Chestburster>> NETHER_CHESTBURSTER = AVPEntityTypes.register(
        "nether_chestburster",
        EntityType.Builder.<Chestburster>of(
            (type, level) -> nethermorphFactory(Chestburster::new, type, level),
            AVPEntityTypes.ALIEN_CATEGORY
        )
            .sized(0.35f, 0.35f)
    );

    public static final AVPDeferredHolder<EntityType<Drone>> NETHER_DRONE = AVPEntityTypes.register(
        "nether_drone",
        EntityType.Builder.<Drone>of(
            (type, level) -> nethermorphFactory(Drone::new, type, level),
            AVPEntityTypes.ALIEN_CATEGORY
        )
            .sized(0.8f, 1.98f)
    );

    public static final AVPDeferredHolder<EntityType<Facehugger>> NETHER_FACEHUGGER = AVPEntityTypes.register(
        "nether_facehugger",
        EntityType.Builder.<Facehugger>of(
            (type, level) -> nethermorphFactory(Facehugger::new, type, level),
            AVPEntityTypes.ALIEN_CATEGORY
        )
            .sized(0.8f, 0.25f)
    );

    public static final AVPDeferredHolder<EntityType<Ovomorph>> NETHER_OVOMORPH = AVPEntityTypes.register(
        // TODO: Change this to "ovomorph" with 0.2.0.
        "nether_ovamorph",
        EntityType.Builder.<Ovomorph>of(
            (type, level) -> nethermorphFactory(Ovomorph::new, type, level),
            AVPEntityTypes.ALIEN_CATEGORY
        )
            .sized(0.65f, 0.8f)
    );

    public static final AVPDeferredHolder<EntityType<Praetorian>> NETHER_PRAETORIAN = AVPEntityTypes.register(
        "nether_praetorian",
        EntityType.Builder.<Praetorian>of(
            (type, level) -> nethermorphFactory(Praetorian::new, type, level),
            AVPEntityTypes.ALIEN_CATEGORY
        )
            .sized(0.98f, 3.98f)
    );

    public static final AVPDeferredHolder<EntityType<Prowler>> NETHER_PROWLER = AVPEntityTypes.register(
        "nether_prowler",
        EntityType.Builder.of(Prowler::new, AVPEntityTypes.ALIEN_CATEGORY)
            .sized(0.8f, 0.98f)
    );

    public static final AVPDeferredHolder<EntityType<Queen>> NETHER_QUEEN = AVPEntityTypes.register(
        "nether_queen",
        EntityType.Builder.<Queen>of(
            (type, level) -> nethermorphFactory(Queen::new, type, level),
            AVPEntityTypes.ALIEN_CATEGORY
        )
            .sized(1.98f, 3.98f)
    );

    public static final AVPDeferredHolder<EntityType<Runner>> NETHER_RUNNER = AVPEntityTypes.register(
        "nether_runner",
        EntityType.Builder.of(Runner::new, AVPEntityTypes.ALIEN_CATEGORY)
            .sized(0.8f, 0.98f)
    );

    public static final AVPDeferredHolder<EntityType<Warrior>> NETHER_WARRIOR = AVPEntityTypes.register(
        "nether_warrior",
        EntityType.Builder.<Warrior>of(
            (type, level) -> nethermorphFactory(Warrior::new, type, level),
            AVPEntityTypes.ALIEN_CATEGORY
        )
            .sized(0.8f, 1.98f)
    );

    public static final AVPDeferredHolder<EntityType<Ovomorph>> OVOMORPH = AVPEntityTypes.register(
        // TODO: Change this to "ovomorph" with 0.2.0.
        "ovamorph",
        EntityType.Builder.of(Ovomorph::new, AVPEntityTypes.ALIEN_CATEGORY)
            .sized(0.65f, 0.8f)
    );

    public static final AVPDeferredHolder<EntityType<Praetorian>> PRAETORIAN = AVPEntityTypes.register(
        "praetorian",
        EntityType.Builder.of(Praetorian::new, AVPEntityTypes.ALIEN_CATEGORY)
            .sized(0.98f, 3.98f)
    );

    public static final AVPDeferredHolder<EntityType<Prowler>> PROWLER = AVPEntityTypes.register(
        "prowler",
        EntityType.Builder.of(Prowler::new, AVPEntityTypes.ALIEN_CATEGORY)
            .sized(0.8f, 0.98f)
    );

    public static final AVPDeferredHolder<EntityType<Queen>> QUEEN = AVPEntityTypes.register(
        "queen",
        EntityType.Builder.of(Queen::new, AVPEntityTypes.ALIEN_CATEGORY)
            .sized(1.98f, 3.98f)
    );

    public static final AVPDeferredHolder<EntityType<Chestburster>> ROYAL_ABERRANT_CHESTBURSTER = AVPEntityTypes.register(
        "royal_aberrant_chestburster",
        EntityType.Builder.<Chestburster>of(
            (type, level) -> royalAberrantFactory(Chestburster::new, type, level),
            AVPEntityTypes.ALIEN_CATEGORY
        )
            .sized(0.35f, 0.35f)
    );

    public static final AVPDeferredHolder<EntityType<Facehugger>> ROYAL_ABERRANT_FACEHUGGER = AVPEntityTypes.register(
        "royal_aberrant_facehugger",
        EntityType.Builder.<Facehugger>of(
            (type, level) -> royalAberrantFactory(Facehugger::new, type, level),
            AVPEntityTypes.ALIEN_CATEGORY
        )
            .sized(0.8f, 0.25f)
    );

    public static final AVPDeferredHolder<EntityType<Ovomorph>> ROYAL_ABERRANT_OVOMORPH = AVPEntityTypes.register(
        // TODO: Change this to "ovomorph" with 0.2.0.
        "royal_aberrant_ovamorph",
        EntityType.Builder.<Ovomorph>of(
            (type, level) -> royalAberrantFactory(Ovomorph::new, type, level),
            AVPEntityTypes.ALIEN_CATEGORY
        )
            .sized(0.65f, 0.8f)
    );

    public static final AVPDeferredHolder<EntityType<Chestburster>> ROYAL_CHESTBURSTER = AVPEntityTypes.register(
        "royal_chestburster",
        EntityType.Builder.<Chestburster>of(
            (type, level) -> royalFactory(Chestburster::new, type, level),
            AVPEntityTypes.ALIEN_CATEGORY
        )
            .sized(0.35f, 0.35f)
    );

    public static final AVPDeferredHolder<EntityType<Facehugger>> ROYAL_FACEHUGGER = AVPEntityTypes.register(
        "royal_facehugger",
        EntityType.Builder.<Facehugger>of(
            (type, level) -> royalFactory(Facehugger::new, type, level),
            AVPEntityTypes.ALIEN_CATEGORY
        )
            .sized(0.8f, 0.25f)
    );

    public static final AVPDeferredHolder<EntityType<Ovomorph>> ROYAL_OVOMORPH = AVPEntityTypes.register(
        // TODO: Change this to "ovomorph" with 0.2.0.
        "royal_ovamorph",
        EntityType.Builder.<Ovomorph>of(
            (type, level) -> royalFactory(Ovomorph::new, type, level),
            AVPEntityTypes.ALIEN_CATEGORY
        )
            .sized(0.65f, 0.8f)
    );

    public static final AVPDeferredHolder<EntityType<Chestburster>> ROYAL_NETHER_CHESTBURSTER = AVPEntityTypes.register(
        "royal_nether_chestburster",
        EntityType.Builder.<Chestburster>of(
            (type, level) -> royalNethermorphFactory(Chestburster::new, type, level),
            AVPEntityTypes.ALIEN_CATEGORY
        )
            .sized(0.35f, 0.35f)
    );

    public static final AVPDeferredHolder<EntityType<Facehugger>> ROYAL_NETHER_FACEHUGGER = AVPEntityTypes.register(
        "royal_nether_facehugger",
        EntityType.Builder.<Facehugger>of(
            (type, level) -> royalNethermorphFactory(Facehugger::new, type, level),
            AVPEntityTypes.ALIEN_CATEGORY
        )
            .sized(0.8f, 0.25f)
    );

    public static final AVPDeferredHolder<EntityType<Ovomorph>> ROYAL_NETHER_OVOMORPH = AVPEntityTypes.register(
        // TODO: Change this to "ovomorph" with 0.2.0.
        "royal_nether_ovamorph",
        EntityType.Builder.<Ovomorph>of(
            (type, level) -> royalNethermorphFactory(Ovomorph::new, type, level),
            AVPEntityTypes.ALIEN_CATEGORY
        )
            .sized(0.65f, 0.8f)
    );

    public static final AVPDeferredHolder<EntityType<Runner>> RUNNER = AVPEntityTypes.register(
        "runner",
        EntityType.Builder.of(Runner::new, AVPEntityTypes.ALIEN_CATEGORY)
            .sized(0.8f, 0.98f)
    );

    public static final AVPDeferredHolder<EntityType<Warrior>> WARRIOR = AVPEntityTypes.register(
        "warrior",
        EntityType.Builder.of(Warrior::new, AVPEntityTypes.ALIEN_CATEGORY)
            .sized(0.8f, 1.98f)
    );

    private static <T extends Alien> T aberrantFactory(
        BiFunction<EntityType<T>, Level, T> entityFactory,
        EntityType<T> entityType,
        Level level
    ) {
        var entity = entityFactory.apply(entityType, level);
        entity.getGeneManager().minimize(GeneKeys.GENETIC_INTEGRITY);
        return entity;
    }

    private static <T extends Alien> T nethermorphFactory(
        BiFunction<EntityType<T>, Level, T> entityFactory,
        EntityType<T> entityType,
        Level level
    ) {
        var entity = entityFactory.apply(entityType, level);
        entity.getGeneManager().minimize(GeneKeys.COLD_RESISTANCE);
        entity.getGeneManager().maximize(GeneKeys.FIRE_RESISTANCE);
        return entity;
    }

    private static <T extends Alien> T royalFactory(
        BiFunction<EntityType<T>, Level, T> entityFactory,
        EntityType<T> entityType,
        Level level
    ) {
        var entity = entityFactory.apply(entityType, level);
        entity.setRoyal(true);
        return entity;
    }

    private static <T extends Alien> T royalAberrantFactory(
        BiFunction<EntityType<T>, Level, T> entityFactory,
        EntityType<T> entityType,
        Level level
    ) {
        var entity = entityFactory.apply(entityType, level);
        entity.setRoyal(true);
        entity.getGeneManager().minimize(GeneKeys.GENETIC_INTEGRITY);
        return entity;
    }

    private static <T extends Alien> T royalNethermorphFactory(
        BiFunction<EntityType<T>, Level, T> entityFactory,
        EntityType<T> entityType,
        Level level
    ) {
        var entity = entityFactory.apply(entityType, level);
        entity.setRoyal(true);
        entity.getGeneManager().minimize(GeneKeys.COLD_RESISTANCE);
        entity.getGeneManager().maximize(GeneKeys.FIRE_RESISTANCE);
        return entity;
    }

    public static void initialize() {
        Services.REGISTRY.registerEntityAttributes(ABERRANT_CHESTBURSTER, Chestburster::createChestbursterAttributes);
        Services.REGISTRY.registerEntityAttributes(ABERRANT_DRONE, Drone::createDroneAttributes);
        Services.REGISTRY.registerEntityAttributes(ABERRANT_FACEHUGGER, Facehugger::createFacehuggerAttributes);
        Services.REGISTRY.registerEntityAttributes(ABERRANT_OVOMORPH, Ovomorph::createOvomorphAttributes);
        Services.REGISTRY.registerEntityAttributes(ABERRANT_PRAETORIAN, Praetorian::createPraetorianAttributes);
        Services.REGISTRY.registerEntityAttributes(ABERRANT_PROWLER, Prowler::createProwlerAttributes);
        Services.REGISTRY.registerEntityAttributes(ABERRANT_QUEEN, Queen::createQueenAttributes);
        Services.REGISTRY.registerEntityAttributes(ABERRANT_RUNNER, Runner::createRunnerAttributes);
        Services.REGISTRY.registerEntityAttributes(ABERRANT_WARRIOR, Warrior::createWarriorAttributes);
        Services.REGISTRY.registerEntityAttributes(CHESTBURSTER, Chestburster::createChestbursterAttributes);
        Services.REGISTRY.registerEntityAttributes(DRONE, Drone::createDroneAttributes);
        Services.REGISTRY.registerEntityAttributes(FACEHUGGER, Facehugger::createFacehuggerAttributes);
        Services.REGISTRY.registerEntityAttributes(IRRADIATED_DRONE, Drone::createDroneAttributes);
        Services.REGISTRY.registerEntityAttributes(IRRADIATED_PRAETORIAN, Praetorian::createPraetorianAttributes);
        Services.REGISTRY.registerEntityAttributes(IRRADIATED_PROWLER, Prowler::createProwlerAttributes);
        Services.REGISTRY.registerEntityAttributes(IRRADIATED_QUEEN, Queen::createQueenAttributes);
        Services.REGISTRY.registerEntityAttributes(IRRADIATED_RUNNER, Runner::createRunnerAttributes);
        Services.REGISTRY.registerEntityAttributes(IRRADIATED_WARRIOR, Warrior::createWarriorAttributes);
        Services.REGISTRY.registerEntityAttributes(NETHER_CHESTBURSTER, Chestburster::createChestbursterAttributes);
        Services.REGISTRY.registerEntityAttributes(NETHER_DRONE, Drone::createDroneAttributes);
        Services.REGISTRY.registerEntityAttributes(NETHER_FACEHUGGER, Facehugger::createFacehuggerAttributes);
        Services.REGISTRY.registerEntityAttributes(NETHER_OVOMORPH, Ovomorph::createOvomorphAttributes);
        Services.REGISTRY.registerEntityAttributes(NETHER_PRAETORIAN, Praetorian::createPraetorianAttributes);
        Services.REGISTRY.registerEntityAttributes(NETHER_PROWLER, Prowler::createProwlerAttributes);
        Services.REGISTRY.registerEntityAttributes(NETHER_QUEEN, Queen::createQueenAttributes);
        Services.REGISTRY.registerEntityAttributes(NETHER_RUNNER, Runner::createRunnerAttributes);
        Services.REGISTRY.registerEntityAttributes(NETHER_WARRIOR, Warrior::createWarriorAttributes);
        Services.REGISTRY.registerEntityAttributes(OVOMORPH, Ovomorph::createOvomorphAttributes);
        Services.REGISTRY.registerEntityAttributes(PRAETORIAN, Praetorian::createPraetorianAttributes);
        Services.REGISTRY.registerEntityAttributes(PROWLER, Prowler::createProwlerAttributes);
        Services.REGISTRY.registerEntityAttributes(QUEEN, Queen::createQueenAttributes);
        Services.REGISTRY.registerEntityAttributes(ROYAL_ABERRANT_CHESTBURSTER, Chestburster::createChestbursterAttributes);
        Services.REGISTRY.registerEntityAttributes(ROYAL_ABERRANT_FACEHUGGER, Facehugger::createFacehuggerAttributes);
        Services.REGISTRY.registerEntityAttributes(ROYAL_ABERRANT_OVOMORPH, Ovomorph::createOvomorphAttributes);
        Services.REGISTRY.registerEntityAttributes(ROYAL_CHESTBURSTER, Chestburster::createChestbursterAttributes);
        Services.REGISTRY.registerEntityAttributes(ROYAL_FACEHUGGER, Facehugger::createFacehuggerAttributes);
        Services.REGISTRY.registerEntityAttributes(ROYAL_NETHER_CHESTBURSTER, Chestburster::createChestbursterAttributes);
        Services.REGISTRY.registerEntityAttributes(ROYAL_NETHER_FACEHUGGER, Facehugger::createFacehuggerAttributes);
        Services.REGISTRY.registerEntityAttributes(ROYAL_NETHER_OVOMORPH, Ovomorph::createOvomorphAttributes);
        Services.REGISTRY.registerEntityAttributes(ROYAL_OVOMORPH, Ovomorph::createOvomorphAttributes);
        Services.REGISTRY.registerEntityAttributes(RUNNER, Runner::createRunnerAttributes);
        Services.REGISTRY.registerEntityAttributes(WARRIOR, Warrior::createWarriorAttributes);
    }
}
