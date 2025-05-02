package com.avp.fabric.common.entity.type;

import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;

import com.avp.AVPResources;
import com.avp.common.entity.AVPMobCategories;
import com.avp.common.entity.type.SilencedEntityTypeBuilder;
import com.avp.fabric.common.entity.projectile.BulletProjectile;

public class AVPEntityTypes {

    public static final MobCategory ALIEN_CATEGORY = AVPMobCategories.ALIENS;

    public static final MobCategory PREDATOR_CATEGORY = AVPMobCategories.PREDATOR;

    public static final EntityType<BulletProjectile> BULLET = register(
        "bullet",
        EntityType.Builder.<BulletProjectile>of(BulletProjectile::new, MobCategory.MISC)
            .sized(0.25F, 0.25F)
    );

    private static <T extends Entity> EntityType<T> register(String name, EntityType.Builder<T> builder) {
        var entityType = ((SilencedEntityTypeBuilder) builder).<T>buildWithoutDataFixerCheck();
        var resourceLocation = AVPResources.location(name);
        Registry.register(BuiltInRegistries.ENTITY_TYPE, resourceLocation, entityType);
        return entityType;
    }

    public static void initialize() {}
}
