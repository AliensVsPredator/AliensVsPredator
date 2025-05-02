package com.avp.common.entity.type;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;

import com.avp.common.entity.AVPMobCategories;
import com.avp.common.entity.projectile.ThrownGrenade;
import com.avp.common.registry.AVPDeferredHolder;
import com.avp.service.Services;

public class TempAVPEntityTypes {

    public static final MobCategory ALIEN_CATEGORY = AVPMobCategories.ALIENS;

    public static final MobCategory PREDATOR_CATEGORY = AVPMobCategories.PREDATOR;

    public static final AVPDeferredHolder<EntityType<ThrownGrenade>> GRENADE_THROWN = register(
        "grenade_thrown",
        EntityType.Builder.<ThrownGrenade>of(ThrownGrenade::new, MobCategory.MISC)
            .sized(0.25F, 0.25F)
    );

    private static <T extends Entity> AVPDeferredHolder<EntityType<T>> register(String id, EntityType.Builder<T> builder) {
        return Services.REGISTRY.register(
            BuiltInRegistries.ENTITY_TYPE,
            id,
            () -> ((SilencedEntityTypeBuilder) builder).buildWithoutDataFixerCheck()
        );
    }

    public static void initialize() {}
}
