package com.alien.common.registry.init;

import com.alien.common.gameplay.entity.acid.Acid;
import com.alien.common.gameplay.entity.living.alien.Alien;
import com.alien.common.gameplay.entity.living.alien.chestburster.Chestburster;
import com.alien.common.gameplay.entity.living.alien.ovomorph.Ovomorph;
import com.alien.common.gameplay.entity.living.alien.parasite.facehugger.Facehugger;
import com.alien.common.gameplay.entity.living.alien.xenomorph.drone.Drone;
import com.alien.common.gameplay.entity.living.alien.xenomorph.praetorian.Praetorian;
import com.alien.common.gameplay.entity.living.alien.xenomorph.queen.Queen;
import com.alien.common.gameplay.entity.living.alien.xenomorph.warrior.Warrior;
import com.lib.common.gameplay.gene.GeneKeys;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.level.Level;

import java.util.function.BiFunction;

import com.avp.common.registry.AVPDeferredHolder;
import com.avp.common.registry.init.entity_type.HumanEntityTypes;
import com.avp.service.Services;

public class AlienEntityTypes {

    public static final AVPDeferredHolder<EntityType<Chestburster>> ABERRANT_CHESTBURSTER = HumanEntityTypes.register(
        "aberrant_chestburster",
        EntityType.Builder.<Chestburster>of(
            (type, level) -> aberrantFactory(Chestburster::new, type, level),
            HumanEntityTypes.ALIEN_CATEGORY
        )
            .sized(0.35f, 0.35f)
    );

    public static final AVPDeferredHolder<EntityType<Drone>> ABERRANT_DRONE = HumanEntityTypes.register(
        "aberrant_drone",
        EntityType.Builder.<Drone>of(
            (type, level) -> aberrantFactory(Drone::new, type, level),
            HumanEntityTypes.ALIEN_CATEGORY
        )
            .sized(0.8f, 1.98f)
    );

    public static final AVPDeferredHolder<EntityType<Facehugger>> ABERRANT_FACEHUGGER = HumanEntityTypes.register(
        "aberrant_facehugger",
        EntityType.Builder.<Facehugger>of(
            (type, level) -> aberrantFactory(Facehugger::new, type, level),
            HumanEntityTypes.ALIEN_CATEGORY
        )
            .sized(0.8f, 0.25f)
    );

    public static final AVPDeferredHolder<EntityType<Ovomorph>> ABERRANT_OVOMORPH = HumanEntityTypes.register(
        // TODO: Change this to "ovomorph" with 0.2.0.
        "aberrant_ovamorph",
        EntityType.Builder.<Ovomorph>of(
            (type, level) -> aberrantFactory(Ovomorph::new, type, level),
            HumanEntityTypes.ALIEN_CATEGORY
        )
            .sized(0.65f, 0.8f)
    );

    public static final AVPDeferredHolder<EntityType<Praetorian>> ABERRANT_PRAETORIAN = HumanEntityTypes.register(
        "aberrant_praetorian",
        EntityType.Builder.<Praetorian>of(
            (type, level) -> aberrantFactory(Praetorian::new, type, level),
            HumanEntityTypes.ALIEN_CATEGORY
        )
            .sized(0.98f, 3.98f)
    );

    public static final AVPDeferredHolder<EntityType<Warrior>> ABERRANT_WARRIOR = HumanEntityTypes.register(
        "aberrant_warrior",
        EntityType.Builder.<Warrior>of(
            (type, level) -> aberrantFactory(Warrior::new, type, level),
            HumanEntityTypes.ALIEN_CATEGORY
        )
            .sized(0.8f, 1.98f)
    );

    public static final AVPDeferredHolder<EntityType<Queen>> ABERRANT_QUEEN = HumanEntityTypes.register(
        "aberrant_queen",
        EntityType.Builder.<Queen>of(
            (type, level) -> aberrantFactory(Queen::new, type, level),
            HumanEntityTypes.ALIEN_CATEGORY
        )
            .sized(1.98f, 3.98f)
    );

    public static final AVPDeferredHolder<EntityType<Acid>> ACID = HumanEntityTypes.register(
        "acid",
        EntityType.Builder.of(Acid::new, MobCategory.MISC)
            .sized(0.66F, 0.05F)
    );

    public static final AVPDeferredHolder<EntityType<Chestburster>> CHESTBURSTER = HumanEntityTypes.register(
        "chestburster",
        EntityType.Builder.of(Chestburster::new, HumanEntityTypes.ALIEN_CATEGORY)
            .sized(0.35f, 0.35f)
    );

    public static final AVPDeferredHolder<EntityType<Drone>> DRONE = HumanEntityTypes.register(
        "drone",
        EntityType.Builder.of(Drone::new, HumanEntityTypes.ALIEN_CATEGORY)
            .sized(0.8f, 1.98f)
    );

    public static final AVPDeferredHolder<EntityType<Facehugger>> FACEHUGGER = HumanEntityTypes.register(
        "facehugger",
        EntityType.Builder.of(Facehugger::new, HumanEntityTypes.ALIEN_CATEGORY)
            .sized(0.8f, 0.25f)
    );

    public static final AVPDeferredHolder<EntityType<Drone>> IRRADIATED_DRONE = HumanEntityTypes.register(
        "irradiated_drone",
        EntityType.Builder.of(Drone::new, HumanEntityTypes.ALIEN_CATEGORY)
            .sized(0.8f, 1.98f)
    );

    public static final AVPDeferredHolder<EntityType<Praetorian>> IRRADIATED_PRAETORIAN = HumanEntityTypes.register(
        "irradiated_praetorian",
        EntityType.Builder.<Praetorian>of(Praetorian::new, HumanEntityTypes.ALIEN_CATEGORY)
            .sized(0.98f, 3.98f)
    );

    public static final AVPDeferredHolder<EntityType<Queen>> IRRADIATED_QUEEN = HumanEntityTypes.register(
        "irradiated_queen",
        EntityType.Builder.of(Queen::new, HumanEntityTypes.ALIEN_CATEGORY)
            .sized(1.98f, 3.98f)
    );

    public static final AVPDeferredHolder<EntityType<Warrior>> IRRADIATED_WARRIOR = HumanEntityTypes.register(
        "irradiated_warrior",
        EntityType.Builder.of(Warrior::new, HumanEntityTypes.ALIEN_CATEGORY)
            .sized(0.8f, 1.98f)
    );

    public static final AVPDeferredHolder<EntityType<Chestburster>> NETHER_CHESTBURSTER = HumanEntityTypes.register(
        "nether_chestburster",
        EntityType.Builder.<Chestburster>of(
            (type, level) -> nethermorphFactory(Chestburster::new, type, level),
            HumanEntityTypes.ALIEN_CATEGORY
        )
            .sized(0.35f, 0.35f)
    );

    public static final AVPDeferredHolder<EntityType<Drone>> NETHER_DRONE = HumanEntityTypes.register(
        "nether_drone",
        EntityType.Builder.<Drone>of(
            (type, level) -> nethermorphFactory(Drone::new, type, level),
            HumanEntityTypes.ALIEN_CATEGORY
        )
            .sized(0.8f, 1.98f)
    );

    public static final AVPDeferredHolder<EntityType<Facehugger>> NETHER_FACEHUGGER = HumanEntityTypes.register(
        "nether_facehugger",
        EntityType.Builder.<Facehugger>of(
            (type, level) -> nethermorphFactory(Facehugger::new, type, level),
            HumanEntityTypes.ALIEN_CATEGORY
        )
            .sized(0.8f, 0.25f)
    );

    public static final AVPDeferredHolder<EntityType<Ovomorph>> NETHER_OVOMORPH = HumanEntityTypes.register(
        // TODO: Change this to "ovomorph" with 0.2.0.
        "nether_ovamorph",
        EntityType.Builder.<Ovomorph>of(
            (type, level) -> nethermorphFactory(Ovomorph::new, type, level),
            HumanEntityTypes.ALIEN_CATEGORY
        )
            .sized(0.65f, 0.8f)
    );

    public static final AVPDeferredHolder<EntityType<Praetorian>> NETHER_PRAETORIAN = HumanEntityTypes.register(
        "nether_praetorian",
        EntityType.Builder.<Praetorian>of(
            (type, level) -> nethermorphFactory(Praetorian::new, type, level),
            HumanEntityTypes.ALIEN_CATEGORY
        )
            .sized(0.98f, 3.98f)
    );

    public static final AVPDeferredHolder<EntityType<Warrior>> NETHER_WARRIOR = HumanEntityTypes.register(
        "nether_warrior",
        EntityType.Builder.<Warrior>of(
            (type, level) -> nethermorphFactory(Warrior::new, type, level),
            HumanEntityTypes.ALIEN_CATEGORY
        )
            .sized(0.8f, 1.98f)
    );

    public static final AVPDeferredHolder<EntityType<Queen>> NETHER_QUEEN = HumanEntityTypes.register(
        "nether_queen",
        EntityType.Builder.<Queen>of(
            (type, level) -> nethermorphFactory(Queen::new, type, level),
            HumanEntityTypes.ALIEN_CATEGORY
        )
            .sized(1.98f, 3.98f)
    );

    public static final AVPDeferredHolder<EntityType<Ovomorph>> OVOMORPH = HumanEntityTypes.register(
        // TODO: Change this to "ovomorph" with 0.2.0.
        "ovamorph",
        EntityType.Builder.of(Ovomorph::new, HumanEntityTypes.ALIEN_CATEGORY)
            .sized(0.65f, 0.8f)
    );

    public static final AVPDeferredHolder<EntityType<Praetorian>> PRAETORIAN = HumanEntityTypes.register(
        "praetorian",
        EntityType.Builder.of(Praetorian::new, HumanEntityTypes.ALIEN_CATEGORY)
            .sized(0.98f, 3.98f)
    );

    public static final AVPDeferredHolder<EntityType<Queen>> QUEEN = HumanEntityTypes.register(
        "queen",
        EntityType.Builder.of(Queen::new, HumanEntityTypes.ALIEN_CATEGORY)
            .sized(1.98f, 3.98f)
    );

    public static final AVPDeferredHolder<EntityType<Chestburster>> ROYAL_ABERRANT_CHESTBURSTER = HumanEntityTypes.register(
        "royal_aberrant_chestburster",
        EntityType.Builder.<Chestburster>of(
            (type, level) -> royalAberrantFactory(Chestburster::new, type, level),
            HumanEntityTypes.ALIEN_CATEGORY
        )
            .sized(0.35f, 0.35f)
    );

    public static final AVPDeferredHolder<EntityType<Facehugger>> ROYAL_ABERRANT_FACEHUGGER = HumanEntityTypes.register(
        "royal_aberrant_facehugger",
        EntityType.Builder.<Facehugger>of(
            (type, level) -> royalAberrantFactory(Facehugger::new, type, level),
            HumanEntityTypes.ALIEN_CATEGORY
        )
            .sized(0.8f, 0.25f)
    );

    public static final AVPDeferredHolder<EntityType<Ovomorph>> ROYAL_ABERRANT_OVOMORPH = HumanEntityTypes.register(
        // TODO: Change this to "ovomorph" with 0.2.0.
        "royal_aberrant_ovamorph",
        EntityType.Builder.<Ovomorph>of(
            (type, level) -> royalAberrantFactory(Ovomorph::new, type, level),
            HumanEntityTypes.ALIEN_CATEGORY
        )
            .sized(0.65f, 0.8f)
    );

    public static final AVPDeferredHolder<EntityType<Chestburster>> ROYAL_CHESTBURSTER = HumanEntityTypes.register(
        "royal_chestburster",
        EntityType.Builder.<Chestburster>of(
            (type, level) -> royalFactory(Chestburster::new, type, level),
            HumanEntityTypes.ALIEN_CATEGORY
        )
            .sized(0.35f, 0.35f)
    );

    public static final AVPDeferredHolder<EntityType<Facehugger>> ROYAL_FACEHUGGER = HumanEntityTypes.register(
        "royal_facehugger",
        EntityType.Builder.<Facehugger>of(
            (type, level) -> royalFactory(Facehugger::new, type, level),
            HumanEntityTypes.ALIEN_CATEGORY
        )
            .sized(0.8f, 0.25f)
    );

    public static final AVPDeferredHolder<EntityType<Ovomorph>> ROYAL_OVOMORPH = HumanEntityTypes.register(
        // TODO: Change this to "ovomorph" with 0.2.0.
        "royal_ovamorph",
        EntityType.Builder.<Ovomorph>of(
            (type, level) -> royalFactory(Ovomorph::new, type, level),
            HumanEntityTypes.ALIEN_CATEGORY
        )
            .sized(0.65f, 0.8f)
    );

    public static final AVPDeferredHolder<EntityType<Chestburster>> ROYAL_NETHER_CHESTBURSTER = HumanEntityTypes.register(
        "royal_nether_chestburster",
        EntityType.Builder.<Chestburster>of(
            (type, level) -> royalNethermorphFactory(Chestburster::new, type, level),
            HumanEntityTypes.ALIEN_CATEGORY
        )
            .sized(0.35f, 0.35f)
    );

    public static final AVPDeferredHolder<EntityType<Facehugger>> ROYAL_NETHER_FACEHUGGER = HumanEntityTypes.register(
        "royal_nether_facehugger",
        EntityType.Builder.<Facehugger>of(
            (type, level) -> royalNethermorphFactory(Facehugger::new, type, level),
            HumanEntityTypes.ALIEN_CATEGORY
        )
            .sized(0.8f, 0.25f)
    );

    public static final AVPDeferredHolder<EntityType<Ovomorph>> ROYAL_NETHER_OVOMORPH = HumanEntityTypes.register(
        // TODO: Change this to "ovomorph" with 0.2.0.
        "royal_nether_ovamorph",
        EntityType.Builder.<Ovomorph>of(
            (type, level) -> royalNethermorphFactory(Ovomorph::new, type, level),
            HumanEntityTypes.ALIEN_CATEGORY
        )
            .sized(0.65f, 0.8f)
    );

    public static final AVPDeferredHolder<EntityType<Warrior>> WARRIOR = HumanEntityTypes.register(
        "warrior",
        EntityType.Builder.of(Warrior::new, HumanEntityTypes.ALIEN_CATEGORY)
            .sized(0.8f, 1.98f)
    );

    private static <T extends Alien> T aberrantFactory(
        BiFunction<EntityType<T>, Level, T> entityFactory,
        EntityType<T> entityType,
        Level level
    ) {
        var entity = entityFactory.apply(entityType, level);
        entity.geneManager().minimize(GeneKeys.GENETIC_INTEGRITY);
        return entity;
    }

    private static <T extends Alien> T nethermorphFactory(
        BiFunction<EntityType<T>, Level, T> entityFactory,
        EntityType<T> entityType,
        Level level
    ) {
        var entity = entityFactory.apply(entityType, level);
        entity.geneManager().minimize(GeneKeys.COLD_RESISTANCE);
        entity.geneManager().maximize(GeneKeys.FIRE_RESISTANCE);
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
        entity.geneManager().minimize(GeneKeys.GENETIC_INTEGRITY);
        return entity;
    }

    private static <T extends Alien> T royalNethermorphFactory(
        BiFunction<EntityType<T>, Level, T> entityFactory,
        EntityType<T> entityType,
        Level level
    ) {
        var entity = entityFactory.apply(entityType, level);
        entity.setRoyal(true);
        entity.geneManager().minimize(GeneKeys.COLD_RESISTANCE);
        entity.geneManager().maximize(GeneKeys.FIRE_RESISTANCE);
        return entity;
    }

    public static void initialize() {
        Services.REGISTRY.registerEntityAttributes(ABERRANT_CHESTBURSTER, Chestburster::createChestbursterAttributes);
        Services.REGISTRY.registerEntityAttributes(ABERRANT_DRONE, Drone::createDroneAttributes);
        Services.REGISTRY.registerEntityAttributes(ABERRANT_FACEHUGGER, Facehugger::createFacehuggerAttributes);
        Services.REGISTRY.registerEntityAttributes(ABERRANT_OVOMORPH, Ovomorph::createOvomorphAttributes);
        Services.REGISTRY.registerEntityAttributes(ABERRANT_PRAETORIAN, Praetorian::createPraetorianAttributes);
        Services.REGISTRY.registerEntityAttributes(ABERRANT_QUEEN, Queen::createQueenAttributes);
        Services.REGISTRY.registerEntityAttributes(ABERRANT_WARRIOR, Warrior::createWarriorAttributes);
        Services.REGISTRY.registerEntityAttributes(CHESTBURSTER, Chestburster::createChestbursterAttributes);
        Services.REGISTRY.registerEntityAttributes(DRONE, Drone::createDroneAttributes);
        Services.REGISTRY.registerEntityAttributes(FACEHUGGER, Facehugger::createFacehuggerAttributes);
        Services.REGISTRY.registerEntityAttributes(IRRADIATED_DRONE, Drone::createDroneAttributes);
        Services.REGISTRY.registerEntityAttributes(IRRADIATED_PRAETORIAN, Praetorian::createPraetorianAttributes);
        Services.REGISTRY.registerEntityAttributes(IRRADIATED_QUEEN, Queen::createQueenAttributes);
        Services.REGISTRY.registerEntityAttributes(IRRADIATED_WARRIOR, Warrior::createWarriorAttributes);
        Services.REGISTRY.registerEntityAttributes(NETHER_CHESTBURSTER, Chestburster::createChestbursterAttributes);
        Services.REGISTRY.registerEntityAttributes(NETHER_DRONE, Drone::createDroneAttributes);
        Services.REGISTRY.registerEntityAttributes(NETHER_FACEHUGGER, Facehugger::createFacehuggerAttributes);
        Services.REGISTRY.registerEntityAttributes(NETHER_OVOMORPH, Ovomorph::createOvomorphAttributes);
        Services.REGISTRY.registerEntityAttributes(NETHER_PRAETORIAN, Praetorian::createPraetorianAttributes);
        Services.REGISTRY.registerEntityAttributes(NETHER_QUEEN, Queen::createQueenAttributes);
        Services.REGISTRY.registerEntityAttributes(NETHER_WARRIOR, Warrior::createWarriorAttributes);
        Services.REGISTRY.registerEntityAttributes(OVOMORPH, Ovomorph::createOvomorphAttributes);
        Services.REGISTRY.registerEntityAttributes(PRAETORIAN, Praetorian::createPraetorianAttributes);
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
        Services.REGISTRY.registerEntityAttributes(WARRIOR, Warrior::createWarriorAttributes);
    }
}
