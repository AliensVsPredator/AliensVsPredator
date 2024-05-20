package org.avp.common.entity.type;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;

import org.avp.api.Holder;
import org.avp.common.entity.living.*;
import org.avp.common.registry.AVPSimpleDeferredEntityTypeRegistry;

public class AVPExoticAlienEntityTypes extends AVPSimpleDeferredEntityTypeRegistry {

    public static final AVPExoticAlienEntityTypes INSTANCE = new AVPExoticAlienEntityTypes();

    public final Holder<EntityType<DeaconAdultEngineer>> deaconAdultEngineer = createMobHolder(
        "deacon_adult_engineer",
        0x8896A5,
        0x495256,
        EntityType.Builder.of(DeaconAdultEngineer::new, MobCategory.MONSTER)
            .sized(0.98F, 2.98F)
    );

    public final Holder<EntityType<ChestbursterDraco>> chestbursterDraco = createMobHolder(
        "chestburster_draco",
        0xD8B877,
        0xF7E2B4,
        EntityType.Builder.of(ChestbursterDraco::new, MobCategory.MONSTER)
            .sized(0.75F, 0.98F)
    );

    public final Holder<EntityType<Dracomorph>> dracomorph = createMobHolder(
        "dracomorph",
        0x212121,
        0x535353,
        EntityType.Builder.of(Dracomorph::new, MobCategory.MONSTER)
            .sized(1.98F, 2.98F)
    );

    public final Holder<EntityType<Octohugger>> octohugger = createMobHolder(
        "octohugger",
        0xC2BCC8,
        0xC09CAE,
        EntityType.Builder.of(Octohugger::new, MobCategory.MONSTER)
            .sized(0.25F, 0.75F)
    );

    public final Holder<EntityType<OvamorphDraco>> ovamorphDraco = createMobHolder(
        "ovamorph_draco",
        0x2F2F2F,
        0xA36762,
        EntityType.Builder.of(OvamorphDraco::new, MobCategory.MONSTER)
            .sized(0.98F, 0.98F)
    );

    public final Holder<EntityType<Ultramorph>> ultramorph = createMobHolder(
        "ultramorph",
        0x3E474E,
        0x696E76,
        EntityType.Builder.of(Ultramorph::new, MobCategory.MONSTER)
            .sized(0.98F, 1.98F)
    );

    private AVPExoticAlienEntityTypes() {}
}
