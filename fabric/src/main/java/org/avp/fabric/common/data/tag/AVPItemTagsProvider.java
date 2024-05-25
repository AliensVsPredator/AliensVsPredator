package org.avp.fabric.common.data.tag;

import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricTagProvider;
import net.minecraft.core.HolderLookup;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.item.Items;
import org.avp.common.registry.AVPDeferredBlockRegistry;
import org.avp.common.registry.AVPDeferredItemRegistry;
import org.avp.common.tag.AVPItemTags;

import java.util.concurrent.CompletableFuture;

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
