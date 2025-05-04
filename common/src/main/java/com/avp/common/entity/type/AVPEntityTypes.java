package com.avp.common.entity.type;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.level.Level;

import java.util.function.BiFunction;

import com.avp.common.entity.AVPMobCategories;
import com.avp.common.entity.acid.Acid;
import com.avp.common.entity.living.alien.Alien;
import com.avp.common.entity.living.alien.chestburster.Chestburster;
import com.avp.common.entity.living.alien.ovamorph.Ovamorph;
import com.avp.common.entity.living.alien.parasite.facehugger.Facehugger;
import com.avp.common.entity.living.alien.xenomorph.drone.Drone;
import com.avp.common.entity.living.alien.xenomorph.praetorian.Praetorian;
import com.avp.common.entity.living.alien.xenomorph.queen.Queen;
import com.avp.common.entity.living.alien.xenomorph.warrior.Warrior;
import com.avp.common.entity.living.gene.GeneKeys;
import com.avp.common.entity.living.human.EyeColorGenerator;
import com.avp.common.entity.living.human.HairColorGenerator;
import com.avp.common.entity.living.human.SkinColorGenerator;
import com.avp.common.entity.living.human.marine.Marine;
import com.avp.common.entity.living.yautja.Yautja;
import com.avp.common.entity.machine.SentryTurret;
import com.avp.common.entity.nuke.MushroomCloudEntity;
import com.avp.common.entity.nuke.PrimedNuke;
import com.avp.common.entity.projectile.Flamethrow;
import com.avp.common.entity.projectile.Rocket;
import com.avp.common.entity.projectile.ShurikenItemEntity;
import com.avp.common.entity.projectile.SmartDiscItemEntity;
import com.avp.common.entity.projectile.ThrownGrenade;
import com.avp.common.registry.AVPDeferredHolder;
import com.avp.service.Services;

public class AVPEntityTypes {

    public static final MobCategory ALIEN_CATEGORY = AVPMobCategories.ALIENS;

    public static final MobCategory PREDATOR_CATEGORY = AVPMobCategories.PREDATOR;

    public static final AVPDeferredHolder<EntityType<Chestburster>> ABERRANT_CHESTBURSTER = register(
        "aberrant_chestburster",
        EntityType.Builder.<Chestburster>of(
            (type, level) -> aberrantFactory(Chestburster::new, type, level),
            ALIEN_CATEGORY
        )
            .sized(0.35f, 0.35f)
    );

    public static final AVPDeferredHolder<EntityType<Drone>> ABERRANT_DRONE = register(
        "aberrant_drone",
        EntityType.Builder.<Drone>of(
            (type, level) -> aberrantFactory(Drone::new, type, level),
            ALIEN_CATEGORY
        )
            .sized(0.8f, 1.98f)
    );

    public static final AVPDeferredHolder<EntityType<Facehugger>> ABERRANT_FACEHUGGER = register(
        "aberrant_facehugger",
        EntityType.Builder.<Facehugger>of(
            (type, level) -> aberrantFactory(Facehugger::new, type, level),
            ALIEN_CATEGORY
        )
            .sized(0.8f, 0.25f)
    );

    public static final AVPDeferredHolder<EntityType<Ovamorph>> ABERRANT_OVAMORPH = register(
        "aberrant_ovamorph",
        EntityType.Builder.<Ovamorph>of(
            (type, level) -> aberrantFactory(Ovamorph::new, type, level),
            ALIEN_CATEGORY
        )
            .sized(0.65f, 0.8f)
    );

    public static final AVPDeferredHolder<EntityType<Praetorian>> ABERRANT_PRAETORIAN = register(
        "aberrant_praetorian",
        EntityType.Builder.<Praetorian>of(
            (type, level) -> aberrantFactory(Praetorian::new, type, level),
            ALIEN_CATEGORY
        )
            .sized(0.98f, 3.98f)
    );

    public static final AVPDeferredHolder<EntityType<Warrior>> ABERRANT_WARRIOR = register(
        "aberrant_warrior",
        EntityType.Builder.<Warrior>of(
            (type, level) -> aberrantFactory(Warrior::new, type, level),
            ALIEN_CATEGORY
        )
            .sized(0.8f, 1.98f)
    );

    public static final AVPDeferredHolder<EntityType<Queen>> ABERRANT_QUEEN = register(
        "aberrant_queen",
        EntityType.Builder.<Queen>of(
            (type, level) -> aberrantFactory(Queen::new, type, level),
            ALIEN_CATEGORY
        )
            .sized(1.98f, 3.98f)
    );

    public static final AVPDeferredHolder<EntityType<Acid>> ACID = register(
        "acid",
        EntityType.Builder.of(Acid::new, MobCategory.MISC)
            .sized(0.66F, 0.05F)
    );

    public static final AVPDeferredHolder<EntityType<Chestburster>> CHESTBURSTER = register(
        "chestburster",
        EntityType.Builder.of(Chestburster::new, ALIEN_CATEGORY)
            .sized(0.35f, 0.35f)
    );

    public static final AVPDeferredHolder<EntityType<Drone>> DRONE = register(
        "drone",
        EntityType.Builder.of(Drone::new, ALIEN_CATEGORY)
            .sized(0.8f, 1.98f)
    );

    public static final AVPDeferredHolder<EntityType<Facehugger>> FACEHUGGER = register(
        "facehugger",
        EntityType.Builder.of(Facehugger::new, ALIEN_CATEGORY)
            .sized(0.8f, 0.25f)
    );

    public static final AVPDeferredHolder<EntityType<Flamethrow>> FLAMETHROW = register(
        "flamethrow",
        EntityType.Builder.<Flamethrow>of(Flamethrow::new, MobCategory.MISC)
            .sized(0.1F, 0.1F)
            .clientTrackingRange(8)
            .updateInterval(10)
    );

    public static final AVPDeferredHolder<EntityType<ThrownGrenade>> GRENADE_THROWN = register(
        "grenade_thrown",
        EntityType.Builder.<ThrownGrenade>of(ThrownGrenade::new, MobCategory.MISC)
            .sized(0.25F, 0.25F)
    );

    public static final AVPDeferredHolder<EntityType<Drone>> IRRADIATED_DRONE = register(
        "irradiated_drone",
        EntityType.Builder.<Drone>of(
            (type, level) -> irradiatedFactory(Drone::new, type, level),
            ALIEN_CATEGORY
        )
            .sized(0.8f, 1.98f)
    );

    public static final AVPDeferredHolder<EntityType<Praetorian>> IRRADIATED_PRAETORIAN = register(
        "irradiated_praetorian",
        EntityType.Builder.<Praetorian>of(
            (type, level) -> irradiatedFactory(Praetorian::new, type, level),
            ALIEN_CATEGORY
        )
            .sized(0.98f, 3.98f)
    );

    public static final AVPDeferredHolder<EntityType<Queen>> IRRADIATED_QUEEN = register(
        "irradiated_queen",
        EntityType.Builder.<Queen>of(
            (type, level) -> irradiatedFactory(Queen::new, type, level),
            ALIEN_CATEGORY
        )
            .sized(1.98f, 3.98f)
    );

    public static final AVPDeferredHolder<EntityType<Warrior>> IRRADIATED_WARRIOR = register(
        "irradiated_warrior",
        EntityType.Builder.<Warrior>of(
            (type, level) -> irradiatedFactory(Warrior::new, type, level),
            ALIEN_CATEGORY
        )
            .sized(0.8f, 1.98f)
    );

    public static final AVPDeferredHolder<EntityType<Marine>> MARINE = register(
        "marine",
        EntityType.Builder.<Marine>of((entityType, level) -> {
            var entity = new Marine(entityType, level);

            var random = entity.getRandom();
            entity.setMale(random.nextBoolean());
            var isMale = entity.isMale();

            if (isMale) {
                entity.setBeardVariant(random.nextInt(3));
            }

            entity.setEyeColor(EyeColorGenerator.random(random));
            entity.setHairColor(HairColorGenerator.random(random));
            entity.setHairVariant(random.nextInt(isMale ? 5 : 6));
            entity.setSkinColor(SkinColorGenerator.random(random));

            return entity;
        }, MobCategory.CREATURE).sized(0.7F, 1.95F)
    );

    public static final AVPDeferredHolder<EntityType<MushroomCloudEntity>> MUSHROOM_CLOUD = register(
        "mushroom_cloud",
        EntityType.Builder.of(MushroomCloudEntity::new, MobCategory.MISC)
    );

    public static final AVPDeferredHolder<EntityType<Chestburster>> NETHER_CHESTBURSTER = register(
        "nether_chestburster",
        EntityType.Builder.<Chestburster>of(
            (type, level) -> nethermorphFactory(Chestburster::new, type, level),
            ALIEN_CATEGORY
        )
            .sized(0.35f, 0.35f)
    );

    public static final AVPDeferredHolder<EntityType<Drone>> NETHER_DRONE = register(
        "nether_drone",
        EntityType.Builder.<Drone>of(
            (type, level) -> nethermorphFactory(Drone::new, type, level),
            ALIEN_CATEGORY
        )
            .sized(0.8f, 1.98f)
    );

    public static final AVPDeferredHolder<EntityType<Facehugger>> NETHER_FACEHUGGER = register(
        "nether_facehugger",
        EntityType.Builder.<Facehugger>of(
            (type, level) -> nethermorphFactory(Facehugger::new, type, level),
            ALIEN_CATEGORY
        )
            .sized(0.8f, 0.25f)
    );

    public static final AVPDeferredHolder<EntityType<Ovamorph>> NETHER_OVAMORPH = register(
        "nether_ovamorph",
        EntityType.Builder.<Ovamorph>of(
            (type, level) -> nethermorphFactory(Ovamorph::new, type, level),
            ALIEN_CATEGORY
        )
            .sized(0.65f, 0.8f)
    );

    public static final AVPDeferredHolder<EntityType<Praetorian>> NETHER_PRAETORIAN = register(
        "nether_praetorian",
        EntityType.Builder.<Praetorian>of(
            (type, level) -> nethermorphFactory(Praetorian::new, type, level),
            ALIEN_CATEGORY
        )
            .sized(0.98f, 3.98f)
    );

    public static final AVPDeferredHolder<EntityType<Warrior>> NETHER_WARRIOR = register(
        "nether_warrior",
        EntityType.Builder.<Warrior>of(
            (type, level) -> nethermorphFactory(Warrior::new, type, level),
            ALIEN_CATEGORY
        )
            .sized(0.8f, 1.98f)
    );

    public static final AVPDeferredHolder<EntityType<Queen>> NETHER_QUEEN = register(
        "nether_queen",
        EntityType.Builder.<Queen>of(
            (type, level) -> nethermorphFactory(Queen::new, type, level),
            ALIEN_CATEGORY
        )
            .sized(1.98f, 3.98f)
    );

    public static final AVPDeferredHolder<EntityType<PrimedNuke>> NUKE = register(
        "nuke",
        EntityType.Builder.<PrimedNuke>of(PrimedNuke::new, MobCategory.MISC)
            .sized(0.1F, 0.1F)
            .noSummon()
            .clientTrackingRange(100)
            .updateInterval(100)
    );

    public static final AVPDeferredHolder<EntityType<Ovamorph>> OVAMORPH = register(
        "ovamorph",
        EntityType.Builder.of(Ovamorph::new, ALIEN_CATEGORY)
            .sized(0.65f, 0.8f)
    );

    public static final AVPDeferredHolder<EntityType<Praetorian>> PRAETORIAN = register(
        "praetorian",
        EntityType.Builder.of(Praetorian::new, ALIEN_CATEGORY)
            .sized(0.98f, 3.98f)
    );

    public static final AVPDeferredHolder<EntityType<Queen>> QUEEN = register(
        "queen",
        EntityType.Builder.of(Queen::new, ALIEN_CATEGORY)
            .sized(1.98f, 3.98f)
    );

    public static final AVPDeferredHolder<EntityType<Rocket>> ROCKET = register(
        "rocket",
        EntityType.Builder.<Rocket>of(Rocket::new, MobCategory.MISC)
            .sized(0.1F, 0.1F)
            .clientTrackingRange(8)
            .updateInterval(10)
    );

    public static final AVPDeferredHolder<EntityType<Chestburster>> ROYAL_ABERRANT_CHESTBURSTER = register(
        "royal_aberrant_chestburster",
        EntityType.Builder.<Chestburster>of(
            (type, level) -> royalAberrantFactory(Chestburster::new, type, level),
            ALIEN_CATEGORY
        )
            .sized(0.35f, 0.35f)
    );

    public static final AVPDeferredHolder<EntityType<Facehugger>> ROYAL_ABERRANT_FACEHUGGER = register(
        "royal_aberrant_facehugger",
        EntityType.Builder.<Facehugger>of(
            (type, level) -> royalAberrantFactory(Facehugger::new, type, level),
            ALIEN_CATEGORY
        )
            .sized(0.8f, 0.25f)
    );

    public static final AVPDeferredHolder<EntityType<Ovamorph>> ROYAL_ABERRANT_OVAMORPH = register(
        "royal_aberrant_ovamorph",
        EntityType.Builder.<Ovamorph>of(
            (type, level) -> royalAberrantFactory(Ovamorph::new, type, level),
            ALIEN_CATEGORY
        )
            .sized(0.65f, 0.8f)
    );

    public static final AVPDeferredHolder<EntityType<Chestburster>> ROYAL_CHESTBURSTER = register(
        "royal_chestburster",
        EntityType.Builder.<Chestburster>of(
            (type, level) -> royalFactory(Chestburster::new, type, level),
            ALIEN_CATEGORY
        )
            .sized(0.35f, 0.35f)
    );

    public static final AVPDeferredHolder<EntityType<Facehugger>> ROYAL_FACEHUGGER = register(
        "royal_facehugger",
        EntityType.Builder.<Facehugger>of(
            (type, level) -> royalFactory(Facehugger::new, type, level),
            ALIEN_CATEGORY
        )
            .sized(0.8f, 0.25f)
    );

    public static final AVPDeferredHolder<EntityType<Ovamorph>> ROYAL_OVAMORPH = register(
        "royal_ovamorph",
        EntityType.Builder.<Ovamorph>of(
            (type, level) -> royalFactory(Ovamorph::new, type, level),
            ALIEN_CATEGORY
        )
            .sized(0.65f, 0.8f)
    );

    public static final AVPDeferredHolder<EntityType<Chestburster>> ROYAL_NETHER_CHESTBURSTER = register(
        "royal_nether_chestburster",
        EntityType.Builder.<Chestburster>of(
            (type, level) -> royalNethermorphFactory(Chestburster::new, type, level),
            ALIEN_CATEGORY
        )
            .sized(0.35f, 0.35f)
    );

    public static final AVPDeferredHolder<EntityType<Facehugger>> ROYAL_NETHER_FACEHUGGER = register(
        "royal_nether_facehugger",
        EntityType.Builder.<Facehugger>of(
            (type, level) -> royalNethermorphFactory(Facehugger::new, type, level),
            ALIEN_CATEGORY
        )
            .sized(0.8f, 0.25f)
    );

    public static final AVPDeferredHolder<EntityType<Ovamorph>> ROYAL_NETHER_OVAMORPH = register(
        "royal_nether_ovamorph",
        EntityType.Builder.<Ovamorph>of(
            (type, level) -> royalNethermorphFactory(Ovamorph::new, type, level),
            ALIEN_CATEGORY
        )
            .sized(0.65f, 0.8f)
    );

    public static final AVPDeferredHolder<EntityType<SentryTurret>> SENTRY_TURRET = register(
        "sentry_turret",
        EntityType.Builder.of(SentryTurret::new, MobCategory.MISC).sized(1.0F, 1.0F).noSummon()
    );

    public static final AVPDeferredHolder<EntityType<ShurikenItemEntity>> SHURIKEN = register(
        "shuriken",
        EntityType.Builder.<ShurikenItemEntity>of(ShurikenItemEntity::new, MobCategory.MISC)
            .sized(0.25F, 0.25F)
    );

    public static final AVPDeferredHolder<EntityType<SmartDiscItemEntity>> SMART_DISC = register(
        "smart_disc",
        EntityType.Builder.<SmartDiscItemEntity>of(SmartDiscItemEntity::new, MobCategory.MISC)
            .sized(0.25F, 0.25F)
    );

    public static final AVPDeferredHolder<EntityType<Warrior>> WARRIOR = register(
        "warrior",
        EntityType.Builder.of(Warrior::new, ALIEN_CATEGORY)
            .sized(0.8f, 1.98f)
    );

    public static final AVPDeferredHolder<EntityType<Yautja>> YAUTJA = register(
        "yautja",
        EntityType.Builder.of(Yautja::new, PREDATOR_CATEGORY)
            .sized(0.98f, 2.48f)
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

    private static <T extends Alien> T irradiatedFactory(
        BiFunction<EntityType<T>, Level, T> entityFactory,
        EntityType<T> entityType,
        Level level
    ) {
        var entity = entityFactory.apply(entityType, level);
        entity.setIrradiated(true);
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

    private static <T extends Entity> AVPDeferredHolder<EntityType<T>> register(String id, EntityType.Builder<T> builder) {
        return Services.REGISTRY.register(
            BuiltInRegistries.ENTITY_TYPE,
            id,
            () -> ((SilencedEntityTypeBuilder) builder).buildWithoutDataFixerCheck()
        );
    }

    public static void initialize() {
        Services.REGISTRY.registerEntityAttributes(ABERRANT_CHESTBURSTER, Chestburster::createChestbursterAttributes);
        Services.REGISTRY.registerEntityAttributes(ABERRANT_DRONE, Drone::createDroneAttributes);
        Services.REGISTRY.registerEntityAttributes(ABERRANT_FACEHUGGER, Facehugger::createFacehuggerAttributes);
        Services.REGISTRY.registerEntityAttributes(ABERRANT_OVAMORPH, Ovamorph::createOvamorphAttributes);
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
        Services.REGISTRY.registerEntityAttributes(MARINE, Marine::createMarineAttributes);
        Services.REGISTRY.registerEntityAttributes(NETHER_CHESTBURSTER, Chestburster::createChestbursterAttributes);
        Services.REGISTRY.registerEntityAttributes(NETHER_DRONE, Drone::createDroneAttributes);
        Services.REGISTRY.registerEntityAttributes(NETHER_FACEHUGGER, Facehugger::createFacehuggerAttributes);
        Services.REGISTRY.registerEntityAttributes(NETHER_OVAMORPH, Ovamorph::createOvamorphAttributes);
        Services.REGISTRY.registerEntityAttributes(NETHER_PRAETORIAN, Praetorian::createPraetorianAttributes);
        Services.REGISTRY.registerEntityAttributes(NETHER_QUEEN, Queen::createQueenAttributes);
        Services.REGISTRY.registerEntityAttributes(NETHER_WARRIOR, Warrior::createWarriorAttributes);
        Services.REGISTRY.registerEntityAttributes(OVAMORPH, Ovamorph::createOvamorphAttributes);
        Services.REGISTRY.registerEntityAttributes(PRAETORIAN, Praetorian::createPraetorianAttributes);
        Services.REGISTRY.registerEntityAttributes(QUEEN, Queen::createQueenAttributes);
        Services.REGISTRY.registerEntityAttributes(ROYAL_ABERRANT_CHESTBURSTER, Chestburster::createChestbursterAttributes);
        Services.REGISTRY.registerEntityAttributes(ROYAL_ABERRANT_FACEHUGGER, Facehugger::createFacehuggerAttributes);
        Services.REGISTRY.registerEntityAttributes(ROYAL_ABERRANT_OVAMORPH, Ovamorph::createOvamorphAttributes);
        Services.REGISTRY.registerEntityAttributes(ROYAL_CHESTBURSTER, Chestburster::createChestbursterAttributes);
        Services.REGISTRY.registerEntityAttributes(ROYAL_FACEHUGGER, Facehugger::createFacehuggerAttributes);
        Services.REGISTRY.registerEntityAttributes(ROYAL_NETHER_CHESTBURSTER, Chestburster::createChestbursterAttributes);
        Services.REGISTRY.registerEntityAttributes(ROYAL_NETHER_FACEHUGGER, Facehugger::createFacehuggerAttributes);
        Services.REGISTRY.registerEntityAttributes(ROYAL_NETHER_OVAMORPH, Ovamorph::createOvamorphAttributes);
        Services.REGISTRY.registerEntityAttributes(ROYAL_OVAMORPH, Ovamorph::createOvamorphAttributes);
        Services.REGISTRY.registerEntityAttributes(SENTRY_TURRET, SentryTurret::createSentryTurretAttributes);
        Services.REGISTRY.registerEntityAttributes(WARRIOR, Warrior::createWarriorAttributes);
        Services.REGISTRY.registerEntityAttributes(YAUTJA, Yautja::createYautjaAttributes);
    }
}
