package org.avp.common.registry.block;

import net.minecraft.tags.BlockTags;
import net.minecraft.world.item.DyeItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;

import java.util.Set;
import java.util.function.Function;

import org.avp.api.common.data.block.BlockData;
import org.avp.api.common.data.block.BlockModelData;
import org.avp.api.common.data.block.BlockTagData;
import org.avp.api.common.registry.AVPDeferredBlockRegistry;
import org.avp.api.common.registry.holder.BLHolder;
import org.avp.api.common.registry.holder.BlockHolderSet;
import org.avp.api.common.registry.holder.BlockHolderSetData;
import org.avp.common.data.tag.AVPBlockTags;

public class AVPPlasticBlockRegistry extends AVPDeferredBlockRegistry {

    public static final AVPPlasticBlockRegistry INSTANCE = new AVPPlasticBlockRegistry();

    public final PlasticBlockSet plasticBlack;

    public final PlasticBlockSet plasticBlue;

    public final PlasticBlockSet plasticBrown;

    public final PlasticBlockSet plasticCyan;

    public final PlasticBlockSet plasticGray;

    public final PlasticBlockSet plasticGreen;

    public final PlasticBlockSet plasticLightBlue;

    public final PlasticBlockSet plasticLightGray;

    public final PlasticBlockSet plasticLime;

    public final PlasticBlockSet plasticMagenta;

    public final PlasticBlockSet plasticOrange;

    public final PlasticBlockSet plasticPink;

    public final PlasticBlockSet plasticPurple;

    public final PlasticBlockSet plasticRed;

    public final PlasticBlockSet plasticWhite;

    public final PlasticBlockSet plasticYellow;

    @Override
    protected BLHolder<Block> createHolder(BlockData blockData) {
        return super.createHolder(blockData.withRegistryName("plastic_" + blockData.registryName()));
    }

    private AVPPlasticBlockRegistry() {
        Function<DyeItem, BlockBehaviour.Properties> propertiesSupplier = dyeItem -> BlockBehaviour.Properties.of()
            .mapColor(dyeItem.getDyeColor())
            .requiresCorrectToolForDrops()
            .sound(SoundType.STONE)
            .strength(9, 10);

        plasticBlack = createSet(Items.BLACK_DYE, "black", propertiesSupplier);
        plasticBlue = createSet(Items.BLUE_DYE, "blue", propertiesSupplier);
        plasticBrown = createSet(Items.BROWN_DYE, "brown", propertiesSupplier);
        plasticCyan = createSet(Items.CYAN_DYE, "cyan", propertiesSupplier);
        plasticGray = createSet(Items.GRAY_DYE, "gray", propertiesSupplier);
        plasticGreen = createSet(Items.GREEN_DYE, "green", propertiesSupplier);
        plasticLightBlue = createSet(Items.LIGHT_BLUE_DYE, "light_blue", propertiesSupplier);
        plasticLightGray = createSet(Items.LIGHT_GRAY_DYE, "light_gray", propertiesSupplier);
        plasticLime = createSet(Items.LIME_DYE, "lime", propertiesSupplier);
        plasticMagenta = createSet(Items.MAGENTA_DYE, "magenta", propertiesSupplier);
        plasticOrange = createSet(Items.ORANGE_DYE, "orange", propertiesSupplier);
        plasticPink = createSet(Items.PINK_DYE, "pink", propertiesSupplier);
        plasticPurple = createSet(Items.PURPLE_DYE, "purple", propertiesSupplier);
        plasticRed = createSet(Items.RED_DYE, "red", propertiesSupplier);
        plasticWhite = createSet(Items.WHITE_DYE, "white", propertiesSupplier);
        plasticYellow = createSet(Items.YELLOW_DYE, "yellow", propertiesSupplier);
    }

    private PlasticBlockSet createSet(Item dyeItem, String prefix, Function<DyeItem, BlockBehaviour.Properties> propertiesProvider) {
        var properties = propertiesProvider.apply((DyeItem) dyeItem);
        var blockTagData = BlockTagData.ofBlock(
            Set.of(AVPBlockTags.ACID_IMMUNE, BlockTags.NEEDS_IRON_TOOL, BlockTags.MINEABLE_WITH_PICKAXE, AVPBlockTags.PLASTIC)
        );
        var blockData = new BlockData(prefix, BlockModelData.cube(properties), blockTagData);
        var baseSet = registerBlockHolderSet(new BlockHolderSetData(properties, blockData));
        return new PlasticBlockSet(dyeItem, baseSet);
    }

    public record PlasticBlockSet(
        Item dyeItem,
        BlockHolderSet base
    ) {}
}
