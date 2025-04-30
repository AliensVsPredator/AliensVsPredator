package com.avp.fabric.common.creative_mode_tab.initializer;

import com.avp.common.block.TempAVPBlocks;
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.minecraft.world.item.DyeColor;

import java.util.Arrays;

import com.avp.fabric.common.block.AVPBlocks;
import com.avp.fabric.common.creative_mode_tab.CreativeModeTabs;

public class ColoredBlocksCreativeModeTabInitializer {

    public static void initialize() {
        ItemGroupEvents.modifyEntriesEvent(CreativeModeTabs.COLORED_BLOCKS_KEY).register(entries -> {
            Arrays.stream(DyeColor.values()).forEach(dyeColor -> {
                entries.accept(TempAVPBlocks.DYE_COLOR_TO_CONCRETE_STAIRS.get(dyeColor).get());
                entries.accept(TempAVPBlocks.DYE_COLOR_TO_CONCRETE_SLAB.get(dyeColor).get());

                entries.accept(TempAVPBlocks.DYE_COLOR_TO_INDUSTRIAL_CONCRETE.get(dyeColor).get());
                entries.accept(TempAVPBlocks.DYE_COLOR_TO_INDUSTRIAL_CONCRETE_STAIRS.get(dyeColor).get());
                entries.accept(TempAVPBlocks.DYE_COLOR_TO_INDUSTRIAL_CONCRETE_SLAB.get(dyeColor).get());
                entries.accept(TempAVPBlocks.DYE_COLOR_TO_INDUSTRIAL_CONCRETE_WALL.get(dyeColor).get());
            });

            entries.accept(AVPBlocks.INDUSTRIAL_GLASS);
            Arrays.stream(DyeColor.values()).forEach(dyeColor -> entries.accept(TempAVPBlocks.DYE_COLOR_TO_INDUSTRIAL_GLASS.get(dyeColor).get()));

            entries.accept(AVPBlocks.INDUSTRIAL_GLASS_PANE);
            Arrays.stream(DyeColor.values())
                .forEach(dyeColor -> entries.accept(TempAVPBlocks.DYE_COLOR_TO_INDUSTRIAL_GLASS_PANE.get(dyeColor).get()));

            Arrays.stream(DyeColor.values()).forEach(dyeColor -> {
                entries.accept(TempAVPBlocks.DYE_COLOR_TO_PADDING.get(dyeColor).get());
                entries.accept(TempAVPBlocks.DYE_COLOR_TO_PADDING_STAIRS.get(dyeColor).get());
                entries.accept(TempAVPBlocks.DYE_COLOR_TO_PADDING_SLAB.get(dyeColor).get());
            });

            Arrays.stream(DyeColor.values()).forEach(dyeColor -> {
                entries.accept(TempAVPBlocks.DYE_COLOR_TO_PANEL_PADDING.get(dyeColor).get());
                entries.accept(TempAVPBlocks.DYE_COLOR_TO_PANEL_PADDING_STAIRS.get(dyeColor).get());
                entries.accept(TempAVPBlocks.DYE_COLOR_TO_PANEL_PADDING_SLAB.get(dyeColor).get());
            });

            Arrays.stream(DyeColor.values()).forEach(dyeColor -> {
                entries.accept(TempAVPBlocks.DYE_COLOR_TO_PIPE_PADDING.get(dyeColor).get());
                entries.accept(TempAVPBlocks.DYE_COLOR_TO_PIPE_PADDING_STAIRS.get(dyeColor).get());
                entries.accept(TempAVPBlocks.DYE_COLOR_TO_PIPE_PADDING_SLAB.get(dyeColor).get());
            });

            Arrays.stream(DyeColor.values()).forEach(dyeColor -> {
                entries.accept(TempAVPBlocks.DYE_COLOR_TO_PLASTIC.get(dyeColor).get());
                entries.accept(TempAVPBlocks.DYE_COLOR_TO_PLASTIC_STAIRS.get(dyeColor).get());
                entries.accept(TempAVPBlocks.DYE_COLOR_TO_PLASTIC_SLAB.get(dyeColor).get());
            });

            Arrays.stream(DyeColor.values()).forEach(dyeColor -> {
                entries.accept(TempAVPBlocks.DYE_COLOR_TO_CUT_PLASTIC.get(dyeColor).get());
                entries.accept(TempAVPBlocks.DYE_COLOR_TO_CUT_PLASTIC_STAIRS.get(dyeColor).get());
                entries.accept(TempAVPBlocks.DYE_COLOR_TO_CUT_PLASTIC_SLAB.get(dyeColor).get());
            });
        });
    }
}
