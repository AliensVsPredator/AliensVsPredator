package org.avp.common.entity.type;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MobCategory;

import org.avp.api.GameObject;
import org.avp.common.entity.living.*;
import org.avp.common.registry.AVPRegistry;
import org.avp.common.service.Services;

public class AVPEntityTypes {

    public static final GameObject<EntityType<Belugaburster>> BELUGABURSTER = registerLiving(
        "belugaburster",
        0xC2C1BD,
        0x646857,
        EntityType.Builder.of(Belugaburster::new, MobCategory.MONSTER)
            .sized(0.78F, 0.98F)
    );

    public static final GameObject<EntityType<Belugamorph>> BELUGAMORPH = registerLiving(
        "belugamorph",
        0xBCC9C6,
        0x646E65,
        EntityType.Builder.of(Belugamorph::new, MobCategory.MONSTER)
            .sized(0.75F, 2.98F)
    );

    public static <T extends Mob> GameObject<EntityType<T>> registerLiving(
        String registryName,
        int backgroundColor,
        int highlightColor,
        EntityType.Builder<T> builder
    ) {
        return Services.ENTITY_REGISTRY.registerWithSpawnEgg(
            registryName,
            backgroundColor,
            highlightColor,
            () -> builder.build(registryName)
        );
    }

    public static void forceInitialization() {
        // This method doesn't need to do anything
    }

    private AVPEntityTypes() {
        throw new UnsupportedOperationException();
    }
}
