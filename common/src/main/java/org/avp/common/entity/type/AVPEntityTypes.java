package org.avp.common.entity.type;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;

import org.avp.api.Holder;
import org.avp.common.entity.Acid;
import org.avp.common.entity.living.*;
import org.avp.common.registry.AVPSimpleDeferredEntityTypeRegistry;

public class AVPEntityTypes extends AVPSimpleDeferredEntityTypeRegistry {

    public static final AVPEntityTypes INSTANCE = new AVPEntityTypes();

    public final Holder<EntityType<Acid>> ACID = createHolder(
        "acid",
        EntityType.Builder.of(Acid::new, MobCategory.MISC)
            .sized(0.8F, 0.05F)
    );

    public final Holder<EntityType<Belugaburster>> BELUGABURSTER = createMobHolder(
        "belugaburster",
        0xC2C1BD,
        0x646857,
        EntityType.Builder.of(Belugaburster::new, MobCategory.MONSTER)
            .sized(0.78F, 0.98F)
    );

    public final Holder<EntityType<Belugamorph>> BELUGAMORPH = createMobHolder(
        "belugamorph",
        0xBCC9C6,
        0x646E65,
        EntityType.Builder.of(Belugamorph::new, MobCategory.MONSTER)
            .sized(0.75F, 2.98F)
    );

    private AVPEntityTypes() {}
}
