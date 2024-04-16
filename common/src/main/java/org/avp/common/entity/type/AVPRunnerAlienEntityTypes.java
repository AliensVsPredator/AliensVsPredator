package org.avp.common.entity.type;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;

import org.avp.api.GameObject;
import org.avp.common.entity.living.ChestbursterRunner;
import org.avp.common.entity.living.Crusher;
import org.avp.common.entity.living.DroneRunner;
import org.avp.common.entity.living.WarriorRunner;

public class AVPRunnerAlienEntityTypes {

    public static final GameObject<EntityType<ChestbursterRunner>> CHESTBURSTER_RUNNER = AVPEntityTypes.registerLiving(
        "chestburster_runner",
        0xD8B877,
        0xF7E2B4,
        EntityType.Builder.of(ChestbursterRunner::new, MobCategory.MONSTER)
            .sized(0.75F, 0.98F)
    );

    public static final GameObject<EntityType<Crusher>> CRUSHER = AVPEntityTypes.registerLiving(
        "crusher",
        0x2E2921,
        0x534A3B,
        EntityType.Builder.of(Crusher::new, MobCategory.MONSTER)
            .sized(1.48F, 2.48F)
    );

    public static final GameObject<EntityType<DroneRunner>> DRONE_RUNNER = AVPEntityTypes.registerLiving(
        "drone_runner",
        0x503D34,
        0xA69E85,
        EntityType.Builder.of(DroneRunner::new, MobCategory.MONSTER)
            .sized(0.98F, 1.98F)
    );

    public static final GameObject<EntityType<WarriorRunner>> WARRIOR_RUNNER = AVPEntityTypes.registerLiving(
        "warrior_runner",
        0x1A1917,
        0x61615E,
        EntityType.Builder.of(WarriorRunner::new, MobCategory.MONSTER)
            .sized(0.98F, 1.98F)
    );

    public static void forceInitialization() {
        // This method doesn't need to do anything
    }

    private AVPRunnerAlienEntityTypes() {
        throw new UnsupportedOperationException();
    }
}
