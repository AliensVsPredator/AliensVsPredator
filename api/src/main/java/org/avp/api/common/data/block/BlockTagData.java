package org.avp.api.common.data.block;

import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;

import java.util.Set;

public record BlockTagData(
    Set<TagKey<Block>> blockTags,
    Set<TagKey<Item>> itemTags
) {

    public static BlockTagData none() {
        return new BlockTagData(Set.of(), Set.of());
    }

    public static BlockTagData ofBlock(Set<TagKey<Block>> blockTags) {
        return new BlockTagData(blockTags, Set.of());
    }

    public static BlockTagData ofItem(Set<TagKey<Item>> itemTags) {
        return new BlockTagData(Set.of(), itemTags);
    }
}
