package com.avp.fabric.data.tag;

import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricTagProvider;
import net.minecraft.core.HolderLookup;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.item.Items;

import java.util.concurrent.CompletableFuture;
import java.util.function.Supplier;

import com.avp.common.block.TempAVPBlocks;
import com.avp.common.item.AVPArmorItems;
import com.avp.common.item.AVPItemTags;
import com.avp.common.item.TempAVPBlockItems;
import com.avp.common.item.TempAVPItems;
import com.avp.fabric.common.item.AVPItems;
import com.avp.fabric.data.compatibility.common.CommonConstants;

public class AVPItemTagProvider extends FabricTagProvider.ItemTagProvider {

    public AVPItemTagProvider(FabricDataOutput output, CompletableFuture<HolderLookup.Provider> completableFuture) {
        super(output, completableFuture);
    }

    @Override
    protected void addTags(HolderLookup.Provider wrapperLookup) {
        getOrCreateTagBuilder(AVPItemTags.RADIATION_CURE_ITEMS)
            .add(
                Items.GOLDEN_APPLE,
                Items.ENCHANTED_GOLDEN_APPLE
            );

        getOrCreateTagBuilder(AVPItemTags.RADIATION_RESISTANT_ARMOR)
            .add(
                AVPArmorItems.MK50_HELMET.get(),
                AVPArmorItems.MK50_CHESTPLATE.get(),
                AVPArmorItems.MK50_LEGGINGS.get(),
                AVPArmorItems.MK50_BOOTS.get()
            );

        getOrCreateTagBuilder(AVPItemTags.JUNGLE_PREDATOR_ARMOR)
            .add(
                AVPArmorItems.JUNGLE_PREDATOR_BOOTS.get(),
                AVPArmorItems.JUNGLE_PREDATOR_CHESTPLATE.get(),
                AVPArmorItems.JUNGLE_PREDATOR_HELMET.get(),
                AVPArmorItems.JUNGLE_PREDATOR_LEGGINGS.get()
            );

        getOrCreateTagBuilder(AVPItemTags.MK50_ARMOR)
            .add(
                AVPArmorItems.MK50_BOOTS.get(),
                AVPArmorItems.MK50_CHESTPLATE.get(),
                AVPArmorItems.MK50_HELMET.get(),
                AVPArmorItems.MK50_LEGGINGS.get()
            );

        getOrCreateTagBuilder(AVPItemTags.NETHER_CHITIN_ARMOR)
            .add(
                AVPArmorItems.NETHER_CHITIN_BOOTS.get(),
                AVPArmorItems.NETHER_CHITIN_CHESTPLATE.get(),
                AVPArmorItems.NETHER_CHITIN_HELMET.get(),
                AVPArmorItems.NETHER_CHITIN_LEGGINGS.get()
            );

        getOrCreateTagBuilder(AVPItemTags.PLATED_NETHER_CHITIN_ARMOR)
            .add(
                AVPArmorItems.PLATED_NETHER_CHITIN_BOOTS.get(),
                AVPArmorItems.PLATED_NETHER_CHITIN_CHESTPLATE.get(),
                AVPArmorItems.PLATED_NETHER_CHITIN_HELMET.get(),
                AVPArmorItems.PLATED_NETHER_CHITIN_LEGGINGS.get()
            );

        getOrCreateTagBuilder(AVPItemTags.PRESSURE_ARMOR)
            .add(
                AVPArmorItems.PRESSURE_BOOTS.get(),
                AVPArmorItems.PRESSURE_CHESTPLATE.get(),
                AVPArmorItems.PRESSURE_HELMET.get(),
                AVPArmorItems.PRESSURE_LEGGINGS.get()
            );

        // Start composite tags

        getOrCreateTagBuilder(AVPItemTags.FIRE_RESISTANT_ARMOR)
            .addTag(AVPItemTags.NETHER_CHITIN_ARMOR)
            .addTag(AVPItemTags.PLATED_NETHER_CHITIN_ARMOR);

        getOrCreateTagBuilder(AVPItemTags.PREDATOR_ARMOR)
            .addTag(AVPItemTags.JUNGLE_PREDATOR_ARMOR);

        // End composite tags

        getOrCreateTagBuilder(AVPItemTags.RADIATION_ITEMS)
            .add(
                TempAVPItems.AUTUNITE_DUST.get(),
                TempAVPItems.URANIUM_NUGGET.get(),
                TempAVPItems.URANIUM_INGOT.get(),
                TempAVPItems.IRRADIATED_CHITIN.get(),
                TempAVPItems.PLATED_IRRADIATED_CHITIN.get(),
                TempAVPItems.IRRADIATED_RESIN_BALL.get(),
                TempAVPBlockItems.AUTUNITE_BLOCK.get(),
                TempAVPBlockItems.AUTUNITE_ORE.get(),
                TempAVPBlockItems.URANIUM_BLOCK.get(),
                TempAVPBlockItems.TRINITITE_BLOCK.get(),
                TempAVPBlockItems.IRRADIATED_RESIN.get(),
                TempAVPBlockItems.IRRADIATED_RESIN_NODE.get(),
                TempAVPBlockItems.IRRADIATED_RESIN_VEIN.get(),
                TempAVPBlockItems.IRRADIATED_RESIN_WEB.get()
            )
            .addOptionalTag(CommonConstants.URANIUM);

        getOrCreateTagBuilder(AVPItemTags.AMMO_ITEMS)
            .add(
                TempAVPItems.CASELESS_BULLET.get(),
                TempAVPItems.HEAVY_BULLET.get(),
                TempAVPItems.SMALL_BULLET.get(),
                TempAVPItems.MEDIUM_BULLET.get(),
                TempAVPItems.SHOTGUN_SHELL.get(),
                TempAVPItems.ROCKET.get(),
                TempAVPItems.FUEL_TANK.get()
            );

        getOrCreateTagBuilder(AVPItemTags.FACEHUGGER_PROTECTION_HELMET)
            .add(
                AVPArmorItems.JUNGLE_PREDATOR_HELMET.get()
            );

        getOrCreateTagBuilder(AVPItemTags.HOSTILE_WEAPON)
            .addTag(AVPItemTags.GUNS)
            .addTag(ItemTags.AXES)
            .addTag(ItemTags.SWORDS)
            .add(
                Items.BOW,
                Items.CROSSBOW
            );

        // Acid-resistant items
        getOrCreateTagBuilder(AVPItemTags.ACID_IMMUNE)
            .add(
                AVPArmorItems.ABERRANT_CHITIN_HELMET.get(),
                AVPArmorItems.ABERRANT_CHITIN_CHESTPLATE.get(),
                AVPArmorItems.ABERRANT_CHITIN_LEGGINGS.get(),
                AVPArmorItems.ABERRANT_CHITIN_BOOTS.get(),

                AVPArmorItems.CHITIN_HELMET.get(),
                AVPArmorItems.CHITIN_CHESTPLATE.get(),
                AVPArmorItems.CHITIN_LEGGINGS.get(),
                AVPArmorItems.CHITIN_BOOTS.get(),

                AVPArmorItems.IRRADIATED_CHITIN_HELMET.get(),
                AVPArmorItems.IRRADIATED_CHITIN_CHESTPLATE.get(),
                AVPArmorItems.IRRADIATED_CHITIN_LEGGINGS.get(),
                AVPArmorItems.IRRADIATED_CHITIN_BOOTS.get(),

                AVPArmorItems.NETHER_CHITIN_HELMET.get(),
                AVPArmorItems.NETHER_CHITIN_CHESTPLATE.get(),
                AVPArmorItems.NETHER_CHITIN_LEGGINGS.get(),
                AVPArmorItems.NETHER_CHITIN_BOOTS.get(),

                AVPArmorItems.PLATED_ABERRANT_CHITIN_HELMET.get(),
                AVPArmorItems.PLATED_ABERRANT_CHITIN_CHESTPLATE.get(),
                AVPArmorItems.PLATED_ABERRANT_CHITIN_LEGGINGS.get(),
                AVPArmorItems.PLATED_ABERRANT_CHITIN_BOOTS.get(),

                AVPArmorItems.PLATED_CHITIN_HELMET.get(),
                AVPArmorItems.PLATED_CHITIN_CHESTPLATE.get(),
                AVPArmorItems.PLATED_CHITIN_LEGGINGS.get(),
                AVPArmorItems.PLATED_CHITIN_BOOTS.get(),

                AVPArmorItems.PLATED_IRRADIATED_CHITIN_HELMET.get(),
                AVPArmorItems.PLATED_IRRADIATED_CHITIN_CHESTPLATE.get(),
                AVPArmorItems.PLATED_IRRADIATED_CHITIN_LEGGINGS.get(),
                AVPArmorItems.PLATED_IRRADIATED_CHITIN_BOOTS.get(),

                AVPArmorItems.PLATED_NETHER_CHITIN_HELMET.get(),
                AVPArmorItems.PLATED_NETHER_CHITIN_CHESTPLATE.get(),
                AVPArmorItems.PLATED_NETHER_CHITIN_LEGGINGS.get(),
                AVPArmorItems.PLATED_NETHER_CHITIN_BOOTS.get(),

                TempAVPItems.CHITIN.get(),
                TempAVPItems.NETHER_CHITIN.get(),
                TempAVPItems.ABERRANT_CHITIN.get(),
                TempAVPItems.IRRADIATED_CHITIN.get(),
                TempAVPItems.PLATED_CHITIN.get(),
                TempAVPItems.PLATED_NETHER_CHITIN.get(),
                TempAVPItems.PLATED_ABERRANT_CHITIN.get(),
                TempAVPItems.PLATED_IRRADIATED_CHITIN.get()
            );

        getOrCreateTagBuilder(AVPItemTags.DECORATIVE_POT_SHERDS)
            .add(
                TempAVPItems.OVOID_POTTERY_SHERD.get(),
                TempAVPItems.PARASITE_POTTERY_SHERD.get(),
                TempAVPItems.ROYALTY_POTTERY_SHERD.get(),
                TempAVPItems.VECTOR_POTTERY_SHERD.get()
            );

        getOrCreateTagBuilder(ItemTags.DECORATED_POT_SHERDS)
            .addTag(AVPItemTags.DECORATIVE_POT_SHERDS);

        getOrCreateTagBuilder(AVPItemTags.IRON_BLOCK_LIKE)
            .add(
                Items.IRON_BLOCK,
                TempAVPBlockItems.ALUMINUM_BLOCK.get(),
                TempAVPBlockItems.FERROALUMINUM_BLOCK.get(),
                TempAVPBlockItems.STEEL_BLOCK.get(),
                TempAVPBlockItems.ZINC_BLOCK.get()
            );

        getOrCreateTagBuilder(AVPItemTags.IRON_INGOT_LIKE)
            .add(
                Items.IRON_INGOT,
                TempAVPItems.ALUMINUM_INGOT.get(),
                TempAVPItems.FERROALUMINUM_INGOT.get(),
                TempAVPItems.STEEL_INGOT.get(),
                TempAVPItems.ZINC_INGOT.get()
            );

        getOrCreateTagBuilder(AVPItemTags.URANIUM_NUGGET_LIKE)
            .add(
                TempAVPItems.URANIUM_NUGGET.get(),
                TempAVPItems.IRRADIATED_CHITIN.get()
            );

        var industrialGlassBlockTagBuilder = getOrCreateTagBuilder(AVPItemTags.INDUSTRIAL_GLASS_BLOCK);

        industrialGlassBlockTagBuilder.add(TempAVPBlockItems.INDUSTRIAL_GLASS.get());
        TempAVPBlockItems.DYE_COLOR_TO_INDUSTRIAL_GLASS.forEach(
            ($, blockItemSupplier) -> industrialGlassBlockTagBuilder.add(blockItemSupplier.get())
        );

        var industrialGlassPaneTagBuilder = getOrCreateTagBuilder(AVPItemTags.INDUSTRIAL_GLASS_PANE);

        industrialGlassPaneTagBuilder.add(TempAVPBlockItems.INDUSTRIAL_GLASS_PANE.get());
        TempAVPBlockItems.DYE_COLOR_TO_INDUSTRIAL_GLASS_PANE.forEach(
            ($, blockItemSupplier) -> industrialGlassPaneTagBuilder.add(blockItemSupplier.get())
        );

        getOrCreateTagBuilder(AVPItemTags.INDUSTRIAL_GLASS)
            .addTag(AVPItemTags.INDUSTRIAL_GLASS_BLOCK)
            .addTag(AVPItemTags.INDUSTRIAL_GLASS_PANE);

        getOrCreateTagBuilder(AVPItemTags.LITHIUM)
            .add(
                TempAVPBlocks.LITHIUM_BLOCK.get().asItem(),
                TempAVPBlocks.LITHIUM_ORE.get().asItem(),
                TempAVPItems.LITHIUM_DUST.get()
            );

        getOrCreateTagBuilder(ItemTags.AXES)
            .add(
                TempAVPItems.STEEL_AXE.get(),
                TempAVPItems.TITANIUM_AXE.get(),
                TempAVPItems.VERITANIUM_AXE.get()
            );
        getOrCreateTagBuilder(ItemTags.HOES)
            .add(
                TempAVPItems.STEEL_HOE.get(),
                TempAVPItems.TITANIUM_HOE.get(),
                TempAVPItems.VERITANIUM_HOE.get()
            );
        getOrCreateTagBuilder(ItemTags.PICKAXES)
            .add(
                TempAVPItems.STEEL_PICKAXE.get(),
                TempAVPItems.TITANIUM_PICKAXE.get(),
                TempAVPItems.VERITANIUM_PICKAXE.get()
            );
        getOrCreateTagBuilder(ItemTags.SHOVELS)
            .add(
                TempAVPItems.STEEL_SHOVEL.get(),
                TempAVPItems.TITANIUM_SHOVEL.get(),
                TempAVPItems.VERITANIUM_SHOVEL.get()
            );
        getOrCreateTagBuilder(ItemTags.SWORDS)
            .add(
                TempAVPItems.STEEL_SWORD.get(),
                TempAVPItems.TITANIUM_SWORD.get(),
                TempAVPItems.VERITANIUM_SWORD.get()
            );

        getOrCreateTagBuilder(ItemTags.HEAD_ARMOR)
            .add(
                AVPArmorItems.ABERRANT_CHITIN_HELMET.get(),
                AVPArmorItems.CHITIN_HELMET.get(),
                AVPArmorItems.IRRADIATED_CHITIN_HELMET.get(),
                AVPArmorItems.JUNGLE_PREDATOR_HELMET.get(),
                AVPArmorItems.NETHER_CHITIN_HELMET.get(),
                AVPArmorItems.MK50_HELMET.get(),
                AVPArmorItems.PLATED_ABERRANT_CHITIN_HELMET.get(),
                AVPArmorItems.PLATED_CHITIN_HELMET.get(),
                AVPArmorItems.PLATED_IRRADIATED_CHITIN_HELMET.get(),
                AVPArmorItems.PLATED_NETHER_CHITIN_HELMET.get(),
                AVPArmorItems.PRESSURE_HELMET.get(),
                AVPArmorItems.STEEL_HELMET.get(),
                AVPArmorItems.TACTICAL_HELMET.get(),
                AVPArmorItems.TACTICAL_CAMO_HELMET.get(),
                AVPArmorItems.TITANIUM_HELMET.get()
            );
        getOrCreateTagBuilder(ItemTags.CHEST_ARMOR)
            .add(
                AVPArmorItems.ABERRANT_CHITIN_CHESTPLATE.get(),
                AVPArmorItems.CHITIN_CHESTPLATE.get(),
                AVPArmorItems.IRRADIATED_CHITIN_CHESTPLATE.get(),
                AVPArmorItems.JUNGLE_PREDATOR_CHESTPLATE.get(),
                AVPArmorItems.NETHER_CHITIN_CHESTPLATE.get(),
                AVPArmorItems.MK50_CHESTPLATE.get(),
                AVPArmorItems.PLATED_ABERRANT_CHITIN_CHESTPLATE.get(),
                AVPArmorItems.PLATED_CHITIN_CHESTPLATE.get(),
                AVPArmorItems.PLATED_IRRADIATED_CHITIN_CHESTPLATE.get(),
                AVPArmorItems.PLATED_NETHER_CHITIN_CHESTPLATE.get(),
                AVPArmorItems.PRESSURE_CHESTPLATE.get(),
                AVPArmorItems.STEEL_CHESTPLATE.get(),
                AVPArmorItems.TACTICAL_CHESTPLATE.get(),
                AVPArmorItems.TACTICAL_CAMO_CHESTPLATE.get(),
                AVPArmorItems.TITANIUM_CHESTPLATE.get()
            );
        getOrCreateTagBuilder(ItemTags.LEG_ARMOR)
            .add(
                AVPArmorItems.ABERRANT_CHITIN_LEGGINGS.get(),
                AVPArmorItems.CHITIN_LEGGINGS.get(),
                AVPArmorItems.IRRADIATED_CHITIN_LEGGINGS.get(),
                AVPArmorItems.JUNGLE_PREDATOR_LEGGINGS.get(),
                AVPArmorItems.NETHER_CHITIN_LEGGINGS.get(),
                AVPArmorItems.MK50_LEGGINGS.get(),
                AVPArmorItems.PLATED_ABERRANT_CHITIN_LEGGINGS.get(),
                AVPArmorItems.PLATED_CHITIN_LEGGINGS.get(),
                AVPArmorItems.PLATED_IRRADIATED_CHITIN_LEGGINGS.get(),
                AVPArmorItems.PLATED_NETHER_CHITIN_LEGGINGS.get(),
                AVPArmorItems.PRESSURE_LEGGINGS.get(),
                AVPArmorItems.STEEL_LEGGINGS.get(),
                AVPArmorItems.TACTICAL_LEGGINGS.get(),
                AVPArmorItems.TACTICAL_CAMO_LEGGINGS.get(),
                AVPArmorItems.TITANIUM_LEGGINGS.get()
            );
        getOrCreateTagBuilder(ItemTags.FOOT_ARMOR)
            .add(
                AVPArmorItems.ABERRANT_CHITIN_BOOTS.get(),
                AVPArmorItems.CHITIN_BOOTS.get(),
                AVPArmorItems.IRRADIATED_CHITIN_BOOTS.get(),
                AVPArmorItems.JUNGLE_PREDATOR_BOOTS.get(),
                AVPArmorItems.NETHER_CHITIN_BOOTS.get(),
                AVPArmorItems.MK50_BOOTS.get(),
                AVPArmorItems.PLATED_ABERRANT_CHITIN_BOOTS.get(),
                AVPArmorItems.PLATED_CHITIN_BOOTS.get(),
                AVPArmorItems.PLATED_IRRADIATED_CHITIN_BOOTS.get(),
                AVPArmorItems.PLATED_NETHER_CHITIN_BOOTS.get(),
                AVPArmorItems.PRESSURE_BOOTS.get(),
                AVPArmorItems.STEEL_BOOTS.get(),
                AVPArmorItems.TACTICAL_BOOTS.get(),
                AVPArmorItems.TACTICAL_CAMO_BOOTS.get(),
                AVPArmorItems.TITANIUM_BOOTS.get()
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
                AVPItems.M88MOD4_COMBAT_PISTOL,
                AVPItems.OLD_PAINLESS,
                AVPItems.ZX_76_SHOTGUN
            );

        getOrCreateTagBuilder(ItemTags.DYEABLE)
            .add(
                AVPArmorItems.MK50_HELMET.get(),
                AVPArmorItems.MK50_CHESTPLATE.get(),
                AVPArmorItems.MK50_LEGGINGS.get(),
                AVPArmorItems.MK50_BOOTS.get()
            );

        var fenceTagProvider = getOrCreateTagBuilder(ItemTags.FENCES);

        fenceTagProvider.add(
            TempAVPBlockItems.FERROALUMINUM_CHAIN_FENCE.get(),
            TempAVPBlockItems.STEEL_CHAIN_FENCE.get(),
            TempAVPBlockItems.TITANIUM_CHAIN_FENCE.get()
        );

        var doorTagProvider = getOrCreateTagBuilder(ItemTags.DOORS);

        doorTagProvider.add(
            TempAVPBlockItems.FERROALUMINUM_DOOR.get(),
            TempAVPBlockItems.INDUSTRIAL_GLASS_DOOR.get(),
            TempAVPBlockItems.STEEL_DOOR.get(),
            TempAVPBlockItems.TITANIUM_DOOR.get()
        );

        var trapdoorTagProvider = getOrCreateTagBuilder(ItemTags.TRAPDOORS);

        trapdoorTagProvider.add(
            TempAVPBlockItems.FERROALUMINUM_TRAP_DOOR.get(),
            TempAVPBlockItems.INDUSTRIAL_GLASS_TRAP_DOOR.get(),
            TempAVPBlockItems.STEEL_TRAP_DOOR.get(),
            TempAVPBlockItems.TITANIUM_TRAP_DOOR.get()
        );

        var slabTagProvider = getOrCreateTagBuilder(ItemTags.SLABS);

        slabTagProvider.add(
            TempAVPBlockItems.CUT_FERROALUMINUM_SLAB.get(),
            TempAVPBlockItems.CUT_STEEL_SLAB.get(),
            TempAVPBlockItems.CUT_TITANIUM_SLAB.get(),
            TempAVPBlockItems.FERROALUMINUM_FASTENED_SIDING_SLAB.get(),
            TempAVPBlockItems.FERROALUMINUM_FASTENED_STANDING_SLAB.get(),
            TempAVPBlockItems.FERROALUMINUM_GRATE_SLAB.get(),
            TempAVPBlockItems.FERROALUMINUM_PLATING_SLAB.get(),
            TempAVPBlockItems.FERROALUMINUM_SIDING_SLAB.get(),
            TempAVPBlockItems.FERROALUMINUM_SLAB.get(),
            TempAVPBlockItems.FERROALUMINUM_STANDING_SLAB.get(),
            TempAVPBlockItems.FERROALUMINUM_TREAD_SLAB.get(),
            TempAVPBlockItems.INDUSTRIAL_GLASS_SLAB.get(),
            TempAVPBlockItems.STEEL_FASTENED_SIDING_SLAB.get(),
            TempAVPBlockItems.STEEL_FASTENED_STANDING_SLAB.get(),
            TempAVPBlockItems.STEEL_GRATE_SLAB.get(),
            TempAVPBlockItems.STEEL_PLATING_SLAB.get(),
            TempAVPBlockItems.STEEL_SIDING_SLAB.get(),
            TempAVPBlockItems.STEEL_SLAB.get(),
            TempAVPBlockItems.STEEL_STANDING_SLAB.get(),
            TempAVPBlockItems.STEEL_TREAD_SLAB.get(),
            TempAVPBlockItems.TITANIUM_FASTENED_SIDING_SLAB.get(),
            TempAVPBlockItems.TITANIUM_FASTENED_STANDING_SLAB.get(),
            TempAVPBlockItems.TITANIUM_GRATE_SLAB.get(),
            TempAVPBlockItems.TITANIUM_PLATING_SLAB.get(),
            TempAVPBlockItems.TITANIUM_SIDING_SLAB.get(),
            TempAVPBlockItems.TITANIUM_SLAB.get(),
            TempAVPBlockItems.TITANIUM_STANDING_SLAB.get(),
            TempAVPBlockItems.TITANIUM_TREAD_SLAB.get()
        );

        // TODO: Use a stream concat here.
        TempAVPBlockItems.DYE_COLOR_TO_CONCRETE_SLAB.values().stream().map(Supplier::get).forEach(slabTagProvider::add);
        TempAVPBlockItems.DYE_COLOR_TO_CUT_PLASTIC_SLAB.values().stream().map(Supplier::get).forEach(slabTagProvider::add);
        TempAVPBlockItems.DYE_COLOR_TO_INDUSTRIAL_CONCRETE_SLAB.values().stream().map(Supplier::get).forEach(slabTagProvider::add);
        TempAVPBlockItems.DYE_COLOR_TO_PADDING_SLAB.values().stream().map(Supplier::get).forEach(slabTagProvider::add);
        TempAVPBlockItems.DYE_COLOR_TO_PANEL_PADDING_SLAB.values().stream().map(Supplier::get).forEach(slabTagProvider::add);
        TempAVPBlockItems.DYE_COLOR_TO_PIPE_PADDING_SLAB.values().stream().map(Supplier::get).forEach(slabTagProvider::add);
        TempAVPBlockItems.DYE_COLOR_TO_PLASTIC_SLAB.values().stream().map(Supplier::get).forEach(slabTagProvider::add);

        var buttonTagProvider = getOrCreateTagBuilder(ItemTags.BUTTONS);

        buttonTagProvider.add(
            TempAVPBlockItems.FERROALUMINUM_BUTTON.get(),
            TempAVPBlockItems.STEEL_BUTTON.get(),
            TempAVPBlockItems.TITANIUM_BUTTON.get()
        );

        var stairsTagProvider = getOrCreateTagBuilder(ItemTags.STAIRS);

        stairsTagProvider.add(
            TempAVPBlockItems.CUT_FERROALUMINUM_STAIRS.get(),
            TempAVPBlockItems.CUT_STEEL_STAIRS.get(),
            TempAVPBlockItems.CUT_TITANIUM_STAIRS.get(),
            TempAVPBlockItems.FERROALUMINUM_FASTENED_SIDING_STAIRS.get(),
            TempAVPBlockItems.FERROALUMINUM_FASTENED_STANDING_STAIRS.get(),
            TempAVPBlockItems.FERROALUMINUM_GRATE_STAIRS.get(),
            TempAVPBlockItems.FERROALUMINUM_PLATING_STAIRS.get(),
            TempAVPBlockItems.FERROALUMINUM_SIDING_STAIRS.get(),
            TempAVPBlockItems.FERROALUMINUM_STAIRS.get(),
            TempAVPBlockItems.FERROALUMINUM_STANDING_STAIRS.get(),
            TempAVPBlockItems.FERROALUMINUM_TREAD_STAIRS.get(),
            TempAVPBlockItems.INDUSTRIAL_GLASS_STAIRS.get(),
            TempAVPBlockItems.STEEL_FASTENED_SIDING_STAIRS.get(),
            TempAVPBlockItems.STEEL_FASTENED_STANDING_STAIRS.get(),
            TempAVPBlockItems.STEEL_GRATE_STAIRS.get(),
            TempAVPBlockItems.STEEL_PLATING_STAIRS.get(),
            TempAVPBlockItems.STEEL_SIDING_STAIRS.get(),
            TempAVPBlockItems.STEEL_STAIRS.get(),
            TempAVPBlockItems.STEEL_STANDING_STAIRS.get(),
            TempAVPBlockItems.STEEL_TREAD_STAIRS.get(),
            TempAVPBlockItems.TITANIUM_FASTENED_SIDING_STAIRS.get(),
            TempAVPBlockItems.TITANIUM_FASTENED_STANDING_STAIRS.get(),
            TempAVPBlockItems.TITANIUM_GRATE_STAIRS.get(),
            TempAVPBlockItems.TITANIUM_PLATING_STAIRS.get(),
            TempAVPBlockItems.TITANIUM_SIDING_STAIRS.get(),
            TempAVPBlockItems.TITANIUM_STAIRS.get(),
            TempAVPBlockItems.TITANIUM_STANDING_STAIRS.get(),
            TempAVPBlockItems.TITANIUM_TREAD_STAIRS.get()
        );

        // TODO: Use a stream concat here.
        TempAVPBlockItems.DYE_COLOR_TO_CONCRETE_STAIRS.values().stream().map(Supplier::get).forEach(stairsTagProvider::add);
        TempAVPBlockItems.DYE_COLOR_TO_CUT_PLASTIC_STAIRS.values().stream().map(Supplier::get).forEach(stairsTagProvider::add);
        TempAVPBlockItems.DYE_COLOR_TO_INDUSTRIAL_CONCRETE_STAIRS.values().stream().map(Supplier::get).forEach(stairsTagProvider::add);
        TempAVPBlockItems.DYE_COLOR_TO_PADDING_STAIRS.values().stream().map(Supplier::get).forEach(stairsTagProvider::add);
        TempAVPBlockItems.DYE_COLOR_TO_PANEL_PADDING_STAIRS.values().stream().map(Supplier::get).forEach(stairsTagProvider::add);
        TempAVPBlockItems.DYE_COLOR_TO_PIPE_PADDING_STAIRS.values().stream().map(Supplier::get).forEach(stairsTagProvider::add);
        TempAVPBlockItems.DYE_COLOR_TO_PLASTIC_STAIRS.values().stream().map(Supplier::get).forEach(stairsTagProvider::add);

        var wallTagBuilder = getOrCreateTagBuilder(ItemTags.WALLS);

        TempAVPBlockItems.DYE_COLOR_TO_INDUSTRIAL_CONCRETE_WALL.values().stream().map(Supplier::get).forEach(wallTagBuilder::add);

        var freezeImmuneTagBuilder = getOrCreateTagBuilder(ItemTags.FREEZE_IMMUNE_WEARABLES);

        freezeImmuneTagBuilder.add(
            AVPArmorItems.JUNGLE_PREDATOR_HELMET.get(),
            AVPArmorItems.JUNGLE_PREDATOR_CHESTPLATE.get(),
            AVPArmorItems.JUNGLE_PREDATOR_LEGGINGS.get(),
            AVPArmorItems.JUNGLE_PREDATOR_BOOTS.get()
        );

        getOrCreateTagBuilder(AVPItemTags.MELEE_WEAPONS)
            .addOptionalTag(ItemTags.AXES)
            .addOptionalTag(ItemTags.SWORDS)
            .add(
                Items.MACE
            );

        getOrCreateTagBuilder(AVPItemTags.RANGED_WEAPONS)
            .addTag(AVPItemTags.GUNS)
            .add(
                Items.BOW,
                Items.CROSSBOW
            );

        addCompatibilityTags();
    }

    private void addCompatibilityTags() {
        getOrCreateTagBuilder(CommonConstants.INGOTS)
            .setReplace(false)
            .add(
                TempAVPItems.ALUMINUM_INGOT.get(),
                TempAVPItems.BRASS_INGOT.get(),
                TempAVPItems.FERROALUMINUM_INGOT.get(),
                TempAVPItems.LEAD_INGOT.get(),
                TempAVPItems.STEEL_INGOT.get(),
                TempAVPItems.TITANIUM_INGOT.get(),
                TempAVPItems.URANIUM_INGOT.get(),
                TempAVPItems.ZINC_INGOT.get()
            );

        getOrCreateTagBuilder(CommonConstants.INGOTS_ALUMINUM)
            .setReplace(false)
            .add(
                TempAVPItems.ALUMINUM_INGOT.get()
            );

        getOrCreateTagBuilder(CommonConstants.INGOTS_BRASS)
            .setReplace(false)
            .add(
                TempAVPItems.BRASS_INGOT.get()
            );

        getOrCreateTagBuilder(CommonConstants.INGOTS_LEAD)
            .setReplace(false)
            .add(
                TempAVPItems.LEAD_INGOT.get()
            );

        getOrCreateTagBuilder(CommonConstants.INGOTS_STEEL)
            .setReplace(false)
            .add(
                TempAVPItems.STEEL_INGOT.get()
            );

        getOrCreateTagBuilder(CommonConstants.INGOTS_TITANIUM)
            .setReplace(false)
            .add(
                TempAVPItems.TITANIUM_INGOT.get()
            );

        getOrCreateTagBuilder(CommonConstants.INGOTS_ZINC)
            .setReplace(false)
            .add(
                TempAVPItems.ZINC_INGOT.get()
            );

        getOrCreateTagBuilder(CommonConstants.NUGGETS)
            .setReplace(false)
            .add(
                TempAVPItems.ALUMINUM_NUGGET.get(),
                TempAVPItems.BRASS_NUGGET.get(),
                TempAVPItems.FERROALUMINUM_NUGGET.get(),
                TempAVPItems.LEAD_NUGGET.get(),
                TempAVPItems.STEEL_NUGGET.get(),
                TempAVPItems.TITANIUM_NUGGET.get(),
                TempAVPItems.URANIUM_NUGGET.get(),
                TempAVPItems.ZINC_NUGGET.get()
            );

        getOrCreateTagBuilder(CommonConstants.NUGGETS_ALUMINUM)
            .setReplace(false)
            .add(
                TempAVPItems.ALUMINUM_NUGGET.get()
            );

        getOrCreateTagBuilder(CommonConstants.NUGGETS_BRASS)
            .setReplace(false)
            .add(
                TempAVPItems.BRASS_NUGGET.get()
            );

        getOrCreateTagBuilder(CommonConstants.NUGGETS_LEAD)
            .setReplace(false)
            .add(
                TempAVPItems.LEAD_NUGGET.get()
            );

        getOrCreateTagBuilder(CommonConstants.NUGGETS_STEEL)
            .setReplace(false)
            .add(
                TempAVPItems.STEEL_NUGGET.get()
            );

        getOrCreateTagBuilder(CommonConstants.NUGGETS_TITANIUM)
            .setReplace(false)
            .add(
                TempAVPItems.TITANIUM_NUGGET.get()
            );

        getOrCreateTagBuilder(CommonConstants.NUGGETS_ZINC)
            .setReplace(false)
            .add(
                TempAVPItems.ZINC_NUGGET.get()
            );

        getOrCreateTagBuilder(CommonConstants.ORES)
            .setReplace(false)
            .add(
                TempAVPBlockItems.AUTUNITE_ORE.get(),
                TempAVPBlockItems.BAUXITE_ORE.get(),
                TempAVPBlockItems.DEEPSLATE_TITANIUM_ORE.get(),
                TempAVPBlockItems.DEEPSLATE_ZINC_ORE.get(),
                TempAVPBlockItems.GALENA_ORE.get(),
                TempAVPBlockItems.LITHIUM_ORE.get(),
                TempAVPBlockItems.MONAZITE_ORE.get(),
                TempAVPBlockItems.ZINC_ORE.get()
            );
    }
}
