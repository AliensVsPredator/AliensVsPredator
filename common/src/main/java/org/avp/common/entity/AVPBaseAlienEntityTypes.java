package org.avp.common.entity;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;

import org.avp.common.entity.living.*;
import org.avp.common.registry.AVPRegistry;
import org.avp.api.GameObject;

/**
 * @author Boston Vanseghi
 */
public class AVPBaseAlienEntityTypes implements AVPRegistry {

    private static final AVPBaseAlienEntityTypes INSTANCE = new AVPBaseAlienEntityTypes();

    public static AVPBaseAlienEntityTypes getInstance() {
        return INSTANCE;
    }

    public static final GameObject<EntityType<Chestburster>> CHESTBURSTER = AVPEntityTypes.registerLiving(
        "chestburster",
        0xD8B877,
        0xF7E2B4,
        EntityType.Builder.of(Chestburster::new, MobCategory.MONSTER)
    );

    public static final GameObject<EntityType<Drone>> DRONE = AVPEntityTypes.registerLiving(
        "drone",
        0x010202,
        0xDFE2E4,
        EntityType.Builder.of(Drone::new, MobCategory.MONSTER)
    );

    public static final GameObject<EntityType<Facehugger>> FACEHUGGER = AVPEntityTypes.registerLiving(
        "facehugger",
        0xE4D597,
        0xA55863,
        EntityType.Builder.of(Facehugger::new, MobCategory.MONSTER)
    );

    public static final GameObject<EntityType<FacehuggerRoyal>> FACEHUGGER_ROYAL = AVPEntityTypes.registerLiving(
        "facehugger_royal",
        0x81785E,
        0x583A3A,
        EntityType.Builder.of(FacehuggerRoyal::new, MobCategory.MONSTER)
    );

    public static final GameObject<EntityType<Ovamorph>> OVAMORPH = AVPEntityTypes.registerLiving(
        "ovamorph",
        0x615B45,
        0xBF7872,
        EntityType.Builder.of(Ovamorph::new, MobCategory.MONSTER)
    );

    public static final GameObject<EntityType<Praetorian>> PRAETORIAN = AVPEntityTypes.registerLiving(
        "praetorian",
        0x010202,
        0x363534,
        EntityType.Builder.of(Praetorian::new, MobCategory.MONSTER)
    );

    public static final GameObject<EntityType<Queen>> QUEEN = AVPEntityTypes.registerLiving(
        "queen",
        0x010202,
        0x363534,
        EntityType.Builder.of(Queen::new, MobCategory.MONSTER)
    );

    public static final GameObject<EntityType<Warrior>> WARRIOR = AVPEntityTypes.registerLiving(
        "warrior",
        0x010202,
        0x4A4E55,
        EntityType.Builder.of(Warrior::new, MobCategory.MONSTER)
    );

    @Override
    public void register() {}
}
