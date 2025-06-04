package com.avp.common.registry.init.entity_type;

import com.human.common.gameplay.entity.living.human.marine.Marine;
import com.human.common.gameplay.entity.machine.SentryTurret;
import com.human.common.gameplay.entity.nuke.MushroomCloudEntity;
import com.human.common.gameplay.entity.nuke.PrimedNuke;
import com.human.common.gameplay.entity.projectile.Flamethrow;
import com.human.common.gameplay.entity.projectile.Rocket;
import com.human.common.gameplay.entity.projectile.ThrownGrenade;
import com.human.common.gameplay.util.EyeColorGenerator;
import com.human.common.gameplay.util.HairColorGenerator;
import com.human.common.gameplay.util.SkinColorGenerator;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.avp.common.registry.AVPDeferredHolder;
import com.avp.common.registry.init.AVPMobCategories;
import com.avp.service.Services;

public class HumanEntityTypes {

    public static final MobCategory ALIEN_CATEGORY = AVPMobCategories.ALIENS;

    public static final MobCategory PREDATOR_CATEGORY = AVPMobCategories.PREDATOR;

    private static final List<AVPDeferredHolder<? extends EntityType<?>>> ENTITY_TYPE_HOLDERS = new ArrayList<>();

    public static List<AVPDeferredHolder<? extends EntityType<?>>> getAll() {
        return Collections.unmodifiableList(ENTITY_TYPE_HOLDERS);
    }

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

    public static final AVPDeferredHolder<EntityType<PrimedNuke>> NUKE = register(
        "nuke",
        EntityType.Builder.<PrimedNuke>of(PrimedNuke::new, MobCategory.MISC)
            .sized(0.1F, 0.1F)
            .noSummon()
            .clientTrackingRange(100)
            .updateInterval(100)
    );

    public static final AVPDeferredHolder<EntityType<Rocket>> ROCKET = register(
        "rocket",
        EntityType.Builder.<Rocket>of(Rocket::new, MobCategory.MISC)
            .sized(0.1F, 0.1F)
            .clientTrackingRange(8)
            .updateInterval(10)
    );

    public static final AVPDeferredHolder<EntityType<SentryTurret>> SENTRY_TURRET = register(
        "sentry_turret",
        EntityType.Builder.of(SentryTurret::new, MobCategory.MISC).sized(1.0F, 1.0F).noSummon()
    );

    public static <T extends Entity> AVPDeferredHolder<EntityType<T>> register(String id, EntityType.Builder<T> builder) {
        var holder = Services.REGISTRY.register(
            BuiltInRegistries.ENTITY_TYPE,
            id,
            () -> ((SilencedEntityTypeBuilder) builder).<T>buildWithoutDataFixerCheck()
        );

        ENTITY_TYPE_HOLDERS.add(holder);

        return holder;
    }

    public static void initialize() {
        Services.REGISTRY.registerEntityAttributes(MARINE, Marine::createMarineAttributes);
        Services.REGISTRY.registerEntityAttributes(SENTRY_TURRET, SentryTurret::createSentryTurretAttributes);
    }
}
