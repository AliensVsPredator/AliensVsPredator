package com.avp.fabric.data.model.generator;

import com.mojang.datafixers.util.Pair;
import net.minecraft.Util;
import net.minecraft.data.models.BlockModelGenerators;
import net.minecraft.data.models.blockstates.Condition;
import net.minecraft.data.models.blockstates.MultiPartGenerator;
import net.minecraft.data.models.model.ModelLocationUtils;
import net.minecraft.world.level.block.Block;

import static net.minecraft.data.models.BlockModelGenerators.MULTIFACE_GENERATOR;

public class MultiFaceGenerator {

    public static void generate(BlockModelGenerators generator, Block block) {
        generator.createSimpleFlatItemModel(block);

        var resourcelocation = ModelLocationUtils.getModelLocation(block);
        var multipartgenerator = MultiPartGenerator.multiPart(block);
        var terminalCondition = Util.make(
            Condition.condition(),
            condition -> MULTIFACE_GENERATOR.stream().map(Pair::getFirst).forEach(booleanProperty -> {
                if (block.defaultBlockState().hasProperty(booleanProperty)) {
                    condition.term(booleanProperty, false);
                }
            })
        );

        for (var pair : MULTIFACE_GENERATOR) {
            var booleanproperty = pair.getFirst();
            var function = pair.getSecond();

            if (block.defaultBlockState().hasProperty(booleanproperty)) {
                multipartgenerator.with(Condition.condition().term(booleanproperty, true), function.apply(resourcelocation));
                multipartgenerator.with(terminalCondition, function.apply(resourcelocation));
            }
        }

        generator.blockStateOutput.accept(multipartgenerator);
    }
}
