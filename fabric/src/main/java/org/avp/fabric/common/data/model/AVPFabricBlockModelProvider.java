package org.avp.fabric.common.data.model;

import net.minecraft.data.models.BlockModelGenerators;
import net.minecraft.data.models.model.ModelLocationUtils;
import net.minecraft.world.level.block.Block;
import org.avp.api.block.BlockData;
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
    }

    private AVPFabricBlockModelProvider() {
        throw new UnsupportedOperationException();
    }
}
