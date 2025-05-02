package com.avp.fabric.common.entity.type;

import net.fabricmc.fabric.api.object.builder.v1.entity.FabricDefaultAttributeRegistry;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.level.Level;

import java.util.function.BiFunction;

import com.avp.AVPResources;
import com.avp.common.entity.AVPMobCategories;
import com.avp.common.entity.gene.GeneKeys;
import com.avp.common.entity.type.SilencedEntityTypeBuilder;
import com.avp.fabric.common.entity.acid.Acid;
import com.avp.fabric.common.entity.living.alien.Alien;
import com.avp.fabric.common.entity.living.alien.chestburster.Chestburster;
import com.avp.fabric.common.entity.living.alien.ovamorph.Ovamorph;
import com.avp.fabric.common.entity.living.alien.parasite.facehugger.Facehugger;
import com.avp.fabric.common.entity.living.alien.xenomorph.drone.Drone;
import com.avp.fabric.common.entity.living.alien.xenomorph.praetorian.Praetorian;
import com.avp.fabric.common.entity.living.alien.xenomorph.queen.Queen;
import com.avp.fabric.common.entity.living.alien.xenomorph.warrior.Warrior;
import com.avp.fabric.common.entity.living.human.EyeColorGenerator;
import com.avp.fabric.common.entity.living.human.HairColorGenerator;
import com.avp.fabric.common.entity.living.human.SkinColorGenerator;
import com.avp.fabric.common.entity.living.human.marine.Marine;
import com.avp.fabric.common.entity.living.yautja.Yautja;
import com.avp.fabric.common.entity.projectile.BulletProjectile;

public class AVPEntityTypes {

    public static final MobCategory ALIEN_CATEGORY = AVPMobCategories.ALIENS;

    public static final MobCategory PREDATOR_CATEGORY = AVPMobCategories.PREDATOR;

    public static final EntityType<Acid> ACID = register(
        "acid",
        EntityType.Builder.of(Acid::new, MobCategory.MISC)
            .sized(0.66F, 0.05F)
    );

    public static final EntityType<Chestburster> CHESTBURSTER = register(
        "chestburster",
        EntityType.Builder.of(Chestburster::new, ALIEN_CATEGORY)
            .sized(0.35f, 0.35f)
    );

    public static final EntityType<Drone> DRONE = register(
        "drone",
        EntityType.Builder.of(Drone::new, ALIEN_CATEGORY)
            .sized(0.8f, 1.98f)
    );

    public static final EntityType<Facehugger> FACEHUGGER = register(
        "facehugger",
        EntityType.Builder.of(Facehugger::new, ALIEN_CATEGORY)
            .sized(0.8f, 0.25f)
    );

    public static final EntityType<BulletProjectile> BULLET = register(
        "bullet",
        EntityType.Builder.<BulletProjectile>of(BulletProjectile::new, MobCategory.MISC)
            .sized(0.25F, 0.25F)
    );

    public static final EntityType<Ovamorph> OVAMORPH = register(
        "ovamorph",
        EntityType.Builder.of(Ovamorph::new, ALIEN_CATEGORY)
            .sized(0.65f, 0.8f)
    );

    public static final EntityType<Praetorian> PRAETORIAN = register(
        "praetorian",
        EntityType.Builder.of(Praetorian::new, ALIEN_CATEGORY)
            .sized(0.98f, 3.98f)
    );

    public static final EntityType<Queen> QUEEN = register(
        "queen",
        EntityType.Builder.of(Queen::new, ALIEN_CATEGORY)
            .sized(1.98f, 3.98f)
    );

    public static final EntityType<Warrior> WARRIOR = register(
        "warrior",
        EntityType.Builder.of(Warrior::new, ALIEN_CATEGORY)
            .sized(0.8f, 1.98f)
    );

    public static final EntityType<Yautja> YAUTJA = register(
        "yautja",
        EntityType.Builder.of(Yautja::new, PREDATOR_CATEGORY)
            .sized(0.98f, 2.48f)
    );

    public static final EntityType<Marine> MARINE = register(
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

    // These are "deferred" entity types for our existing entities. We want different spawn colors for these spawn eggs,
    // but spawn eggs unfortunately map themselves under their respective entity type, meaning that we can't simply
    // create multiple spawn eggs for the same entity type.

    public static final EntityType<Chestburster> ABERRANT_CHESTBURSTER = register(
        "aberrant_chestburster",
        EntityType.Builder.<Chestburster>of(
            (type, level) -> aberrantFactory(Chestburster::new, type, level),
            AVPEntityTypes.ALIEN_CATEGORY
        )
            .sized(0.35f, 0.35f)
    );

    public static final EntityType<Drone> ABERRANT_DRONE = register(
        "aberrant_drone",
        EntityType.Builder.<Drone>of(
            (type, level) -> aberrantFactory(Drone::new, type, level),
            AVPEntityTypes.ALIEN_CATEGORY
        )
            .sized(0.8f, 1.98f)
    );

    public static final EntityType<Facehugger> ABERRANT_FACEHUGGER = register(
        "aberrant_facehugger",
        EntityType.Builder.<Facehugger>of(
            (type, level) -> aberrantFactory(Facehugger::new, type, level),
            AVPEntityTypes.ALIEN_CATEGORY
        )
            .sized(0.8f, 0.25f)
    );

    public static final EntityType<Ovamorph> ABERRANT_OVAMORPH = register(
        "aberrant_ovamorph",
        EntityType.Builder.<Ovamorph>of(
            (type, level) -> aberrantFactory(Ovamorph::new, type, level),
            AVPEntityTypes.ALIEN_CATEGORY
        )
            .sized(0.65f, 0.8f)
    );

    public static final EntityType<Praetorian> ABERRANT_PRAETORIAN = register(
        "aberrant_praetorian",
        EntityType.Builder.<Praetorian>of(
            (type, level) -> aberrantFactory(Praetorian::new, type, level),
            AVPEntityTypes.ALIEN_CATEGORY
        )
            .sized(0.98f, 3.98f)
    );

    public static final EntityType<Warrior> ABERRANT_WARRIOR = register(
        "aberrant_warrior",
        EntityType.Builder.<Warrior>of(
            (type, level) -> aberrantFactory(Warrior::new, type, level),
            AVPEntityTypes.ALIEN_CATEGORY
        )
            .sized(0.8f, 1.98f)
    );

    public static final EntityType<Queen> ABERRANT_QUEEN = register(
        "aberrant_queen",
        EntityType.Builder.<Queen>of(
            (type, level) -> aberrantFactory(Queen::new, type, level),
            AVPEntityTypes.ALIEN_CATEGORY
        )
            .sized(1.98f, 3.98f)
    );

    public static final EntityType<Drone> IRRADIATED_DRONE = register(
        "irradiated_drone",
        EntityType.Builder.<Drone>of(
            (type, level) -> irradiatedFactory(Drone::new, type, level),
            AVPEntityTypes.ALIEN_CATEGORY
        )
            .sized(0.8f, 1.98f)
    );

    public static final EntityType<Praetorian> IRRADIATED_PRAETORIAN = register(
        "irradiated_praetorian",
        EntityType.Builder.<Praetorian>of(
            (type, level) -> irradiatedFactory(Praetorian::new, type, level),
            AVPEntityTypes.ALIEN_CATEGORY
        )
            .sized(0.98f, 3.98f)
    );

    public static final EntityType<Queen> IRRADIATED_QUEEN = register(
        "irradiated_queen",
        EntityType.Builder.<Queen>of(
            (type, level) -> irradiatedFactory(Queen::new, type, level),
            AVPEntityTypes.ALIEN_CATEGORY
        )
            .sized(1.98f, 3.98f)
    );

    public static final EntityType<Warrior> IRRADIATED_WARRIOR = register(
        "irradiated_warrior",
        EntityType.Builder.<Warrior>of(
            (type, level) -> irradiatedFactory(Warrior::new, type, level),
            AVPEntityTypes.ALIEN_CATEGORY
        )
            .sized(0.8f, 1.98f)
    );

    public static final EntityType<Chestburster> NETHER_CHESTBURSTER = register(
        "nether_chestburster",
        EntityType.Builder.<Chestburster>of(
            (type, level) -> nethermorphFactory(Chestburster::new, type, level),
            AVPEntityTypes.ALIEN_CATEGORY
        )
            .sized(0.35f, 0.35f)
    );

    public static final EntityType<Drone> NETHER_DRONE = register(
        "nether_drone",
        EntityType.Builder.<Drone>of(
            (type, level) -> nethermorphFactory(Drone::new, type, level),
            AVPEntityTypes.ALIEN_CATEGORY
        )
            .sized(0.8f, 1.98f)
    );

    public static final EntityType<Facehugger> NETHER_FACEHUGGER = register(
        "nether_facehugger",
        EntityType.Builder.<Facehugger>of(
            (type, level) -> nethermorphFactory(Facehugger::new, type, level),
            AVPEntityTypes.ALIEN_CATEGORY
        )
            .sized(0.8f, 0.25f)
    );

    public static final EntityType<Ovamorph> NETHER_OVAMORPH = register(
        "nether_ovamorph",
        EntityType.Builder.<Ovamorph>of(
            (type, level) -> nethermorphFactory(Ovamorph::new, type, level),
            AVPEntityTypes.ALIEN_CATEGORY
        )
            .sized(0.65f, 0.8f)
    );

    public static final EntityType<Praetorian> NETHER_PRAETORIAN = register(
        "nether_praetorian",
        EntityType.Builder.<Praetorian>of(
            (type, level) -> nethermorphFactory(Praetorian::new, type, level),
            AVPEntityTypes.ALIEN_CATEGORY
        )
            .sized(0.98f, 3.98f)
    );

    public static final EntityType<Warrior> NETHER_WARRIOR = register(
        "nether_warrior",
        EntityType.Builder.<Warrior>of(
            (type, level) -> nethermorphFactory(Warrior::new, type, level),
            AVPEntityTypes.ALIEN_CATEGORY
        )
            .sized(0.8f, 1.98f)
    );

    public static final EntityType<Queen> NETHER_QUEEN = register(
        "nether_queen",
        EntityType.Builder.<Queen>of(
            (type, level) -> nethermorphFactory(Queen::new, type, level),
            AVPEntityTypes.ALIEN_CATEGORY
        )
            .sized(1.98f, 3.98f)
    );

    public static final EntityType<Chestburster> ROYAL_ABERRANT_CHESTBURSTER = register(
        "royal_aberrant_chestburster",
        EntityType.Builder.<Chestburster>of(
            (type, level) -> royalAberrantFactory(Chestburster::new, type, level),
            AVPEntityTypes.ALIEN_CATEGORY
        )
            .sized(0.35f, 0.35f)
    );

    public static final EntityType<Facehugger> ROYAL_ABERRANT_FACEHUGGER = register(
        "royal_aberrant_facehugger",
        EntityType.Builder.<Facehugger>of(
            (type, level) -> royalAberrantFactory(Facehugger::new, type, level),
            AVPEntityTypes.ALIEN_CATEGORY
        )
            .sized(0.8f, 0.25f)
    );

    public static final EntityType<Ovamorph> ROYAL_ABERRANT_OVAMORPH = register(
        "royal_aberrant_ovamorph",
        EntityType.Builder.<Ovamorph>of(
            (type, level) -> royalAberrantFactory(Ovamorph::new, type, level),
            AVPEntityTypes.ALIEN_CATEGORY
        )
            .sized(0.65f, 0.8f)
    );

    public static final EntityType<Chestburster> ROYAL_CHESTBURSTER = register(
        "royal_chestburster",
        EntityType.Builder.<Chestburster>of(
            (type, level) -> royalFactory(Chestburster::new, type, level),
            AVPEntityTypes.ALIEN_CATEGORY
        )
            .sized(0.35f, 0.35f)
    );

    public static final EntityType<Facehugger> ROYAL_FACEHUGGER = register(
        "royal_facehugger",
        EntityType.Builder.<Facehugger>of(
            (type, level) -> royalFactory(Facehugger::new, type, level),
            AVPEntityTypes.ALIEN_CATEGORY
        )
            .sized(0.8f, 0.25f)
    );

    public static final EntityType<Ovamorph> ROYAL_OVAMORPH = register(
        "royal_ovamorph",
        EntityType.Builder.<Ovamorph>of(
            (type, level) -> royalFactory(Ovamorph::new, type, level),
            AVPEntityTypes.ALIEN_CATEGORY
        )
            .sized(0.65f, 0.8f)
    );

    public static final EntityType<Chestburster> ROYAL_NETHER_CHESTBURSTER = register(
        "royal_nether_chestburster",
        EntityType.Builder.<Chestburster>of(
            (type, level) -> royalNethermorphFactory(Chestburster::new, type, level),
            AVPEntityTypes.ALIEN_CATEGORY
        )
            .sized(0.35f, 0.35f)
    );

    public static final EntityType<Facehugger> ROYAL_NETHER_FACEHUGGER = register(
        "royal_nether_facehugger",
        EntityType.Builder.<Facehugger>of(
            (type, level) -> royalNethermorphFactory(Facehugger::new, type, level),
            AVPEntityTypes.ALIEN_CATEGORY
        )
            .sized(0.8f, 0.25f)
    );

    public static final EntityType<Ovamorph> ROYAL_NETHER_OVAMORPH = register(
        "royal_nether_ovamorph",
        EntityType.Builder.<Ovamorph>of(
            (type, level) -> royalNethermorphFactory(Ovamorph::new, type, level),
            AVPEntityTypes.ALIEN_CATEGORY
        )
            .sized(0.65f, 0.8f)
    );

    private static <T extends Alien> T irradiatedFactory(
        BiFunction<EntityType<T>, Level, T> entityFactory,
        EntityType<T> entityType,
        Level level
    ) {
        var entity = entityFactory.apply(entityType, level);
        entity.setIrradiated(true);
        return entity;
    }

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

    private static <T extends Entity> EntityType<T> register(String name, EntityType.Builder<T> builder) {
        var entityType = ((SilencedEntityTypeBuilder) builder).<T>buildWithoutDataFixerCheck();
        var resourceLocation = AVPResources.location(name);
        Registry.register(BuiltInRegistries.ENTITY_TYPE, resourceLocation, entityType);
        return entityType;
    }

    public static void initialize() {
        // Regular variants
        FabricDefaultAttributeRegistry.register(CHESTBURSTER, Chestburster.createChestbursterAttributes());
        FabricDefaultAttributeRegistry.register(DRONE, Drone.createDroneAttributes());
        FabricDefaultAttributeRegistry.register(FACEHUGGER, Facehugger.createFacehuggerAttributes());
        FabricDefaultAttributeRegistry.register(OVAMORPH, Ovamorph.createOvamorphAttributes());
        FabricDefaultAttributeRegistry.register(PRAETORIAN, Praetorian.createPraetorianAttributes());
        FabricDefaultAttributeRegistry.register(QUEEN, Queen.createQueenAttributes());
        FabricDefaultAttributeRegistry.register(WARRIOR, Warrior.createWarriorAttributes());
        // Royals
        FabricDefaultAttributeRegistry.register(ROYAL_CHESTBURSTER, Chestburster.createChestbursterAttributes());
        FabricDefaultAttributeRegistry.register(ROYAL_FACEHUGGER, Facehugger.createFacehuggerAttributes());
        FabricDefaultAttributeRegistry.register(ROYAL_OVAMORPH, Ovamorph.createOvamorphAttributes());

        // Aberrant variants
        FabricDefaultAttributeRegistry.register(ABERRANT_CHESTBURSTER, Chestburster.createChestbursterAttributes());
        FabricDefaultAttributeRegistry.register(ABERRANT_DRONE, Drone.createDroneAttributes());
        FabricDefaultAttributeRegistry.register(ABERRANT_FACEHUGGER, Facehugger.createFacehuggerAttributes());
        FabricDefaultAttributeRegistry.register(ABERRANT_OVAMORPH, Ovamorph.createOvamorphAttributes());
        FabricDefaultAttributeRegistry.register(ABERRANT_PRAETORIAN, Praetorian.createPraetorianAttributes());
        FabricDefaultAttributeRegistry.register(ABERRANT_QUEEN, Queen.createQueenAttributes());
        FabricDefaultAttributeRegistry.register(ABERRANT_WARRIOR, Warrior.createWarriorAttributes());
        // Royals
        FabricDefaultAttributeRegistry.register(ROYAL_ABERRANT_CHESTBURSTER, Chestburster.createChestbursterAttributes());
        FabricDefaultAttributeRegistry.register(ROYAL_ABERRANT_FACEHUGGER, Facehugger.createFacehuggerAttributes());
        FabricDefaultAttributeRegistry.register(ROYAL_ABERRANT_OVAMORPH, Ovamorph.createOvamorphAttributes());

        // Irradiated variants
        FabricDefaultAttributeRegistry.register(IRRADIATED_DRONE, Drone.createDroneAttributes());
        FabricDefaultAttributeRegistry.register(IRRADIATED_PRAETORIAN, Praetorian.createPraetorianAttributes());
        FabricDefaultAttributeRegistry.register(IRRADIATED_QUEEN, Queen.createQueenAttributes());
        FabricDefaultAttributeRegistry.register(IRRADIATED_WARRIOR, Warrior.createWarriorAttributes());

        // Nether variants
        FabricDefaultAttributeRegistry.register(NETHER_CHESTBURSTER, Chestburster.createChestbursterAttributes());
        FabricDefaultAttributeRegistry.register(NETHER_DRONE, Drone.createDroneAttributes());
        FabricDefaultAttributeRegistry.register(NETHER_FACEHUGGER, Facehugger.createFacehuggerAttributes());
        FabricDefaultAttributeRegistry.register(NETHER_OVAMORPH, Ovamorph.createOvamorphAttributes());
        FabricDefaultAttributeRegistry.register(NETHER_PRAETORIAN, Praetorian.createPraetorianAttributes());
        FabricDefaultAttributeRegistry.register(NETHER_QUEEN, Queen.createQueenAttributes());
        FabricDefaultAttributeRegistry.register(NETHER_WARRIOR, Warrior.createWarriorAttributes());
        // Royals
        FabricDefaultAttributeRegistry.register(ROYAL_NETHER_CHESTBURSTER, Chestburster.createChestbursterAttributes());
        FabricDefaultAttributeRegistry.register(ROYAL_NETHER_FACEHUGGER, Facehugger.createFacehuggerAttributes());
        FabricDefaultAttributeRegistry.register(ROYAL_NETHER_OVAMORPH, Ovamorph.createOvamorphAttributes());

        FabricDefaultAttributeRegistry.register(YAUTJA, Yautja.createYautjaAttributes());
        FabricDefaultAttributeRegistry.register(MARINE, Marine.createMarineAttributes());
    }
}
