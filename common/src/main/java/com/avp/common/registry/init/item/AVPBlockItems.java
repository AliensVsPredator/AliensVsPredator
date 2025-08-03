package com.avp.common.registry.init.item;

import com.alien.common.gameplay.item.RoyalJellyBlockItem;
import com.human.common.gameplay.block_item.AmmoChestBlockItem;
import com.human.common.gameplay.block_item.LeadChestBlockItem;
import com.human.common.gameplay.block_item.SentryTurretBlockItem;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;

import java.util.Arrays;
import java.util.Collections;
import java.util.Map;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import com.avp.common.registry.AVPDeferredHolder;
import com.avp.common.registry.init.block.AVPBlocks;
import com.avp.common.registry.init.block.CoreBlocks;
import com.avp.service.Services;

public class AVPBlockItems {

    public static final AVPDeferredHolder<BlockItem> ALUMINUM_BLOCK = register("aluminum_block", CoreBlocks.ALUMINUM_BLOCK);

    public static final AVPDeferredHolder<BlockItem> AMMO_CHEST = registerWithSupplier("ammo_chest", AmmoChestBlockItem::new);

    public static final AVPDeferredHolder<BlockItem> ASH_BLOCK = register("ash_block", CoreBlocks.ASH_BLOCK);

    public static final AVPDeferredHolder<BlockItem> AUTUNITE_BLOCK = register("autunite_block", CoreBlocks.AUTUNITE_BLOCK);

    public static final AVPDeferredHolder<BlockItem> AUTUNITE_ORE = register("autunite_ore", CoreBlocks.AUTUNITE_ORE);

    public static final AVPDeferredHolder<BlockItem> BAUXITE_ORE = register("bauxite_ore", CoreBlocks.BAUXITE_ORE);

    public static final AVPDeferredHolder<BlockItem> BLUEPRINT_BLOCK = register("blueprint_block", AVPBlocks.BLUEPRINT_BLOCK);

    public static final AVPDeferredHolder<BlockItem> CABLE = register("cable", AVPBlocks.CABLE);

    public static final AVPDeferredHolder<BlockItem> BRASS_BLOCK = register("brass_block", CoreBlocks.BRASS_BLOCK);

    public static final AVPDeferredHolder<BlockItem> DEEPSLATE_TITANIUM_ORE = register(
        "deepslate_titanium_ore",
        CoreBlocks.DEEPSLATE_TITANIUM_ORE
    );

    public static final AVPDeferredHolder<BlockItem> DEEPSLATE_ZINC_ORE = register("deepslate_zinc_ore", CoreBlocks.DEEPSLATE_ZINC_ORE);

    public static final AVPDeferredHolder<BlockItem> DESK_TERMINAL_BLOCK = register("desk_terminal", AVPBlocks.DESK_TERMINAL_BLOCK);

    public static final AVPDeferredHolder<BlockItem> GALENA_ORE = register("galena_ore", CoreBlocks.GALENA_ORE);

    public static final AVPDeferredHolder<BlockItem> INDUSTRIAL_FURNACE_BLOCK = register(
        "industrial_furnace_block",
        AVPBlocks.INDUSTRIAL_FURNACE
    );

    public static final AVPDeferredHolder<BlockItem> INFINITE_POWER_GENERATOR = register(
        "infinite_power_generator",
        AVPBlocks.INFINITE_POWER_GENERATOR
    );

    public static final AVPDeferredHolder<BlockItem> LEAD_BLOCK = register("lead_block", CoreBlocks.LEAD_BLOCK);

    public static final AVPDeferredHolder<BlockItem> LEAD_CHEST = registerWithSupplier("lead_chest", LeadChestBlockItem::new);

    public static final AVPDeferredHolder<BlockItem> LITHIUM_BLOCK = register("lithium_block", CoreBlocks.LITHIUM_BLOCK);

    public static final AVPDeferredHolder<BlockItem> LITHIUM_ORE = register("lithium_ore", CoreBlocks.LITHIUM_ORE);

    public static final AVPDeferredHolder<BlockItem> MONAZITE_ORE = register("monazite_ore", CoreBlocks.MONAZITE_ORE);

    public static final AVPDeferredHolder<BlockItem> NUKE_BLOCK = register("nuke", AVPBlocks.NUKE_BLOCK);

    public static final AVPDeferredHolder<BlockItem> RAW_BAUXITE_BLOCK = register("raw_bauxite_block", CoreBlocks.RAW_BAUXITE_BLOCK);

    public static final AVPDeferredHolder<BlockItem> RAW_GALENA_BLOCK = register("raw_galena_block", CoreBlocks.RAW_GALENA_BLOCK);

    public static final AVPDeferredHolder<BlockItem> RAW_MONAZITE_BLOCK = register("raw_monazite_block", CoreBlocks.RAW_MONAZITE_BLOCK);

    public static final AVPDeferredHolder<BlockItem> RAW_TITANIUM_BLOCK = register("raw_titanium_block", CoreBlocks.RAW_TITANIUM_BLOCK);

    public static final AVPDeferredHolder<BlockItem> RAW_ZINC_BLOCK = register("raw_zinc_block", CoreBlocks.RAW_ZINC_BLOCK);

    public static final AVPDeferredHolder<BlockItem> RAZOR_WIRE = register("razor_wire", AVPBlocks.RAZOR_WIRE);

    public static final AVPDeferredHolder<BlockItem> REDSTONE_GENERATOR = register("redstone_generator", AVPBlocks.REDSTONE_GENERATOR);

    public static final AVPDeferredHolder<BlockItem> RESONATOR_BLOCK = register("resonator", AVPBlocks.RESONATOR_BLOCK);

    public static final AVPDeferredHolder<BlockItem> ROYAL_JELLY_BLOCK = registerWithSupplier(
        "royal_jelly_block",
        RoyalJellyBlockItem::new
    );

    public static final AVPDeferredHolder<BlockItem> SENTRY_TURRET = registerWithSupplier("sentry_turret", SentryTurretBlockItem::new);

    public static final AVPDeferredHolder<BlockItem> SILICA_GRAVEL = register("silica_gravel", CoreBlocks.SILICA_GRAVEL);

    // TODO: Change this to "silicon_block" with 0.2.0.
    public static final AVPDeferredHolder<BlockItem> SILICON_BLOCK = register("raw_silica_block", CoreBlocks.SILICON_BLOCK);

    public static final AVPDeferredHolder<BlockItem> TRINITITE_BLOCK = register("trinitite_block", CoreBlocks.TRINITITE_BLOCK);

    public static final AVPDeferredHolder<BlockItem> URANIUM_BLOCK = register("uranium_block", CoreBlocks.URANIUM_BLOCK);

    public static final AVPDeferredHolder<BlockItem> ZINC_BLOCK = register("zinc_block", CoreBlocks.ZINC_BLOCK);

    public static final AVPDeferredHolder<BlockItem> ZINC_ORE = register("zinc_ore", CoreBlocks.ZINC_ORE);

    public static final Map<DyeColor, Supplier<BlockItem>> DYE_COLOR_TO_CONCRETE_SLAB =
        Collections.unmodifiableMap(
            Arrays.stream(DyeColor.values())
                .collect(
                    Collectors.toMap(
                        Function.identity(),
                        dyeColor -> register(
                            dyeColor.getName() + "_concrete_slab",
                            AVPBlocks.DYE_COLOR_TO_CONCRETE_SLAB.get(dyeColor)
                        )
                    )
                )
        );

    public static final Map<DyeColor, Supplier<BlockItem>> DYE_COLOR_TO_CONCRETE_STAIRS =
        Collections.unmodifiableMap(
            Arrays.stream(DyeColor.values())
                .collect(
                    Collectors.toMap(
                        Function.identity(),
                        dyeColor -> register(
                            dyeColor.getName() + "_concrete_stairs",
                            AVPBlocks.DYE_COLOR_TO_CONCRETE_STAIRS.get(dyeColor)
                        )
                    )
                )
        );

    public static AVPDeferredHolder<BlockItem> register(String id, Supplier<? extends Block> blockSupplier) {
        return register(id, blockSupplier, new Item.Properties());
    }

    public static AVPDeferredHolder<BlockItem> register(String id, Supplier<? extends Block> blockSupplier, Item.Properties properties) {
        return registerWithSupplier(id, () -> new BlockItem(blockSupplier.get(), properties));
    }

    public static AVPDeferredHolder<BlockItem> registerWithSupplier(String id, Supplier<BlockItem> blockItemSupplier) {
        return AVPItems.register(id, blockItemSupplier);
    }

    public static void initialize() {
        Services.REGISTRY.registerAzureLibIdentity(RESONATOR_BLOCK);
        Services.REGISTRY.registerAzureLibIdentity(SENTRY_TURRET);
    }
}
