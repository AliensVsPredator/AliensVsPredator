package com.avp.common.block.entity;

import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;

import com.avp.AVPResources;
import com.avp.common.block.AVPBlocks;
import com.avp.common.block.entity.resin_node.ResinNodeBlockEntity;

public class AVPBlockEntityTypes {

    public static final BlockEntityType<ResinNodeBlockEntity> RESIN_NODE = register(
        "resin_node",
        BlockEntityType.Builder.of(
            ResinNodeBlockEntity::new,
            AVPBlocks.IRRADIATED_RESIN_NODE,
            AVPBlocks.ABERRANT_RESIN_NODE,
            AVPBlocks.NETHER_RESIN_NODE,
            AVPBlocks.RESIN_NODE
        )
    );

    public static final BlockEntityType<IndustrialFurnaceBlockEntity> INDUSTRIAL_FURNACE = register(
        "industrial_furnace",
        BlockEntityType.Builder.of(IndustrialFurnaceBlockEntity::new, AVPBlocks.INDUSTRIAL_FURNACE)
    );

    public static final BlockEntityType<LeadChestBlockEntity> LEAD_CHEST = register(
        "lead_chest",
        BlockEntityType.Builder.of(LeadChestBlockEntity::new, AVPBlocks.LEAD_CHEST)
    );

    public static final BlockEntityType<AmmoChestBlockEntity> AMMO_CHEST = register(
        "ammo_chest",
        BlockEntityType.Builder.of(AmmoChestBlockEntity::new, AVPBlocks.AMMO_CHEST)
    );

    public static final BlockEntityType<SentryTurretBlockEntity> SENTRY_TURRET = register(
        "sentry_turret",
        BlockEntityType.Builder.of(SentryTurretBlockEntity::new, AVPBlocks.SENTRY_TURRET)
    );

    public static final BlockEntityType<DeskTerminalBlockEntity> DESK_TERMINAL = register(
        "desk_terminal",
        BlockEntityType.Builder.of(DeskTerminalBlockEntity::new, AVPBlocks.DESK_TERMINAL_BLOCK)
    );

    public static final BlockEntityType<TripMineBlockEntity> TRIP_MINE = register(
        "trip_mine",
        BlockEntityType.Builder.of(TripMineBlockEntity::new, AVPBlocks.TRIP_MINE_BLOCK)
    );

    public static final BlockEntityType<ResonatorBlockEntity> RESONATOR = register(
        "resonator",
        BlockEntityType.Builder.of(ResonatorBlockEntity::new, AVPBlocks.RESONATOR_BLOCK)
    );

    private static <T extends BlockEntity> BlockEntityType<T> register(String id, BlockEntityType.Builder<T> builder) {
        var resourceLocation = AVPResources.location(id);
        return Registry.register(BuiltInRegistries.BLOCK_ENTITY_TYPE, resourceLocation, builder.build(null));
    }

    public static void initialize() {}
}
