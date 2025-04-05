package com.avp.common.block.entity;

import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;

import com.avp.AVPResources;
import com.avp.common.block.AVPBlocks;
import com.avp.common.block.entity.resin_node.ResinNodeBlockEntity;

public class BlockEntityTypes {

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

    public static final BlockEntityType<IndustrialFurnaceBE> INDUSTRIAL_FURNACE_BE = register(
        "industrial_furnace_be",
        BlockEntityType.Builder.of(IndustrialFurnaceBE::new, AVPBlocks.INDUSTRIAL_FURNACE)
    );

    public static final BlockEntityType<LeadChestBE> LEAD_CHEST_BE = register(
        "lead_chest_be",
        BlockEntityType.Builder.of(LeadChestBE::new, AVPBlocks.LEAD_CHEST)
    );

    public static final BlockEntityType<AmmoChestBE> AMMO_CHEST_BE = register(
        "ammo_chest_be",
        BlockEntityType.Builder.of(AmmoChestBE::new, AVPBlocks.AMMO_CHEST)
    );

    public static final BlockEntityType<SentryTurretBE> SENTRY_TURRET_BE = register(
        "sentry_turret_be",
        BlockEntityType.Builder.of(SentryTurretBE::new, AVPBlocks.SENTRY_TURRET)
    );

    public static final BlockEntityType<DeskTerminalBE> DESK_TERMINAL_BE = register(
        "desk_terminal_be",
        BlockEntityType.Builder.of(DeskTerminalBE::new, AVPBlocks.DESK_TERMINAL_BLOCK)
    );

    public static final BlockEntityType<TripMineBE> TRIP_MINE_BE = register(
        "trip_mine_be",
        BlockEntityType.Builder.of(TripMineBE::new, AVPBlocks.TRIP_MINE_BLOCK)
    );

    public static final BlockEntityType<ResonatorBE> RESONATOR_BE = register(
        "resonator_be",
        BlockEntityType.Builder.of(ResonatorBE::new, AVPBlocks.RESONATOR_BLOCK)
    );

    private static <T extends BlockEntity> BlockEntityType<T> register(String id, BlockEntityType.Builder<T> builder) {
        var resourceLocation = AVPResources.location(id);
        return Registry.register(BuiltInRegistries.BLOCK_ENTITY_TYPE, resourceLocation, builder.build(null));
    }

    public static void initialize() {}
}
