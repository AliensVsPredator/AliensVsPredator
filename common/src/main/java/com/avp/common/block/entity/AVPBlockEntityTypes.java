package com.avp.common.block.entity;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;

import java.util.function.Supplier;

import com.avp.common.block.AVPBlocks;
import com.avp.common.block.entity.resin_node.ResinNodeBlockEntity;
import com.avp.common.registry.AVPDeferredHolder;
import com.avp.service.Services;

public class AVPBlockEntityTypes {

    public static final AVPDeferredHolder<BlockEntityType<ResinNodeBlockEntity>> RESIN_NODE = register(
        "resin_node",
        () -> BlockEntityType.Builder.of(
            ResinNodeBlockEntity::new,
            AVPBlocks.IRRADIATED_RESIN_NODE.get(),
            AVPBlocks.ABERRANT_RESIN_NODE.get(),
            AVPBlocks.NETHER_RESIN_NODE.get(),
            AVPBlocks.RESIN_NODE.get()
        )
    );

    public static final AVPDeferredHolder<BlockEntityType<IndustrialFurnaceBlockEntity>> INDUSTRIAL_FURNACE = register(
        "industrial_furnace",
        () -> BlockEntityType.Builder.of(IndustrialFurnaceBlockEntity::new, AVPBlocks.INDUSTRIAL_FURNACE.get())
    );

    public static final AVPDeferredHolder<BlockEntityType<LeadChestBlockEntity>> LEAD_CHEST = register(
        "lead_chest",
        () -> BlockEntityType.Builder.of(LeadChestBlockEntity::new, AVPBlocks.LEAD_CHEST.get())
    );

    public static final AVPDeferredHolder<BlockEntityType<AmmoChestBlockEntity>> AMMO_CHEST = register(
        "ammo_chest",
        () -> BlockEntityType.Builder.of(AmmoChestBlockEntity::new, AVPBlocks.AMMO_CHEST.get())
    );

    public static final AVPDeferredHolder<BlockEntityType<DeskTerminalBlockEntity>> DESK_TERMINAL = register(
        "desk_terminal",
        () -> BlockEntityType.Builder.of(DeskTerminalBlockEntity::new, AVPBlocks.DESK_TERMINAL_BLOCK.get())
    );

    public static final AVPDeferredHolder<BlockEntityType<TripMineBlockEntity>> TRIP_MINE = register(
        "trip_mine",
        () -> BlockEntityType.Builder.of(TripMineBlockEntity::new, AVPBlocks.TRIP_MINE_BLOCK.get())
    );

    public static final AVPDeferredHolder<BlockEntityType<ResonatorBlockEntity>> RESONATOR = register(
        "resonator",
        () -> BlockEntityType.Builder.of(ResonatorBlockEntity::new, AVPBlocks.RESONATOR_BLOCK.get())
    );

    private static <T extends BlockEntity> AVPDeferredHolder<BlockEntityType<T>> register(
        String id,
        Supplier<BlockEntityType.Builder<T>> builder
    ) {
        return Services.REGISTRY.register(BuiltInRegistries.BLOCK_ENTITY_TYPE, id, () -> builder.get().build(null));
    }

    public static void initialize() {}
}
