package com.avp.fabric.common.creative_mode_tab.initializer;

import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;

import com.avp.common.item.TempAVPItems;
import com.avp.fabric.common.creative_mode_tab.CreativeModeTabs;
import com.avp.fabric.common.item.AVPItems;

public class ToolsAndUtilitiesCreativeModeTabInitializer {

    public static void initialize() {
        ItemGroupEvents.modifyEntriesEvent(CreativeModeTabs.TOOLS_AND_UTILITIES_KEY).register(entries -> {
            CreativeModeTabUtil.accept(entries, AVPItems.ARMOR_CASE);
            CreativeModeTabUtil.accept(entries, TempAVPItems.CANISTER);
            CreativeModeTabUtil.accept(entries, TempAVPItems.WATER_CANISTER);
            CreativeModeTabUtil.accept(entries, TempAVPItems.LAVA_CANISTER);
            CreativeModeTabUtil.accept(entries, TempAVPItems.MILK_CANISTER);
            CreativeModeTabUtil.accept(entries, TempAVPItems.POWDER_SNOW_CANISTER);
            CreativeModeTabUtil.accept(entries, AVPItems.STEEL_AXE);
            CreativeModeTabUtil.accept(entries, AVPItems.STEEL_HOE);
            CreativeModeTabUtil.accept(entries, AVPItems.STEEL_PICKAXE);
            CreativeModeTabUtil.accept(entries, AVPItems.STEEL_SHOVEL);
            CreativeModeTabUtil.accept(entries, AVPItems.STEEL_SWORD);
            CreativeModeTabUtil.accept(entries, AVPItems.TITANIUM_AXE);
            CreativeModeTabUtil.accept(entries, AVPItems.TITANIUM_HOE);
            CreativeModeTabUtil.accept(entries, AVPItems.TITANIUM_PICKAXE);
            CreativeModeTabUtil.accept(entries, AVPItems.TITANIUM_SHOVEL);
            CreativeModeTabUtil.accept(entries, AVPItems.TITANIUM_SWORD);
            CreativeModeTabUtil.accept(entries, AVPItems.VERITANIUM_AXE);
            CreativeModeTabUtil.accept(entries, AVPItems.VERITANIUM_HOE);
            CreativeModeTabUtil.accept(entries, AVPItems.VERITANIUM_PICKAXE);
            CreativeModeTabUtil.accept(entries, AVPItems.VERITANIUM_SHOVEL);
            CreativeModeTabUtil.accept(entries, AVPItems.VERITANIUM_SWORD);
            CreativeModeTabUtil.accept(entries, AVPItems.ALIEN_MUSIC_DISC_1);
            CreativeModeTabUtil.accept(entries, AVPItems.PREDATOR_MUSIC_DISC_1);
        });
    }
}
