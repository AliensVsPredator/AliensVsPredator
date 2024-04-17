package org.avp.common.entity.type;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;

import org.avp.api.GameObject;
import org.avp.common.entity.living.*;
import org.avp.common.registry.AVPSimpleDeferredEntityTypeRegistry;

public class AVPBaseAlienEntityTypes extends AVPSimpleDeferredEntityTypeRegistry {

    public static final AVPBaseAlienEntityTypes INSTANCE = new AVPBaseAlienEntityTypes();

    public final GameObject<EntityType<Boiler>> BOILER = createMobHolder(
        "boiler",
        0x010202,
        0x9DA930,
        EntityType.Builder.of(Boiler::new, MobCategory.MONSTER)
            .sized(0.98F, 2.48F)
    );

    public final GameObject<EntityType<Chestburster>> CHESTBURSTER = createMobHolder(
        "chestburster",
        0xD8B877,
        0xF7E2B4,
        EntityType.Builder.of(Chestburster::new, MobCategory.MONSTER)
            .sized(0.75F, 0.3F)
    );

    public final GameObject<EntityType<Drone>> DRONE = createMobHolder(
        "drone",
        0x010202,
        0xDFE2E4,
        EntityType.Builder.of(Drone::new, MobCategory.MONSTER)
            .sized(0.98F, 1.98F)
    );

    public final GameObject<EntityType<Facehugger>> FACEHUGGER = createMobHolder(
        "facehugger",
        0xE4D597,
        0xA55863,
        EntityType.Builder.of(Facehugger::new, MobCategory.MONSTER)
            .sized(0.75F, 0.3F)
    );

    public final GameObject<EntityType<FacehuggerRoyal>> FACEHUGGER_ROYAL = createMobHolder(
        "facehugger_royal",
        0x81785E,
        0x583A3A,
        EntityType.Builder.of(FacehuggerRoyal::new, MobCategory.MONSTER)
            .sized(0.75F, 0.3F)
    );

    public final GameObject<EntityType<Ovamorph>> OVAMORPH = createMobHolder(
        "ovamorph",
        0x615B45,
        0xBF7872,
        EntityType.Builder.of(Ovamorph::new, MobCategory.MONSTER)
            .sized(0.88F, 0.98F)
    );

    public final GameObject<EntityType<Praetorian>> PRAETORIAN = createMobHolder(
        "praetorian",
        0x010202,
        0x363534,
        EntityType.Builder.of(Praetorian::new, MobCategory.MONSTER)
            .sized(0.98F, 2.48F)
    );

    public final GameObject<EntityType<Queen>> QUEEN = createMobHolder(
        "queen",
        0x010202,
        0x363534,
        EntityType.Builder.of(Queen::new, MobCategory.MONSTER)
            .sized(1.98F, 3.98F)
    );

    public final GameObject<EntityType<Spitter>> SPITTER = createMobHolder(
        "spitter",
        0x010202,
        0x3CDC09,
        EntityType.Builder.of(Spitter::new, MobCategory.MONSTER)
            .sized(0.98F, 1.98F)
    );

    public final GameObject<EntityType<Warrior>> WARRIOR = createMobHolder(
        "warrior",
        0x010202,
        0x4A4E55,
        EntityType.Builder.of(Warrior::new, MobCategory.MONSTER)
            .sized(0.98F, 1.98F)
    );

    private AVPBaseAlienEntityTypes() {}
}
