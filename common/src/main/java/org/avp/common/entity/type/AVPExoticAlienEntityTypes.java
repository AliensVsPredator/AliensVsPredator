package org.avp.common.entity.type;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;

import org.avp.api.GameObject;
import org.avp.common.entity.living.*;

public class AVPExoticAlienEntityTypes {

    public static final GameObject<EntityType<DeaconAdultEngineer>> DEACON_ADULT_ENGINEER = AVPEntityTypes
        .registerLiving(
            "deacon_adult_engineer",
            0x8896A5,
            0x495256,
            EntityType.Builder.of(DeaconAdultEngineer::new, MobCategory.MONSTER)
                .sized(0.98F, 2.98F)
        );

    public static final GameObject<EntityType<Dracoburster>> DRACOBURSTER = AVPEntityTypes.registerLiving(
        "chestburster_draco",
        0xD8B877,
        0xF7E2B4,
        EntityType.Builder.of(Dracoburster::new, MobCategory.MONSTER)
            .sized(0.98F, 1.98F)
    );

    public static final GameObject<EntityType<Dracomorph>> DRACOMORPH = AVPEntityTypes.registerLiving(
        "dracomorph",
        0x212121,
        0x535353,
        EntityType.Builder.of(Dracomorph::new, MobCategory.MONSTER)
            .sized(1.98F, 2.98F)
    );

    public static final GameObject<EntityType<Octohugger>> OCTOHUGGER = AVPEntityTypes.registerLiving(
        "octohugger",
        0xC2BCC8,
        0xC09CAE,
        EntityType.Builder.of(Octohugger::new, MobCategory.MONSTER)
            .sized(0.25F, 0.75F)
    );

    public static final GameObject<EntityType<OvamorphDraco>> OVAMORPH_DRACO = AVPEntityTypes.registerLiving(
        "ovamorph_draco",
        0x2F2F2F,
        0xA36762,
        EntityType.Builder.of(OvamorphDraco::new, MobCategory.MONSTER)
            .sized(0.98F, 0.98F)
    );

    public static final GameObject<EntityType<Ultramorph>> ULTRAMORPH = AVPEntityTypes.registerLiving(
        "ultramorph",
        0x3E474E,
        0x696E76,
        EntityType.Builder.of(Ultramorph::new, MobCategory.MONSTER)
            .sized(0.98F, 1.98F)
    );

    public static void forceInitialization() {
        // This method doesn't need to do anything
    }

    private AVPExoticAlienEntityTypes() {
        throw new UnsupportedOperationException();
    }
}
