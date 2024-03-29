package org.avp.common.entity;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;

import org.avp.common.entity.living.*;
import org.avp.common.registry.AVPRegistry;
import org.avp.api.GameObject;

/**
 * @author Boston Vanseghi
 */
public class AVPExoticAlienEntityTypes implements AVPRegistry {

    private static final AVPExoticAlienEntityTypes INSTANCE = new AVPExoticAlienEntityTypes();

    public static AVPExoticAlienEntityTypes getInstance() {
        return INSTANCE;
    }

    public static final GameObject<EntityType<DeaconAdultEngineer>> DEACON_ADULT_ENGINEER = AVPEntityTypes
        .registerLiving(
            "deacon_adult_engineer",
            0x8896A5,
            0x495256,
            EntityType.Builder.of(DeaconAdultEngineer::new, MobCategory.MONSTER)
        );

    public static final GameObject<EntityType<Dracoburster>> DRACOBURSTER = AVPEntityTypes.registerLiving(
        "dracoburster",
        0xD8B877,
        0xF7E2B4,
        EntityType.Builder.of(Dracoburster::new, MobCategory.MONSTER)
    );

    public static final GameObject<EntityType<Dracomorph>> DRACOMORPH = AVPEntityTypes.registerLiving(
        "dracomorph",
        0x212121,
        0x535353,
        EntityType.Builder.of(Dracomorph::new, MobCategory.MONSTER)
    );

    public static final GameObject<EntityType<Octohugger>> OCTOHUGGER = AVPEntityTypes.registerLiving(
        "octohugger",
        0xC2BCC8,
        0xC09CAE,
        EntityType.Builder.of(Octohugger::new, MobCategory.MONSTER)
    );

    public static final GameObject<EntityType<OvamorphDraco>> OVAMORPH_DRACO = AVPEntityTypes.registerLiving(
        "ovamorph_draco",
        0x2F2F2F,
        0xA36762,
        EntityType.Builder.of(OvamorphDraco::new, MobCategory.MONSTER)
    );

    public static final GameObject<EntityType<Ultramorph>> ULTRAMORPH = AVPEntityTypes.registerLiving(
        "ultramorph",
        0x3E474E,
        0x696E76,
        EntityType.Builder.of(Ultramorph::new, MobCategory.MONSTER)
    );

    @Override
    public void register() {}
}
