package org.avp.api.block;

import net.minecraft.tags.TagKey;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockBehaviour;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.avp.api.Holder;
import org.avp.api.block.drop.BlockDrop;
import org.avp.api.block.drop.BlockDrops;
import org.avp.api.block.factory.BlockFactories;
import org.avp.api.block.factory.BlockFactory;

public class BlockData {

    public static BlockData.Builder builder(BlockBehaviour.Properties properties) {
        return new BlockData.Builder(properties);
    }

    public static BlockData.Builder simple(BlockBehaviour.Properties properties) {
        return BlockData.builder(properties);
    }

    private final Holder<Block> parentBlockHolder;

    private final BlockFactory blockFactory;

    private final BlockDrop blockDrop;

    private final List<TagKey<Block>> relatedTags;

    private final BlockBehaviour.Properties properties;

    private BlockData(
        Holder<Block> parentBlockHolder,
        BlockFactory blockFactory,
        BlockDrop blockDrop,
        List<TagKey<Block>> relatedTags,
        BlockBehaviour.Properties properties
    ) {
        this.parentBlockHolder = parentBlockHolder;
        this.blockFactory = blockFactory;
        this.blockDrop = blockDrop;
        this.relatedTags = relatedTags;
        this.properties = properties;
    }

    public Block create() {
        return blockFactory.create(properties);
    }

    public Holder<Block> getParent() {
        return parentBlockHolder;
    }

    public BlockDrop getDrop() {
        return blockDrop;
    }

    public List<TagKey<Block>> getRelatedTags() {
        return relatedTags;
    }

    public BlockFactory getFactory() {
        return blockFactory;
    }

    public static class Builder {

        private Holder<Block> parentHolder;

        private BlockFactory blockFactory;

        private BlockDrop blockDrop;

        private final List<TagKey<Block>> relatedTags;

        private final BlockBehaviour.Properties properties;

        private Builder(BlockBehaviour.Properties properties) {
            blockFactory = BlockFactories.CUBE;
            blockDrop = BlockDrops.SELF;
            relatedTags = new ArrayList<>();
            this.properties = properties;
        }

        public Builder drop(BlockDrop blockDrop) {
            this.blockDrop = blockDrop;
            return this;
        }

        public Builder factory(BlockFactory blockFactory) {
            this.blockFactory = blockFactory;
            return this;
        }

        @SafeVarargs
        public final Builder tags(TagKey<Block>... tags) {
            relatedTags.addAll(Arrays.asList(tags));
            return this;
        }

        public final Builder tags(List<TagKey<Block>> tags) {
            relatedTags.addAll(tags);
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
                blockDrop,
                relatedTags,
                properties
            );
        }
    }
}
