package com.avp.common.creative_mode_tab.initializer;

import net.minecraft.world.item.CreativeModeTab;

import java.util.function.Consumer;

import com.avp.common.item.TempAVPItems;

public class ToolsAndUtilitiesCreativeModeTabInitializer {

    public static final Consumer<CreativeModeTab.Output> OUTPUT_CONSUMER = output -> {
        // CreativeModeTabUtil.accept(output, AVPItems.ARMOR_CASE);
        CreativeModeTabUtil.accept(output, TempAVPItems.CANISTER);
        CreativeModeTabUtil.accept(output, TempAVPItems.WATER_CANISTER);
        CreativeModeTabUtil.accept(output, TempAVPItems.LAVA_CANISTER);
        CreativeModeTabUtil.accept(output, TempAVPItems.MILK_CANISTER);
        CreativeModeTabUtil.accept(output, TempAVPItems.POWDER_SNOW_CANISTER);
        CreativeModeTabUtil.accept(output, TempAVPItems.STEEL_AXE);
        CreativeModeTabUtil.accept(output, TempAVPItems.STEEL_HOE);
        CreativeModeTabUtil.accept(output, TempAVPItems.STEEL_PICKAXE);
        CreativeModeTabUtil.accept(output, TempAVPItems.STEEL_SHOVEL);
        CreativeModeTabUtil.accept(output, TempAVPItems.STEEL_SWORD);
        CreativeModeTabUtil.accept(output, TempAVPItems.TITANIUM_AXE);
        CreativeModeTabUtil.accept(output, TempAVPItems.TITANIUM_HOE);
        CreativeModeTabUtil.accept(output, TempAVPItems.TITANIUM_PICKAXE);
        CreativeModeTabUtil.accept(output, TempAVPItems.TITANIUM_SHOVEL);
        CreativeModeTabUtil.accept(output, TempAVPItems.TITANIUM_SWORD);
        CreativeModeTabUtil.accept(output, TempAVPItems.VERITANIUM_AXE);
        CreativeModeTabUtil.accept(output, TempAVPItems.VERITANIUM_HOE);
        CreativeModeTabUtil.accept(output, TempAVPItems.VERITANIUM_PICKAXE);
        CreativeModeTabUtil.accept(output, TempAVPItems.VERITANIUM_SHOVEL);
        CreativeModeTabUtil.accept(output, TempAVPItems.VERITANIUM_SWORD);
        // FIXME:
        // CreativeModeTabUtil.accept(output, TempAVPItems.ALIEN_MUSIC_DISC_1);
        // CreativeModeTabUtil.accept(output, TempAVPItems.PREDATOR_MUSIC_DISC_1);
    };
}
