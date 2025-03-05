package com.avp.core.common.creative_mode_tab.initializer;

import com.avp.core.common.creative_mode_tab.AVPCreativeModeTabOutputAdapter;
import com.avp.core.common.item.AVPItems;
import net.minecraft.world.item.CreativeModeTab;

import java.util.function.Consumer;

public class ToolsAndUtilitiesCreativeModeTabInitializer {

    public static final Consumer<CreativeModeTab.Output> CONSUMER = output -> {
        var adaptedOutput = new AVPCreativeModeTabOutputAdapter(output);

        adaptedOutput.accept(AVPItems.ARMOR_CASE);
        adaptedOutput.accept(AVPItems.CANISTER);
        adaptedOutput.accept(AVPItems.ROYAL_JELLY_CANISTER);
        adaptedOutput.accept(AVPItems.STEEL_AXE);
        adaptedOutput.accept(AVPItems.STEEL_HOE);
        adaptedOutput.accept(AVPItems.STEEL_PICKAXE);
        adaptedOutput.accept(AVPItems.STEEL_SHOVEL);
        adaptedOutput.accept(AVPItems.STEEL_SWORD);
        adaptedOutput.accept(AVPItems.TITANIUM_AXE);
        adaptedOutput.accept(AVPItems.TITANIUM_HOE);
        adaptedOutput.accept(AVPItems.TITANIUM_PICKAXE);
        adaptedOutput.accept(AVPItems.TITANIUM_SHOVEL);
        adaptedOutput.accept(AVPItems.TITANIUM_SWORD);
        adaptedOutput.accept(AVPItems.VERITANIUM_AXE);
        adaptedOutput.accept(AVPItems.VERITANIUM_HOE);
        adaptedOutput.accept(AVPItems.VERITANIUM_PICKAXE);
        adaptedOutput.accept(AVPItems.VERITANIUM_SHOVEL);
        adaptedOutput.accept(AVPItems.VERITANIUM_SWORD);
    };
}
