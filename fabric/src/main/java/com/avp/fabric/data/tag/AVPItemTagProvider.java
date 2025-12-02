package com.avp.fabric.data.tag;

import com.avp.common.registry.tag.AVPItemTags;
import com.compat.CommonItemTags;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricTagProvider;
import net.minecraft.core.HolderLookup;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.item.Items;

import java.util.concurrent.CompletableFuture;

public class AVPItemTagProvider extends FabricTagProvider.ItemTagProvider {

    public AVPItemTagProvider(FabricDataOutput output, CompletableFuture<HolderLookup.Provider> completableFuture) {
        super(output, completableFuture);
    }

    @Override
    protected void addTags(HolderLookup.Provider wrapperLookup) {
        getOrCreateTagBuilder(AVPItemTags.DECORATIVE_POT_SHERDS);

        getOrCreateTagBuilder(ItemTags.DECORATED_POT_SHERDS)
            .addTag(AVPItemTags.DECORATIVE_POT_SHERDS);

        getOrCreateTagBuilder(AVPItemTags.MELEE_WEAPONS)
            .addOptionalTag(ItemTags.AXES)
            .addOptionalTag(ItemTags.SWORDS)
            .add(
                Items.MACE
            );

        getOrCreateTagBuilder(AVPItemTags.RANGED_WEAPONS)
            .add(
                Items.BOW,
                Items.CROSSBOW
            );

        addCompatibilityTags();
    }

    private void addCompatibilityTags() {
        getOrCreateTagBuilder(CommonItemTags.DIAMONDS)
            .setReplace(false)
            .add(Items.DIAMOND);

        getOrCreateTagBuilder(CommonItemTags.DUSTS_COAL)
            .setReplace(false);

        getOrCreateTagBuilder(CommonItemTags.DUSTS_REDSTONE)
            .setReplace(false)
            .add(Items.REDSTONE);

        getOrCreateTagBuilder(CommonItemTags.GEMS_DIAMOND)
            .setReplace(false)
            .addTag(CommonItemTags.DIAMONDS);

        getOrCreateTagBuilder(CommonItemTags.INGOTS)
            .setReplace(false)
            .addTag(CommonItemTags.INGOTS_GOLD)
            .addTag(CommonItemTags.INGOTS_COPPER);


        getOrCreateTagBuilder(CommonItemTags.INGOTS_COPPER)
            .setReplace(false)
            .add(Items.COPPER_INGOT);

        getOrCreateTagBuilder(CommonItemTags.INGOTS_GOLD)
            .setReplace(false)
            .add(Items.GOLD_INGOT);

        getOrCreateTagBuilder(CommonItemTags.NUGGETS)
            .setReplace(false)
            .addTag(CommonItemTags.NUGGETS_GOLD)
            .addTag(CommonItemTags.NUGGETS_IRON);

        getOrCreateTagBuilder(CommonItemTags.NUGGETS_GOLD)
            .setReplace(false)
            .add(Items.GOLD_NUGGET);

        getOrCreateTagBuilder(CommonItemTags.NUGGETS_IRON)
            .setReplace(false)
            .add(Items.IRON_NUGGET);

        getOrCreateTagBuilder(CommonItemTags.RAW_MATERIALS)
            .setReplace(false)
            .addTag(CommonItemTags.RAW_MATERIALS_COPPER)
            .addTag(CommonItemTags.RAW_MATERIALS_IRON);

        getOrCreateTagBuilder(CommonItemTags.RAW_MATERIALS_COPPER)
            .setReplace(false)
            .add(Items.RAW_COPPER);

        getOrCreateTagBuilder(CommonItemTags.RAW_MATERIALS_IRON)
            .setReplace(false)
            .add(Items.RAW_IRON);

        getOrCreateTagBuilder(CommonItemTags.RODS_WOODEN)
            .setReplace(false)
            .add(Items.STICK);

        getOrCreateTagBuilder(CommonItemTags.STRINGS)
            .setReplace(false)
            .add(Items.STRING);
    }
}
