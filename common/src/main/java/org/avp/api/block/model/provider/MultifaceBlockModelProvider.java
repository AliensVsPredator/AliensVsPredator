package org.avp.api.block.model.provider;

import com.mojang.datafixers.util.Pair;
import net.minecraft.Util;
import net.minecraft.data.models.BlockModelGenerators;
import net.minecraft.data.models.blockstates.Condition;
import net.minecraft.data.models.blockstates.MultiPartGenerator;
import net.minecraft.data.models.blockstates.Variant;
import net.minecraft.data.models.model.ModelLocationUtils;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import org.avp.mixin.MixinBlockModelsGenerator_Accessor;

import java.util.function.Function;

import static net.minecraft.data.models.BlockModelGenerators.MULTIFACE_GENERATOR;

public class MultifaceBlockModelProvider extends BlockModelProvider {

    private final Block block;

    public MultifaceBlockModelProvider(Block block) {
        this.block = block;
    }

    @Override
    public void run(BlockModelGenerators blockModelGenerators) {
        var blockModelGeneratorsAccessor = (MixinBlockModelsGenerator_Accessor) blockModelGenerators;
        blockModelGeneratorsAccessor.createSimpleFlatItemModel(block);
        ResourceLocation resourcelocation = ModelLocationUtils.getModelLocation(block);
        MultiPartGenerator multipartgenerator = MultiPartGenerator.multiPart(block);
        Condition.TerminalCondition terminalCondition = Util.make(
            Condition.condition(), condition -> MULTIFACE_GENERATOR.stream().map(Pair::getFirst).forEach(booleanProperty -> {
                if (block.defaultBlockState().hasProperty(booleanProperty)) {
                    condition.term(booleanProperty, false);
                }
            })
        );

        for(Pair<BooleanProperty, Function<ResourceLocation, Variant>> pair : MULTIFACE_GENERATOR) {
            BooleanProperty booleanproperty = pair.getFirst();
            Function<ResourceLocation, Variant> function = pair.getSecond();
            if (block.defaultBlockState().hasProperty(booleanproperty)) {
                multipartgenerator.with(Condition.condition().term(booleanproperty, true), function.apply(resourcelocation));
                multipartgenerator.with(terminalCondition, function.apply(resourcelocation));
            }
        }

        blockModelGeneratorsAccessor.getBlockStateOutput().accept(multipartgenerator);
    }
}
