package org.avp.common.entity;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;

import org.avp.api.GameObject;
import org.avp.common.entity.living.*;
import org.avp.common.registry.AVPRegistry;

/**
 * @author Boston Vanseghi
 */
public class AVPPrometheusAlienEntityTypes implements AVPRegistry {

    private static final AVPPrometheusAlienEntityTypes INSTANCE = new AVPPrometheusAlienEntityTypes();

    public static AVPPrometheusAlienEntityTypes getInstance() {
        return INSTANCE;
    }

    public static final GameObject<EntityType<Deacon>> DEACON = AVPEntityTypes.registerLiving(
        "deacon",
        0x8896A5,
        0x495256,
        EntityType.Builder.of(Deacon::new, MobCategory.MONSTER)
    );

    public static final GameObject<EntityType<DeaconAdult>> DEACON_ADULT = AVPEntityTypes.registerLiving(
        "deacon_adult",
        0x8896A5,
        0x495256,
        EntityType.Builder.of(DeaconAdult::new, MobCategory.MONSTER)
    );

    public static final GameObject<EntityType<Trilobite>> TRILOBITE = AVPEntityTypes.registerLiving(
        "trilobite",
        0xCCC2A5,
        0x987379,
        EntityType.Builder.of(Trilobite::new, MobCategory.MONSTER)
    );

    public static final GameObject<EntityType<TrilobiteBaby>> TRILOBITE_BABY = AVPEntityTypes.registerLiving(
        "trilobite_baby",
        0xCCC2A5,
        0x987379,
        EntityType.Builder.of(TrilobiteBaby::new, MobCategory.MONSTER)
    );

    @Override
    public void register() {}
}
