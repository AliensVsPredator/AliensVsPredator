package com.avp.common.creative_mode_tab.initializer;

import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;

import com.avp.common.creative_mode_tab.CreativeModeTabs;
import com.avp.common.item.AVPItems;

public class ToolsAndUtilitiesCreativeModeTabInitializer {

    public static void initialize() {
        ItemGroupEvents.modifyEntriesEvent(CreativeModeTabs.TOOLS_AND_UTILITIES_KEY).register(entries -> {
            entries.accept(AVPItems.ARMOR_CASE);
            entries.accept(AVPItems.CANISTER);
            entries.accept(AVPItems.ROYAL_JELLY_CANISTER);
            entries.accept(AVPItems.STEEL_AXE);
            entries.accept(AVPItems.STEEL_HOE);
            entries.accept(AVPItems.STEEL_PICKAXE);
            entries.accept(AVPItems.STEEL_SHOVEL);
            entries.accept(AVPItems.STEEL_SWORD);
            entries.accept(AVPItems.TITANIUM_AXE);
            entries.accept(AVPItems.TITANIUM_HOE);
            entries.accept(AVPItems.TITANIUM_PICKAXE);
            entries.accept(AVPItems.TITANIUM_SHOVEL);
            entries.accept(AVPItems.TITANIUM_SWORD);
            entries.accept(AVPItems.VERITANIUM_AXE);
            entries.accept(AVPItems.VERITANIUM_HOE);
            entries.accept(AVPItems.VERITANIUM_PICKAXE);
            entries.accept(AVPItems.VERITANIUM_SHOVEL);
            entries.accept(AVPItems.VERITANIUM_SWORD);
        });
    }
}
