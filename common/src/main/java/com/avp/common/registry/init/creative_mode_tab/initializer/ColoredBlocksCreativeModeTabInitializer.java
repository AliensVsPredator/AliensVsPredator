package com.avp.common.registry.init.creative_mode_tab.initializer;

import com.human.common.registry.init.block.HumanIndustrialConcreteBlocks;
import com.human.common.registry.init.block.HumanIndustrialGlassBlocks;
import com.human.common.registry.init.block.HumanPaddingBlocks;
import com.human.common.registry.init.block.HumanPlasticBlocks;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.DyeColor;

import java.util.Arrays;
import java.util.function.Consumer;

import com.avp.common.registry.init.block.AVPBlocks;

public class ColoredBlocksCreativeModeTabInitializer {

    public static final Consumer<CreativeModeTab.Output> OUTPUT_CONSUMER = output -> {
        Arrays.stream(DyeColor.values()).forEach(dyeColor -> {
            output.accept(AVPBlocks.DYE_COLOR_TO_CONCRETE_STAIRS.get(dyeColor).get());
            output.accept(AVPBlocks.DYE_COLOR_TO_CONCRETE_SLAB.get(dyeColor).get());

            output.accept(HumanIndustrialConcreteBlocks.DYE_COLOR_TO_INDUSTRIAL_CONCRETE.get(dyeColor).get());
            output.accept(HumanIndustrialConcreteBlocks.DYE_COLOR_TO_INDUSTRIAL_CONCRETE_STAIRS.get(dyeColor).get());
            output.accept(HumanIndustrialConcreteBlocks.DYE_COLOR_TO_INDUSTRIAL_CONCRETE_SLAB.get(dyeColor).get());
            output.accept(HumanIndustrialConcreteBlocks.DYE_COLOR_TO_INDUSTRIAL_CONCRETE_WALL.get(dyeColor).get());
        });

        CreativeModeTabUtil.accept(output, HumanIndustrialGlassBlocks.INDUSTRIAL_GLASS);
        Arrays.stream(DyeColor.values())
            .forEach(dyeColor -> output.accept(HumanIndustrialGlassBlocks.DYE_COLOR_TO_INDUSTRIAL_GLASS.get(dyeColor).get()));

        CreativeModeTabUtil.accept(output, HumanIndustrialGlassBlocks.INDUSTRIAL_GLASS_PANE);
        Arrays.stream(DyeColor.values())
            .forEach(dyeColor -> output.accept(HumanIndustrialGlassBlocks.DYE_COLOR_TO_INDUSTRIAL_GLASS_PANE.get(dyeColor).get()));

        Arrays.stream(DyeColor.values()).forEach(dyeColor -> {
            output.accept(HumanPaddingBlocks.DYE_COLOR_TO_PADDING.get(dyeColor).get());
            output.accept(HumanPaddingBlocks.DYE_COLOR_TO_PADDING_STAIRS.get(dyeColor).get());
            output.accept(HumanPaddingBlocks.DYE_COLOR_TO_PADDING_SLAB.get(dyeColor).get());
        });

        Arrays.stream(DyeColor.values()).forEach(dyeColor -> {
            output.accept(HumanPaddingBlocks.DYE_COLOR_TO_PANEL_PADDING.get(dyeColor).get());
            output.accept(HumanPaddingBlocks.DYE_COLOR_TO_PANEL_PADDING_STAIRS.get(dyeColor).get());
            output.accept(HumanPaddingBlocks.DYE_COLOR_TO_PANEL_PADDING_SLAB.get(dyeColor).get());
        });

        Arrays.stream(DyeColor.values()).forEach(dyeColor -> {
            output.accept(HumanPaddingBlocks.DYE_COLOR_TO_PIPE_PADDING.get(dyeColor).get());
            output.accept(HumanPaddingBlocks.DYE_COLOR_TO_PIPE_PADDING_STAIRS.get(dyeColor).get());
            output.accept(HumanPaddingBlocks.DYE_COLOR_TO_PIPE_PADDING_SLAB.get(dyeColor).get());
        });

        Arrays.stream(DyeColor.values()).forEach(dyeColor -> {
            output.accept(HumanPlasticBlocks.DYE_COLOR_TO_PLASTIC.get(dyeColor).get());
            output.accept(HumanPlasticBlocks.DYE_COLOR_TO_PLASTIC_STAIRS.get(dyeColor).get());
            output.accept(HumanPlasticBlocks.DYE_COLOR_TO_PLASTIC_SLAB.get(dyeColor).get());
        });

        Arrays.stream(DyeColor.values()).forEach(dyeColor -> {
            output.accept(HumanPlasticBlocks.DYE_COLOR_TO_CUT_PLASTIC.get(dyeColor).get());
            output.accept(HumanPlasticBlocks.DYE_COLOR_TO_CUT_PLASTIC_STAIRS.get(dyeColor).get());
            output.accept(HumanPlasticBlocks.DYE_COLOR_TO_CUT_PLASTIC_SLAB.get(dyeColor).get());
        });

        Arrays.stream(DyeColor.values())
            .forEach(dyeColor -> output.accept(HumanPlasticBlocks.DYE_COLOR_TO_FRAMED_PLASTIC.get(dyeColor).get()));
    };
}
