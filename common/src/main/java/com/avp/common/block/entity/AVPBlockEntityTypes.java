package com.avp.common.block.entity;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;

import java.util.function.Supplier;

import com.avp.common.block.TempAVPBlocks;
import com.avp.common.block.entity.resin_node.ResinNodeBlockEntity;
import com.avp.common.registry.AVPDeferredHolder;
import com.avp.service.Services;

public class AVPBlockEntityTypes {

    public static final AVPDeferredHolder<BlockEntityType<ResinNodeBlockEntity>> RESIN_NODE = register(
        "resin_node",
        () -> BlockEntityType.Builder.of(
            ResinNodeBlockEntity::new,
            TempAVPBlocks.IRRADIATED_RESIN_NODE.get(),
            TempAVPBlocks.ABERRANT_RESIN_NODE.get(),
            TempAVPBlocks.NETHER_RESIN_NODE.get(),
            TempAVPBlocks.RESIN_NODE.get()
        )
    );

    public static final AVPDeferredHolder<BlockEntityType<IndustrialFurnaceBlockEntity>> INDUSTRIAL_FURNACE = register(
        "industrial_furnace",
        () -> BlockEntityType.Builder.of(IndustrialFurnaceBlockEntity::new, TempAVPBlocks.INDUSTRIAL_FURNACE.get())
    );

    public static final AVPDeferredHolder<BlockEntityType<LeadChestBlockEntity>> LEAD_CHEST = register(
        "lead_chest",
        () -> BlockEntityType.Builder.of(LeadChestBlockEntity::new, TempAVPBlocks.LEAD_CHEST.get())
    );

    public static final AVPDeferredHolder<BlockEntityType<AmmoChestBlockEntity>> AMMO_CHEST = register(
        "ammo_chest",
        () -> BlockEntityType.Builder.of(AmmoChestBlockEntity::new, TempAVPBlocks.AMMO_CHEST.get())
    );

    public static final AVPDeferredHolder<BlockEntityType<DeskTerminalBlockEntity>> DESK_TERMINAL = register(
        "desk_terminal",
        () -> BlockEntityType.Builder.of(DeskTerminalBlockEntity::new, TempAVPBlocks.DESK_TERMINAL_BLOCK.get())
    );

    public static final AVPDeferredHolder<BlockEntityType<TripMineBlockEntity>> TRIP_MINE = register(
        "trip_mine",
        () -> BlockEntityType.Builder.of(TripMineBlockEntity::new, TempAVPBlocks.TRIP_MINE_BLOCK.get())
    );

    public static final AVPDeferredHolder<BlockEntityType<ResonatorBlockEntity>> RESONATOR = register(
        "resonator",
        () -> BlockEntityType.Builder.of(ResonatorBlockEntity::new, TempAVPBlocks.RESONATOR_BLOCK.get())
    );

    private static <T extends BlockEntity> AVPDeferredHolder<BlockEntityType<T>> register(
        String id,
        Supplier<BlockEntityType.Builder<T>> builder
    ) {
        return Services.REGISTRY.register(BuiltInRegistries.BLOCK_ENTITY_TYPE, id, () -> builder.get().build(null));
    }

    public static void initialize() {}
}
