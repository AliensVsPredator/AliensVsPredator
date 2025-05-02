package com.avp.fabric.common.block;

import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockBehaviour;

import com.avp.AVPResources;
import com.avp.common.block.BlockProperties;
import com.avp.common.block.BlockPropertyBuilder;

public class AVPBlocks {

    public static final Block AMMO_CHEST = register(new AmmoChestBlock(BlockProperties.LEAD.build()), "ammo_chest");

    public static final Block REDSTONE_GENERATOR = register(
        new RedstoneGeneratorBlock(BlockProperties.STEEL.build().randomTicks()),
        "redstone_generator"
    );

    public static final Block DESK_TERMINAL_BLOCK = register(
        new DeskTerminalBlock(BlockProperties.STEEL.build().noOcclusion()),
        "desk_terminal"
    );

    public static final Block LEAD_CHEST = register(new LeadChestBlock(BlockProperties.LEAD.build()), "lead_chest");

    public static final Block NUKE_BLOCK = register(new NukeBlock(BlockProperties.NUKE.build()), "nuke");

    public static final Block TRIP_MINE_BLOCK = register(new TripMineBlock(BlockProperties.TITANIUM.build().noOcclusion()), "trip_mine");

    public static final Block RESONATOR_BLOCK = register(new ResonatorBlock(BlockProperties.STEEL.build().noOcclusion()), "resonator");

    public static final Block SENTRY_TURRET = register(new SentryTurretBlock(BlockProperties.STEEL.build().noOcclusion()), "sentry_turret");

    public static final Block INDUSTRIAL_FURNACE = register(
        new IndustrialFurnaceBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.STONE)),
        "industrial_furnace_block"
    );

    public static final Block RAZOR_WIRE = register(new RazorWireBlock(BlockProperties.RAZOR_WIRE.build()), "razor_wire");

    // Doors And Trapdoors

    public static Block registerRadiatedBlock(BlockBehaviour.Properties properties, String id) {
        return register(new RadiatedBlock(properties), id);
    }

    public static Block register(BlockPropertyBuilder builder, String id) {
        return register(builder.build(), id);
    }

    public static Block register(BlockBehaviour.Properties properties, String id) {
        return register(new Block(properties), id);
    }

    public static Block register(Block block, String id) {
        return Registry.register(BuiltInRegistries.BLOCK, AVPResources.location(id), block);
    }

    public static void initialize() {}
}
