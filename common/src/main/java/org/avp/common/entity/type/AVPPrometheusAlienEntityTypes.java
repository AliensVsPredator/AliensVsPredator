package org.avp.common.entity.type;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;

import org.avp.api.GameObject;
import org.avp.common.entity.living.*;

public class AVPPrometheusAlienEntityTypes {

    public static final GameObject<EntityType<Deacon>> DEACON = AVPEntityTypes.registerLiving(
        "deacon",
        0x8896A5,
        0x495256,
        EntityType.Builder.of(Deacon::new, MobCategory.MONSTER)
            .sized(0.75F, 1.98F)
    );

    public static final GameObject<EntityType<DeaconAdult>> DEACON_ADULT = AVPEntityTypes.registerLiving(
        "deacon_adult",
        0x8896A5,
        0x495256,
        EntityType.Builder.of(DeaconAdult::new, MobCategory.MONSTER)
            .sized(0.98F, 2.48F)
    );

    public static final GameObject<EntityType<Trilobite>> TRILOBITE = AVPEntityTypes.registerLiving(
        "trilobite",
        0xCCC2A5,
        0x987379,
        EntityType.Builder.of(Trilobite::new, MobCategory.MONSTER)
            .sized(1.98F, 1.98F)
    );

    public static final GameObject<EntityType<TrilobiteBaby>> TRILOBITE_BABY = AVPEntityTypes.registerLiving(
        "trilobite_baby",
        0xCCC2A5,
        0x987379,
        EntityType.Builder.of(TrilobiteBaby::new, MobCategory.MONSTER)
            .sized(0.5F, 0.25F)
    );

    public static void forceInitialization() {
        // This method doesn't need to do anything
    }

    private AVPPrometheusAlienEntityTypes() {
        throw new UnsupportedOperationException();
    }
}
