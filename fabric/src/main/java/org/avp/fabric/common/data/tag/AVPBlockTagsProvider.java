package org.avp.fabric.common.data.tag;

import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricTagProvider;
import net.minecraft.core.HolderLookup;
import net.minecraft.tags.BlockTags;

import java.util.concurrent.CompletableFuture;

import org.avp.common.block.AVPBlocks;
import org.avp.common.tag.AVPBlockTags;

public class AVPBlockTagsProvider extends FabricTagProvider.BlockTagProvider {

    public AVPBlockTagsProvider(FabricDataOutput output, CompletableFuture<HolderLookup.Provider> registriesFuture) {
        super(output, registriesFuture);
    }

    @Override
    protected void addTags(HolderLookup.Provider provider) {
        // Acid-resistant blocks
        // TODO: For some reason, adding irreplaceable features crashes datagen. Investigate later.
        getOrCreateTagBuilder(AVPBlockTags.ACID_IMMUNE).addOptionalTag(BlockTags.FEATURES_CANNOT_REPLACE);

        AVPBlocks.getEntries().forEach(tuple -> {
            var block = tuple.first().get();
            var blockData = tuple.second();

            blockData.getRelatedTags().forEach(tag -> getOrCreateTagBuilder(tag).add(block));
        });
    }
}
