package com.avp.core.common.creative_mode_tab.initializer;

import com.avp.core.common.block.AVPBlocks;
import com.avp.core.common.creative_mode_tab.AVPCreativeModeTabOutputAdapter;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.DyeColor;

import java.util.Arrays;
import java.util.function.Consumer;

public class ColoredBlocksCreativeModeTabInitializer {

    public static final Consumer<CreativeModeTab.Output> CONSUMER = output -> {
        var adaptedOutput = new AVPCreativeModeTabOutputAdapter(output);
        
        Arrays.stream(DyeColor.values()).forEach(dyeColor -> {
            adaptedOutput.accept(AVPBlocks.DYE_COLOR_TO_CONCRETE_STAIRS.get(dyeColor));
            adaptedOutput.accept(AVPBlocks.DYE_COLOR_TO_CONCRETE_SLAB.get(dyeColor));

            adaptedOutput.accept(AVPBlocks.DYE_COLOR_TO_INDUSTRIAL_CONCRETE.get(dyeColor));
            adaptedOutput.accept(AVPBlocks.DYE_COLOR_TO_INDUSTRIAL_CONCRETE_STAIRS.get(dyeColor));
            adaptedOutput.accept(AVPBlocks.DYE_COLOR_TO_INDUSTRIAL_CONCRETE_SLAB.get(dyeColor));
            adaptedOutput.accept(AVPBlocks.DYE_COLOR_TO_INDUSTRIAL_CONCRETE_WALL.get(dyeColor));
        });

        adaptedOutput.accept(AVPBlocks.INDUSTRIAL_GLASS);
        Arrays.stream(DyeColor.values()).forEach(dyeColor -> adaptedOutput.accept(AVPBlocks.DYE_COLOR_TO_INDUSTRIAL_GLASS.get(dyeColor)));

        adaptedOutput.accept(AVPBlocks.INDUSTRIAL_GLASS_PANE);
        Arrays.stream(DyeColor.values())
            .forEach(dyeColor -> adaptedOutput.accept(AVPBlocks.DYE_COLOR_TO_INDUSTRIAL_GLASS_PANE.get(dyeColor)));

        Arrays.stream(DyeColor.values()).forEach(dyeColor -> {
            adaptedOutput.accept(AVPBlocks.DYE_COLOR_TO_PADDING.get(dyeColor));
            adaptedOutput.accept(AVPBlocks.DYE_COLOR_TO_PADDING_STAIRS.get(dyeColor));
            adaptedOutput.accept(AVPBlocks.DYE_COLOR_TO_PADDING_SLAB.get(dyeColor));
        });

        Arrays.stream(DyeColor.values()).forEach(dyeColor -> {
            adaptedOutput.accept(AVPBlocks.DYE_COLOR_TO_PANEL_PADDING.get(dyeColor));
            adaptedOutput.accept(AVPBlocks.DYE_COLOR_TO_PANEL_PADDING_STAIRS.get(dyeColor));
            adaptedOutput.accept(AVPBlocks.DYE_COLOR_TO_PANEL_PADDING_SLAB.get(dyeColor));
        });

        Arrays.stream(DyeColor.values()).forEach(dyeColor -> {
            adaptedOutput.accept(AVPBlocks.DYE_COLOR_TO_PIPE_PADDING.get(dyeColor));
            adaptedOutput.accept(AVPBlocks.DYE_COLOR_TO_PIPE_PADDING_STAIRS.get(dyeColor));
            adaptedOutput.accept(AVPBlocks.DYE_COLOR_TO_PIPE_PADDING_SLAB.get(dyeColor));
        });

        Arrays.stream(DyeColor.values()).forEach(dyeColor -> {
            adaptedOutput.accept(AVPBlocks.DYE_COLOR_TO_PLASTIC.get(dyeColor));
            adaptedOutput.accept(AVPBlocks.DYE_COLOR_TO_PLASTIC_STAIRS.get(dyeColor));
            adaptedOutput.accept(AVPBlocks.DYE_COLOR_TO_PLASTIC_SLAB.get(dyeColor));
        });

        Arrays.stream(DyeColor.values()).forEach(dyeColor -> {
            adaptedOutput.accept(AVPBlocks.DYE_COLOR_TO_CUT_PLASTIC.get(dyeColor));
            adaptedOutput.accept(AVPBlocks.DYE_COLOR_TO_CUT_PLASTIC_STAIRS.get(dyeColor));
            adaptedOutput.accept(AVPBlocks.DYE_COLOR_TO_CUT_PLASTIC_SLAB.get(dyeColor));
        });
    };
}
