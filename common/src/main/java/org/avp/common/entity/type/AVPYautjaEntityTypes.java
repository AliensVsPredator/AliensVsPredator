package org.avp.common.entity.type;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;

import org.avp.api.Holder;
import org.avp.common.entity.living.Yautja;
import org.avp.common.registry.AVPSimpleDeferredEntityTypeRegistry;

public class AVPYautjaEntityTypes extends AVPSimpleDeferredEntityTypeRegistry {

    public static final AVPYautjaEntityTypes INSTANCE = new AVPYautjaEntityTypes();

    public final Holder<EntityType<Yautja>> yautja = createMobHolder(
        "yautja",
        0xB9A86C,
        0x5A4728,
        EntityType.Builder.of(Yautja::new, MobCategory.MONSTER)
            .sized(0.98F, 2.48F)
    );

    private AVPYautjaEntityTypes() {}
}
