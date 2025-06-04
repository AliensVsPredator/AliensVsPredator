package com.avp.fabric.data.tag;

import com.alien.common.registry.init.AlienItems;
import com.alien.common.registry.init.item.AlienArmorItems;
import com.alien.common.registry.init.item.AlienBlockItems;
import com.human.common.registry.init.item.HumanFerroaluminumBlockItems;
import com.human.common.registry.init.item.HumanGunItems;
import com.human.common.registry.init.item.HumanIndustrialConcreteBlockItems;
import com.human.common.registry.init.item.HumanIndustrialGlassBlockItems;
import com.human.common.registry.init.item.HumanPaddingBlockItems;
import com.human.common.registry.init.item.HumanPlasticBlockItems;
import com.human.common.registry.init.item.HumanSteelBlockItems;
import com.human.common.registry.init.item.HumanTitaniumBlockItems;
import com.predator.common.registry.init.item.PredatorArmorItems;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricTagProvider;
import net.minecraft.core.HolderLookup;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.item.Items;

import java.util.Collection;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.function.Supplier;
import java.util.stream.Stream;

import com.avp.common.registry.init.block.CoreBlocks;
import com.avp.common.registry.init.item.AVPArmorItems;
import com.avp.common.registry.init.item.AVPBlockItems;
import com.avp.common.registry.init.item.AVPItems;
import com.avp.common.registry.tag.AVPItemTags;
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
                PredatorArmorItems.JUNGLE_PREDATOR_BOOTS.get(),
                PredatorArmorItems.JUNGLE_PREDATOR_CHESTPLATE.get(),
                PredatorArmorItems.JUNGLE_PREDATOR_HELMET.get(),
                PredatorArmorItems.JUNGLE_PREDATOR_LEGGINGS.get()
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
                AlienArmorItems.NETHER_CHITIN_BOOTS.get(),
                AlienArmorItems.NETHER_CHITIN_CHESTPLATE.get(),
                AlienArmorItems.NETHER_CHITIN_HELMET.get(),
                AlienArmorItems.NETHER_CHITIN_LEGGINGS.get()
            );

        getOrCreateTagBuilder(AVPItemTags.PLATED_NETHER_CHITIN_ARMOR)
            .add(
                AlienArmorItems.PLATED_NETHER_CHITIN_BOOTS.get(),
                AlienArmorItems.PLATED_NETHER_CHITIN_CHESTPLATE.get(),
                AlienArmorItems.PLATED_NETHER_CHITIN_HELMET.get(),
                AlienArmorItems.PLATED_NETHER_CHITIN_LEGGINGS.get()
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
                AVPItems.AUTUNITE_DUST.get(),
                AVPItems.URANIUM_NUGGET.get(),
                AVPItems.URANIUM_INGOT.get(),
                AlienItems.IRRADIATED_CHITIN.get(),
                AlienItems.PLATED_IRRADIATED_CHITIN.get(),
                AlienItems.IRRADIATED_RESIN_BALL.get(),
                AVPBlockItems.AUTUNITE_BLOCK.get(),
                AVPBlockItems.AUTUNITE_ORE.get(),
                AVPBlockItems.URANIUM_BLOCK.get(),
                AVPBlockItems.TRINITITE_BLOCK.get(),
                AlienBlockItems.IRRADIATED_RESIN.get(),
                AlienBlockItems.IRRADIATED_RESIN_NODE.get(),
                AlienBlockItems.IRRADIATED_RESIN_VEIN.get(),
                AlienBlockItems.IRRADIATED_RESIN_WEB.get()
            )
            .addOptionalTag(CommonConstants.URANIUM);

        getOrCreateTagBuilder(AVPItemTags.AMMO_ITEMS)
            .add(
                AVPItems.CASELESS_BULLET.get(),
                AVPItems.HEAVY_BULLET.get(),
                AVPItems.SMALL_BULLET.get(),
                AVPItems.MEDIUM_BULLET.get(),
                AVPItems.SHOTGUN_SHELL.get(),
                AVPItems.ROCKET.get(),
                AVPItems.FUEL_TANK.get()
            );

        getOrCreateTagBuilder(AVPItemTags.FACEHUGGER_PROTECTION_HELMET)
            .add(
                PredatorArmorItems.JUNGLE_PREDATOR_HELMET.get()
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
                AlienArmorItems.ABERRANT_CHITIN_HELMET.get(),
                AlienArmorItems.ABERRANT_CHITIN_CHESTPLATE.get(),
                AlienArmorItems.ABERRANT_CHITIN_LEGGINGS.get(),
                AlienArmorItems.ABERRANT_CHITIN_BOOTS.get(),

                AlienArmorItems.CHITIN_HELMET.get(),
                AlienArmorItems.CHITIN_CHESTPLATE.get(),
                AlienArmorItems.CHITIN_LEGGINGS.get(),
                AlienArmorItems.CHITIN_BOOTS.get(),

                AlienArmorItems.IRRADIATED_CHITIN_HELMET.get(),
                AlienArmorItems.IRRADIATED_CHITIN_CHESTPLATE.get(),
                AlienArmorItems.IRRADIATED_CHITIN_LEGGINGS.get(),
                AlienArmorItems.IRRADIATED_CHITIN_BOOTS.get(),

                AlienArmorItems.NETHER_CHITIN_HELMET.get(),
                AlienArmorItems.NETHER_CHITIN_CHESTPLATE.get(),
                AlienArmorItems.NETHER_CHITIN_LEGGINGS.get(),
                AlienArmorItems.NETHER_CHITIN_BOOTS.get(),

                AlienArmorItems.PLATED_ABERRANT_CHITIN_HELMET.get(),
                AlienArmorItems.PLATED_ABERRANT_CHITIN_CHESTPLATE.get(),
                AlienArmorItems.PLATED_ABERRANT_CHITIN_LEGGINGS.get(),
                AlienArmorItems.PLATED_ABERRANT_CHITIN_BOOTS.get(),

                AlienArmorItems.PLATED_CHITIN_HELMET.get(),
                AlienArmorItems.PLATED_CHITIN_CHESTPLATE.get(),
                AlienArmorItems.PLATED_CHITIN_LEGGINGS.get(),
                AlienArmorItems.PLATED_CHITIN_BOOTS.get(),

                AlienArmorItems.PLATED_IRRADIATED_CHITIN_HELMET.get(),
                AlienArmorItems.PLATED_IRRADIATED_CHITIN_CHESTPLATE.get(),
                AlienArmorItems.PLATED_IRRADIATED_CHITIN_LEGGINGS.get(),
                AlienArmorItems.PLATED_IRRADIATED_CHITIN_BOOTS.get(),

                AlienArmorItems.PLATED_NETHER_CHITIN_HELMET.get(),
                AlienArmorItems.PLATED_NETHER_CHITIN_CHESTPLATE.get(),
                AlienArmorItems.PLATED_NETHER_CHITIN_LEGGINGS.get(),
                AlienArmorItems.PLATED_NETHER_CHITIN_BOOTS.get(),

                AlienItems.CHITIN.get(),
                AlienItems.NETHER_CHITIN.get(),
                AlienItems.ABERRANT_CHITIN.get(),
                AlienItems.IRRADIATED_CHITIN.get(),
                AlienItems.PLATED_CHITIN.get(),
                AlienItems.PLATED_NETHER_CHITIN.get(),
                AlienItems.PLATED_ABERRANT_CHITIN.get(),
                AlienItems.PLATED_IRRADIATED_CHITIN.get()
            );

        getOrCreateTagBuilder(AVPItemTags.DECORATIVE_POT_SHERDS)
            .add(
                AlienItems.OVOID_POTTERY_SHERD.get(),
                AlienItems.PARASITE_POTTERY_SHERD.get(),
                AlienItems.ROYALTY_POTTERY_SHERD.get(),
                AlienItems.VECTOR_POTTERY_SHERD.get()
            );

        getOrCreateTagBuilder(ItemTags.DECORATED_POT_SHERDS)
            .addTag(AVPItemTags.DECORATIVE_POT_SHERDS);

        getOrCreateTagBuilder(AVPItemTags.IRON_BLOCK_LIKE)
            .add(
                Items.IRON_BLOCK,
                AVPBlockItems.ALUMINUM_BLOCK.get(),
                HumanFerroaluminumBlockItems.FERROALUMINUM_BLOCK.get(),
                HumanSteelBlockItems.STEEL_BLOCK.get(),
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

        getOrCreateTagBuilder(AVPItemTags.URANIUM_NUGGET_LIKE)
            .add(
                AVPItems.URANIUM_NUGGET.get(),
                AlienItems.IRRADIATED_CHITIN.get()
            );

        var industrialGlassBlockTagBuilder = getOrCreateTagBuilder(AVPItemTags.INDUSTRIAL_GLASS_BLOCK);

        industrialGlassBlockTagBuilder.add(HumanIndustrialGlassBlockItems.INDUSTRIAL_GLASS.get());
        HumanIndustrialGlassBlockItems.DYE_COLOR_TO_INDUSTRIAL_GLASS.forEach(
            ($, blockItemSupplier) -> industrialGlassBlockTagBuilder.add(blockItemSupplier.get())
        );

        var industrialGlassPaneTagBuilder = getOrCreateTagBuilder(AVPItemTags.INDUSTRIAL_GLASS_PANE);

        industrialGlassPaneTagBuilder.add(HumanIndustrialGlassBlockItems.INDUSTRIAL_GLASS_PANE.get());
        HumanIndustrialGlassBlockItems.DYE_COLOR_TO_INDUSTRIAL_GLASS_PANE.forEach(
            ($, blockItemSupplier) -> industrialGlassPaneTagBuilder.add(blockItemSupplier.get())
        );

        getOrCreateTagBuilder(AVPItemTags.INDUSTRIAL_GLASS)
            .addTag(AVPItemTags.INDUSTRIAL_GLASS_BLOCK)
            .addTag(AVPItemTags.INDUSTRIAL_GLASS_PANE);

        getOrCreateTagBuilder(AVPItemTags.LITHIUM)
            .add(
                CoreBlocks.LITHIUM_BLOCK.get().asItem(),
                CoreBlocks.LITHIUM_ORE.get().asItem(),
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
                AlienArmorItems.ABERRANT_CHITIN_HELMET.get(),
                AlienArmorItems.CHITIN_HELMET.get(),
                AlienArmorItems.IRRADIATED_CHITIN_HELMET.get(),
                PredatorArmorItems.JUNGLE_PREDATOR_HELMET.get(),
                AlienArmorItems.NETHER_CHITIN_HELMET.get(),
                AVPArmorItems.MK50_HELMET.get(),
                AlienArmorItems.PLATED_ABERRANT_CHITIN_HELMET.get(),
                AlienArmorItems.PLATED_CHITIN_HELMET.get(),
                AlienArmorItems.PLATED_IRRADIATED_CHITIN_HELMET.get(),
                AlienArmorItems.PLATED_NETHER_CHITIN_HELMET.get(),
                AVPArmorItems.PRESSURE_HELMET.get(),
                AVPArmorItems.STEEL_HELMET.get(),
                AVPArmorItems.TACTICAL_HELMET.get(),
                AVPArmorItems.TACTICAL_CAMO_HELMET.get(),
                AVPArmorItems.TITANIUM_HELMET.get()
            );
        getOrCreateTagBuilder(ItemTags.CHEST_ARMOR)
            .add(
                AlienArmorItems.ABERRANT_CHITIN_CHESTPLATE.get(),
                AlienArmorItems.CHITIN_CHESTPLATE.get(),
                AlienArmorItems.IRRADIATED_CHITIN_CHESTPLATE.get(),
                PredatorArmorItems.JUNGLE_PREDATOR_CHESTPLATE.get(),
                AlienArmorItems.NETHER_CHITIN_CHESTPLATE.get(),
                AVPArmorItems.MK50_CHESTPLATE.get(),
                AlienArmorItems.PLATED_ABERRANT_CHITIN_CHESTPLATE.get(),
                AlienArmorItems.PLATED_CHITIN_CHESTPLATE.get(),
                AlienArmorItems.PLATED_IRRADIATED_CHITIN_CHESTPLATE.get(),
                AlienArmorItems.PLATED_NETHER_CHITIN_CHESTPLATE.get(),
                AVPArmorItems.PRESSURE_CHESTPLATE.get(),
                AVPArmorItems.STEEL_CHESTPLATE.get(),
                AVPArmorItems.TACTICAL_CHESTPLATE.get(),
                AVPArmorItems.TACTICAL_CAMO_CHESTPLATE.get(),
                AVPArmorItems.TITANIUM_CHESTPLATE.get()
            );
        getOrCreateTagBuilder(ItemTags.LEG_ARMOR)
            .add(
                AlienArmorItems.ABERRANT_CHITIN_LEGGINGS.get(),
                AlienArmorItems.CHITIN_LEGGINGS.get(),
                AlienArmorItems.IRRADIATED_CHITIN_LEGGINGS.get(),
                PredatorArmorItems.JUNGLE_PREDATOR_LEGGINGS.get(),
                AlienArmorItems.NETHER_CHITIN_LEGGINGS.get(),
                AVPArmorItems.MK50_LEGGINGS.get(),
                AlienArmorItems.PLATED_ABERRANT_CHITIN_LEGGINGS.get(),
                AlienArmorItems.PLATED_CHITIN_LEGGINGS.get(),
                AlienArmorItems.PLATED_IRRADIATED_CHITIN_LEGGINGS.get(),
                AlienArmorItems.PLATED_NETHER_CHITIN_LEGGINGS.get(),
                AVPArmorItems.PRESSURE_LEGGINGS.get(),
                AVPArmorItems.STEEL_LEGGINGS.get(),
                AVPArmorItems.TACTICAL_LEGGINGS.get(),
                AVPArmorItems.TACTICAL_CAMO_LEGGINGS.get(),
                AVPArmorItems.TITANIUM_LEGGINGS.get()
            );
        getOrCreateTagBuilder(ItemTags.FOOT_ARMOR)
            .add(
                AlienArmorItems.ABERRANT_CHITIN_BOOTS.get(),
                AlienArmorItems.CHITIN_BOOTS.get(),
                AlienArmorItems.IRRADIATED_CHITIN_BOOTS.get(),
                PredatorArmorItems.JUNGLE_PREDATOR_BOOTS.get(),
                AlienArmorItems.NETHER_CHITIN_BOOTS.get(),
                AVPArmorItems.MK50_BOOTS.get(),
                AlienArmorItems.PLATED_ABERRANT_CHITIN_BOOTS.get(),
                AlienArmorItems.PLATED_CHITIN_BOOTS.get(),
                AlienArmorItems.PLATED_IRRADIATED_CHITIN_BOOTS.get(),
                AlienArmorItems.PLATED_NETHER_CHITIN_BOOTS.get(),
                AVPArmorItems.PRESSURE_BOOTS.get(),
                AVPArmorItems.STEEL_BOOTS.get(),
                AVPArmorItems.TACTICAL_BOOTS.get(),
                AVPArmorItems.TACTICAL_CAMO_BOOTS.get(),
                AVPArmorItems.TITANIUM_BOOTS.get()
            );

        getOrCreateTagBuilder(AVPItemTags.GUNS)
            .add(
                HumanGunItems.F903WE_RIFLE.get(),
                HumanGunItems.FLAMETHROWER_SEVASTOPOL.get(),
                HumanGunItems.M37_12_SHOTGUN.get(),
                HumanGunItems.M41A_PULSE_RIFLE.get(),
                HumanGunItems.M42A3_SNIPER_RIFLE.get(),
                HumanGunItems.M4RA_BATTLE_RIFLE.get(),
                HumanGunItems.M56_SMARTGUN.get(),
                HumanGunItems.M6B_ROCKET_LAUNCHER.get(),
                HumanGunItems.M88MOD4_COMBAT_PISTOL.get(),
                HumanGunItems.OLD_PAINLESS.get(),
                HumanGunItems.ZX_76_SHOTGUN.get()
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
            HumanFerroaluminumBlockItems.FERROALUMINUM_CHAIN_FENCE.get(),
            HumanSteelBlockItems.STEEL_CHAIN_FENCE.get(),
            HumanTitaniumBlockItems.TITANIUM_CHAIN_FENCE.get()
        );

        var doorTagProvider = getOrCreateTagBuilder(ItemTags.DOORS);

        doorTagProvider.add(
            HumanFerroaluminumBlockItems.FERROALUMINUM_DOOR.get(),
            HumanIndustrialGlassBlockItems.INDUSTRIAL_GLASS_DOOR.get(),
            HumanSteelBlockItems.STEEL_DOOR.get(),
            HumanTitaniumBlockItems.TITANIUM_DOOR.get()
        );

        var trapdoorTagProvider = getOrCreateTagBuilder(ItemTags.TRAPDOORS);

        trapdoorTagProvider.add(
            HumanFerroaluminumBlockItems.FERROALUMINUM_TRAP_DOOR.get(),
            HumanIndustrialGlassBlockItems.INDUSTRIAL_GLASS_TRAP_DOOR.get(),
            HumanSteelBlockItems.STEEL_TRAP_DOOR.get(),
            HumanTitaniumBlockItems.TITANIUM_TRAP_DOOR.get()
        );

        var slabTagProvider = getOrCreateTagBuilder(ItemTags.SLABS);

        slabTagProvider.add(
            HumanFerroaluminumBlockItems.CUT_FERROALUMINUM_SLAB.get(),
            HumanSteelBlockItems.CUT_STEEL_SLAB.get(),
            HumanTitaniumBlockItems.CUT_TITANIUM_SLAB.get(),
            HumanFerroaluminumBlockItems.FERROALUMINUM_FASTENED_SIDING_SLAB.get(),
            HumanFerroaluminumBlockItems.FERROALUMINUM_FASTENED_STANDING_SLAB.get(),
            HumanFerroaluminumBlockItems.FERROALUMINUM_GRATE_SLAB.get(),
            HumanFerroaluminumBlockItems.FERROALUMINUM_PLATING_SLAB.get(),
            HumanFerroaluminumBlockItems.FERROALUMINUM_SIDING_SLAB.get(),
            HumanFerroaluminumBlockItems.FERROALUMINUM_SLAB.get(),
            HumanFerroaluminumBlockItems.FERROALUMINUM_STANDING_SLAB.get(),
            HumanFerroaluminumBlockItems.FERROALUMINUM_TREAD_SLAB.get(),
            HumanIndustrialGlassBlockItems.INDUSTRIAL_GLASS_SLAB.get(),
            HumanSteelBlockItems.STEEL_FASTENED_SIDING_SLAB.get(),
            HumanSteelBlockItems.STEEL_FASTENED_STANDING_SLAB.get(),
            HumanSteelBlockItems.STEEL_GRATE_SLAB.get(),
            HumanSteelBlockItems.STEEL_PLATING_SLAB.get(),
            HumanSteelBlockItems.STEEL_SIDING_SLAB.get(),
            HumanSteelBlockItems.STEEL_SLAB.get(),
            HumanSteelBlockItems.STEEL_STANDING_SLAB.get(),
            HumanSteelBlockItems.STEEL_TREAD_SLAB.get(),
            HumanTitaniumBlockItems.TITANIUM_FASTENED_SIDING_SLAB.get(),
            HumanTitaniumBlockItems.TITANIUM_FASTENED_STANDING_SLAB.get(),
            HumanTitaniumBlockItems.TITANIUM_GRATE_SLAB.get(),
            HumanTitaniumBlockItems.TITANIUM_PLATING_SLAB.get(),
            HumanTitaniumBlockItems.TITANIUM_SIDING_SLAB.get(),
            HumanTitaniumBlockItems.TITANIUM_SLAB.get(),
            HumanTitaniumBlockItems.TITANIUM_STANDING_SLAB.get(),
            HumanTitaniumBlockItems.TITANIUM_TREAD_SLAB.get()
        );

        Stream.of(
            AVPBlockItems.DYE_COLOR_TO_CONCRETE_SLAB,
            HumanPlasticBlockItems.DYE_COLOR_TO_CUT_PLASTIC_SLAB,
            HumanIndustrialConcreteBlockItems.DYE_COLOR_TO_INDUSTRIAL_CONCRETE_SLAB,
            HumanPaddingBlockItems.DYE_COLOR_TO_PADDING_SLAB,
            HumanPaddingBlockItems.DYE_COLOR_TO_PANEL_PADDING_SLAB,
            HumanPaddingBlockItems.DYE_COLOR_TO_PIPE_PADDING_SLAB,
            HumanPlasticBlockItems.DYE_COLOR_TO_PLASTIC_SLAB
        )
            .map(Map::values)
            .flatMap(Collection::stream)
            .map(Supplier::get)
            .forEach(slabTagProvider::add);

        var buttonTagProvider = getOrCreateTagBuilder(ItemTags.BUTTONS);

        buttonTagProvider.add(
            HumanFerroaluminumBlockItems.FERROALUMINUM_BUTTON.get(),
            HumanSteelBlockItems.STEEL_BUTTON.get(),
            HumanTitaniumBlockItems.TITANIUM_BUTTON.get()
        );

        var stairsTagProvider = getOrCreateTagBuilder(ItemTags.STAIRS);

        stairsTagProvider.add(
            HumanFerroaluminumBlockItems.CUT_FERROALUMINUM_STAIRS.get(),
            HumanSteelBlockItems.CUT_STEEL_STAIRS.get(),
            HumanTitaniumBlockItems.CUT_TITANIUM_STAIRS.get(),
            HumanFerroaluminumBlockItems.FERROALUMINUM_FASTENED_SIDING_STAIRS.get(),
            HumanFerroaluminumBlockItems.FERROALUMINUM_FASTENED_STANDING_STAIRS.get(),
            HumanFerroaluminumBlockItems.FERROALUMINUM_GRATE_STAIRS.get(),
            HumanFerroaluminumBlockItems.FERROALUMINUM_PLATING_STAIRS.get(),
            HumanFerroaluminumBlockItems.FERROALUMINUM_SIDING_STAIRS.get(),
            HumanFerroaluminumBlockItems.FERROALUMINUM_STAIRS.get(),
            HumanFerroaluminumBlockItems.FERROALUMINUM_STANDING_STAIRS.get(),
            HumanFerroaluminumBlockItems.FERROALUMINUM_TREAD_STAIRS.get(),
            HumanIndustrialGlassBlockItems.INDUSTRIAL_GLASS_STAIRS.get(),
            HumanSteelBlockItems.STEEL_FASTENED_SIDING_STAIRS.get(),
            HumanSteelBlockItems.STEEL_FASTENED_STANDING_STAIRS.get(),
            HumanSteelBlockItems.STEEL_GRATE_STAIRS.get(),
            HumanSteelBlockItems.STEEL_PLATING_STAIRS.get(),
            HumanSteelBlockItems.STEEL_SIDING_STAIRS.get(),
            HumanSteelBlockItems.STEEL_STAIRS.get(),
            HumanSteelBlockItems.STEEL_STANDING_STAIRS.get(),
            HumanSteelBlockItems.STEEL_TREAD_STAIRS.get(),
            HumanTitaniumBlockItems.TITANIUM_FASTENED_SIDING_STAIRS.get(),
            HumanTitaniumBlockItems.TITANIUM_FASTENED_STANDING_STAIRS.get(),
            HumanTitaniumBlockItems.TITANIUM_GRATE_STAIRS.get(),
            HumanTitaniumBlockItems.TITANIUM_PLATING_STAIRS.get(),
            HumanTitaniumBlockItems.TITANIUM_SIDING_STAIRS.get(),
            HumanTitaniumBlockItems.TITANIUM_STAIRS.get(),
            HumanTitaniumBlockItems.TITANIUM_STANDING_STAIRS.get(),
            HumanTitaniumBlockItems.TITANIUM_TREAD_STAIRS.get()
        );

        Stream.of(
            AVPBlockItems.DYE_COLOR_TO_CONCRETE_STAIRS,
            HumanPlasticBlockItems.DYE_COLOR_TO_CUT_PLASTIC_STAIRS,
            HumanIndustrialConcreteBlockItems.DYE_COLOR_TO_INDUSTRIAL_CONCRETE_STAIRS,
            HumanPaddingBlockItems.DYE_COLOR_TO_PADDING_STAIRS,
            HumanPaddingBlockItems.DYE_COLOR_TO_PANEL_PADDING_STAIRS,
            HumanPaddingBlockItems.DYE_COLOR_TO_PIPE_PADDING_STAIRS,
            HumanPlasticBlockItems.DYE_COLOR_TO_PLASTIC_STAIRS
        )
            .map(Map::values)
            .flatMap(Collection::stream)
            .map(Supplier::get)
            .forEach(stairsTagProvider::add);

        var wallTagBuilder = getOrCreateTagBuilder(ItemTags.WALLS);

        HumanIndustrialConcreteBlockItems.DYE_COLOR_TO_INDUSTRIAL_CONCRETE_WALL.values()
            .stream()
            .map(Supplier::get)
            .forEach(wallTagBuilder::add);

        var freezeImmuneTagBuilder = getOrCreateTagBuilder(ItemTags.FREEZE_IMMUNE_WEARABLES);

        freezeImmuneTagBuilder.add(
            PredatorArmorItems.JUNGLE_PREDATOR_HELMET.get(),
            PredatorArmorItems.JUNGLE_PREDATOR_CHESTPLATE.get(),
            PredatorArmorItems.JUNGLE_PREDATOR_LEGGINGS.get(),
            PredatorArmorItems.JUNGLE_PREDATOR_BOOTS.get()
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
                AVPItems.ALUMINUM_INGOT.get(),
                AVPItems.BRASS_INGOT.get(),
                AVPItems.FERROALUMINUM_INGOT.get(),
                AVPItems.LEAD_INGOT.get(),
                AVPItems.STEEL_INGOT.get(),
                AVPItems.TITANIUM_INGOT.get(),
                AVPItems.URANIUM_INGOT.get(),
                AVPItems.ZINC_INGOT.get()
            );

        getOrCreateTagBuilder(CommonConstants.INGOTS_ALUMINUM)
            .setReplace(false)
            .add(
                AVPItems.ALUMINUM_INGOT.get()
            );

        getOrCreateTagBuilder(CommonConstants.INGOTS_BRASS)
            .setReplace(false)
            .add(
                AVPItems.BRASS_INGOT.get()
            );

        getOrCreateTagBuilder(CommonConstants.INGOTS_LEAD)
            .setReplace(false)
            .add(
                AVPItems.LEAD_INGOT.get()
            );

        getOrCreateTagBuilder(CommonConstants.INGOTS_STEEL)
            .setReplace(false)
            .add(
                AVPItems.STEEL_INGOT.get()
            );

        getOrCreateTagBuilder(CommonConstants.INGOTS_TITANIUM)
            .setReplace(false)
            .add(
                AVPItems.TITANIUM_INGOT.get()
            );

        getOrCreateTagBuilder(CommonConstants.INGOTS_ZINC)
            .setReplace(false)
            .add(
                AVPItems.ZINC_INGOT.get()
            );

        getOrCreateTagBuilder(CommonConstants.NUGGETS)
            .setReplace(false)
            .add(
                AVPItems.ALUMINUM_NUGGET.get(),
                AVPItems.BRASS_NUGGET.get(),
                AVPItems.FERROALUMINUM_NUGGET.get(),
                AVPItems.LEAD_NUGGET.get(),
                AVPItems.STEEL_NUGGET.get(),
                AVPItems.TITANIUM_NUGGET.get(),
                AVPItems.URANIUM_NUGGET.get(),
                AVPItems.ZINC_NUGGET.get()
            );

        getOrCreateTagBuilder(CommonConstants.NUGGETS_ALUMINUM)
            .setReplace(false)
            .add(
                AVPItems.ALUMINUM_NUGGET.get()
            );

        getOrCreateTagBuilder(CommonConstants.NUGGETS_BRASS)
            .setReplace(false)
            .add(
                AVPItems.BRASS_NUGGET.get()
            );

        getOrCreateTagBuilder(CommonConstants.NUGGETS_LEAD)
            .setReplace(false)
            .add(
                AVPItems.LEAD_NUGGET.get()
            );

        getOrCreateTagBuilder(CommonConstants.NUGGETS_STEEL)
            .setReplace(false)
            .add(
                AVPItems.STEEL_NUGGET.get()
            );

        getOrCreateTagBuilder(CommonConstants.NUGGETS_TITANIUM)
            .setReplace(false)
            .add(
                AVPItems.TITANIUM_NUGGET.get()
            );

        getOrCreateTagBuilder(CommonConstants.NUGGETS_ZINC)
            .setReplace(false)
            .add(
                AVPItems.ZINC_NUGGET.get()
            );

        getOrCreateTagBuilder(CommonConstants.ORES)
            .setReplace(false)
            .add(
                AVPBlockItems.AUTUNITE_ORE.get(),
                AVPBlockItems.BAUXITE_ORE.get(),
                AVPBlockItems.DEEPSLATE_TITANIUM_ORE.get(),
                AVPBlockItems.DEEPSLATE_ZINC_ORE.get(),
                AVPBlockItems.GALENA_ORE.get(),
                AVPBlockItems.LITHIUM_ORE.get(),
                AVPBlockItems.MONAZITE_ORE.get(),
                AVPBlockItems.ZINC_ORE.get()
            );
    }
}
