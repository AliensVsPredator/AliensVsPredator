package org.avp.common.entity.type;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;

import org.avp.api.Holder;
import org.avp.common.entity.living.*;
import org.avp.common.registry.AVPSimpleDeferredEntityTypeRegistry;

public class AVPPrometheusAlienEntityTypes extends AVPSimpleDeferredEntityTypeRegistry {

    public static final AVPPrometheusAlienEntityTypes INSTANCE = new AVPPrometheusAlienEntityTypes();

    public final Holder<EntityType<Deacon>> deacon = createMobHolder(
        "deacon",
        0x8896A5,
        0x495256,
        EntityType.Builder.of(Deacon::new, MobCategory.MONSTER)
            .sized(0.75F, 1.98F)
    );

    public final Holder<EntityType<DeaconAdult>> deaconAdult = createMobHolder(
        "deacon_adult",
        0x8896A5,
        0x495256,
        EntityType.Builder.of(DeaconAdult::new, MobCategory.MONSTER)
            .sized(0.98F, 2.48F)
    );

    public final Holder<EntityType<Trilobite>> trilobite = createMobHolder(
        "trilobite",
        0xCCC2A5,
        0x987379,
        EntityType.Builder.of(Trilobite::new, MobCategory.MONSTER)
            .sized(1.98F, 1.98F)
    );

    public final Holder<EntityType<TrilobiteBaby>> trilobiteBaby = createMobHolder(
        "trilobite_baby",
        0xCCC2A5,
        0x987379,
        EntityType.Builder.of(TrilobiteBaby::new, MobCategory.MONSTER)
            .sized(0.5F, 0.25F)
    );

    private AVPPrometheusAlienEntityTypes() {}
}
