package org.avp.fabric.common.data.model;

import net.minecraft.data.models.BlockModelGenerators;
import net.minecraft.data.models.model.ModelLocationUtils;
import net.minecraft.world.level.block.Block;
import org.avp.api.block.BlockData;
import org.avp.api.block.model.provider.ItemModelDelegator;
import org.avp.common.item.AVPSpawnEggItems;
import org.avp.common.registry.AVPDeferredBlockRegistry;

public class AVPFabricBlockModelProvider {

    public static void addBlockModels(BlockModelGenerators generator) {
        AVPDeferredBlockRegistry.getDataEntries().forEach(tuple -> computeBlockModels(generator, tuple.first().get(), tuple.second()));

        // Listen, I don't like this any more than you do. But Mojang also does this, so...
        AVPSpawnEggItems.INSTANCE.getValues()
            .forEach(
                holder -> generator.delegateItemModel(
                    holder.get(),
                    ModelLocationUtils.decorateItemModelLocation("template_spawn_egg")
                )
            );
    }

    private static void computeBlockModels(BlockModelGenerators generator, Block block, BlockData blockData) {
        var blockModelProvider = blockData.blockModelData().blockModelProvider().apply(block);
        blockModelProvider.run(generator);

        // We have to do this weird post-processing because attempting to mixin to delegateItemModel causes a StackOverflowError
        // for some reason... but this stupid approach is the only thing that worked, so I'll take it. -_-
        if (blockModelProvider instanceof ItemModelDelegator itemModelDelegator) {
            var modelTemplate = itemModelDelegator.getItemModelDelegateData().modelTemplate();
            var textureMapping = itemModelDelegator.getItemModelDelegateData().textureMapping();
            var inventoryResourceLocation = modelTemplate.create(block, textureMapping, generator.modelOutput);
            generator.delegateItemModel(block.asItem(), inventoryResourceLocation);
        }
    }

    private AVPFabricBlockModelProvider() {
        throw new UnsupportedOperationException();
    }
}
