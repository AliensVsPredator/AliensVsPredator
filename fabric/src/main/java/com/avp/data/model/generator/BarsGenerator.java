package com.avp.data.model.generator;

import net.minecraft.data.models.BlockModelGenerators;
import net.minecraft.data.models.blockstates.Condition;
import net.minecraft.data.models.blockstates.MultiPartGenerator;
import net.minecraft.data.models.blockstates.Variant;
import net.minecraft.data.models.blockstates.VariantProperties;
import net.minecraft.data.models.model.TextureMapping;
import net.minecraft.data.models.model.TextureSlot;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;

import com.avp.data.model.AVPModelTemplates;
import com.avp.data.model.AVPTextureSlot;

public class BarsGenerator {

    public static void generate(BlockModelGenerators generator, Block block) {
        var baseResourceLocation = TextureMapping.getBlockTexture(block);
        var textureMapping = new TextureMapping()
            .put(AVPTextureSlot.BARS, baseResourceLocation)
            .put(TextureSlot.EDGE, baseResourceLocation)
            .put(TextureSlot.PARTICLE, baseResourceLocation);
        var postEndsResourceLocation = AVPModelTemplates.BARS_POST_ENDS.create(block, textureMapping, generator.modelOutput);
        var postResourceLocation = AVPModelTemplates.BARS_POST.create(block, textureMapping, generator.modelOutput);
        var capResourceLocation = AVPModelTemplates.BARS_CAP.create(block, textureMapping, generator.modelOutput);
        var capAltResourceLocation = AVPModelTemplates.BARS_CAP_ALT.create(block, textureMapping, generator.modelOutput);
        var sideResourceLocation = AVPModelTemplates.BARS_SIDE.create(block, textureMapping, generator.modelOutput);
        var sideAltResourceLocation = AVPModelTemplates.BARS_SIDE_ALT.create(block, textureMapping, generator.modelOutput);

        generator.blockStateOutput
            .accept(
                MultiPartGenerator.multiPart(block)
                    .with(Variant.variant().with(VariantProperties.MODEL, postEndsResourceLocation))
                    .with(
                        Condition.condition()
                            .term(BlockStateProperties.NORTH, false)
                            .term(BlockStateProperties.EAST, false)
                            .term(BlockStateProperties.SOUTH, false)
                            .term(BlockStateProperties.WEST, false),
                        Variant.variant().with(VariantProperties.MODEL, postResourceLocation)
                    )
                    .with(
                        Condition.condition()
                            .term(BlockStateProperties.NORTH, true)
                            .term(BlockStateProperties.EAST, false)
                            .term(BlockStateProperties.SOUTH, false)
                            .term(BlockStateProperties.WEST, false),
                        Variant.variant().with(VariantProperties.MODEL, capResourceLocation)
                    )
                    .with(
                        Condition.condition()
                            .term(BlockStateProperties.NORTH, false)
                            .term(BlockStateProperties.EAST, true)
                            .term(BlockStateProperties.SOUTH, false)
                            .term(BlockStateProperties.WEST, false),
                        Variant.variant()
                            .with(VariantProperties.MODEL, capResourceLocation)
                            .with(VariantProperties.Y_ROT, VariantProperties.Rotation.R90)
                    )
                    .with(
                        Condition.condition()
                            .term(BlockStateProperties.NORTH, false)
                            .term(BlockStateProperties.EAST, false)
                            .term(BlockStateProperties.SOUTH, true)
                            .term(BlockStateProperties.WEST, false),
                        Variant.variant().with(VariantProperties.MODEL, capAltResourceLocation)
                    )
                    .with(
                        Condition.condition()
                            .term(BlockStateProperties.NORTH, false)
                            .term(BlockStateProperties.EAST, false)
                            .term(BlockStateProperties.SOUTH, false)
                            .term(BlockStateProperties.WEST, true),
                        Variant.variant()
                            .with(VariantProperties.MODEL, capAltResourceLocation)
                            .with(VariantProperties.Y_ROT, VariantProperties.Rotation.R90)
                    )
                    .with(
                        Condition.condition().term(BlockStateProperties.NORTH, true),
                        Variant.variant().with(VariantProperties.MODEL, sideResourceLocation)
                    )
                    .with(
                        Condition.condition().term(BlockStateProperties.EAST, true),
                        Variant.variant()
                            .with(VariantProperties.MODEL, sideResourceLocation)
                            .with(VariantProperties.Y_ROT, VariantProperties.Rotation.R90)
                    )
                    .with(
                        Condition.condition().term(BlockStateProperties.SOUTH, true),
                        Variant.variant().with(VariantProperties.MODEL, sideAltResourceLocation)
                    )
                    .with(
                        Condition.condition().term(BlockStateProperties.WEST, true),
                        Variant.variant()
                            .with(VariantProperties.MODEL, sideAltResourceLocation)
                            .with(VariantProperties.Y_ROT, VariantProperties.Rotation.R90)
                    )
            );
        generator.createSimpleFlatItemModel(block);
    }
}
