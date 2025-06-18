package com.avp.common.registry.init.creative_mode_tab.initializer;

import com.alien.common.registry.init.AlienItems;
import net.minecraft.world.item.CreativeModeTab;

import java.util.function.Consumer;

import com.avp.common.registry.init.item.AVPItems;

public class ToolsAndUtilitiesCreativeModeTabInitializer {

    public static final Consumer<CreativeModeTab.Output> OUTPUT_CONSUMER = output -> {
        CreativeModeTabUtil.accept(output, AVPItems.ARMOR_CASE);
        CreativeModeTabUtil.accept(output, AVPItems.SYRINGE);
        CreativeModeTabUtil.accept(output, AVPItems.CANISTER);
        CreativeModeTabUtil.accept(output, AVPItems.WATER_CANISTER);
        CreativeModeTabUtil.accept(output, AVPItems.LAVA_CANISTER);
        CreativeModeTabUtil.accept(output, AVPItems.MILK_CANISTER);
        CreativeModeTabUtil.accept(output, AVPItems.POWDER_SNOW_CANISTER);
        CreativeModeTabUtil.accept(output, AVPItems.STEEL_AXE);
        CreativeModeTabUtil.accept(output, AVPItems.STEEL_HOE);
        CreativeModeTabUtil.accept(output, AVPItems.STEEL_PICKAXE);
        CreativeModeTabUtil.accept(output, AVPItems.STEEL_SHOVEL);
        CreativeModeTabUtil.accept(output, AVPItems.STEEL_SWORD);
        CreativeModeTabUtil.accept(output, AVPItems.TITANIUM_AXE);
        CreativeModeTabUtil.accept(output, AVPItems.TITANIUM_HOE);
        CreativeModeTabUtil.accept(output, AVPItems.TITANIUM_PICKAXE);
        CreativeModeTabUtil.accept(output, AVPItems.TITANIUM_SHOVEL);
        CreativeModeTabUtil.accept(output, AVPItems.TITANIUM_SWORD);
        CreativeModeTabUtil.accept(output, AVPItems.VERITANIUM_AXE);
        CreativeModeTabUtil.accept(output, AVPItems.VERITANIUM_HOE);
        CreativeModeTabUtil.accept(output, AVPItems.VERITANIUM_PICKAXE);
        CreativeModeTabUtil.accept(output, AVPItems.VERITANIUM_SHOVEL);
        CreativeModeTabUtil.accept(output, AVPItems.VERITANIUM_SWORD);
        CreativeModeTabUtil.accept(output, AlienItems.ALIEN_MUSIC_DISC_1);
        CreativeModeTabUtil.accept(output, AVPItems.PREDATOR_MUSIC_DISC_1);
    };
}
