package com.avp.common.registry.init.block;

import com.human.common.gameplay.block.AmmoChestBlock;
import com.human.common.gameplay.block.DeskTerminalBlock;
import com.human.common.gameplay.block.IndustrialFurnaceBlock;
import com.human.common.gameplay.block.LeadChestBlock;
import com.human.common.gameplay.block.NukeBlock;
import com.human.common.gameplay.block.RazorWireBlock;
import com.human.common.gameplay.block.RedstoneGeneratorBlock;
import com.human.common.gameplay.block.SentryTurretBlock;
import com.human.common.gameplay.block.power.BatteryBlock;
import com.human.common.gameplay.block.power.CableBlock;
import com.human.common.gameplay.block.power.InfinitePowerGeneratorBlock;
import com.human.common.gameplay.block.power.ResonatorBlock;
import com.human.common.gameplay.block.power.SolarPanelBlock;
import com.human.common.gameplay.block.power.ThermalGeneratorBlock;
import com.human.common.gameplay.block.power.WindTurbineBlock;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.SlabBlock;
import net.minecraft.world.level.block.StairBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import com.avp.common.gameplay.block.property.BlockProperties;
import com.avp.common.gameplay.block.property.BlockPropertyBuilder;
import com.avp.common.registry.AVPDeferredHolder;
import com.avp.service.Services;

public class AVPBlocks {

    private static final List<AVPDeferredHolder<? extends Block>> HOLDERS = new ArrayList<>();

    public static List<AVPDeferredHolder<? extends Block>> getAll() {
        return Collections.unmodifiableList(HOLDERS);
    }

    public static final AVPDeferredHolder<Block> AMMO_CHEST = register(
        "ammo_chest",
        () -> new AmmoChestBlock(BlockProperties.LEAD.build())
    );

    public static final AVPDeferredHolder<Block> BATTERY = register(
        "battery",
        // TODO: Use custom properties here.
        () -> new BatteryBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.COBBLESTONE))
    );

    public static final AVPDeferredHolder<Block> BLUEPRINT_BLOCK = register("blueprint_block", BlockProperties.STEEL);

    public static final AVPDeferredHolder<Block> CABLE = register(
        "cable",
        // TODO: Use custom properties here.
        () -> new CableBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.COBBLESTONE))
    );

    public static final AVPDeferredHolder<Block> DESK_TERMINAL_BLOCK = register(
        "desk_terminal",
        () -> new DeskTerminalBlock(BlockProperties.STEEL.build().noOcclusion())
    );

    public static final AVPDeferredHolder<Block> INDUSTRIAL_FURNACE = register(
        "industrial_furnace_block",
        // TODO: Use custom properties here.
        () -> new IndustrialFurnaceBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.BLAST_FURNACE))
    );

    public static final AVPDeferredHolder<Block> INFINITE_POWER_GENERATOR = register(
        "infinite_power_generator",
        // TODO: Use custom properties here.
        () -> new InfinitePowerGeneratorBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.COBBLESTONE))
    );

    public static final AVPDeferredHolder<Block> LEAD_CHEST = register(
        "lead_chest",
        () -> new LeadChestBlock(BlockProperties.LEAD.build())
    );

    public static final AVPDeferredHolder<Block> NUKE_BLOCK = register("nuke", () -> new NukeBlock(BlockProperties.NUKE.build()));

    public static final AVPDeferredHolder<Block> RAZOR_WIRE = register(
        "razor_wire",
        () -> new RazorWireBlock(BlockProperties.RAZOR_WIRE.build())
    );

    public static final AVPDeferredHolder<Block> REDSTONE_GENERATOR = register(
        "redstone_generator",
        () -> new RedstoneGeneratorBlock(BlockProperties.STEEL.build().randomTicks())
    );

    public static final AVPDeferredHolder<Block> RESONATOR_BLOCK = register(
        "resonator",
        () -> new ResonatorBlock(BlockProperties.STEEL.build().noOcclusion())
    );

    public static final AVPDeferredHolder<Block> SENTRY_TURRET = register("sentry_turret", SentryTurretBlock::new);

    public static final AVPDeferredHolder<Block> SOLAR_PANEL = register(
        "solar_panel",
        // TODO: Use custom properties here.
        () -> new SolarPanelBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.COBBLESTONE))
    );

    public static final AVPDeferredHolder<Block> THERMAL_GENERATOR = register(
        "thermal_generator",
        // TODO: Use custom properties here.
        () -> new ThermalGeneratorBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.COBBLESTONE))
    );

    public static final AVPDeferredHolder<Block> WIND_TURBINE = register(
        "wind_turbine",
        // TODO: Use custom properties here.
        () -> new WindTurbineBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.COBBLESTONE))
    );

    public static final Map<DyeColor, AVPDeferredHolder<Block>> DYE_COLOR_TO_CONCRETE_SLAB =
        Collections.unmodifiableMap(
            Arrays.stream(DyeColor.values())
                .collect(
                    Collectors.toMap(
                        Function.identity(),
                        dyeColor -> register(
                            dyeColor.getName() + "_concrete_slab",
                            () -> new SlabBlock(BlockProperties.DYE_COLOR_TO_CONCRETE_PROPERTIES.get(dyeColor).build())
                        )
                    )
                )
        );

    public static final Map<DyeColor, AVPDeferredHolder<Block>> DYE_COLOR_TO_CONCRETE_STAIRS =
        Collections.unmodifiableMap(
            Arrays.stream(DyeColor.values())
                .collect(
                    Collectors.toMap(
                        Function.identity(),
                        dyeColor -> register(
                            dyeColor.getName() + "_concrete_stairs",
                            () -> new StairBlock(
                                BlockProperties.DYE_COLOR_TO_CONCRETE_BLOCKS.get(dyeColor).defaultBlockState(),
                                BlockProperties.DYE_COLOR_TO_CONCRETE_PROPERTIES.get(dyeColor).build()
                            )
                        )
                    )
                )
        );

    public static AVPDeferredHolder<Block> register(String id, BlockPropertyBuilder blockPropertyBuilder) {
        return register(id, () -> new Block(blockPropertyBuilder.build()));
    }

    public static <T extends Block> AVPDeferredHolder<T> register(String id, Supplier<T> blockSupplier) {
        var holder = Services.REGISTRY.register(BuiltInRegistries.BLOCK, id, blockSupplier);
        HOLDERS.add(holder);
        return holder;
    }

    public static void initialize() {}
}
