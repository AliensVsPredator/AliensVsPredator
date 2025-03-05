package com.avp.common.entity.type;

import net.fabricmc.fabric.api.object.builder.v1.entity.FabricDefaultAttributeRegistry;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.level.Level;

import java.util.function.BiFunction;
import java.util.function.Supplier;

import com.avp.AVP;
import com.avp.AVPResources;
import com.avp.common.config.ConfigProperties;
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
import com.avp.common.entity.living.yautja.Yautja;
import com.avp.common.entity.projectile.Flamethrow;
import com.avp.common.entity.projectile.Rocket;
import com.avp.common.gene.GeneKeys;

public class AVPEntityTypes {

    public static final MobCategory ALIEN_CATEGORY = ((Supplier<MobCategory>) () -> {
        if (AVP.SPAWNING_CONFIG.properties().getOrThrow(ConfigProperties.ALIEN_CUSTOM_MOB_CATEGORY_ENABLED)) {
            return AVPMobCategories.ALIENS;
        }

        return MobCategory.MONSTER;
    }).get();

    public static final EntityType<Acid> ACID = register(
        "acid",
        EntityType.Builder.of(Acid::new, MobCategory.MISC).sized(0.66F, 0.05F)
    );

    public static final EntityType<Chestburster> CHESTBURSTER = register(
        "chestburster",
        EntityType.Builder.of(Chestburster::new, ALIEN_CATEGORY).sized(0.35f, 0.35f)
    );

    public static final EntityType<Queen> CHESTBURSTER_QUEEN = register(
        "chestburster_queen",
        // TODO:
        EntityType.Builder.of(Queen::new, ALIEN_CATEGORY).sized(1.98f, 3.98f)
    );

    public static final EntityType<Drone> DRONE = register(
        "drone",
        EntityType.Builder.of(Drone::new, ALIEN_CATEGORY).sized(0.8f, 1.98f)
    );

    public static final EntityType<Facehugger> FACEHUGGER = register(
        "facehugger",
        EntityType.Builder.of(Facehugger::new, ALIEN_CATEGORY).sized(0.8f, 0.25f)
    );

    public static final EntityType<Flamethrow> FLAMETHROW = register(
        "flamethrow",
        EntityType.Builder.<Flamethrow>of(Flamethrow::new, MobCategory.MISC)
            .sized(0.1F, 0.1F)
            .clientTrackingRange(8)
            .updateInterval(10)
    );

    public static final EntityType<Ovamorph> OVAMORPH = register(
        "ovamorph",
        EntityType.Builder.of(Ovamorph::new, ALIEN_CATEGORY).sized(0.65f, 0.8f)
    );

    public static final EntityType<Praetorian> PRAETORIAN = register(
        "praetorian",
        EntityType.Builder.of(Praetorian::new, ALIEN_CATEGORY).sized(0.98f, 3.98f)
    );

    public static final EntityType<Queen> QUEEN = register(
        "queen",
        EntityType.Builder.of(Queen::new, ALIEN_CATEGORY).sized(1.98f, 3.98f)
    );

    public static final EntityType<Rocket> ROCKET = register(
        "rocket",
        EntityType.Builder.<Rocket>of(Rocket::new, MobCategory.MISC)
            .sized(0.1F, 0.1F)
            .clientTrackingRange(8)
            .updateInterval(10)
    );

    public static final EntityType<Warrior> WARRIOR = register(
        "warrior",
        EntityType.Builder.of(Warrior::new, ALIEN_CATEGORY).sized(0.8f, 1.98f)
    );

    public static final EntityType<Yautja> YAUTJA = register(
        "yautja",
        EntityType.Builder.of(Yautja::new, MobCategory.MONSTER).sized(0.98f, 2.48f)
    );

    // These are "deferred" entity types for our existing entities. We want different spawn colors for these spawn eggs,
    // but spawn eggs unfortunately map themselves under their respective entity type, meaning that we can't simply
    // create multiple spawn eggs for the same entity type.

    public static final EntityType<Chestburster> ABERRANT_CHESTBURSTER = register(
        "aberrant_chestburster",
        EntityType.Builder.of(aberrantFactory(AVPEntityTypes.CHESTBURSTER, Chestburster::new), AVPEntityTypes.ALIEN_CATEGORY)
    );

    public static final EntityType<Drone> ABERRANT_DRONE = register(
        "aberrant_drone",
        EntityType.Builder.of(aberrantFactory(AVPEntityTypes.DRONE, Drone::new), AVPEntityTypes.ALIEN_CATEGORY)
    );

    public static final EntityType<Facehugger> ABERRANT_FACEHUGGER = register(
        "aberrant_facehugger",
        EntityType.Builder.of(aberrantFactory(AVPEntityTypes.FACEHUGGER, Facehugger::new), AVPEntityTypes.ALIEN_CATEGORY)
    );

    public static final EntityType<Ovamorph> ABERRANT_OVAMORPH = register(
        "aberrant_ovamorph",
        EntityType.Builder.of(aberrantFactory(AVPEntityTypes.OVAMORPH, Ovamorph::new), AVPEntityTypes.ALIEN_CATEGORY)
    );

    public static final EntityType<Praetorian> ABERRANT_PRAETORIAN = register(
        "aberrant_praetorian",
        EntityType.Builder.of(aberrantFactory(AVPEntityTypes.PRAETORIAN, Praetorian::new), AVPEntityTypes.ALIEN_CATEGORY)
    );

    public static final EntityType<Warrior> ABERRANT_WARRIOR = register(
        "aberrant_warrior",
        EntityType.Builder.of(aberrantFactory(AVPEntityTypes.WARRIOR, Warrior::new), AVPEntityTypes.ALIEN_CATEGORY)
    );

    public static final EntityType<Chestburster> NETHER_CHESTBURSTER = register(
        "nether_chestburster",
        EntityType.Builder.of(nethermorphFactory(AVPEntityTypes.CHESTBURSTER, Chestburster::new), AVPEntityTypes.ALIEN_CATEGORY)
    );

    public static final EntityType<Drone> NETHER_DRONE = register(
        "nether_drone",
        EntityType.Builder.of(nethermorphFactory(AVPEntityTypes.DRONE, Drone::new), AVPEntityTypes.ALIEN_CATEGORY)
    );

    public static final EntityType<Facehugger> NETHER_FACEHUGGER = register(
        "nether_facehugger",
        EntityType.Builder.of(nethermorphFactory(AVPEntityTypes.FACEHUGGER, Facehugger::new), AVPEntityTypes.ALIEN_CATEGORY)
    );

    public static final EntityType<Ovamorph> NETHER_OVAMORPH = register(
        "nether_ovamorph",
        EntityType.Builder.of(nethermorphFactory(AVPEntityTypes.OVAMORPH, Ovamorph::new), AVPEntityTypes.ALIEN_CATEGORY)
    );

    public static final EntityType<Praetorian> NETHER_PRAETORIAN = register(
        "nether_praetorian",
        EntityType.Builder.of(nethermorphFactory(AVPEntityTypes.PRAETORIAN, Praetorian::new), AVPEntityTypes.ALIEN_CATEGORY)
    );

    public static final EntityType<Warrior> NETHER_WARRIOR = register(
        "nether_warrior",
        EntityType.Builder.of(nethermorphFactory(AVPEntityTypes.WARRIOR, Warrior::new), AVPEntityTypes.ALIEN_CATEGORY)
    );

    private static <T extends Alien> EntityType.EntityFactory<T> aberrantFactory(
        EntityType<T> overridingEntityType,
        BiFunction<EntityType<T>, Level, T> entityFactory
    ) {
        return (entityType, level) -> {
            var entity = entityFactory.apply(overridingEntityType, level);
            entity.geneManager().minimize(GeneKeys.GENETIC_INTEGRITY);
            return entity;
        };
    }

    private static <T extends Alien> EntityType.EntityFactory<T> nethermorphFactory(
        EntityType<T> overridingEntityType,
        BiFunction<EntityType<T>, Level, T> entityFactory
    ) {
        return (entityType, level) -> {
            var entity = entityFactory.apply(overridingEntityType, level);
            entity.geneManager().minimize(GeneKeys.COLD_RESISTANCE);
            entity.geneManager().maximize(GeneKeys.FIRE_RESISTANCE);
            return entity;
        };
    }

    private static <T extends Entity> EntityType<T> register(String name, EntityType.Builder<T> builder) {
        var entityType = ((SilencedEntityTypeBuilder) builder).<T>buildWithoutDataFixerCheck();
        var resourceLocation = AVPResources.location(name);
        Registry.register(BuiltInRegistries.ENTITY_TYPE, resourceLocation, entityType);
        return entityType;
    }

    public static void initialize() {
        FabricDefaultAttributeRegistry.register(CHESTBURSTER, Chestburster.createChestbursterAttributes());
        FabricDefaultAttributeRegistry.register(DRONE, Drone.createDroneAttributes());
        FabricDefaultAttributeRegistry.register(FACEHUGGER, Facehugger.createFacehuggerAttributes());
        FabricDefaultAttributeRegistry.register(OVAMORPH, Ovamorph.createOvamorphAttributes());
        FabricDefaultAttributeRegistry.register(PRAETORIAN, Praetorian.createPraetorianAttributes());
        FabricDefaultAttributeRegistry.register(QUEEN, Queen.createQueenAttributes());
        FabricDefaultAttributeRegistry.register(WARRIOR, Warrior.createWarriorAttributes());
        FabricDefaultAttributeRegistry.register(YAUTJA, Yautja.createYautjaAttributes());
    }
}
