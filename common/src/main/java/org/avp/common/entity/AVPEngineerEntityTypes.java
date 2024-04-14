package org.avp.common.entity;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;

import org.avp.api.GameObject;
import org.avp.common.entity.living.Engineer;
import org.avp.common.registry.AVPRegistry;

public class AVPEngineerEntityTypes {

    public static final GameObject<EntityType<Engineer>> ENGINEER = AVPEntityTypes.registerLiving(
        "engineer",
        0xB8B1B6,
        0x99AFBD,
        EntityType.Builder.of(Engineer::new, MobCategory.MONSTER)
    );

    public static void forceInitialization() {
        // This method doesn't need to do anything
    }

    private AVPEngineerEntityTypes() {
        throw new UnsupportedOperationException();
    }
}
