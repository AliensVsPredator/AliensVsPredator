package com.avp.common.entity.type;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;

import com.avp.common.entity.AVPMobCategories;
import com.avp.common.entity.living.human.EyeColorGenerator;
import com.avp.common.entity.living.human.HairColorGenerator;
import com.avp.common.entity.living.human.SkinColorGenerator;
import com.avp.common.entity.living.human.marine.Marine;
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

public class TempAVPEntityTypes {

    public static final MobCategory ALIEN_CATEGORY = AVPMobCategories.ALIENS;

    public static final MobCategory PREDATOR_CATEGORY = AVPMobCategories.PREDATOR;

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

    private static <T extends Entity> AVPDeferredHolder<EntityType<T>> register(String id, EntityType.Builder<T> builder) {
        return Services.REGISTRY.register(
            BuiltInRegistries.ENTITY_TYPE,
            id,
            () -> ((SilencedEntityTypeBuilder) builder).buildWithoutDataFixerCheck()
        );
    }

    public static void initialize() {
        Services.REGISTRY.registerEntityAttributes(MARINE, Marine::createMarineAttributes);
        Services.REGISTRY.registerEntityAttributes(SENTRY_TURRET, SentryTurret::createSentryTurretAttributes);
    }
}
