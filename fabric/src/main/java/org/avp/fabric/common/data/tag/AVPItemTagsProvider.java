package org.avp.fabric.common.data.tag;

import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricTagProvider;
import net.minecraft.core.HolderLookup;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Blocks;

import java.util.concurrent.CompletableFuture;

import org.avp.api.common.registry.AVPDeferredBlockRegistry;
import org.avp.api.common.registry.AVPDeferredItemRegistry;
import org.avp.common.data.tag.AVPItemTags;

public class AVPItemTagsProvider extends FabricTagProvider.ItemTagProvider {

    public AVPItemTagsProvider(FabricDataOutput output, CompletableFuture<HolderLookup.Provider> registriesFuture) {
        super(output, registriesFuture);
    }

    @Override
    protected void addTags(HolderLookup.Provider provider) {
        AVPDeferredItemRegistry.getDataEntries().forEach(holderItemDataTuple -> {
            var item = holderItemDataTuple.first().get();
            var itemTagData = holderItemDataTuple.second().itemTagData();
            itemTagData.forEach(itemTagKey -> getOrCreateTagBuilder(itemTagKey).add(item));
        });

        getOrCreateTagBuilder(AVPItemTags.CONCRETE)
            .add(
                Blocks.BLACK_CONCRETE.asItem(),
                Blocks.BLUE_CONCRETE.asItem(),
                Blocks.BROWN_CONCRETE.asItem(),
                Blocks.CYAN_CONCRETE.asItem(),
                Blocks.GRAY_CONCRETE.asItem(),
                Blocks.GREEN_CONCRETE.asItem(),
                Blocks.LIGHT_BLUE_CONCRETE.asItem(),
                Blocks.LIGHT_GRAY_CONCRETE.asItem(),
                Blocks.LIME_CONCRETE.asItem(),
                Blocks.MAGENTA_CONCRETE.asItem(),
                Blocks.ORANGE_CONCRETE.asItem(),
                Blocks.PINK_CONCRETE.asItem(),
                Blocks.PURPLE_CONCRETE.asItem(),
                Blocks.RED_CONCRETE.asItem(),
                Blocks.WHITE_CONCRETE.asItem(),
                Blocks.YELLOW_CONCRETE.asItem()
            );

        getOrCreateTagBuilder(AVPItemTags.THREATENS_PREDATORS)
            .addTag(AVPItemTags.GUNS)
            .addOptionalTag(ItemTags.SWORDS)
            .addOptionalTag(ItemTags.AXES)
            .add(Items.TRIDENT);

        AVPDeferredBlockRegistry.getDataEntries().forEach(tuple -> {
            var block = tuple.first().get();
            var blockData = tuple.second();

            blockData.blockTagData().itemTags().forEach(tag -> getOrCreateTagBuilder(tag).add(block.asItem()));
        });
    }
}
