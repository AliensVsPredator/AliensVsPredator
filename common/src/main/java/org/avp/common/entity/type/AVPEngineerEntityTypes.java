package org.avp.common.entity.type;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;

import org.avp.api.Holder;
import org.avp.common.entity.living.Engineer;
import org.avp.common.registry.AVPSimpleDeferredEntityTypeRegistry;

public class AVPEngineerEntityTypes extends AVPSimpleDeferredEntityTypeRegistry {

    public static final AVPEngineerEntityTypes INSTANCE = new AVPEngineerEntityTypes();

    public final Holder<EntityType<Engineer>> ENGINEER = createMobHolder(
        "engineer",
        0xB8B1B6,
        0x99AFBD,
        EntityType.Builder.of(Engineer::new, MobCategory.MONSTER)
    );

    private AVPEngineerEntityTypes() {}
}
