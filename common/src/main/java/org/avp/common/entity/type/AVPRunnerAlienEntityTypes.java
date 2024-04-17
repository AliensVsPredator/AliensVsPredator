package org.avp.common.entity.type;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;

import org.avp.api.GameObject;
import org.avp.common.entity.living.ChestbursterRunner;
import org.avp.common.entity.living.Crusher;
import org.avp.common.entity.living.DroneRunner;
import org.avp.common.entity.living.WarriorRunner;
import org.avp.common.registry.AVPSimpleDeferredEntityTypeRegistry;

public class AVPRunnerAlienEntityTypes extends AVPSimpleDeferredEntityTypeRegistry {

    public static final AVPRunnerAlienEntityTypes INSTANCE = new AVPRunnerAlienEntityTypes();

    public final GameObject<EntityType<ChestbursterRunner>> CHESTBURSTER_RUNNER = createMobHolder(
        "chestburster_runner",
        0xD8B877,
        0xF7E2B4,
        EntityType.Builder.of(ChestbursterRunner::new, MobCategory.MONSTER)
            .sized(0.75F, 0.98F)
    );

    public final GameObject<EntityType<Crusher>> CRUSHER = createMobHolder(
        "crusher",
        0x2E2921,
        0x534A3B,
        EntityType.Builder.of(Crusher::new, MobCategory.MONSTER)
            .sized(1.48F, 2.48F)
    );

    public final GameObject<EntityType<DroneRunner>> DRONE_RUNNER = createMobHolder(
        "drone_runner",
        0x503D34,
        0xA69E85,
        EntityType.Builder.of(DroneRunner::new, MobCategory.MONSTER)
            .sized(0.98F, 1.98F)
    );

    public final GameObject<EntityType<WarriorRunner>> WARRIOR_RUNNER = createMobHolder(
        "warrior_runner",
        0x1A1917,
        0x61615E,
        EntityType.Builder.of(WarriorRunner::new, MobCategory.MONSTER)
            .sized(0.98F, 1.98F)
    );

    private AVPRunnerAlienEntityTypes() {}
}
