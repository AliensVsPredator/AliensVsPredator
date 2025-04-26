package com.avp.data.tag;

import com.avp.data.compatibility.common.CommonConstants;
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
        getOrCreateTagBuilder(AVPItemTags.RADIATION_CURE_ITEMS)
            .add(
                Items.GOLDEN_APPLE,
                Items.ENCHANTED_GOLDEN_APPLE
            );

        getOrCreateTagBuilder(AVPItemTags.RADIATION_RESISTANT_ARMOR)
            .add(
                ArmorItems.MK50_HELMET,
                ArmorItems.MK50_CHESTPLATE,
                ArmorItems.MK50_LEGGINGS,
                ArmorItems.MK50_BOOTS
            );

        getOrCreateTagBuilder(AVPItemTags.JUNGLE_PREDATOR_ARMOR)
            .add(
                ArmorItems.JUNGLE_PREDATOR_BOOTS,
                ArmorItems.JUNGLE_PREDATOR_CHESTPLATE,
                ArmorItems.JUNGLE_PREDATOR_HELMET,
                ArmorItems.JUNGLE_PREDATOR_LEGGINGS
            );

        getOrCreateTagBuilder(AVPItemTags.MK50_ARMOR)
            .add(
                ArmorItems.MK50_BOOTS,
                ArmorItems.MK50_CHESTPLATE,
                ArmorItems.MK50_HELMET,
                ArmorItems.MK50_LEGGINGS
            );

        getOrCreateTagBuilder(AVPItemTags.NETHER_CHITIN_ARMOR)
            .add(
                ArmorItems.NETHER_CHITIN_BOOTS,
                ArmorItems.NETHER_CHITIN_CHESTPLATE,
                ArmorItems.NETHER_CHITIN_HELMET,
                ArmorItems.NETHER_CHITIN_LEGGINGS
            );

        getOrCreateTagBuilder(AVPItemTags.PLATED_NETHER_CHITIN_ARMOR)
            .add(
                ArmorItems.PLATED_NETHER_CHITIN_BOOTS,
                ArmorItems.PLATED_NETHER_CHITIN_CHESTPLATE,
                ArmorItems.PLATED_NETHER_CHITIN_HELMET,
                ArmorItems.PLATED_NETHER_CHITIN_LEGGINGS
            );

        getOrCreateTagBuilder(AVPItemTags.PRESSURE_ARMOR)
            .add(
                ArmorItems.PRESSURE_BOOTS,
                ArmorItems.PRESSURE_CHESTPLATE,
                ArmorItems.PRESSURE_HELMET,
                ArmorItems.PRESSURE_LEGGINGS
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
                AVPItems.AUTUNITE_DUST,
                AVPItems.URANIUM_NUGGET,
                AVPItems.URANIUM_INGOT,
                AVPItems.IRRADIATED_CHITIN,
                AVPItems.PLATED_IRRADIATED_CHITIN,
                AVPItems.IRRADIATED_RESIN_BALL,
                AVPBlockItems.AUTUNITE_BLOCK,
                AVPBlockItems.AUTUNITE_ORE,
                AVPBlockItems.URANIUM_BLOCK,
                AVPBlockItems.TRINITITE_BLOCK,
                AVPBlockItems.IRRADIATED_RESIN,
                AVPBlockItems.IRRADIATED_RESIN_NODE,
                AVPBlockItems.IRRADIATED_RESIN_VEIN,
                AVPBlockItems.IRRADIATED_RESIN_WEB
            )
            .addOptionalTag(CommonConstants.URANIUM);

        getOrCreateTagBuilder(AVPItemTags.AMMO_ITEMS)
            .add(
                AVPItems.CASELESS_BULLET,
                AVPItems.HEAVY_BULLET,
                AVPItems.SMALL_BULLET,
                AVPItems.MEDIUM_BULLET,
                AVPItems.SHOTGUN_SHELL,
                AVPItems.ROCKET,
                AVPItems.FUEL_TANK
            );

        getOrCreateTagBuilder(AVPItemTags.FACEHUGGER_PROTECTION_HELMET)
            .add(
                ArmorItems.JUNGLE_PREDATOR_HELMET
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
                ArmorItems.ABERRANT_CHITIN_HELMET,
                ArmorItems.ABERRANT_CHITIN_CHESTPLATE,
                ArmorItems.ABERRANT_CHITIN_LEGGINGS,
                ArmorItems.ABERRANT_CHITIN_BOOTS,

                ArmorItems.CHITIN_HELMET,
                ArmorItems.CHITIN_CHESTPLATE,
                ArmorItems.CHITIN_LEGGINGS,
                ArmorItems.CHITIN_BOOTS,

                ArmorItems.IRRADIATED_CHITIN_HELMET,
                ArmorItems.IRRADIATED_CHITIN_CHESTPLATE,
                ArmorItems.IRRADIATED_CHITIN_LEGGINGS,
                ArmorItems.IRRADIATED_CHITIN_BOOTS,

                ArmorItems.NETHER_CHITIN_HELMET,
                ArmorItems.NETHER_CHITIN_CHESTPLATE,
                ArmorItems.NETHER_CHITIN_LEGGINGS,
                ArmorItems.NETHER_CHITIN_BOOTS,

                ArmorItems.PLATED_ABERRANT_CHITIN_HELMET,
                ArmorItems.PLATED_ABERRANT_CHITIN_CHESTPLATE,
                ArmorItems.PLATED_ABERRANT_CHITIN_LEGGINGS,
                ArmorItems.PLATED_ABERRANT_CHITIN_BOOTS,

                ArmorItems.PLATED_CHITIN_HELMET,
                ArmorItems.PLATED_CHITIN_CHESTPLATE,
                ArmorItems.PLATED_CHITIN_LEGGINGS,
                ArmorItems.PLATED_CHITIN_BOOTS,

                ArmorItems.PLATED_IRRADIATED_CHITIN_HELMET,
                ArmorItems.PLATED_IRRADIATED_CHITIN_CHESTPLATE,
                ArmorItems.PLATED_IRRADIATED_CHITIN_LEGGINGS,
                ArmorItems.PLATED_IRRADIATED_CHITIN_BOOTS,

                ArmorItems.PLATED_NETHER_CHITIN_HELMET,
                ArmorItems.PLATED_NETHER_CHITIN_CHESTPLATE,
                ArmorItems.PLATED_NETHER_CHITIN_LEGGINGS,
                ArmorItems.PLATED_NETHER_CHITIN_BOOTS,

                AVPItems.CHITIN,
                AVPItems.NETHER_CHITIN,
                AVPItems.ABERRANT_CHITIN,
                AVPItems.IRRADIATED_CHITIN,
                AVPItems.PLATED_CHITIN,
                AVPItems.PLATED_NETHER_CHITIN,
                AVPItems.PLATED_ABERRANT_CHITIN,
                AVPItems.PLATED_IRRADIATED_CHITIN
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

        getOrCreateTagBuilder(AVPItemTags.URANIUM_NUGGET_LIKE)
            .add(
                AVPItems.URANIUM_NUGGET,
                AVPItems.IRRADIATED_CHITIN
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
                ArmorItems.ABERRANT_CHITIN_HELMET,
                ArmorItems.CHITIN_HELMET,
                ArmorItems.IRRADIATED_CHITIN_HELMET,
                ArmorItems.JUNGLE_PREDATOR_HELMET,
                ArmorItems.NETHER_CHITIN_HELMET,
                ArmorItems.MK50_HELMET,
                ArmorItems.PLATED_ABERRANT_CHITIN_HELMET,
                ArmorItems.PLATED_CHITIN_HELMET,
                ArmorItems.PLATED_IRRADIATED_CHITIN_HELMET,
                ArmorItems.PLATED_NETHER_CHITIN_HELMET,
                ArmorItems.PRESSURE_HELMET,
                ArmorItems.STEEL_HELMET,
                ArmorItems.TACTICAL_HELMET,
                ArmorItems.TACTICAL_CAMO_HELMET,
                ArmorItems.TITANIUM_HELMET
            );
        getOrCreateTagBuilder(ItemTags.CHEST_ARMOR)
            .add(
                ArmorItems.ABERRANT_CHITIN_CHESTPLATE,
                ArmorItems.CHITIN_CHESTPLATE,
                ArmorItems.IRRADIATED_CHITIN_CHESTPLATE,
                ArmorItems.JUNGLE_PREDATOR_CHESTPLATE,
                ArmorItems.NETHER_CHITIN_CHESTPLATE,
                ArmorItems.MK50_CHESTPLATE,
                ArmorItems.PLATED_ABERRANT_CHITIN_CHESTPLATE,
                ArmorItems.PLATED_CHITIN_CHESTPLATE,
                ArmorItems.PLATED_IRRADIATED_CHITIN_CHESTPLATE,
                ArmorItems.PLATED_NETHER_CHITIN_CHESTPLATE,
                ArmorItems.PRESSURE_CHESTPLATE,
                ArmorItems.STEEL_CHESTPLATE,
                ArmorItems.TACTICAL_CHESTPLATE,
                ArmorItems.TACTICAL_CAMO_CHESTPLATE,
                ArmorItems.TITANIUM_CHESTPLATE
            );
        getOrCreateTagBuilder(ItemTags.LEG_ARMOR)
            .add(
                ArmorItems.ABERRANT_CHITIN_LEGGINGS,
                ArmorItems.CHITIN_LEGGINGS,
                ArmorItems.IRRADIATED_CHITIN_LEGGINGS,
                ArmorItems.JUNGLE_PREDATOR_LEGGINGS,
                ArmorItems.NETHER_CHITIN_LEGGINGS,
                ArmorItems.MK50_LEGGINGS,
                ArmorItems.PLATED_ABERRANT_CHITIN_LEGGINGS,
                ArmorItems.PLATED_CHITIN_LEGGINGS,
                ArmorItems.PLATED_IRRADIATED_CHITIN_LEGGINGS,
                ArmorItems.PLATED_NETHER_CHITIN_LEGGINGS,
                ArmorItems.PRESSURE_LEGGINGS,
                ArmorItems.STEEL_LEGGINGS,
                ArmorItems.TACTICAL_LEGGINGS,
                ArmorItems.TACTICAL_CAMO_LEGGINGS,
                ArmorItems.TITANIUM_LEGGINGS
            );
        getOrCreateTagBuilder(ItemTags.FOOT_ARMOR)
            .add(
                ArmorItems.ABERRANT_CHITIN_BOOTS,
                ArmorItems.CHITIN_BOOTS,
                ArmorItems.IRRADIATED_CHITIN_BOOTS,
                ArmorItems.JUNGLE_PREDATOR_BOOTS,
                ArmorItems.NETHER_CHITIN_BOOTS,
                ArmorItems.MK50_BOOTS,
                ArmorItems.PLATED_ABERRANT_CHITIN_BOOTS,
                ArmorItems.PLATED_CHITIN_BOOTS,
                ArmorItems.PLATED_IRRADIATED_CHITIN_BOOTS,
                ArmorItems.PLATED_NETHER_CHITIN_BOOTS,
                ArmorItems.PRESSURE_BOOTS,
                ArmorItems.STEEL_BOOTS,
                ArmorItems.TACTICAL_BOOTS,
                ArmorItems.TACTICAL_CAMO_BOOTS,
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
                AVPItems.M88MOD4_COMBAT_PISTOL,
                AVPItems.OLD_PAINLESS,
                AVPItems.ZX_76_SHOTGUN
            );

        getOrCreateTagBuilder(ItemTags.DYEABLE)
            .add(
                ArmorItems.MK50_HELMET,
                ArmorItems.MK50_CHESTPLATE,
                ArmorItems.MK50_LEGGINGS,
                ArmorItems.MK50_BOOTS
            );

        var fenceTagProvider = getOrCreateTagBuilder(ItemTags.FENCES);

        fenceTagProvider.add(
            AVPBlockItems.FERROALUMINUM_CHAIN_FENCE,
            AVPBlockItems.STEEL_CHAIN_FENCE,
            AVPBlockItems.TITANIUM_CHAIN_FENCE
        );

        var doorTagProvider = getOrCreateTagBuilder(ItemTags.DOORS);

        doorTagProvider.add(
            AVPBlockItems.FERROALUMINUM_DOOR,
            AVPBlockItems.INDUSTRIAL_GLASS_DOOR,
            AVPBlockItems.STEEL_DOOR,
            AVPBlockItems.TITANIUM_DOOR
        );

        var trapdoorTagProvider = getOrCreateTagBuilder(ItemTags.TRAPDOORS);

        trapdoorTagProvider.add(
            AVPBlockItems.FERROALUMINUM_TRAP_DOOR,
            AVPBlockItems.INDUSTRIAL_GLASS_TRAP_DOOR,
            AVPBlockItems.STEEL_TRAP_DOOR,
            AVPBlockItems.TITANIUM_TRAP_DOOR
        );

        var slabTagProvider = getOrCreateTagBuilder(ItemTags.SLABS);

        slabTagProvider.add(
            AVPBlockItems.CUT_FERROALUMINUM_SLAB,
            AVPBlockItems.CUT_STEEL_SLAB,
            AVPBlockItems.CUT_TITANIUM_SLAB,
            AVPBlockItems.FERROALUMINUM_FASTENED_SIDING_SLAB,
            AVPBlockItems.FERROALUMINUM_FASTENED_STANDING_SLAB,
            AVPBlockItems.FERROALUMINUM_GRATE_SLAB,
            AVPBlockItems.FERROALUMINUM_PLATING_SLAB,
            AVPBlockItems.FERROALUMINUM_SIDING_SLAB,
            AVPBlockItems.FERROALUMINUM_SLAB,
            AVPBlockItems.FERROALUMINUM_STANDING_SLAB,
            AVPBlockItems.FERROALUMINUM_TREAD_SLAB,
            AVPBlockItems.INDUSTRIAL_GLASS_SLAB,
            AVPBlockItems.STEEL_FASTENED_SIDING_SLAB,
            AVPBlockItems.STEEL_FASTENED_STANDING_SLAB,
            AVPBlockItems.STEEL_GRATE_SLAB,
            AVPBlockItems.STEEL_PLATING_SLAB,
            AVPBlockItems.STEEL_SIDING_SLAB,
            AVPBlockItems.STEEL_SLAB,
            AVPBlockItems.STEEL_STANDING_SLAB,
            AVPBlockItems.STEEL_TREAD_SLAB,
            AVPBlockItems.TITANIUM_FASTENED_SIDING_SLAB,
            AVPBlockItems.TITANIUM_FASTENED_STANDING_SLAB,
            AVPBlockItems.TITANIUM_GRATE_SLAB,
            AVPBlockItems.TITANIUM_PLATING_SLAB,
            AVPBlockItems.TITANIUM_SIDING_SLAB,
            AVPBlockItems.TITANIUM_SLAB,
            AVPBlockItems.TITANIUM_STANDING_SLAB,
            AVPBlockItems.TITANIUM_TREAD_SLAB
        );

        AVPBlockItems.DYE_COLOR_TO_CONCRETE_SLAB.values().forEach(slabTagProvider::add);
        AVPBlockItems.DYE_COLOR_TO_CUT_PLASTIC_SLAB.values().forEach(slabTagProvider::add);
        AVPBlockItems.DYE_COLOR_TO_INDUSTRIAL_CONCRETE_SLAB.values().forEach(slabTagProvider::add);
        AVPBlockItems.DYE_COLOR_TO_PADDING_SLAB.values().forEach(slabTagProvider::add);
        AVPBlockItems.DYE_COLOR_TO_PANEL_PADDING_SLAB.values().forEach(slabTagProvider::add);
        AVPBlockItems.DYE_COLOR_TO_PIPE_PADDING_SLAB.values().forEach(slabTagProvider::add);
        AVPBlockItems.DYE_COLOR_TO_PLASTIC_SLAB.values().forEach(slabTagProvider::add);

        var buttonTagProvider = getOrCreateTagBuilder(ItemTags.BUTTONS);

        buttonTagProvider.add(
            AVPBlockItems.FERROALUMINUM_BUTTON,
            AVPBlockItems.STEEL_BUTTON,
            AVPBlockItems.TITANIUM_BUTTON
        );

        var stairsTagProvider = getOrCreateTagBuilder(ItemTags.STAIRS);

        stairsTagProvider.add(
            AVPBlockItems.CUT_FERROALUMINUM_STAIRS,
            AVPBlockItems.CUT_STEEL_STAIRS,
            AVPBlockItems.CUT_TITANIUM_STAIRS,
            AVPBlockItems.FERROALUMINUM_FASTENED_SIDING_STAIRS,
            AVPBlockItems.FERROALUMINUM_FASTENED_STANDING_STAIRS,
            AVPBlockItems.FERROALUMINUM_GRATE_STAIRS,
            AVPBlockItems.FERROALUMINUM_PLATING_STAIRS,
            AVPBlockItems.FERROALUMINUM_SIDING_STAIRS,
            AVPBlockItems.FERROALUMINUM_STAIRS,
            AVPBlockItems.FERROALUMINUM_STANDING_STAIRS,
            AVPBlockItems.FERROALUMINUM_TREAD_STAIRS,
            AVPBlockItems.INDUSTRIAL_GLASS_STAIRS,
            AVPBlockItems.STEEL_FASTENED_SIDING_STAIRS,
            AVPBlockItems.STEEL_FASTENED_STANDING_STAIRS,
            AVPBlockItems.STEEL_GRATE_STAIRS,
            AVPBlockItems.STEEL_PLATING_STAIRS,
            AVPBlockItems.STEEL_SIDING_STAIRS,
            AVPBlockItems.STEEL_STAIRS,
            AVPBlockItems.STEEL_STANDING_STAIRS,
            AVPBlockItems.STEEL_TREAD_STAIRS,
            AVPBlockItems.TITANIUM_FASTENED_SIDING_STAIRS,
            AVPBlockItems.TITANIUM_FASTENED_STANDING_STAIRS,
            AVPBlockItems.TITANIUM_GRATE_STAIRS,
            AVPBlockItems.TITANIUM_PLATING_STAIRS,
            AVPBlockItems.TITANIUM_SIDING_STAIRS,
            AVPBlockItems.TITANIUM_STAIRS,
            AVPBlockItems.TITANIUM_STANDING_STAIRS,
            AVPBlockItems.TITANIUM_TREAD_STAIRS
        );

        AVPBlockItems.DYE_COLOR_TO_CONCRETE_STAIRS.values().forEach(stairsTagProvider::add);
        AVPBlockItems.DYE_COLOR_TO_CUT_PLASTIC_STAIRS.values().forEach(stairsTagProvider::add);
        AVPBlockItems.DYE_COLOR_TO_INDUSTRIAL_CONCRETE_STAIRS.values().forEach(stairsTagProvider::add);
        AVPBlockItems.DYE_COLOR_TO_PADDING_STAIRS.values().forEach(stairsTagProvider::add);
        AVPBlockItems.DYE_COLOR_TO_PANEL_PADDING_STAIRS.values().forEach(stairsTagProvider::add);
        AVPBlockItems.DYE_COLOR_TO_PIPE_PADDING_STAIRS.values().forEach(stairsTagProvider::add);
        AVPBlockItems.DYE_COLOR_TO_PLASTIC_STAIRS.values().forEach(stairsTagProvider::add);

        var wallTagBuilder = getOrCreateTagBuilder(ItemTags.WALLS);

        AVPBlockItems.DYE_COLOR_TO_INDUSTRIAL_CONCRETE_WALL.values().forEach(wallTagBuilder::add);

        var freezeImmuneTagBuilder = getOrCreateTagBuilder(ItemTags.FREEZE_IMMUNE_WEARABLES);

        freezeImmuneTagBuilder.add(
            ArmorItems.JUNGLE_PREDATOR_HELMET,
            ArmorItems.JUNGLE_PREDATOR_CHESTPLATE,
            ArmorItems.JUNGLE_PREDATOR_LEGGINGS,
            ArmorItems.JUNGLE_PREDATOR_BOOTS
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
                AVPItems.ALUMINUM_INGOT,
                AVPItems.BRASS_INGOT,
                AVPItems.FERROALUMINUM_INGOT,
                AVPItems.LEAD_INGOT,
                AVPItems.STEEL_INGOT,
                AVPItems.TITANIUM_INGOT,
                AVPItems.URANIUM_INGOT,
                AVPItems.ZINC_INGOT
            );
        getOrCreateTagBuilder(CommonConstants.INGOTS_ALUMINUM)
            .setReplace(false)
            .add(
                AVPItems.ALUMINUM_INGOT
            );
        getOrCreateTagBuilder(CommonConstants.INGOTS_BRASS)
            .setReplace(false)
            .add(
                AVPItems.BRASS_INGOT
            );
        getOrCreateTagBuilder(CommonConstants.INGOTS_LEAD)
            .setReplace(false)
            .add(
                AVPItems.LEAD_INGOT
            );
        getOrCreateTagBuilder(CommonConstants.INGOTS_STEEL)
            .setReplace(false)
            .add(
                AVPItems.STEEL_INGOT
            );
        getOrCreateTagBuilder(CommonConstants.INGOTS_TITANIUM)
            .setReplace(false)
            .add(
                AVPItems.TITANIUM_INGOT
            );
        getOrCreateTagBuilder(CommonConstants.INGOTS_ZINC)
            .setReplace(false)
            .add(
                AVPItems.ZINC_INGOT
            );
        getOrCreateTagBuilder(CommonConstants.NUGGETS)
            .setReplace(false)
            .add(
                AVPItems.ALUMINUM_NUGGET,
                AVPItems.BRASS_NUGGET,
                AVPItems.FERROALUMINUM_NUGGET,
                AVPItems.LEAD_NUGGET,
                AVPItems.STEEL_NUGGET,
                AVPItems.TITANIUM_NUGGET,
                AVPItems.URANIUM_NUGGET,
                AVPItems.ZINC_NUGGET
            );
        getOrCreateTagBuilder(CommonConstants.NUGGETS_ALUMINUM)
            .setReplace(false)
            .add(
                AVPItems.ALUMINUM_NUGGET
            );
        getOrCreateTagBuilder(CommonConstants.NUGGETS_BRASS)
            .setReplace(false)
            .add(
                AVPItems.BRASS_NUGGET
            );
        getOrCreateTagBuilder(CommonConstants.NUGGETS_LEAD)
            .setReplace(false)
            .add(
                AVPItems.LEAD_NUGGET
            );
        getOrCreateTagBuilder(CommonConstants.NUGGETS_STEEL)
            .setReplace(false)
            .add(
                AVPItems.STEEL_NUGGET
            );
        getOrCreateTagBuilder(CommonConstants.NUGGETS_TITANIUM)
            .setReplace(false)
            .add(
                AVPItems.TITANIUM_NUGGET
            );
        getOrCreateTagBuilder(CommonConstants.NUGGETS_ZINC)
            .setReplace(false)
            .add(
                AVPItems.ZINC_NUGGET
            );
        getOrCreateTagBuilder(CommonConstants.ORES)
            .setReplace(false)
            .add(
                AVPBlockItems.AUTUNITE_ORE,
                AVPBlockItems.BAUXITE_ORE,
                AVPBlockItems.LITHIUM_ORE,
                AVPBlockItems.DEEPSLATE_TITANIUM_ORE,
                AVPBlockItems.GALENA_ORE,
                AVPBlockItems.DEEPSLATE_ZINC_ORE,
                AVPBlockItems.MONAZITE_ORE,
                AVPBlockItems.ZINC_ORE
            );
    }
}
