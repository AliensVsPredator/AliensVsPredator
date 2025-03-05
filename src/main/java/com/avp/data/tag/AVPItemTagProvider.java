package com.avp.data.tag;

import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricTagProvider;
import net.minecraft.core.HolderLookup;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.item.Items;

import java.util.concurrent.CompletableFuture;

import com.avp.common.block.AVPBlocks;
import com.avp.common.block_item.AVPBlockItems;
import com.avp.common.item.AVPItemTags;
import com.avp.common.item.AVPItems;
import com.avp.common.item.ArmorItems;

public class AVPItemTagProvider extends FabricTagProvider.ItemTagProvider {

    public AVPItemTagProvider(FabricDataOutput output, CompletableFuture<HolderLookup.Provider> completableFuture) {
        super(output, completableFuture);
    }

    @Override
    protected void addTags(HolderLookup.Provider wrapperLookup) {
        // Acid-resistant items
        getOrCreateTagBuilder(AVPItemTags.ACID_IMMUNE)
            .add(
                ArmorItems.CHITIN_HELMET,
                ArmorItems.CHITIN_CHESTPLATE,
                ArmorItems.CHITIN_LEGGINGS,
                ArmorItems.CHITIN_BOOTS,

                ArmorItems.NETHER_CHITIN_HELMET,
                ArmorItems.NETHER_CHITIN_CHESTPLATE,
                ArmorItems.NETHER_CHITIN_LEGGINGS,
                ArmorItems.NETHER_CHITIN_BOOTS,

                ArmorItems.PLATED_CHITIN_HELMET,
                ArmorItems.PLATED_CHITIN_CHESTPLATE,
                ArmorItems.PLATED_CHITIN_LEGGINGS,
                ArmorItems.PLATED_CHITIN_BOOTS,

                ArmorItems.PLATED_NETHER_CHITIN_HELMET,
                ArmorItems.PLATED_NETHER_CHITIN_CHESTPLATE,
                ArmorItems.PLATED_NETHER_CHITIN_LEGGINGS,
                ArmorItems.PLATED_NETHER_CHITIN_BOOTS,

                AVPItems.CHITIN,
                AVPItems.NETHER_CHITIN,
                AVPItems.PLATED_CHITIN,
                AVPItems.PLATED_NETHER_CHITIN
            );

        getOrCreateTagBuilder(AVPItemTags.DECORATIVE_POT_SHERDS)
            .add(
                AVPItems.OVOID_POTTERY_SHERD,
                AVPItems.PARASITE_POTTERY_SHERD,
                AVPItems.ROYALTY_POTTERY_SHERD,
                AVPItems.VECTOR_POTTERY_SHERD
            );

        getOrCreateTagBuilder(ItemTags.DECORATED_POT_SHERDS)
            .addTag(AVPItemTags.DECORATIVE_POT_SHERDS);

        getOrCreateTagBuilder(AVPItemTags.IRON_BLOCK_LIKE)
            .add(
                Items.IRON_BLOCK,
                AVPBlockItems.ALUMINUM_BLOCK,
                AVPBlockItems.FERROALUMINUM_BLOCK,
                AVPBlockItems.STEEL_BLOCK,
                AVPBlockItems.ZINC_BLOCK
            );

        getOrCreateTagBuilder(AVPItemTags.IRON_INGOT_LIKE)
            .add(
                Items.IRON_INGOT,
                AVPItems.ALUMINUM_INGOT,
                AVPItems.FERROALUMINUM_INGOT,
                AVPItems.STEEL_INGOT,
                AVPItems.ZINC_INGOT
            );

        var industrialGlassBlockTagBuilder = getOrCreateTagBuilder(AVPItemTags.INDUSTRIAL_GLASS_BLOCK);

        industrialGlassBlockTagBuilder.add(AVPBlockItems.INDUSTRIAL_GLASS);
        AVPBlockItems.DYE_COLOR_TO_INDUSTRIAL_GLASS.forEach(($, blockItem) -> industrialGlassBlockTagBuilder.add(blockItem));

        var industrialGlassPaneTagBuilder = getOrCreateTagBuilder(AVPItemTags.INDUSTRIAL_GLASS_PANE);

        industrialGlassPaneTagBuilder.add(AVPBlockItems.INDUSTRIAL_GLASS_PANE);
        AVPBlockItems.DYE_COLOR_TO_INDUSTRIAL_GLASS_PANE.forEach(($, blockItem) -> industrialGlassPaneTagBuilder.add(blockItem));

        getOrCreateTagBuilder(AVPItemTags.INDUSTRIAL_GLASS)
            .addTag(AVPItemTags.INDUSTRIAL_GLASS_BLOCK)
            .addTag(AVPItemTags.INDUSTRIAL_GLASS_PANE);

        getOrCreateTagBuilder(AVPItemTags.LITHIUM)
            .add(
                AVPBlocks.LITHIUM_BLOCK.asItem(),
                AVPBlocks.LITHIUM_ORE.asItem(),
                AVPItems.LITHIUM_DUST
            );

        getOrCreateTagBuilder(ItemTags.AXES)
            .add(
                AVPItems.STEEL_AXE,
                AVPItems.TITANIUM_AXE,
                AVPItems.VERITANIUM_AXE
            );
        getOrCreateTagBuilder(ItemTags.HOES)
            .add(
                AVPItems.STEEL_HOE,
                AVPItems.TITANIUM_HOE,
                AVPItems.VERITANIUM_HOE
            );
        getOrCreateTagBuilder(ItemTags.PICKAXES)
            .add(
                AVPItems.STEEL_PICKAXE,
                AVPItems.TITANIUM_PICKAXE,
                AVPItems.VERITANIUM_PICKAXE
            );
        getOrCreateTagBuilder(ItemTags.SHOVELS)
            .add(
                AVPItems.STEEL_SHOVEL,
                AVPItems.TITANIUM_SHOVEL,
                AVPItems.VERITANIUM_SHOVEL
            );
        getOrCreateTagBuilder(ItemTags.SWORDS)
            .add(
                AVPItems.STEEL_SWORD,
                AVPItems.TITANIUM_SWORD,
                AVPItems.VERITANIUM_SWORD
            );

        getOrCreateTagBuilder(ItemTags.HEAD_ARMOR)
            .add(
                ArmorItems.CHITIN_HELMET,
                ArmorItems.JUNGLE_PREDATOR_HELMET,
                ArmorItems.NETHER_CHITIN_HELMET,
                ArmorItems.MK50_HELMET,
                ArmorItems.PLATED_CHITIN_HELMET,
                ArmorItems.PLATED_NETHER_CHITIN_HELMET,
                ArmorItems.PRESSURE_HELMET,
                ArmorItems.STEEL_HELMET,
                ArmorItems.TACTICAL_HELMET,
                ArmorItems.TITANIUM_HELMET
            );
        getOrCreateTagBuilder(ItemTags.CHEST_ARMOR)
            .add(
                ArmorItems.CHITIN_CHESTPLATE,
                ArmorItems.JUNGLE_PREDATOR_CHESTPLATE,
                ArmorItems.NETHER_CHITIN_CHESTPLATE,
                ArmorItems.MK50_CHESTPLATE,
                ArmorItems.PLATED_CHITIN_CHESTPLATE,
                ArmorItems.PLATED_NETHER_CHITIN_CHESTPLATE,
                ArmorItems.PRESSURE_CHESTPLATE,
                ArmorItems.STEEL_CHESTPLATE,
                ArmorItems.TACTICAL_CHESTPLATE,
                ArmorItems.TITANIUM_CHESTPLATE
            );
        getOrCreateTagBuilder(ItemTags.LEG_ARMOR)
            .add(
                ArmorItems.CHITIN_LEGGINGS,
                ArmorItems.JUNGLE_PREDATOR_LEGGINGS,
                ArmorItems.NETHER_CHITIN_LEGGINGS,
                ArmorItems.MK50_LEGGINGS,
                ArmorItems.PLATED_CHITIN_LEGGINGS,
                ArmorItems.PLATED_NETHER_CHITIN_LEGGINGS,
                ArmorItems.PRESSURE_LEGGINGS,
                ArmorItems.STEEL_LEGGINGS,
                ArmorItems.TACTICAL_LEGGINGS,
                ArmorItems.TITANIUM_LEGGINGS
            );
        getOrCreateTagBuilder(ItemTags.FOOT_ARMOR)
            .add(
                ArmorItems.CHITIN_BOOTS,
                ArmorItems.JUNGLE_PREDATOR_BOOTS,
                ArmorItems.NETHER_CHITIN_BOOTS,
                ArmorItems.MK50_BOOTS,
                ArmorItems.PLATED_CHITIN_BOOTS,
                ArmorItems.PLATED_NETHER_CHITIN_BOOTS,
                ArmorItems.PRESSURE_BOOTS,
                ArmorItems.STEEL_BOOTS,
                ArmorItems.TACTICAL_BOOTS,
                ArmorItems.TITANIUM_BOOTS
            );

        getOrCreateTagBuilder(AVPItemTags.GUNS)
            .add(
                AVPItems.F903WE_RIFLE,
                AVPItems.FLAMETHROWER_SEVASTOPOL,
                AVPItems.M37_12_SHOTGUN,
                AVPItems.M41A_PULSE_RIFLE,
                AVPItems.M42A3_SNIPER_RIFLE,
                AVPItems.M4RA_BATTLE_RIFLE,
                AVPItems.M56_SMARTGUN,
                AVPItems.M6B_ROCKET_LAUNCHER,
                AVPItems.M88_MOD_4_COMBAT_PISTOL,
                AVPItems.OLD_PAINLESS,
                AVPItems.ZX_76_SHOTGUN
            );
    }
}
