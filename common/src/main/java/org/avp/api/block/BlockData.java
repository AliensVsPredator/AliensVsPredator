package org.avp.api.block;

import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.storage.loot.LootTable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Function;

import org.avp.api.Holder;
import org.avp.api.block.factory.BlockFactories;
import org.avp.api.block.factory.BlockFactory;
import org.avp.api.block.loot_table.LootProviders;

public class BlockData {

    public static BlockData.Builder builder(BlockBehaviour.Properties properties) {
        return new BlockData.Builder(properties);
    }

    public static BlockData.Builder simple(BlockBehaviour.Properties properties) {
        return BlockData.builder(properties);
    }

    private final Holder<Block> parentBlockHolder;

    private final BlockFactory blockFactory;

    private final Function<Block, LootTable.Builder> lootProvider;

    private final List<TagKey<Block>> relatedBlockTags;

    private final List<TagKey<Item>> relatedItemTags;

    private final BlockBehaviour.Properties properties;

    private BlockData(
        Holder<Block> parentBlockHolder,
        BlockFactory blockFactory,
        Function<Block, LootTable.Builder> lootProvider,
        List<TagKey<Block>> relatedBlockTags,
        List<TagKey<Item>> relatedItemTags,
        BlockBehaviour.Properties properties
    ) {
        this.parentBlockHolder = parentBlockHolder;
        this.blockFactory = blockFactory;
        this.lootProvider = lootProvider;
        this.relatedBlockTags = relatedBlockTags;
        this.relatedItemTags = relatedItemTags;
        this.properties = properties;
    }

    public Block create() {
        return blockFactory.create(properties);
    }

    public Holder<Block> getParent() {
        return parentBlockHolder;
    }

    public Function<Block, LootTable.Builder> getLootProvider() {
        return lootProvider;
    }

    public List<TagKey<Block>> getRelatedBlockTags() {
        return relatedBlockTags;
    }

    public List<TagKey<Item>> getRelatedItemTags() {
        return relatedItemTags;
    }

    public BlockFactory getFactory() {
        return blockFactory;
    }

    public static class Builder {

        private Holder<Block> parentHolder;

        private BlockFactory blockFactory;

        private Function<Block, LootTable.Builder> lootProvider;

        private final List<TagKey<Block>> relatedBlockTags;

        private final List<TagKey<Item>> relatedItemTags;

        private final BlockBehaviour.Properties properties;

        private Builder(BlockBehaviour.Properties properties) {
            blockFactory = BlockFactories.CUBE;
            lootProvider = LootProviders.SELF;
            relatedBlockTags = new ArrayList<>();
            relatedItemTags = new ArrayList<>();
            this.properties = properties;
        }

        public Builder lootProvider(Function<Block, LootTable.Builder> lootProvider) {
            this.lootProvider = lootProvider;
            return this;
        }

        public Builder factory(BlockFactory blockFactory) {
            this.blockFactory = blockFactory;
            return this;
        }

        @SafeVarargs
        public final Builder blockTags(TagKey<Block>... tags) {
            relatedBlockTags.addAll(Arrays.asList(tags));
            return this;
        }

        public final Builder blockTags(List<TagKey<Block>> tags) {
            relatedBlockTags.addAll(tags);
            return this;
        }

        @SafeVarargs
        public final Builder itemTags(TagKey<Item>... tags) {
            relatedItemTags.addAll(Arrays.asList(tags));
            return this;
        }

        public final Builder itemTags(List<TagKey<Item>> tags) {
            relatedItemTags.addAll(tags);
            return this;
        }

        public Builder parent(Holder<Block> parentHolder) {
            this.parentHolder = parentHolder;
            return this;
        }

        public BlockData build() {
            return new BlockData(
                parentHolder,
                blockFactory,
                lootProvider,
                relatedBlockTags,
                relatedItemTags,
                properties
            );
        }
    }
}
