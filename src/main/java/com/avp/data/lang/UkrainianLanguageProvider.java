package com.avp.data.lang;

import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricLanguageProvider;
import net.minecraft.core.HolderLookup;
import net.minecraft.sounds.SoundEvent;

import java.util.Arrays;
import java.util.concurrent.CompletableFuture;

import com.avp.common.block.AVPBlocks;
import com.avp.common.creative_mode_tab.CreativeModeTabs;
import com.avp.common.entity.type.AVPEntityTypes;
import com.avp.common.item.AVPItems;
import com.avp.common.item.ArmorItems;
import com.avp.common.item.SpawnEggItems;
import com.avp.common.sound.AVPSoundEvents;

public class UkrainianLanguageProvider extends FabricLanguageProvider {

    public UkrainianLanguageProvider(FabricDataOutput dataOutput, CompletableFuture<HolderLookup.Provider> registryLookup) {
        super(dataOutput, "uk_ua", registryLookup);
    }

    @Override
    public void generateTranslations(HolderLookup.Provider registryLookup, TranslationBuilder translationBuilder) {
        // Blocks
        translationBuilder.add(AVPBlocks.ASH_BLOCK, "Ash Block");
        translationBuilder.add(AVPBlocks.NUKE_BLOCK, "Ядерна бомба");
        translationBuilder.add(AVPBlocks.ROYAL_JELLY_BLOCK, "Блок королівського слизу");
        translationBuilder.add(AVPBlocks.TRINITITE_BLOCK, "Блок тринітиту");
        translationBuilder.add(AVPBlocks.ALUMINUM_BLOCK, "Блок алюмінію");
        translationBuilder.add(AVPBlocks.AUTUNITE_BLOCK, "Блок аутиніту");
        translationBuilder.add(AVPBlocks.AUTUNITE_ORE, "Аутинітова руда");
        translationBuilder.add(AVPBlocks.BAUXITE_ORE, "Бокситова руда");
        translationBuilder.add(AVPBlocks.BRASS_BLOCK, "Блок латуні");
        translationBuilder.add(AVPBlocks.CHISELED_FERROALUMINUM, "Різьблений фероалюміній");
        translationBuilder.add(AVPBlocks.CHISELED_STEEL, "Різьблена сталь");
        translationBuilder.add(AVPBlocks.CHISELED_TITANIUM, "Різьблений титан");
        translationBuilder.add(AVPBlocks.CUT_FERROALUMINUM, "Шліфований фероалюміній");
        translationBuilder.add(AVPBlocks.CUT_FERROALUMINUM_SLAB, "Шліфована фероалюмінієва плита");
        translationBuilder.add(AVPBlocks.CUT_FERROALUMINUM_STAIRS, "Шліфовані фероалюмінієві сходи");
        translationBuilder.add(AVPBlocks.CUT_STEEL, "Шліфована сталь");
        translationBuilder.add(AVPBlocks.CUT_STEEL_SLAB, "Шліфована сталева плита");
        translationBuilder.add(AVPBlocks.CUT_STEEL_STAIRS, "Шліфовані сталеві сходи");
        translationBuilder.add(AVPBlocks.CUT_TITANIUM, "Шліфований титан");
        translationBuilder.add(AVPBlocks.CUT_TITANIUM_SLAB, "Шліфована титанова плита");
        translationBuilder.add(AVPBlocks.CUT_TITANIUM_STAIRS, "Шліфовані титанові сходи");
        translationBuilder.add(AVPBlocks.DEEPSLATE_TITANIUM_ORE, "Глибосланцева титанова руда");
        translationBuilder.add(AVPBlocks.DEEPSLATE_ZINC_ORE, "Глибосланцева цинкова руда");
        translationBuilder.add(AVPBlocks.FERROALUMINUM_BLOCK, "Блок фероалюмінію");
        translationBuilder.add(AVPBlocks.FERROALUMINUM_BUTTON, "Фероалюмінієва кнопка");
        translationBuilder.add(AVPBlocks.FERROALUMINUM_CHAIN_FENCE, "Фероалюмінієвий ланцюговий паркан");
        translationBuilder.add(AVPBlocks.FERROALUMINUM_COLUMN, "Фероалюмінієва колона");
        translationBuilder.add(AVPBlocks.FERROALUMINUM_DOOR, "Фероалюмінієві двері");
        translationBuilder.add(AVPBlocks.FERROALUMINUM_FASTENED_SIDING, "Фероалюміній кріплений боком");
        translationBuilder.add(AVPBlocks.FERROALUMINUM_FASTENED_STANDING, "Фероалюміній кріплений стоячи");
        translationBuilder.add(AVPBlocks.FERROALUMINUM_GRATE, "Фероалюмінієва решітка");
        translationBuilder.add(AVPBlocks.FERROALUMINUM_PRESSURE_PLATE, "Фероалюмінієва натисна плита");
        translationBuilder.add(AVPBlocks.FERROALUMINUM_PLATING, "Фероалюмінієва обшивка");
        translationBuilder.add(AVPBlocks.FERROALUMINUM_SIDING, "Фероалюміній в положенні боком");
        translationBuilder.add(AVPBlocks.FERROALUMINUM_STANDING, "Фероалюміній в стоячому положенні");
        translationBuilder.add(AVPBlocks.FERROALUMINUM_TRAP_DOOR, "Фероалюмінієвий люк");
        translationBuilder.add(AVPBlocks.FERROALUMINUM_TREAD, "Фероалюмінієвий протектор");
        translationBuilder.add(AVPBlocks.GALENA_ORE, "Руда галеніту");
        translationBuilder.add(AVPBlocks.INDUSTRIAL_GLASS, "Індустріальне скло");
        translationBuilder.add(AVPBlocks.INDUSTRIAL_GLASS_PANE, "Індустріальна скляна плита");
        translationBuilder.add(AVPBlocks.LEAD_BLOCK, "Блок свинцю");
        translationBuilder.add(AVPBlocks.FERROALUMINUM_FASTENED_STANDING_SLAB, "Фероалюмінієва кріплена стоячи плита");
        translationBuilder.add(AVPBlocks.FERROALUMINUM_FASTENED_STANDING_STAIRS, "Фероалюмінієві кріплені стоячи сходи");
        translationBuilder.add(AVPBlocks.FERROALUMINUM_GRATE_SLAB, "Фероалюмінєва решітчаста плита");
        translationBuilder.add(AVPBlocks.FERROALUMINUM_GRATE_STAIRS, "Фероалюмінієві решітчасті сходи");
        translationBuilder.add(AVPBlocks.FERROALUMINUM_PLATING_SLAB, "Фероалюмінієва обшита плита");
        translationBuilder.add(AVPBlocks.FERROALUMINUM_PLATING_STAIRS, "Фероалюмінієві обшиті сходи");
        translationBuilder.add(AVPBlocks.FERROALUMINUM_STANDING_SLAB, "Фероалюмінієва стояча плита");
        translationBuilder.add(AVPBlocks.FERROALUMINUM_STANDING_STAIRS, "Фероалюмінієві стоячі сходи");
        translationBuilder.add(AVPBlocks.FERROALUMINUM_TREAD_SLAB, "Фероалюмінієва протекторна плита");
        translationBuilder.add(AVPBlocks.FERROALUMINUM_TREAD_STAIRS, "Фероалюмінієві протекторні сходи");
        translationBuilder.add(AVPBlocks.STEEL_FASTENED_STANDING_SLAB, "Сталева кріплена стоячи плита");
        translationBuilder.add(AVPBlocks.STEEL_FASTENED_STANDING_STAIRS, "Сталеві кріплені стоячи сходи");
        translationBuilder.add(AVPBlocks.STEEL_GRATE_SLAB, "Сталева решітчаста плита");
        translationBuilder.add(AVPBlocks.STEEL_GRATE_STAIRS, "Сталеві решітчасті сходи");
        translationBuilder.add(AVPBlocks.STEEL_PLATING_SLAB, "Сталева обшита плита");
        translationBuilder.add(AVPBlocks.STEEL_PLATING_STAIRS, "Сталеві обшиті сходи");
        translationBuilder.add(AVPBlocks.STEEL_TREAD_SLAB, "Сталева протекторна плита");
        translationBuilder.add(AVPBlocks.STEEL_TREAD_STAIRS, "Сталеві протекторні сходи");
        translationBuilder.add(AVPBlocks.TITANIUM_FASTENED_STANDING_SLAB, "Титанова кріплена стоячи плита");
        translationBuilder.add(AVPBlocks.TITANIUM_FASTENED_STANDING_STAIRS, "Титанові кріплені стоячи сходи");
        translationBuilder.add(AVPBlocks.TITANIUM_GRATE_SLAB, "Титанова решітчаста плита");
        translationBuilder.add(AVPBlocks.TITANIUM_GRATE_STAIRS, "Титанові решітчасті сходи");
        translationBuilder.add(AVPBlocks.TITANIUM_PLATING_SLAB, "Титанова обшита плита");
        translationBuilder.add(AVPBlocks.TITANIUM_PLATING_STAIRS, "Титанові обшиті сходи");
        translationBuilder.add(AVPBlocks.TITANIUM_TREAD_SLAB, "Титанова протекторна плита");
        translationBuilder.add(AVPBlocks.TITANIUM_TREAD_STAIRS, "Титанові протекторні сходи");

        AVPBlocks.DYE_COLOR_TO_CONCRETE_SLAB.forEach(
            (key, value) -> translationBuilder.add(value, format(key.getName()) + " бетонна плита")
        );
        AVPBlocks.DYE_COLOR_TO_CONCRETE_STAIRS.forEach(
            (key, value) -> translationBuilder.add(value, format(key.getName()) + " бетонні сходи")
        );

        AVPBlocks.DYE_COLOR_TO_INDUSTRIAL_CONCRETE.forEach(
            (key, value) -> translationBuilder.add(value, format(key.getName()) + " індустріальний бетон")
        );
        AVPBlocks.DYE_COLOR_TO_INDUSTRIAL_CONCRETE_SLAB.forEach(
            (key, value) -> translationBuilder.add(value, format(key.getName()) + " індустріальна бетонна плита")
        );
        AVPBlocks.DYE_COLOR_TO_INDUSTRIAL_CONCRETE_STAIRS.forEach(
            (key, value) -> translationBuilder.add(value, format(key.getName()) + " індустріальні бетонні сходи")
        );
        AVPBlocks.DYE_COLOR_TO_INDUSTRIAL_CONCRETE_WALL.forEach(
            (key, value) -> translationBuilder.add(value, format(key.getName()) + " індустріальна бетонна стіна")
        );
        AVPBlocks.DYE_COLOR_TO_INDUSTRIAL_GLASS.forEach(
            (key, value) -> translationBuilder.add(value, format(key.getName()) + " індустріальне скло")
        );
        AVPBlocks.DYE_COLOR_TO_INDUSTRIAL_GLASS_PANE.forEach(
            (key, value) -> translationBuilder.add(value, format(key.getName()) + " індустрільна скляна панель")
        );

        translationBuilder.add(AVPBlocks.LITHIUM_BLOCK, "Блок літію");
        translationBuilder.add(AVPBlocks.LITHIUM_ORE, "Літієва руда");
        translationBuilder.add(AVPBlocks.MONAZITE_ORE, "Руда моназиту");
        translationBuilder.add(AVPBlocks.NETHER_RESIN, "Пекельна смола");
        translationBuilder.add(AVPBlocks.NETHER_RESIN_NODE, "Вузол пекельної смоли");
        translationBuilder.add(AVPBlocks.NETHER_RESIN_VEIN, "Жила пекельної смоли");
        translationBuilder.add(AVPBlocks.NETHER_RESIN_WEB, "Сплетення пекельної смоли");
        translationBuilder.add(AVPBlocks.ABERRANT_RESIN, "Aberrant Resin");
        translationBuilder.add(AVPBlocks.ABERRANT_RESIN_NODE, "Aberrant Resin");
        translationBuilder.add(AVPBlocks.ABERRANT_RESIN_VEIN, "Aberrant Resin Vein");
        translationBuilder.add(AVPBlocks.ABERRANT_RESIN_WEB, "Aberrant Resin Web");
        translationBuilder.add(AVPBlocks.IRRADIATED_RESIN, "Irradiated Resin");
        translationBuilder.add(AVPBlocks.IRRADIATED_RESIN_NODE, "Irradiated Resin");
        translationBuilder.add(AVPBlocks.IRRADIATED_RESIN_VEIN, "Irradiated Resin Vein");
        translationBuilder.add(AVPBlocks.IRRADIATED_RESIN_WEB, "Irradiated Resin Web");

        AVPBlocks.DYE_COLOR_TO_PADDING.forEach(
            (key, value) -> translationBuilder.add(value, format(key.getName()) + " набивка")
        );
        AVPBlocks.DYE_COLOR_TO_PADDING_SLAB.forEach(
            (key, value) -> translationBuilder.add(value, format(key.getName()) + " набивчаста плита")
        );
        AVPBlocks.DYE_COLOR_TO_PADDING_STAIRS.forEach(
            (key, value) -> translationBuilder.add(value, format(key.getName()) + " набивчасті сходи")
        );

        AVPBlocks.DYE_COLOR_TO_PANEL_PADDING.forEach(
            (key, value) -> translationBuilder.add(value, format(key.getName()) + " набивчаста панель")
        );
        AVPBlocks.DYE_COLOR_TO_PANEL_PADDING_SLAB.forEach(
            (key, value) -> translationBuilder.add(value, format(key.getName()) + " набивчаста панелева плита")
        );
        AVPBlocks.DYE_COLOR_TO_PANEL_PADDING_STAIRS.forEach(
            (key, value) -> translationBuilder.add(value, format(key.getName()) + " набивчасті панелеві сходи")
        );

        AVPBlocks.DYE_COLOR_TO_PIPE_PADDING.forEach(
            (key, value) -> translationBuilder.add(value, format(key.getName()) + " набивчаста труба")
        );
        AVPBlocks.DYE_COLOR_TO_PIPE_PADDING_SLAB.forEach(
            (key, value) -> translationBuilder.add(value, format(key.getName()) + " набивчаста трубчаста плита")
        );
        AVPBlocks.DYE_COLOR_TO_PIPE_PADDING_STAIRS.forEach(
            (key, value) -> translationBuilder.add(value, format(key.getName()) + " набивчасті трубові сходи")
        );

        AVPBlocks.DYE_COLOR_TO_CUT_PLASTIC.forEach(
            (key, value) -> translationBuilder.add(value, format(key.getName()) + " шліфований пластик")
        );
        AVPBlocks.DYE_COLOR_TO_CUT_PLASTIC_SLAB.forEach(
            (key, value) -> translationBuilder.add(value, format(key.getName()) + " шліфована пластикова плита")
        );
        AVPBlocks.DYE_COLOR_TO_CUT_PLASTIC_STAIRS.forEach(
            (key, value) -> translationBuilder.add(value, format(key.getName()) + " шліфовані пластикові сходи")
        );

        AVPBlocks.DYE_COLOR_TO_PLASTIC.forEach(
            (key, value) -> translationBuilder.add(value, format(key.getName()) + " пластик")
        );
        AVPBlocks.DYE_COLOR_TO_PLASTIC_SLAB.forEach(
            (key, value) -> translationBuilder.add(value, format(key.getName()) + " пластикова плита")
        );
        AVPBlocks.DYE_COLOR_TO_PLASTIC_STAIRS.forEach(
            (key, value) -> translationBuilder.add(value, format(key.getName()) + " пластикові сходи")
        );

        translationBuilder.add(AVPBlocks.RAW_BAUXITE_BLOCK, "Блок необробленого бокситу");
        translationBuilder.add(AVPBlocks.RAW_GALENA_BLOCK, "Блок необробленого галеніту");
        translationBuilder.add(AVPBlocks.RAW_MONAZITE_BLOCK, "Блок необробленого моназиту");
        translationBuilder.add(AVPBlocks.RAW_SILICA_BLOCK, "Блок необробленого кварцу");
        translationBuilder.add(AVPBlocks.RAW_TITANIUM_BLOCK, "Блок необробленого титану");
        translationBuilder.add(AVPBlocks.RAW_ZINC_BLOCK, "Блок необробленого цинку");
        translationBuilder.add(AVPBlocks.RAZOR_WIRE, "Колючий дріт");
        translationBuilder.add(AVPBlocks.RESIN, "Смола");
        translationBuilder.add(AVPBlocks.RESIN_NODE, "Вузол смоли");
        translationBuilder.add(AVPBlocks.RESIN_VEIN, "Жила смоли");
        translationBuilder.add(AVPBlocks.RESIN_WEB, "Переплетіння смоли");
        translationBuilder.add(AVPBlocks.SILICA_GRAVEL, "Кварцовий гравій");
        translationBuilder.add(AVPBlocks.STEEL_BARS, "Сталеві ґрати");
        translationBuilder.add(AVPBlocks.STEEL_BLOCK, "Блок сталі");
        translationBuilder.add(AVPBlocks.STEEL_BUTTON, "Сталева кнопка");
        translationBuilder.add(AVPBlocks.STEEL_CHAIN_FENCE, "Сталевий ланцюговий паркан");
        translationBuilder.add(AVPBlocks.STEEL_COLUMN, "Сталева колона");
        translationBuilder.add(AVPBlocks.STEEL_DOOR, "Сталеві двері");
        translationBuilder.add(AVPBlocks.STEEL_FASTENED_SIDING, "Сталь кріплена боком");
        translationBuilder.add(AVPBlocks.STEEL_FASTENED_STANDING, "Сталь кріплена стоячи");
        translationBuilder.add(AVPBlocks.STEEL_GRATE, "Сталева решітка");
        translationBuilder.add(AVPBlocks.STEEL_PRESSURE_PLATE, "Сталева натисна плита");
        translationBuilder.add(AVPBlocks.STEEL_PLATING, "Сталева обшивка");
        translationBuilder.add(AVPBlocks.STEEL_SIDING, "Сталь у боковому положенні");
        translationBuilder.add(AVPBlocks.STEEL_STANDING, "Сталь у стоячому положенні");
        translationBuilder.add(AVPBlocks.STEEL_TRAP_DOOR, "Сталевий люк");
        translationBuilder.add(AVPBlocks.STEEL_TREAD, "Сталевий протектор");
        translationBuilder.add(AVPBlocks.TITANIUM_BLOCK, "Блок титану");
        translationBuilder.add(AVPBlocks.TITANIUM_BUTTON, "Титанова кнопка");
        translationBuilder.add(AVPBlocks.TITANIUM_CHAIN_FENCE, "Титановий ланцюговий паркан");
        translationBuilder.add(AVPBlocks.TITANIUM_COLUMN, "Титанова колона");
        translationBuilder.add(AVPBlocks.TITANIUM_DOOR, "Титанові двері");
        translationBuilder.add(AVPBlocks.TITANIUM_FASTENED_SIDING, "Титан кріплений боком");
        translationBuilder.add(AVPBlocks.TITANIUM_FASTENED_STANDING, "Титан кріплений стоячи");
        translationBuilder.add(AVPBlocks.TITANIUM_GRATE, "Титанова решітка");
        translationBuilder.add(AVPBlocks.TITANIUM_PRESSURE_PLATE, "Титанова натисна плита");
        translationBuilder.add(AVPBlocks.TITANIUM_PLATING, "Титанова обшивка");
        translationBuilder.add(AVPBlocks.TITANIUM_SIDING, "Титан у боковому положенні");
        translationBuilder.add(AVPBlocks.TITANIUM_STANDING, "Титан у стоячому положенні");
        translationBuilder.add(AVPBlocks.TITANIUM_TRAP_DOOR, "Титановий люк");
        translationBuilder.add(AVPBlocks.TITANIUM_TREAD, "Титановий протектор");
        translationBuilder.add(AVPBlocks.URANIUM_BLOCK, "Блок урану");
        translationBuilder.add(AVPBlocks.ZINC_BLOCK, "Блок цинку");
        translationBuilder.add(AVPBlocks.ZINC_ORE, "Цинкова руда");
        translationBuilder.add(AVPBlocks.INDUSTRIAL_FURNACE, "Індустріальна піч");
        translationBuilder.add(AVPBlocks.FERROALUMINUM_SLAB, "Фероалюмінієва плита");
        translationBuilder.add(AVPBlocks.FERROALUMINUM_STAIRS, "Фероалюмінієві сходи");
        translationBuilder.add(AVPBlocks.STEEL_SLAB, "Сталева плита");
        translationBuilder.add(AVPBlocks.STEEL_STAIRS, "Сталеві сходи");
        translationBuilder.add(AVPBlocks.TITANIUM_SLAB, "Титанова плита");
        translationBuilder.add(AVPBlocks.TITANIUM_STAIRS, "Титанові сходи");
        translationBuilder.add(AVPBlocks.FERROALUMINUM_SIDING_SLAB, "Фероалюмінієва бокова плита");
        translationBuilder.add(AVPBlocks.FERROALUMINUM_SIDING_STAIRS, "Фероалюмінієві бокові сходи");
        translationBuilder.add(AVPBlocks.STEEL_SIDING_SLAB, "Сталева бокова плита");
        translationBuilder.add(AVPBlocks.STEEL_SIDING_STAIRS, "Сталева бокові сходи");
        translationBuilder.add(AVPBlocks.TITANIUM_SIDING_SLAB, "Титанова бокова плита");
        translationBuilder.add(AVPBlocks.TITANIUM_SIDING_STAIRS, "Титанові бокові сходи");
        translationBuilder.add(AVPBlocks.STEEL_STANDING_SLAB, "Сталева стояча плита");
        translationBuilder.add(AVPBlocks.STEEL_STANDING_STAIRS, "Сталеві стоячі сходи");
        translationBuilder.add(AVPBlocks.TITANIUM_STANDING_SLAB, "Титанова стояча плита");
        translationBuilder.add(AVPBlocks.TITANIUM_STANDING_STAIRS, "Титанові стоячі сходи");
        translationBuilder.add(AVPBlocks.FERROALUMINUM_FASTENED_SIDING_SLAB, "Фероалюмінієва кріплена боком плита");
        translationBuilder.add(AVPBlocks.FERROALUMINUM_FASTENED_SIDING_STAIRS, "Фероалюмінієві кріплені боком сходи");
        translationBuilder.add(AVPBlocks.STEEL_FASTENED_SIDING_SLAB, "Сталева кріплена боком плита");
        translationBuilder.add(AVPBlocks.STEEL_FASTENED_SIDING_STAIRS, "Сталеві кріплені боком сходи");
        translationBuilder.add(AVPBlocks.TITANIUM_FASTENED_SIDING_SLAB, "Титанова кріплена боком плита");
        translationBuilder.add(AVPBlocks.TITANIUM_FASTENED_SIDING_STAIRS, "Титанові кріплені боком сходи");

        // Creative Mode Tabs
        translationBuilder.add(CreativeModeTabs.BLOCKS_KEY, "ЧПХ: Блоки");
        translationBuilder.add(CreativeModeTabs.COLORED_BLOCKS_KEY, "ЧПХ: Кольорові блоки");
        translationBuilder.add(CreativeModeTabs.COMBAT_KEY, "ЧПХ: Бойові предмети");
        translationBuilder.add(CreativeModeTabs.INGREDIENTS_KEY, "ЧПХ: Інгредієнти");
        translationBuilder.add(CreativeModeTabs.SPAWN_EGGS_KEY, "ЧПХ: Яйця призову");
        translationBuilder.add(CreativeModeTabs.TOOLS_AND_UTILITIES_KEY, "ЧПХ: Інструменти & Утиліти");

        // Entities
        translationBuilder.add(AVPEntityTypes.ACID, "Кислота");
        translationBuilder.add(AVPEntityTypes.CHESTBURSTER, "Грудолом");
        translationBuilder.add(AVPEntityTypes.DRONE, "Ксеноморф");
        translationBuilder.add(AVPEntityTypes.FACEHUGGER, "Лицехап");
        translationBuilder.add(AVPEntityTypes.OVAMORPH, "Оваморф");
        translationBuilder.add(AVPEntityTypes.PRAETORIAN, "Ксеноморф-преторіанець");
        translationBuilder.add(AVPEntityTypes.QUEEN, "Королева ксеноморфів");
        translationBuilder.add(AVPEntityTypes.WARRIOR, "Ксеноморф-воїн");
        translationBuilder.add(AVPEntityTypes.YAUTJA, "Якуджа (хижак)");
        translationBuilder.add(AVPEntityTypes.ROCKET, "Ракета");
        translationBuilder.add(AVPEntityTypes.GRENADE_THROWN, "Граната");
        translationBuilder.add(AVPEntityTypes.MARINE, "Морський котик");

        // Combat Items
        translationBuilder.add(AVPItems.GRENADE, "Граната");
        translationBuilder.add(AVPItems.GRENADE_INCENDIARY, "Запальна граната");
        translationBuilder.add(AVPItems.GRENADE_IRRADIATED, "Радіоактивна граната");
        translationBuilder.add(AVPItems.CASELESS_BULLET, "Патрон без кортеджа");
        translationBuilder.add(ArmorItems.CHITIN_BOOTS, "Хітинові чоботи");
        translationBuilder.add(ArmorItems.CHITIN_CHESTPLATE, "Хітиновий нагрудник");
        translationBuilder.add(ArmorItems.CHITIN_HELMET, "Хітиновий шолом");
        translationBuilder.add(ArmorItems.CHITIN_LEGGINGS, "Хітинові штани");
        translationBuilder.add(AVPItems.F903WE_RIFLE, "F903WE Гвинтівка");
        translationBuilder.add(AVPItems.FLAMETHROWER_SEVASTOPOL, "Вогнемет (Севастополь)");
        translationBuilder.add(AVPItems.FUEL_TANK, "Паливний бак");
        translationBuilder.add(AVPItems.HEAVY_BULLET, "Великий патрон");
        translationBuilder.add(ArmorItems.JUNGLE_PREDATOR_BOOTS, "Чоботи хижака");
        translationBuilder.add(ArmorItems.JUNGLE_PREDATOR_CHESTPLATE, "Нагрудник хижака");
        translationBuilder.add(ArmorItems.JUNGLE_PREDATOR_HELMET, "Маска хижака");
        translationBuilder.add(ArmorItems.JUNGLE_PREDATOR_LEGGINGS, "Штани хижака");
        translationBuilder.add(AVPItems.M37_12_SHOTGUN, "M37-12 Дробовик");
        translationBuilder.add(AVPItems.M41A_PULSE_RIFLE, "M41A Імпульсна гвинтівка");
        translationBuilder.add(AVPItems.M42A3_SNIPER_RIFLE, "M42A3 Снайперська гвинтівка");
        translationBuilder.add(AVPItems.M4RA_BATTLE_RIFLE, "M4RA Бойова гвинтівка");
        translationBuilder.add(AVPItems.M56_SMARTGUN, "M56 Розумна гармата");
        translationBuilder.add(AVPItems.M6B_ROCKET_LAUNCHER, "M6B Ракетомет");
        translationBuilder.add(AVPItems.M88_MOD_4_COMBAT_PISTOL, "88 Mod 4 Бойовий пістолет");
        translationBuilder.add(AVPItems.MEDIUM_BULLET, "Середній патрон");
        translationBuilder.add(ArmorItems.MK50_BOOTS, "MK50 чоботи");
        translationBuilder.add(ArmorItems.MK50_CHESTPLATE, "MK50 нагрудник");
        translationBuilder.add(ArmorItems.MK50_HELMET, "MK50 шолом");
        translationBuilder.add(ArmorItems.MK50_LEGGINGS, "MK50 штани");
        translationBuilder.add(ArmorItems.NETHER_CHITIN_BOOTS, "Незеритові хітинові чоботи");
        translationBuilder.add(ArmorItems.NETHER_CHITIN_CHESTPLATE, "Незеритовий хітиновий нагрудник");
        translationBuilder.add(ArmorItems.NETHER_CHITIN_HELMET, "Незеритовий хітиновий шолом");
        translationBuilder.add(ArmorItems.NETHER_CHITIN_LEGGINGS, "Незеритові хітинові штани");
        translationBuilder.add(AVPItems.OLD_PAINLESS, "Старий безболісний (мініган)");
        translationBuilder.add(ArmorItems.PLATED_CHITIN_BOOTS, "Обшиті хітинові чоботи");
        translationBuilder.add(ArmorItems.PLATED_CHITIN_CHESTPLATE, "Обшитий хітиновий нагрудник");
        translationBuilder.add(ArmorItems.PLATED_CHITIN_HELMET, "Обшитий хітиновий шолом");
        translationBuilder.add(ArmorItems.PLATED_CHITIN_LEGGINGS, "Обшиті хітинові штани");
        translationBuilder.add(ArmorItems.PLATED_NETHER_CHITIN_BOOTS, "Обшиті пекельні хітинові чоботи");
        translationBuilder.add(ArmorItems.PLATED_NETHER_CHITIN_CHESTPLATE, "Обшитий пекельний хітиновий нагрудник");
        translationBuilder.add(ArmorItems.PLATED_NETHER_CHITIN_HELMET, "Обшитий пекельний хітиновий шолом");
        translationBuilder.add(ArmorItems.PLATED_NETHER_CHITIN_LEGGINGS, "Обшиті пекельні хітинові штани");
        translationBuilder.add(ArmorItems.PRESSURE_BOOTS, "Тискові чоботи");
        translationBuilder.add(ArmorItems.PRESSURE_CHESTPLATE, "Тисковий нагрудник");
        translationBuilder.add(ArmorItems.PRESSURE_HELMET, "Тисковий шолом");
        translationBuilder.add(ArmorItems.PRESSURE_LEGGINGS, "Тискові штани");
        translationBuilder.add(AVPItems.ROCKET, "Ракета");
        translationBuilder.add(AVPItems.SHOTGUN_BULLET, "Патрон від дробовика");
        translationBuilder.add(AVPItems.SMALL_BULLET, "Малий патрон");
        translationBuilder.add(ArmorItems.STEEL_BOOTS, "Сталеві чоботи");
        translationBuilder.add(ArmorItems.STEEL_CHESTPLATE, "Сталевий нагрудник");
        translationBuilder.add(ArmorItems.STEEL_HELMET, "Сталевий шолом");
        translationBuilder.add(ArmorItems.STEEL_LEGGINGS, "Сталеві штани");
        translationBuilder.add(ArmorItems.TACTICAL_BOOTS, "Тактичні чоботи");
        translationBuilder.add(ArmorItems.TACTICAL_CHESTPLATE, "Тактичний нагрудник");
        translationBuilder.add(ArmorItems.TACTICAL_HELMET, "Тактичний шолом");
        translationBuilder.add(ArmorItems.TACTICAL_LEGGINGS, "Тактичні штани");
        translationBuilder.add(ArmorItems.TACTICAL_CAMO_BOOTS, "Камуфляжні тактичні чоботи");
        translationBuilder.add(ArmorItems.TACTICAL_CAMO_CHESTPLATE, "Камуфляжний тактичний нагрудник");
        translationBuilder.add(ArmorItems.TACTICAL_CAMO_HELMET, "Камуфляжний тактичний шолом");
        translationBuilder.add(ArmorItems.TACTICAL_CAMO_LEGGINGS, "Камуфляжні тактичні штани");
        translationBuilder.add(ArmorItems.TITANIUM_BOOTS, "Титанові чоботи");
        translationBuilder.add(ArmorItems.TITANIUM_CHESTPLATE, "Титановий нагрудник");
        translationBuilder.add(ArmorItems.TITANIUM_HELMET, "Титановий шолом");
        translationBuilder.add(ArmorItems.TITANIUM_LEGGINGS, "Титанові штани");
        translationBuilder.add(AVPItems.ZX_76_SHOTGUN, "ZX-76 Дробовик");

        // Ingredient Items
        translationBuilder.add(AVPItems.ALUMINUM_INGOT, "Алюмінієвий злиток");
        translationBuilder.add(AVPItems.AUTUNITE_DUST, "Аутинітовий пил");
        translationBuilder.add(AVPItems.BARREL, "Корпус");
        translationBuilder.add(AVPItems.BATTERY_PACK, "Набір батарейок");
        translationBuilder.add(AVPItems.BLUEPRINT_F903WE_RIFLE, "Креслення F903WE гвинтівки");
        translationBuilder.add(AVPItems.BLUEPRINT_FLAMETHROWER_SEVASTOPOL, "Креслення вогнемета (Севастополь)");
        translationBuilder.add(AVPItems.BLUEPRINT_M37_12_SHOTGUN, "Креслення M37-12 дробовика");
        translationBuilder.add(AVPItems.BLUEPRINT_M41A_PULSE_RIFLE, "Креслення M41A імпульсної гвинтівки");
        translationBuilder.add(AVPItems.BLUEPRINT_M42A3_SNIPER_RIFLE, "Креслення M42A3 снайперської гвинтівки");
        translationBuilder.add(AVPItems.BLUEPRINT_M4RA_BATTLE_RIFLE, "Креслення M4RA бойової гвинтівки");
        translationBuilder.add(AVPItems.BLUEPRINT_M56_SMARTGUN, "Креслення M56 розумного дула");
        translationBuilder.add(AVPItems.BLUEPRINT_M6B_ROCKET_LAUNCHER, "Креслення M6B ракетомета");
        translationBuilder.add(AVPItems.BLUEPRINT_M88MOD4_COMBAT_PISTOL, "Креслення M88 Mod 4 бойового пістолета");
        translationBuilder.add(AVPItems.BLUEPRINT_OLD_PAINLESS, "Креслення старого безболісного (мінігану)");
        translationBuilder.add(AVPItems.BLUEPRINT_ZX_76_SHOTGUN, "Креслення ZX-76 дробовика");
        translationBuilder.add(AVPItems.BRASS_INGOT, "Злиток латуні");
        translationBuilder.add(AVPItems.BULLET_TIP, "Наконечник кулі");
        translationBuilder.add(AVPItems.CAPACITOR, "Конденсатор");
        translationBuilder.add(AVPItems.CARBON_DUST, "Карбоновий пил");
        translationBuilder.add(AVPItems.CASELESS_CASING, "Безкорпусний корпус");
        translationBuilder.add(AVPItems.CHITIN, "Хітин");
        translationBuilder.add(AVPItems.CPU, "Процессор");
        translationBuilder.add(AVPItems.DIODE, "Діод");
        translationBuilder.add(AVPItems.FERROALUMINUM_INGOT, "Фероалюмінієвий злиток");
        translationBuilder.add(AVPItems.GRIP, "Ручка");
        translationBuilder.add(AVPItems.HEAVY_CASING, "Великий корпус");
        translationBuilder.add(AVPItems.INTEGRATED_CIRCUIT, "Інтегральна схема");
        translationBuilder.add(AVPItems.LEAD_INGOT, "Свинцевий злиток");
        translationBuilder.add(AVPItems.LED, "LED");
        translationBuilder.add(AVPItems.LED_DISPLAY, "LED Дисплей");
        translationBuilder.add(AVPItems.LITHIUM_DUST, "Літієвий пил");
        translationBuilder.add(AVPItems.MEDIUM_CASING, "Корпус гвинтівки");
        translationBuilder.add(AVPItems.MINIGUN_BARREL, "Корпус мінігана");
        translationBuilder.add(AVPItems.NEODYMIUM_MAGNET, "Неодієвий магніт");
        translationBuilder.add(AVPItems.NETHER_CHITIN, "Пекельний хітин");
        translationBuilder.add(AVPItems.NETHER_RESIN_BALL, "Пекельна смолиста кулька");
        translationBuilder.add(AVPItems.OVOID_POTTERY_SHERD, "Уламок глечика з малюнком оваморфа");
        translationBuilder.add(AVPItems.PARASITE_POTTERY_SHERD, "Уламок глечика з малюнком грудолома");
        translationBuilder.add(AVPItems.ROYALTY_POTTERY_SHERD, "Уламок глечика з малюнком королеви ксеноморфів");
        translationBuilder.add(AVPItems.PLATED_CHITIN, "Обшитий хітин");
        translationBuilder.add(AVPItems.PLATED_NETHER_CHITIN, "Обшитий пекельний хітин");
        translationBuilder.add(AVPItems.POLYMER, "Полімер");
        translationBuilder.add(AVPItems.RAW_BAUXITE, "Необроблений боксит");
        translationBuilder.add(AVPItems.RAW_BRASS, "Необроблена латунь");
        translationBuilder.add(AVPItems.RAW_CRUDE_IRON, "Необроблене сире залізо");
        translationBuilder.add(AVPItems.RAW_FERROBAUXITE, "Необроблений феробоксит");
        translationBuilder.add(AVPItems.RAW_GALENA, "Необроблений галеніт");
        translationBuilder.add(AVPItems.RAW_MONAZITE, "Необроблений моназит");
        translationBuilder.add(AVPItems.RAW_ROYAL_JELLY, "Сирий королівський слиз");
        translationBuilder.add(AVPItems.POISON_JELLY, "Отруйний слиз");
        translationBuilder.add(AVPItems.RAW_SILICA, "Необроблений кварц");
        translationBuilder.add(AVPItems.RAW_TITANIUM, "Необроблений титан");
        translationBuilder.add(AVPItems.RAW_ZINC, "Необроблений цинк");
        translationBuilder.add(AVPItems.RECEIVER, "Отримувач");
        translationBuilder.add(AVPItems.REGULATOR, "Регулятор");
        translationBuilder.add(AVPItems.RESIN_BALL, "Смолиста кулька");
        translationBuilder.add(AVPItems.RESISTOR, "Резистор");
        translationBuilder.add(AVPItems.ROCKET_BARREL, "Ракетна бочка");
        translationBuilder.add(AVPItems.SHOTGUN_CASING, "Корпус дробовика");
        translationBuilder.add(AVPItems.SMALL_CASING, "Корпус пістолета");
        translationBuilder.add(AVPItems.SMART_BARREL, "Корпус розумного дула");
        translationBuilder.add(AVPItems.SMART_RECEIVER, "Розумний отримувач");
        translationBuilder.add(AVPItems.STEEL_INGOT, "Сталевий злиток");
        translationBuilder.add(AVPItems.STOCK, "Приклад");
        translationBuilder.add(AVPItems.TITANIUM_INGOT, "Титановий злиток");
        translationBuilder.add(AVPItems.TRANSISTOR, "Транзистор");
        translationBuilder.add(AVPItems.URANIUM_INGOT, "Злиток урану");
        translationBuilder.add(AVPItems.VECTOR_POTTERY_SHERD, "Уламок глечика з малюнком лицехапа");
        translationBuilder.add(AVPItems.VERITANIUM_SHARD, "Уламок веританію");
        translationBuilder.add(AVPItems.ZINC_INGOT, "Цинковий злиток");
        translationBuilder.add(AVPItems.ALUMINUM_NUGGET, "Алюмінієвий самородок");
        translationBuilder.add(AVPItems.BRASS_NUGGET, "Самородок латуні");
        translationBuilder.add(AVPItems.FERROALUMINUM_NUGGET, "Фероалюмінієвий самородок");
        translationBuilder.add(AVPItems.LEAD_NUGGET, "Свинцевий самородок");
        translationBuilder.add(AVPItems.STEEL_NUGGET, "Сталевий самородок");
        translationBuilder.add(AVPItems.TITANIUM_NUGGET, "Титановий самородок");
        translationBuilder.add(AVPItems.URANIUM_NUGGET, "Самородок урану");
        translationBuilder.add(AVPItems.ZINC_NUGGET, "Цинковий самородок");
        translationBuilder.add(AVPItems.ABERRANT_RESIN_BALL, "Aberant Resin Ball");
        translationBuilder.add(AVPItems.ABERRANT_CHITIN, "Aberant Chitin");
        translationBuilder.add(AVPItems.PLATED_ABERRANT_CHITIN, "Plated Aberant Chitin");
        translationBuilder.add(AVPItems.IRRADIATED_RESIN_BALL, "Irradiated Resin Ball");
        translationBuilder.add(AVPItems.IRRADIATED_CHITIN, "Irradiated Chitin");
        translationBuilder.add(AVPItems.PLATED_IRRADIATED_CHITIN, "Plated Irradiated Chitin");

        // Tools & Utilities Items
        translationBuilder.add(AVPItems.ARMOR_CASE, "Кейс для броні");
        translationBuilder.add(AVPItems.CANISTER, "Каністра");
        translationBuilder.add(AVPItems.ROYAL_JELLY_CANISTER, "Каністра з королівським слизом");
        translationBuilder.add(AVPItems.STEEL_AXE, "Сталева сокира");
        translationBuilder.add(AVPItems.STEEL_HOE, "Сталева мотика");
        translationBuilder.add(AVPItems.STEEL_PICKAXE, "Сталеве кайло");
        translationBuilder.add(AVPItems.STEEL_SHOVEL, "Сталева лопата");
        translationBuilder.add(AVPItems.STEEL_SWORD, "Сталевий меч");
        translationBuilder.add(AVPItems.TITANIUM_AXE, "Титанова сокира");
        translationBuilder.add(AVPItems.TITANIUM_HOE, "Титанова мотика");
        translationBuilder.add(AVPItems.TITANIUM_PICKAXE, "Титанове кайло");
        translationBuilder.add(AVPItems.TITANIUM_SHOVEL, "Титанова лопата");
        translationBuilder.add(AVPItems.TITANIUM_SWORD, "Титановий меч");
        translationBuilder.add(AVPItems.VERITANIUM_AXE, "Веританова сокира");
        translationBuilder.add(AVPItems.VERITANIUM_HOE, "Веританова мотика");
        translationBuilder.add(AVPItems.VERITANIUM_PICKAXE, "Веританове кайло");
        translationBuilder.add(AVPItems.VERITANIUM_SHOVEL, "Веританова лопата");
        translationBuilder.add(AVPItems.VERITANIUM_SWORD, "Веритановий меч");

        // Spawn Egg Items
        translationBuilder.add(SpawnEggItems.ABERRANT_CHESTBURSTER_SPAWN_EGG, "Яйце призову аберрантного грудолома");
        translationBuilder.add(SpawnEggItems.ABERRANT_DRONE_SPAWN_EGG, "Яйце призову аберрантного ксеноморфа");
        translationBuilder.add(SpawnEggItems.ABERRANT_FACEHUGGER_SPAWN_EGG, "Яйце призову аберрантного лицехапа");
        translationBuilder.add(SpawnEggItems.ABERRANT_OVAMORPH_SPAWN_EGG, "Яйце призову аберрантного оваморфа");
        translationBuilder.add(SpawnEggItems.ABERRANT_PRAETORIAN_SPAWN_EGG, "Яйце призову аберрантного ксеноморфа-преторіанця");
        translationBuilder.add(SpawnEggItems.ABERRANT_WARRIOR_SPAWN_EGG, "Яйце призову аберрантного ксеноморфа-воїна");
        translationBuilder.add(SpawnEggItems.ABERRANT_QUEEN_SPAWN_EGG, "Яйце призову аберрантної королеви ксеноморфів");
        translationBuilder.add(SpawnEggItems.CHESTBURSTER_SPAWN_EGG, "Яйце призову грудолома");
        translationBuilder.add(SpawnEggItems.DRONE_SPAWN_EGG, "Яйце призову ксеноморфа");
        translationBuilder.add(SpawnEggItems.FACEHUGGER_SPAWN_EGG, "Яйце призову лицехапа");
        translationBuilder.add(SpawnEggItems.NETHER_CHESTBURSTER_SPAWN_EGG, "Яйце призову пекельного грудолома");
        translationBuilder.add(SpawnEggItems.NETHER_DRONE_SPAWN_EGG, "Яйце призову пекельного ксеноморфа");
        translationBuilder.add(SpawnEggItems.NETHER_FACEHUGGER_SPAWN_EGG, "Яйце призову пекельного лицехапа");
        translationBuilder.add(SpawnEggItems.NETHER_OVAMORPH_SPAWN_EGG, "Яйце призову пекельного оваморфа");
        translationBuilder.add(SpawnEggItems.NETHER_PRAETORIAN_SPAWN_EGG, "Яйце призову пекельного ксеноморфа-преторіанця");
        translationBuilder.add(SpawnEggItems.NETHER_WARRIOR_SPAWN_EGG, "Яйце призову пекельного ксеноморфа-воїна");
        translationBuilder.add(SpawnEggItems.NETHER_QUEEN_SPAWN_EGG, "Яйце призову пекельної королеви ксеноморфів");
        translationBuilder.add(SpawnEggItems.IRRAIATED_DRONE_SPAWN_EGG, "Irraiated Drone Spawn Egg");
        translationBuilder.add(SpawnEggItems.IRRAIATED_WARRIOR_SPAWN_EGG, "Irraiated Warrior Spawn Egg");
        translationBuilder.add(SpawnEggItems.IRRAIATED_PRAETORIAN_SPAWN_EGG, "Irraiated Praetorian Spawn Egg");
        translationBuilder.add(SpawnEggItems.OVAMORPH_SPAWN_EGG, "Яйце призову оваморфа");
        translationBuilder.add(SpawnEggItems.PRAETORIAN_SPAWN_EGG, "Яйце призову ксеноморфа-преторіанця");
        translationBuilder.add(SpawnEggItems.QUEEN_SPAWN_EGG, "Яйце призову королеви ксеноморфів");
        translationBuilder.add(SpawnEggItems.WARRIOR_SPAWN_EGG, "Яйце призову ксеноморфа-воїна");
        translationBuilder.add(SpawnEggItems.YAUTJA_SPAWN_EGG, "Яйце призову Якуджі (хижака)");
        translationBuilder.add(SpawnEggItems.MARINE_SPAWN_EGG, "Яйце призову морського котика");

        // Sounds
        addSound(translationBuilder, AVPSoundEvents.BLOCK_ACID_BURN, "Кислота розчеплює матеріал");

        addSound(translationBuilder, AVPSoundEvents.ENTITY_XENOMORPH_ATTACK, "Ксеноморф нападає");
        addSound(translationBuilder, AVPSoundEvents.ENTITY_XENOMORPH_DEATH, "Ксеноморф помирає");
        addSound(translationBuilder, AVPSoundEvents.ENTITY_XENOMORPH_HISS, "Ксеноморф шипить");
        addSound(translationBuilder, AVPSoundEvents.ENTITY_XENOMORPH_HURT, "Ксеноморф отримав ушкодження");
        addSound(translationBuilder, AVPSoundEvents.ENTITY_XENOMORPH_IDLE, "Ксеноморф дихає");
        addSound(translationBuilder, AVPSoundEvents.ENTITY_XENOMORPH_LUNGE, "Ксеноморф кидається");

        addSound(translationBuilder, AVPSoundEvents.ITEM_ARMOR_EQUIP_CHITIN, "Хітинова броня плескає");
        addSound(translationBuilder, AVPSoundEvents.ITEM_ARMOR_EQUIP_MK50, "MK50 броня шарудить");
        addSound(translationBuilder, AVPSoundEvents.ITEM_ARMOR_EQUIP_PRESSURE, "Тискова броня шарудить");
        addSound(translationBuilder, AVPSoundEvents.ITEM_ARMOR_EQUIP_STEEL, "Сталева броня брязчить");
        addSound(translationBuilder, AVPSoundEvents.ITEM_ARMOR_EQUIP_TACTICAL, "Тактичня броня шарудить");
        addSound(translationBuilder, AVPSoundEvents.ITEM_ARMOR_EQUIP_TITANIUM, "Титанова броня плескає");
        addSound(translationBuilder, AVPSoundEvents.ITEM_ARMOR_EQUIP_VERITANIUM, "Веританова броня плескає");

        addSound(translationBuilder, AVPSoundEvents.WEAPON_FLAMETHROWER_SEVASTOPOL_RELOAD_FINISH, "Вогнемет закінчив перезаряджатися");
        addSound(translationBuilder, AVPSoundEvents.WEAPON_FLAMETHROWER_SEVASTOPOL_RELOAD_START, "Вогнемет перезаряджається");
        addSound(translationBuilder, AVPSoundEvents.WEAPON_FLAMETHROWER_SEVASTOPOL_SHOOT, "Вогнемет стріляє");
        addSound(translationBuilder, AVPSoundEvents.WEAPON_FX_RICOCHET_DIRT, "Куля відрикошетила від землі");
        addSound(translationBuilder, AVPSoundEvents.WEAPON_FX_RICOCHET_GENERIC, "Куля відрикошетила");
        addSound(translationBuilder, AVPSoundEvents.WEAPON_FX_RICOCHET_GLASS, "Куля відрикошетила від скла");
        addSound(translationBuilder, AVPSoundEvents.WEAPON_FX_RICOCHET_METAL, "Куля відрикошетила від металу");
        addSound(translationBuilder, AVPSoundEvents.WEAPON_GENERIC_RELOAD, "Зброя перезаряджається");
        addSound(translationBuilder, AVPSoundEvents.WEAPON_GENERIC_SHOOT, "Зброя стріляє");
        addSound(translationBuilder, AVPSoundEvents.WEAPON_GENERIC_SHOOT_FAIL, "Зброю заклинило");
        addSound(translationBuilder, AVPSoundEvents.WEAPON_M37_12_SHOTGUN_SHOOT, "M37-12 Дробовик стріляє");
        addSound(translationBuilder, AVPSoundEvents.WEAPON_M41A_PULSE_RIFLE_SHOOT, "M41A Імпульсна гвинтівка стріляє");
        addSound(translationBuilder, AVPSoundEvents.WEAPON_M42A3_SNIPER_RIFLE_SHOOT, "M42A3 Снайперська гвинтівка стріляє");
        addSound(translationBuilder, AVPSoundEvents.WEAPON_M4RA_BATTLE_RIFLE_SHOOT, "M4RA Бойова гвинтівка стріляє");
        addSound(translationBuilder, AVPSoundEvents.WEAPON_M56_SMARTGUN_SHOOT, "M56 Розумне дуло стріляє");
        addSound(translationBuilder, AVPSoundEvents.WEAPON_M6B_ROCKET_LAUNCHER_RELOAD_FINISH, "M6B Ракетомет закінчив перезаряджатися");
        addSound(translationBuilder, AVPSoundEvents.WEAPON_M6B_ROCKET_LAUNCHER_RELOAD_START, "M6B Ракетомет перезаряджається");
        addSound(translationBuilder, AVPSoundEvents.WEAPON_M6B_ROCKET_LAUNCHER_SHOOT, "M6B Ракетомет стріляє");
        addSound(translationBuilder, AVPSoundEvents.WEAPON_M88_MOD_4_COMBAT_PISTOL_RELOAD, "M88 Mod 4 Бойовий пістолет перезаряджається");
        addSound(translationBuilder, AVPSoundEvents.WEAPON_M88_MOD_4_COMBAT_PISTOL_SHOOT, "M88 Mod 4 Бойовий пістолет стріляє");
        addSound(translationBuilder, AVPSoundEvents.WEAPON_OLD_PAINLESS_SHOOT, "Старий безболісний (мініган) стріляє");
        addSound(translationBuilder, AVPSoundEvents.WEAPON_OLD_PAINLESS_SHOOT_FINISH, "Старий безболісний (мініган) перестав стріляти");
        addSound(translationBuilder, AVPSoundEvents.WEAPON_OLD_PAINLESS_SHOOT_SPINNING, "Бочка старого безболісного (мінігану) крутиться");
        addSound(translationBuilder, AVPSoundEvents.WEAPON_OLD_PAINLESS_SHOOT_START, "Старий безболісний (мініган) починає стріляти");
        addSound(translationBuilder, AVPSoundEvents.WEAPON_ZX_76_SHOTGUN_SHOOT, "ZX-76 Дробовик стріляє");

        // Tooltips
        translationBuilder.add("tooltip.avp.accuracy", "Точність: ");
        translationBuilder.add("tooltip.avp.ammunition", "Обойма: ");
        translationBuilder.add("tooltip.avp.ammunition_type", "Стріляє: ");
        translationBuilder.add("tooltip.avp.damage", "Урон: ");
        translationBuilder.add("tooltip.avp.fire_mode", "Режим стрільби: ");
        translationBuilder.add("tooltip.avp.fire_rate", "Швидкість стрільби: ");
        translationBuilder.add("tooltip.avp.knockback", "Відкидання противника: ");
        translationBuilder.add("tooltip.avp.recoil", "Віддача: ");

        // Keybinds
        translationBuilder.add("key.avp.reload", "Перезарядитися");
        translationBuilder.add("keybind.category.avp.weapons", "ЧПХ зброя");

        // Death messages
        translationBuilder.add("death.attack.acid", "%1$s розчинився у кислоті");
        translationBuilder.add("death.attack.radiation", "%1$s загинув від високого рівня радіації");
        translationBuilder.add("death.attack.razor_wire", "%1$s заколов себе до смерті в колючому дроті");

        translationBuilder.add("advancements.aliens.root.title", "ЧПХ: Чужі");
        translationBuilder.add("advancements.aliens.root.description", "У Майнкрафті, ніхто не почує твій крик");

        translationBuilder.add("advancements.aliens.kill_an_alien.title", "Неідеальний організм");
        translationBuilder.add("advancements.aliens.kill_an_alien.description", "Вбий ксеноморфа та живи, щоб розказати свою історію");

        translationBuilder.add("advancements.aliens.kill_a_royal_alien.title", "Царевбивство");
        translationBuilder.add("advancements.aliens.kill_a_royal_alien.description", "Вбий королівського ксеноморфа");

        translationBuilder.add("advancements.aliens.kill_all_aliens.title", "Ксеноцид");
        translationBuilder.add("advancements.aliens.kill_all_aliens.description", "Вбий особину кожного типу ксеноморфів");

        translationBuilder.add("advancements.aliens.chitin_armor.title", "Покрий мене у... ее...");
        translationBuilder.add("advancements.aliens.chitin_armor.description", "Вдягни повний набір хітинової броні");

        translationBuilder.add("advancements.aliens.shear_an_ovamorph.title", "Час овосліджень");
        translationBuilder.add("advancements.aliens.shear_an_ovamorph.description", "Звільни оваморфа з його пут");

        translationBuilder.add("advancements.aliens.plated_chitin_armor.title", "Стати на коліна до корони");
        translationBuilder.add("advancements.aliens.plated_chitin_armor.description", "Вдягти повний набір обшитої хітинової броні");

        // Hive boss bar
        translationBuilder.add("bossbar.avp.hive.title", "Вулик");

        translationBuilder.add("avp.industrialfurnace.displayName", "Індустрільна піч");
        translationBuilder.add("effect.avp.radiation", "Радіація");

        // Configs
        translationBuilder.add("config.screen.avp", "AVP Config");

        translationBuilder.add("config.avp.option.hiveConfigs", "Hive Configs");
        translationBuilder.add(
            "config.avp.option.MINIMUM_DISTANCE_BETWEEN_HIVES_IN_BLOCKS",
            "Minimum distance between hive centers in blocks"
        );
        translationBuilder.add("config.avp.option.HIVE_RADIUS_IN_BLOCKS", "The radius of hives in blocks");
        translationBuilder.add(
            "config.avp.option.HIVE_LEASH_RADIUS_IN_BLOCKS",
            "Maximum distance away from a hive that Xenomorphs can join or remain as a member"
        );
        translationBuilder.add("config.avp.option.HIVE_MAX_PRAETORIAN_COUNT", "Maximum number of Praetorians allowed within a hive");
        translationBuilder.add(
            "config.avp.option.HIVE_MEMBERS_REQUIRED_FOR_PRAETORIAN",
            "Number of hive members required to spawn a Praetorian"
        );
        translationBuilder.add(
            "config.avp.option.HIVE_DARKEN_SCREEN",
            "Determines if the screen should darken when the hive boss bar appears"
        );
        translationBuilder.add("config.avp.option.HIVE_DEBUG_ENABLED", "Enables hive debugging");
        translationBuilder.add("config.avp.option.HIVE_DEBUG_HIGHLIGHT_LEADER", "Applies a glow effect to the hive leader");
        translationBuilder.add("config.avp.option.HIVE_DEBUG_HIGHLIGHT_ALL_MEMBERS", "Applies a glow effect to all hive members");
        translationBuilder.add("config.avp.option.HIVE_DEBUG_MARK_HIVE_CENTER", "Marks the hive center with a block");
        translationBuilder.add("config.avp.option.CHESTBURSTER_MAX_GROWTH_TIMER_SECONDS", "Chestburster Max Growth Timer Seconds");
        translationBuilder.add("config.avp.option.DRONE_MAX_GROWTH_TIMER_SECONDS", "Drone Max Growth Timer Seconds");
        translationBuilder.add("config.avp.option.WARRIOR_MAX_GROWTH_TIMER_SECONDS", "Warrior Max Growth Timer Seconds");
        translationBuilder.add("config.avp.option.PRAETORIAN_MAX_GROWTH_TIMER_SECONDS", "Praetorian Max Growth Timer Seconds");
        translationBuilder.add("config.avp.option.PRAETORIAN_SHORTCUT_TIMER_SECONDS", "Praetorian Shortcut Timer Seconds");

        translationBuilder.add("config.avp.option.spawnConfigs", "Mob Spawn Configs");
        translationBuilder.add("config.avp.option.NATURAL_SPAWNING_ENABLED", "Enable natural spawning for Xenomorphs in the overworld.");
        translationBuilder.add(
            "config.avp.option.ADULT_SPAWNING_ENABLED",
            "Enable natural spawning for adult Xenomorphs in the overworld."
        );
        translationBuilder.add(
            "config.avp.option.YOUNG_SPAWNING_ENABLED",
            "Enable natural spawning for young Xenomorphs (eggs, facehuggers, bursters, etc.) in the overworld."
        );
        translationBuilder.add(
            "config.avp.option.REMOVE_VANILLA_SPAWNS",
            "Removes certain hostile monster spawns, allowing others like Xenomorphs to spawn more frequently."
        );
        translationBuilder.add(
            "config.avp.option.ALIEN_CUSTOM_MOB_CATEGORY_ENABLED",
            "Enable separate spawn cap for aliens and predators."
        );
        translationBuilder.add(
            "config.avp.option.ALIEN_CUSTOM_MOB_CATEGORY_LIMIT",
            "Maximum spawn count for aliens in the custom mob category."
        );
        translationBuilder.add(
            "config.avp.option.PREDATOR_CUSTOM_MOB_CATEGORY_LIMIT",
            "Maximum spawn count for predators in the custom mob category."
        );
        translationBuilder.add("config.avp.option.CHESTBURSTER_SPAWN", "Chestburster spawn settings");
        translationBuilder.add("config.avp.option.DRONE_SPAWN", "Drone spawn settings");
        translationBuilder.add("config.avp.option.NETHER_CHESTBURSTER_SPAWN", "Nether Chestburster spawn settings");
        translationBuilder.add("config.avp.option.NETHER_DRONE_SPAWN", "Nether Drone spawn settings");
        translationBuilder.add("config.avp.option.NETHER_OVAMORPH_SPAWN", "Nether Ovamorph spawn settings");
        translationBuilder.add("config.avp.option.NETHER_PRAETORIAN_SPAWN", "Nether Praetorian spawn settings");
        translationBuilder.add("config.avp.option.NETHER_WARRIOR_SPAWN", "Nether Warrior spawn settings");
        translationBuilder.add("config.avp.option.NETHER_QUEEN_SPAWN", "Nether Queen spawn settings");
        translationBuilder.add("config.avp.option.OVAMORPH_SPAWN", "Ovamorph spawn settings");
        translationBuilder.add("config.avp.option.PRAETORIAN_SPAWN", "Praetorian spawn settings");
        translationBuilder.add("config.avp.option.QUEEN_SPAWN", "Queen spawn settings");
        translationBuilder.add("config.avp.option.WARRIOR_SPAWN", "Warrior spawn settings");
        translationBuilder.add("config.avp.option.YAUTJA_SPAWN", "Yautja spawn settings");
        translationBuilder.add("config.avp.option.enabled", "Enable spawning");
        translationBuilder.add("config.avp.option.maxY", "Maximum Y-level at for spawn");
        translationBuilder.add("config.avp.option.minY", "Minimum Y-level at for spawn");
        translationBuilder.add("config.avp.option.minGroupSize", "Minimum group size for spawns");
        translationBuilder.add("config.avp.option.maxGroupSize", "Maximum group size for spawns");
        translationBuilder.add("config.avp.option.weight", "Spawn weight");
        translationBuilder.add("config.avp.option.requiresResin", "Requires resin for Nether Ovamorph spawning");

        translationBuilder.add("config.avp.option.statsConfigs", "Mob Stat Configs");
        translationBuilder.add("config.avp.option.ACID_ATTACK_DAMAGE", "Acid Damage per tick");
        translationBuilder.add("config.avp.option.CHESTBURSTER_STATS", "Chestburster stats");
        translationBuilder.add("config.avp.option.health", "Health value");
        translationBuilder.add("config.avp.option.attackDamage", "Attack damage");
        translationBuilder.add("config.avp.option.healthRegenPerSecond", "Health regeneration per second");
        translationBuilder.add("config.avp.option.knockbackResistance", "Knockback resistance");
        translationBuilder.add("config.avp.option.moveSpeed", "Movement speed");
        translationBuilder.add("config.avp.option.armorToughness", "Armor toughness.");
        translationBuilder.add("config.avp.option.armor", "Armor value");
        translationBuilder.add("config.avp.option.nestTickrate", "Nest tickrate");
        translationBuilder.add("config.avp.option.followRange", "Follow range");
        translationBuilder.add("config.avp.option.DRONE_STATS", "Drone stats");
        translationBuilder.add("config.avp.option.OVAMORPH_STATS", "Ovamorph stats");
        translationBuilder.add("config.avp.option.PRAETORIAN_STATS", "Praetorian stats");
        translationBuilder.add("config.avp.option.QUEEN_STATS", "Queen stats");
        translationBuilder.add("config.avp.option.WARRIOR_STATS", "Warrior stats");
        translationBuilder.add("config.avp.option.YAUTJA_STATS", "Yautja stats");
        translationBuilder.add("config.avp.option.MARINE_STATS", "Marine stats");
        translationBuilder.add("config.avp.option.FACEHUGGER_STATS", "Facehugger stats");

        translationBuilder.add("config.avp.option.weaponConfigs", "Weapon Options");
        translationBuilder.add("config.avp.option.BULLETS_DAMAGE_BLOCKS_ENABLED", "Enable bullet collision damage to blocks");
        translationBuilder.add("config.avp.option.ENABLE_NUKE_BLOCK_MECHS", "Allow nukes to work");
    }

    private void addSound(TranslationBuilder translationBuilder, SoundEvent soundEvent, String value) {
        translationBuilder.add("субтитри." + soundEvent.getLocation().getPath(), value);
    }

    public String format(String input) {
        if (input == null || input.isEmpty()) {
            return input;
        }
        return String.join(" ", Arrays.stream(input.split("_")).map(this::capitalize).toList());
    }

    public String capitalize(String input) {
        if (input == null || input.isEmpty()) {
            return input;
        }
        return input.substring(0, 1).toUpperCase() + input.substring(1);
    }
}
