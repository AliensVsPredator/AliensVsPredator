package org.avp.fabric.common.data.tag;

import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricTagProvider;
import net.minecraft.core.HolderLookup;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.level.block.Blocks;

import java.util.concurrent.CompletableFuture;

import org.avp.api.common.registry.AVPDeferredBlockRegistry;
import org.avp.common.data.tag.AVPBlockTags;
import org.avp.common.registry.block.AVPBlockDataRegistry;

public class AVPBlockTagsProvider extends FabricTagProvider.BlockTagProvider {

    public AVPBlockTagsProvider(FabricDataOutput output, CompletableFuture<HolderLookup.Provider> registriesFuture) {
        super(output, registriesFuture);
    }

    @Override
    protected void addTags(HolderLookup.Provider provider) {
        getOrCreateTagBuilder(AVPBlockTags.SHOULD_NOT_BE_DESTROYED)
            .addOptionalTag(BlockTags.FEATURES_CANNOT_REPLACE)
            .add(
                Blocks.BARRIER,
                Blocks.BEDROCK,
                Blocks.END_PORTAL,
                Blocks.END_PORTAL_FRAME,
                Blocks.END_GATEWAY,
                Blocks.COMMAND_BLOCK,
                Blocks.REPEATING_COMMAND_BLOCK,
                Blocks.CHAIN_COMMAND_BLOCK,
                Blocks.STRUCTURE_BLOCK,
                Blocks.JIGSAW,
                Blocks.MOVING_PISTON,
                Blocks.LIGHT,
                Blocks.REINFORCED_DEEPSLATE
            );

        // Acid-resistant blocks
        getOrCreateTagBuilder(AVPBlockTags.ACID_IMMUNE)
            .addOptionalTag(AVPBlockTags.SHOULD_NOT_BE_DESTROYED);

        AVPBlockDataRegistry.INSTANCE.getEntries().forEach(entry -> {
            var block = entry.getHolder().get();
            entry.getBlockTagData().blockTags().forEach(tag -> getOrCreateTagBuilder(tag).add(block));
        });

        AVPDeferredBlockRegistry.getDataEntries().forEach(entry -> {
            var block = entry.getKey().get();
            var blockData = entry.getValue();

            blockData.blockTagData().blockTags().forEach(tag -> getOrCreateTagBuilder(tag).add(block));
        });
    }
}
