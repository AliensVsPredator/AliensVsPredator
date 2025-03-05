package com.avp.fabric.data.tag;

import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricTagProvider;
import net.minecraft.core.HolderLookup;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.item.Items;

import java.util.concurrent.CompletableFuture;

import com.avp.core.common.block.AVPBlocks;
import com.avp.core.common.block_item.AVPBlockItems;
import com.avp.core.common.item.AVPItemTags;
import com.avp.core.common.item.AVPItems;
import com.avp.core.common.item.ArmorItems;

public class AVPItemTagProvider extends FabricTagProvider.ItemTagProvider {

    public AVPItemTagProvider(FabricDataOutput output, CompletableFuture<HolderLookup.Provider> completableFuture) {
        super(output, completableFuture);
    }

    @Override
    protected void addTags(HolderLookup.Provider wrapperLookup) {
        // Acid-resistant items
        getOrCreateTagBuilder(AVPItemTags.ACID_IMMUNE)
            .add(
                ArmorItems.CHITIN_HELMET.get(),
                ArmorItems.CHITIN_CHESTPLATE.get(),
                ArmorItems.CHITIN_LEGGINGS.get(),
                ArmorItems.CHITIN_BOOTS.get(),

                ArmorItems.NETHER_CHITIN_HELMET.get(),
                ArmorItems.NETHER_CHITIN_CHESTPLATE.get(),
                ArmorItems.NETHER_CHITIN_LEGGINGS.get(),
                ArmorItems.NETHER_CHITIN_BOOTS.get(),

                ArmorItems.PLATED_CHITIN_HELMET.get(),
                ArmorItems.PLATED_CHITIN_CHESTPLATE.get(),
                ArmorItems.PLATED_CHITIN_LEGGINGS.get(),
                ArmorItems.PLATED_CHITIN_BOOTS.get(),

                ArmorItems.PLATED_NETHER_CHITIN_HELMET.get(),
                ArmorItems.PLATED_NETHER_CHITIN_CHESTPLATE.get(),
                ArmorItems.PLATED_NETHER_CHITIN_LEGGINGS.get(),
                ArmorItems.PLATED_NETHER_CHITIN_BOOTS.get(),

                AVPItems.CHITIN.get(),
                AVPItems.NETHER_CHITIN.get(),
                AVPItems.PLATED_CHITIN.get(),
                AVPItems.PLATED_NETHER_CHITIN.get()
            );

        getOrCreateTagBuilder(AVPItemTags.DECORATIVE_POT_SHERDS)
            .add(
                AVPItems.OVOID_POTTERY_SHERD.get(),
                AVPItems.PARASITE_POTTERY_SHERD.get(),
                AVPItems.ROYALTY_POTTERY_SHERD.get(),
                AVPItems.VECTOR_POTTERY_SHERD.get()
            );

        getOrCreateTagBuilder(ItemTags.DECORATED_POT_SHERDS)
            .addTag(AVPItemTags.DECORATIVE_POT_SHERDS);

        getOrCreateTagBuilder(AVPItemTags.IRON_BLOCK_LIKE)
            .add(
                Items.IRON_BLOCK,
                AVPBlockItems.ALUMINUM_BLOCK.get(),
                AVPBlockItems.FERROALUMINUM_BLOCK.get(),
                AVPBlockItems.STEEL_BLOCK.get(),
                AVPBlockItems.ZINC_BLOCK.get()
            );

        getOrCreateTagBuilder(AVPItemTags.IRON_INGOT_LIKE)
            .add(
                Items.IRON_INGOT,
                AVPItems.ALUMINUM_INGOT.get(),
                AVPItems.FERROALUMINUM_INGOT.get(),
                AVPItems.STEEL_INGOT.get(),
                AVPItems.ZINC_INGOT.get()
            );

        var industrialGlassBlockTagBuilder = getOrCreateTagBuilder(AVPItemTags.INDUSTRIAL_GLASS_BLOCK);

        industrialGlassBlockTagBuilder.add(AVPBlockItems.INDUSTRIAL_GLASS.get());
        AVPBlockItems.DYE_COLOR_TO_INDUSTRIAL_GLASS.forEach(($, blockItemSupplier) -> industrialGlassBlockTagBuilder.add(blockItemSupplier.get()));

        var industrialGlassPaneTagBuilder = getOrCreateTagBuilder(AVPItemTags.INDUSTRIAL_GLASS_PANE);

        industrialGlassPaneTagBuilder.add(AVPBlockItems.INDUSTRIAL_GLASS_PANE.get());
        AVPBlockItems.DYE_COLOR_TO_INDUSTRIAL_GLASS_PANE.forEach(($, blockItemSupplier) -> industrialGlassPaneTagBuilder.add(blockItemSupplier.get()));

        getOrCreateTagBuilder(AVPItemTags.INDUSTRIAL_GLASS)
            .addTag(AVPItemTags.INDUSTRIAL_GLASS_BLOCK)
            .addTag(AVPItemTags.INDUSTRIAL_GLASS_PANE);

        getOrCreateTagBuilder(AVPItemTags.LITHIUM)
            .add(
                AVPBlocks.LITHIUM_BLOCK.get().asItem(),
                AVPBlocks.LITHIUM_ORE.get().asItem(),
                AVPItems.LITHIUM_DUST.get()
            );

        getOrCreateTagBuilder(ItemTags.AXES)
            .add(
                AVPItems.STEEL_AXE.get(),
                AVPItems.TITANIUM_AXE.get(),
                AVPItems.VERITANIUM_AXE.get()
            );
        getOrCreateTagBuilder(ItemTags.HOES)
            .add(
                AVPItems.STEEL_HOE.get(),
                AVPItems.TITANIUM_HOE.get(),
                AVPItems.VERITANIUM_HOE.get()
            );
        getOrCreateTagBuilder(ItemTags.PICKAXES)
            .add(
                AVPItems.STEEL_PICKAXE.get(),
                AVPItems.TITANIUM_PICKAXE.get(),
                AVPItems.VERITANIUM_PICKAXE.get()
            );
        getOrCreateTagBuilder(ItemTags.SHOVELS)
            .add(
                AVPItems.STEEL_SHOVEL.get(),
                AVPItems.TITANIUM_SHOVEL.get(),
                AVPItems.VERITANIUM_SHOVEL.get()
            );
        getOrCreateTagBuilder(ItemTags.SWORDS)
            .add(
                AVPItems.STEEL_SWORD.get(),
                AVPItems.TITANIUM_SWORD.get(),
                AVPItems.VERITANIUM_SWORD.get()
            );

        getOrCreateTagBuilder(ItemTags.HEAD_ARMOR)
            .add(
                ArmorItems.CHITIN_HELMET.get(),
                ArmorItems.JUNGLE_PREDATOR_HELMET.get(),
                ArmorItems.NETHER_CHITIN_HELMET.get(),
                ArmorItems.MK50_HELMET.get(),
                ArmorItems.PLATED_CHITIN_HELMET.get(),
                ArmorItems.PLATED_NETHER_CHITIN_HELMET.get(),
                ArmorItems.PRESSURE_HELMET.get(),
                ArmorItems.STEEL_HELMET.get(),
                ArmorItems.TACTICAL_HELMET.get(),
                ArmorItems.TITANIUM_HELMET.get()
            );
        getOrCreateTagBuilder(ItemTags.CHEST_ARMOR)
            .add(
                ArmorItems.CHITIN_CHESTPLATE.get(),
                ArmorItems.JUNGLE_PREDATOR_CHESTPLATE.get(),
                ArmorItems.NETHER_CHITIN_CHESTPLATE.get(),
                ArmorItems.MK50_CHESTPLATE.get(),
                ArmorItems.PLATED_CHITIN_CHESTPLATE.get(),
                ArmorItems.PLATED_NETHER_CHITIN_CHESTPLATE.get(),
                ArmorItems.PRESSURE_CHESTPLATE.get(),
                ArmorItems.STEEL_CHESTPLATE.get(),
                ArmorItems.TACTICAL_CHESTPLATE.get(),
                ArmorItems.TITANIUM_CHESTPLATE.get()
            );
        getOrCreateTagBuilder(ItemTags.LEG_ARMOR)
            .add(
                ArmorItems.CHITIN_LEGGINGS.get(),
                ArmorItems.JUNGLE_PREDATOR_LEGGINGS.get(),
                ArmorItems.NETHER_CHITIN_LEGGINGS.get(),
                ArmorItems.MK50_LEGGINGS.get(),
                ArmorItems.PLATED_CHITIN_LEGGINGS.get(),
                ArmorItems.PLATED_NETHER_CHITIN_LEGGINGS.get(),
                ArmorItems.PRESSURE_LEGGINGS.get(),
                ArmorItems.STEEL_LEGGINGS.get(),
                ArmorItems.TACTICAL_LEGGINGS.get(),
                ArmorItems.TITANIUM_LEGGINGS.get()
            );
        getOrCreateTagBuilder(ItemTags.FOOT_ARMOR)
            .add(
                ArmorItems.CHITIN_BOOTS.get(),
                ArmorItems.JUNGLE_PREDATOR_BOOTS.get(),
                ArmorItems.NETHER_CHITIN_BOOTS.get(),
                ArmorItems.MK50_BOOTS.get(),
                ArmorItems.PLATED_CHITIN_BOOTS.get(),
                ArmorItems.PLATED_NETHER_CHITIN_BOOTS.get(),
                ArmorItems.PRESSURE_BOOTS.get(),
                ArmorItems.STEEL_BOOTS.get(),
                ArmorItems.TACTICAL_BOOTS.get(),
                ArmorItems.TITANIUM_BOOTS.get()
            );

        getOrCreateTagBuilder(AVPItemTags.GUNS)
            .add(
                AVPItems.F903WE_RIFLE.get(),
                AVPItems.FLAMETHROWER_SEVASTOPOL.get(),
                AVPItems.M37_12_SHOTGUN.get(),
                AVPItems.M41A_PULSE_RIFLE.get(),
                AVPItems.M42A3_SNIPER_RIFLE.get(),
                AVPItems.M4RA_BATTLE_RIFLE.get(),
                AVPItems.M56_SMARTGUN.get(),
                AVPItems.M6B_ROCKET_LAUNCHER.get(),
                AVPItems.M88_MOD_4_COMBAT_PISTOL.get(),
                AVPItems.OLD_PAINLESS.get(),
                AVPItems.ZX_76_SHOTGUN.get()
            );
    }
}
