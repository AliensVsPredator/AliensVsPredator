package com.avp.common.registry.init;

import com.alien.common.gameplay.block.entity.resin.node.ResinNodeBlockEntity;
import com.alien.common.gameplay.block.entity.resin.vent.ResinVentBlockEntity;
import com.alien.common.registry.init.block.AlienResinBlocks;
import com.human.common.gameplay.block.entity.AmmoChestBlockEntity;
import com.human.common.gameplay.block.entity.DeskTerminalBlockEntity;
import com.human.common.gameplay.block.entity.IndustrialFurnaceBlockEntity;
import com.human.common.gameplay.block.entity.LeadChestBlockEntity;
import com.human.common.gameplay.block.entity.ResonatorBlockEntity;
import com.human.common.gameplay.block.entity.power.InfinitePowerGeneratorBlockEntity;
import com.predator.common.gameplay.block.entity.TripMineBlockEntity;
import com.predator.common.registry.init.PredatorBlocks;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;

import java.util.function.Supplier;

import com.avp.common.registry.AVPDeferredHolder;
import com.avp.common.registry.init.block.AVPBlocks;
import com.avp.service.Services;

public class AVPBlockEntityTypes {

    public static final AVPDeferredHolder<BlockEntityType<ResinNodeBlockEntity>> RESIN_NODE = register(
        "resin_node",
        () -> BlockEntityType.Builder.of(
            ResinNodeBlockEntity::new,
            AlienResinBlocks.IRRADIATED_RESIN_NODE.get(),
            AlienResinBlocks.ABERRANT_RESIN_NODE.get(),
            AlienResinBlocks.NETHER_RESIN_NODE.get(),
            AlienResinBlocks.RESIN_NODE.get()
        )
    );

    public static final AVPDeferredHolder<BlockEntityType<ResinVentBlockEntity>> RESIN_VENT = register(
        "resin_vent",
        () -> BlockEntityType.Builder.of(
            ResinVentBlockEntity::new,
            AlienResinBlocks.IRRADIATED_RESIN_VENT.get(),
            AlienResinBlocks.ABERRANT_RESIN_VENT.get(),
            AlienResinBlocks.NETHER_RESIN_VENT.get(),
            AlienResinBlocks.RESIN_VENT.get()
        )
    );

    public static final AVPDeferredHolder<BlockEntityType<IndustrialFurnaceBlockEntity>> INDUSTRIAL_FURNACE = register(
        "industrial_furnace",
        () -> BlockEntityType.Builder.of(IndustrialFurnaceBlockEntity::new, AVPBlocks.INDUSTRIAL_FURNACE.get())
    );

    public static final AVPDeferredHolder<BlockEntityType<InfinitePowerGeneratorBlockEntity>> INFINITE_POWER_GENERATOR = register(
        "infinite_power_generator",
        () -> BlockEntityType.Builder.of(InfinitePowerGeneratorBlockEntity::new, AVPBlocks.INFINITE_POWER_GENERATOR.get())
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
        () -> BlockEntityType.Builder.of(TripMineBlockEntity::new, PredatorBlocks.TRIP_MINE_BLOCK.get())
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
