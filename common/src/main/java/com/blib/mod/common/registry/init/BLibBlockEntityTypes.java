package com.blib.mod.common.registry.init;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.level.block.entity.BlockEntityType;

import com.blib.api.common.registry.v1.BLibHolder;
import com.blib.api.common.registry.v1.BLibRegistry;
import com.blib.mod.BLib;
import com.blib.mod.common.gameplay.block.entity.TickingLightBlockEntity;

public class BLibBlockEntityTypes {

    private static final BLibRegistry<BlockEntityType<?>> REGISTRY = BLib.MOD.registries().create(BuiltInRegistries.BLOCK_ENTITY_TYPE);

    public static final BLibHolder<BlockEntityType<TickingLightBlockEntity>> TICKING_LIGHT = REGISTRY.createHolder(
        "ticking_light",
        () -> BlockEntityType.Builder.of(
            TickingLightBlockEntity::new,
            BLibBlocks.TICKING_LIGHT.get()
        )
            .build(null)
    );

    public static void initialize() {
        REGISTRY.registerAll();
    }
}
