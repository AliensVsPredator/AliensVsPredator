package com.human.common.registry.init.entity_type;

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
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;

import com.avp.common.registry.AVPDeferredHolder;
import com.avp.common.registry.init.entity_type.AVPEntityTypes;
import com.avp.service.Services;

public class HumanEntityTypes {

    public static final AVPDeferredHolder<EntityType<Flamethrow>> FLAMETHROW = AVPEntityTypes.register(
        "flamethrow",
        EntityType.Builder.<Flamethrow>of(Flamethrow::new, MobCategory.MISC)
            .sized(0.1F, 0.1F)
            .clientTrackingRange(8)
            .updateInterval(10)
    );

    public static final AVPDeferredHolder<EntityType<ThrownGrenade>> GRENADE_THROWN = AVPEntityTypes.register(
        "grenade_thrown",
        EntityType.Builder.<ThrownGrenade>of(ThrownGrenade::new, MobCategory.MISC)
            .sized(0.25F, 0.25F)
    );

    public static final AVPDeferredHolder<EntityType<Marine>> MARINE = AVPEntityTypes.register(
        "marine",
        EntityType.Builder.<Marine>of((entityType, level) -> {
            var entity = new Marine(entityType, level);

            var random = entity.getRandom();
            entity.isMale.set(random.nextBoolean());
            var isMale = entity.isMale.get();

            if (isMale) {
                entity.setBeardVariant(random.nextInt(3));
            }

            entity.eyeColor.set(EyeColorGenerator.random(random));
            entity.hairColor.set(HairColorGenerator.random(random));
            entity.hairVariant.set(random.nextInt(isMale ? 5 : 6));
            entity.skinColor.set(SkinColorGenerator.random(random));

            return entity;
        }, MobCategory.CREATURE).sized(0.7F, 1.95F)
    );

    public static final AVPDeferredHolder<EntityType<MushroomCloudEntity>> MUSHROOM_CLOUD = AVPEntityTypes.register(
        "mushroom_cloud",
        EntityType.Builder.of(MushroomCloudEntity::new, MobCategory.MISC)
    );

    public static final AVPDeferredHolder<EntityType<PrimedNuke>> NUKE = AVPEntityTypes.register(
        "nuke",
        EntityType.Builder.<PrimedNuke>of(PrimedNuke::new, MobCategory.MISC)
            .sized(0.1F, 0.1F)
            .noSummon()
            .clientTrackingRange(100)
            .updateInterval(100)
    );

    public static final AVPDeferredHolder<EntityType<Rocket>> ROCKET = AVPEntityTypes.register(
        "rocket",
        EntityType.Builder.<Rocket>of(Rocket::new, MobCategory.MISC)
            .sized(0.1F, 0.1F)
            .clientTrackingRange(8)
            .updateInterval(10)
    );

    public static final AVPDeferredHolder<EntityType<SentryTurret>> SENTRY_TURRET = AVPEntityTypes.register(
        "sentry_turret",
        EntityType.Builder.of(SentryTurret::new, MobCategory.MISC).sized(1.0F, 1.0F).noSummon()
    );

    public static void initialize() {
        Services.REGISTRY.registerEntityAttributes(HumanEntityTypes.MARINE, Marine::createMarineAttributes);
        Services.REGISTRY.registerEntityAttributes(HumanEntityTypes.SENTRY_TURRET, SentryTurret::createSentryTurretAttributes);
    }
}
