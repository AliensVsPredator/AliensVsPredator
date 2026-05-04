package com.blib.mod.common.registry.init;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;

import com.blib.api.common.dismemberment.v1.entity.DismemberedLimbEntity;
import com.blib.api.common.entity.v1.SilencedEntityTypeBuilder;
import com.blib.api.common.registry.v1.BLibHolder;
import com.blib.api.common.registry.v1.BLibRegistry;
import com.blib.mod.BLib;

public class BLibEntityTypes {

    private static final BLibRegistry<EntityType<?>> REGISTRY = BLib.MOD.registries().create(BuiltInRegistries.ENTITY_TYPE);

    public static final BLibHolder<EntityType<DismemberedLimbEntity>> DISMEMBERED_LIMB = REGISTRY.createHolder(
        "dismembered_limb",
        () -> ((SilencedEntityTypeBuilder) EntityType.Builder.<DismemberedLimbEntity>of(DismemberedLimbEntity::new, MobCategory.MISC)
            .sized(0.5F, 0.5F)
            .clientTrackingRange(8))
            .blib$buildWithoutDataFixerCheck()
    );

    public static void initialize() {
        REGISTRY.registerAll();
    }
}
