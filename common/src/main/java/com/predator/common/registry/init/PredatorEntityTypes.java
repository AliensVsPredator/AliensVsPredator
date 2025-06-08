package com.predator.common.registry.init;

import com.predator.common.gameplay.entity.living.yautja.Yautja;
import com.predator.common.gameplay.entity.projectile.ShurikenProjectile;
import com.predator.common.gameplay.entity.projectile.SmartDiscProjectile;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;

import com.avp.common.registry.AVPDeferredHolder;
import com.avp.common.registry.init.entity_type.AVPEntityTypes;
import com.avp.service.Services;

public class PredatorEntityTypes {

    public static final AVPDeferredHolder<EntityType<ShurikenProjectile>> SHURIKEN = AVPEntityTypes.register(
        "shuriken",
        EntityType.Builder.<ShurikenProjectile>of(ShurikenProjectile::new, MobCategory.MISC)
            .sized(0.25F, 0.25F)
    );

    public static final AVPDeferredHolder<EntityType<SmartDiscProjectile>> SMART_DISC = AVPEntityTypes.register(
        "smart_disc",
        EntityType.Builder.<SmartDiscProjectile>of(SmartDiscProjectile::new, MobCategory.MISC)
            .sized(0.25F, 0.25F)
    );

    public static final AVPDeferredHolder<EntityType<Yautja>> YAUTJA = AVPEntityTypes.register(
        "yautja",
        EntityType.Builder.of(Yautja::new, AVPEntityTypes.PREDATOR_CATEGORY)
            .sized(0.98f, 2.48f)
    );

    public static void initialize() {
        Services.REGISTRY.registerEntityAttributes(YAUTJA, Yautja::createYautjaAttributes);
    }
}
