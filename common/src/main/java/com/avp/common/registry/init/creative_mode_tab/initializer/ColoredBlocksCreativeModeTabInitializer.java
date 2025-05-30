package com.avp.common.registry.init.creative_mode_tab.initializer;

import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.DyeColor;

import java.util.Arrays;
import java.util.function.Consumer;

import com.avp.common.registry.init.AVPBlocks;

public class ColoredBlocksCreativeModeTabInitializer {

    public static final Consumer<CreativeModeTab.Output> OUTPUT_CONSUMER = output -> {
        Arrays.stream(DyeColor.values()).forEach(dyeColor -> {
            output.accept(AVPBlocks.DYE_COLOR_TO_CONCRETE_STAIRS.get(dyeColor).get());
            output.accept(AVPBlocks.DYE_COLOR_TO_CONCRETE_SLAB.get(dyeColor).get());

            output.accept(AVPBlocks.DYE_COLOR_TO_INDUSTRIAL_CONCRETE.get(dyeColor).get());
            output.accept(AVPBlocks.DYE_COLOR_TO_INDUSTRIAL_CONCRETE_STAIRS.get(dyeColor).get());
            output.accept(AVPBlocks.DYE_COLOR_TO_INDUSTRIAL_CONCRETE_SLAB.get(dyeColor).get());
            output.accept(AVPBlocks.DYE_COLOR_TO_INDUSTRIAL_CONCRETE_WALL.get(dyeColor).get());
        });

        CreativeModeTabUtil.accept(output, AVPBlocks.INDUSTRIAL_GLASS);
        Arrays.stream(DyeColor.values())
            .forEach(dyeColor -> output.accept(AVPBlocks.DYE_COLOR_TO_INDUSTRIAL_GLASS.get(dyeColor).get()));

        CreativeModeTabUtil.accept(output, AVPBlocks.INDUSTRIAL_GLASS_PANE);
        Arrays.stream(DyeColor.values())
            .forEach(dyeColor -> output.accept(AVPBlocks.DYE_COLOR_TO_INDUSTRIAL_GLASS_PANE.get(dyeColor).get()));

        Arrays.stream(DyeColor.values()).forEach(dyeColor -> {
            output.accept(AVPBlocks.DYE_COLOR_TO_PADDING.get(dyeColor).get());
            output.accept(AVPBlocks.DYE_COLOR_TO_PADDING_STAIRS.get(dyeColor).get());
            output.accept(AVPBlocks.DYE_COLOR_TO_PADDING_SLAB.get(dyeColor).get());
        });

        Arrays.stream(DyeColor.values()).forEach(dyeColor -> {
            output.accept(AVPBlocks.DYE_COLOR_TO_PANEL_PADDING.get(dyeColor).get());
            output.accept(AVPBlocks.DYE_COLOR_TO_PANEL_PADDING_STAIRS.get(dyeColor).get());
            output.accept(AVPBlocks.DYE_COLOR_TO_PANEL_PADDING_SLAB.get(dyeColor).get());
        });

        Arrays.stream(DyeColor.values()).forEach(dyeColor -> {
            output.accept(AVPBlocks.DYE_COLOR_TO_PIPE_PADDING.get(dyeColor).get());
            output.accept(AVPBlocks.DYE_COLOR_TO_PIPE_PADDING_STAIRS.get(dyeColor).get());
            output.accept(AVPBlocks.DYE_COLOR_TO_PIPE_PADDING_SLAB.get(dyeColor).get());
        });

        Arrays.stream(DyeColor.values()).forEach(dyeColor -> {
            output.accept(AVPBlocks.DYE_COLOR_TO_PLASTIC.get(dyeColor).get());
            output.accept(AVPBlocks.DYE_COLOR_TO_PLASTIC_STAIRS.get(dyeColor).get());
            output.accept(AVPBlocks.DYE_COLOR_TO_PLASTIC_SLAB.get(dyeColor).get());
        });

        Arrays.stream(DyeColor.values()).forEach(dyeColor -> {
            output.accept(AVPBlocks.DYE_COLOR_TO_CUT_PLASTIC.get(dyeColor).get());
            output.accept(AVPBlocks.DYE_COLOR_TO_CUT_PLASTIC_STAIRS.get(dyeColor).get());
            output.accept(AVPBlocks.DYE_COLOR_TO_CUT_PLASTIC_SLAB.get(dyeColor).get());
        });
    };
}
