package com.avp.data.lang;

import com.avp.common.block.AVPBlocks;
import com.avp.common.creative_mode_tab.CreativeModeTabs;
import com.avp.common.entity.type.AVPEntityTypes;
import com.avp.common.item.AVPItems;
import com.avp.common.item.ArmorItems;
import com.avp.common.item.SpawnEggItems;
import com.avp.common.sound.AVPSoundEvents;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricLanguageProvider;
import net.minecraft.core.HolderLookup;
import net.minecraft.sounds.SoundEvent;

import java.util.Arrays;
import java.util.concurrent.CompletableFuture;

public class RussianLanguageProvider extends FabricLanguageProvider {

    public RussianLanguageProvider(FabricDataOutput dataOutput, CompletableFuture<HolderLookup.Provider> registryLookup) {
        super(dataOutput, "ru_ru", registryLookup);
    }

    @Override
    public void generateTranslations(HolderLookup.Provider registryLookup, TranslationBuilder translationBuilder) {
        // Blocks
        translationBuilder.add(AVPBlocks.NUKE_BLOCK, "Яедрная бомба");
        translationBuilder.add(AVPBlocks.ROYAL_JELLY_BLOCK, "Блок королевской слизи");
        translationBuilder.add(AVPBlocks.TRINITITE_BLOCK, "Блок тринитита");
        translationBuilder.add(AVPBlocks.ALUMINUM_BLOCK, "Блок алюминия");
        translationBuilder.add(AVPBlocks.AUTUNITE_BLOCK, "Блок аутинита");
        translationBuilder.add(AVPBlocks.AUTUNITE_ORE, "Аутинитовая руда");
        translationBuilder.add(AVPBlocks.BAUXITE_ORE, "Алюминиевая руда");
        translationBuilder.add(AVPBlocks.BRASS_BLOCK, "Блок латуни");
        translationBuilder.add(AVPBlocks.CHISELED_FERROALUMINUM, "точёный фероалюминий");
        translationBuilder.add(AVPBlocks.CHISELED_STEEL, "точёная сталь");
        translationBuilder.add(AVPBlocks.CHISELED_TITANIUM, "точёный титан");
        translationBuilder.add(AVPBlocks.CUT_FERROALUMINUM, "точёный фероалюминий");
        translationBuilder.add(AVPBlocks.CUT_FERROALUMINUM_SLAB, "точёная фероалюминиевая плита");
        translationBuilder.add(AVPBlocks.CUT_FERROALUMINUM_STAIRS, "точёные фероалюминивые ступеньки");
        translationBuilder.add(AVPBlocks.CUT_STEEL, "Точёная сталь");
        translationBuilder.add(AVPBlocks.CUT_STEEL_SLAB, "точёная стальная плита");
        translationBuilder.add(AVPBlocks.CUT_STEEL_STAIRS, "точёные стальные ступеньки");
        translationBuilder.add(AVPBlocks.CUT_TITANIUM, "точёный титан");
        translationBuilder.add(AVPBlocks.CUT_TITANIUM_SLAB, "точёная титановая плита");
        translationBuilder.add(AVPBlocks.CUT_TITANIUM_STAIRS, "точёные титановые ступеньки");
        translationBuilder.add(AVPBlocks.DEEPSLATE_TITANIUM_ORE, "Глубосланцевая титановая руда");
        translationBuilder.add(AVPBlocks.DEEPSLATE_ZINC_ORE, "Глубосланцевая цинковая руда");
        translationBuilder.add(AVPBlocks.FERROALUMINUM_BLOCK, "Блок фероалюминия");
        translationBuilder.add(AVPBlocks.FERROALUMINUM_BUTTON, "Фероалюминиевая кнопка");
        translationBuilder.add(AVPBlocks.FERROALUMINUM_CHAIN_FENCE, "Фероалюминиевый цепной забор");
        translationBuilder.add(AVPBlocks.FERROALUMINUM_COLUMN, "Фероалюминиевая колонна");
        translationBuilder.add(AVPBlocks.FERROALUMINUM_DOOR, "Фероалюминиевая дверь");
        translationBuilder.add(AVPBlocks.FERROALUMINUM_FASTENED_SIDING, "Фероалюминий креплённый боком");
        translationBuilder.add(AVPBlocks.FERROALUMINUM_FASTENED_STANDING, "Фероалюминий креплённый прямо");
        translationBuilder.add(AVPBlocks.FERROALUMINUM_GRATE, "Фероалюминиевая решётка");
        translationBuilder.add(AVPBlocks.FERROALUMINUM_PRESSURE_PLATE, "Фероалюминиевая нажимная плита");
        translationBuilder.add(AVPBlocks.FERROALUMINUM_PLATING, "Фероалюминиевая обшивка");
        translationBuilder.add(AVPBlocks.FERROALUMINUM_SIDING, "Фероалюминий боком");
        translationBuilder.add(AVPBlocks.FERROALUMINUM_STANDING, "Фероалюминий прямо");
        translationBuilder.add(AVPBlocks.FERROALUMINUM_TRAP_DOOR, "Фероалюминиевый люк");
        translationBuilder.add(AVPBlocks.FERROALUMINUM_TREAD, "Фероалюминиевый протектор");
        translationBuilder.add(AVPBlocks.GALENA_ORE, "Руда галенита");
        translationBuilder.add(AVPBlocks.INDUSTRIAL_GLASS, "Индустриальное стекло");
        translationBuilder.add(AVPBlocks.INDUSTRIAL_GLASS_PANE, "Индустриальная стеклянная панель");
        translationBuilder.add(AVPBlocks.LEAD_BLOCK, "Блок свинца");
        translationBuilder.add(AVPBlocks.FERROALUMINUM_FASTENED_STANDING_SLAB, "Фероалюминиевая креплённая прямо плита");
        translationBuilder.add(AVPBlocks.FERROALUMINUM_FASTENED_STANDING_STAIRS, "Фероалюминиевые креплённые прямо ступеньки");
        translationBuilder.add(AVPBlocks.FERROALUMINUM_GRATE_SLAB, "Фероалюминиевая решётчастая плита");
        translationBuilder.add(AVPBlocks.FERROALUMINUM_GRATE_STAIRS, "Фероалюминиевые решётчастые ступеньки");
        translationBuilder.add(AVPBlocks.FERROALUMINUM_PLATING_SLAB, "Фероалюминиевая обшивчастая плита");
        translationBuilder.add(AVPBlocks.FERROALUMINUM_PLATING_STAIRS, "Фероалюминиевые обшивчастые ступеньки");
        translationBuilder.add(AVPBlocks.FERROALUMINUM_STANDING_SLAB, "Прямая фероалюминиевая плита");
        translationBuilder.add(AVPBlocks.FERROALUMINUM_STANDING_STAIRS, "Прямые фероалюминиевые ступеньки");
        translationBuilder.add(AVPBlocks.FERROALUMINUM_TREAD_SLAB, "Протекторная фероалюминиевая плита");
        translationBuilder.add(AVPBlocks.FERROALUMINUM_TREAD_STAIRS, "Протекторные фероалюминиевые ступеньки");
        translationBuilder.add(AVPBlocks.STEEL_FASTENED_STANDING_SLAB, "Стальная креплённая прямо плита");
        translationBuilder.add(AVPBlocks.STEEL_FASTENED_STANDING_STAIRS, "Стальные креплённые прямо ступеньки");
        translationBuilder.add(AVPBlocks.STEEL_GRATE_SLAB, "Стальная решётчастая плита");
        translationBuilder.add(AVPBlocks.STEEL_GRATE_STAIRS, "Стальные решётчастые ступеньки");
        translationBuilder.add(AVPBlocks.STEEL_PLATING_SLAB, "Обшивчастая стальная плита");
        translationBuilder.add(AVPBlocks.STEEL_PLATING_STAIRS, "Обшивчастые стальные ступеньки");
        translationBuilder.add(AVPBlocks.STEEL_TREAD_SLAB, "Протекторная стальная плита");
        translationBuilder.add(AVPBlocks.STEEL_TREAD_STAIRS, "Протекторные стальные ступеньки");
        translationBuilder.add(AVPBlocks.TITANIUM_FASTENED_STANDING_SLAB, "Титановая креплённая прямо плита");
        translationBuilder.add(AVPBlocks.TITANIUM_FASTENED_STANDING_STAIRS, "Титановые креплённые прямо ступеньки");
        translationBuilder.add(AVPBlocks.TITANIUM_GRATE_SLAB, "Титановая решётчастая плита");
        translationBuilder.add(AVPBlocks.TITANIUM_GRATE_STAIRS, "Титановые решётчастые ступеньки");
        translationBuilder.add(AVPBlocks.TITANIUM_PLATING_SLAB, "Обшивчастая титановая плита");
        translationBuilder.add(AVPBlocks.TITANIUM_PLATING_STAIRS, "Обшивчастые титановые ступеньки");
        translationBuilder.add(AVPBlocks.TITANIUM_TREAD_SLAB, "Протекторная титановая плита");
        translationBuilder.add(AVPBlocks.TITANIUM_TREAD_STAIRS, "Протекторные титановые ступеньки");

        AVPBlocks.DYE_COLOR_TO_CONCRETE_SLAB.forEach(
            (key, value) -> translationBuilder.add(value, format(key.getName()) + " бетонная плита")
        );
        AVPBlocks.DYE_COLOR_TO_CONCRETE_STAIRS.forEach(
            (key, value) -> translationBuilder.add(value, format(key.getName()) + " бетонные ступеньки")
        );

        AVPBlocks.DYE_COLOR_TO_INDUSTRIAL_CONCRETE.forEach(
            (key, value) -> translationBuilder.add(value, format(key.getName()) + " индустриальный бетон")
        );
        AVPBlocks.DYE_COLOR_TO_INDUSTRIAL_CONCRETE_SLAB.forEach(
            (key, value) -> translationBuilder.add(value, format(key.getName()) + " индустриальная бетоновая плита")
        );
        AVPBlocks.DYE_COLOR_TO_INDUSTRIAL_CONCRETE_STAIRS.forEach(
            (key, value) -> translationBuilder.add(value, format(key.getName()) + " индустриальные бетоновые ступеньки")
        );
        AVPBlocks.DYE_COLOR_TO_INDUSTRIAL_CONCRETE_WALL.forEach(
            (key, value) -> translationBuilder.add(value, format(key.getName()) + " индустриальная бетоновая стена")
        );
        AVPBlocks.DYE_COLOR_TO_INDUSTRIAL_GLASS.forEach(
            (key, value) -> translationBuilder.add(value, format(key.getName()) + " индустриальное стекло")
        );
        AVPBlocks.DYE_COLOR_TO_INDUSTRIAL_GLASS_PANE.forEach(
            (key, value) -> translationBuilder.add(value, format(key.getName()) + " индустриальная стеклянная панель")
        );

        translationBuilder.add(AVPBlocks.LITHIUM_BLOCK, "Блок лития");
        translationBuilder.add(AVPBlocks.LITHIUM_ORE, "Литиевая руда");
        translationBuilder.add(AVPBlocks.MONAZITE_ORE, "Моназитовая руда");
        translationBuilder.add(AVPBlocks.NETHER_RESIN, "Адская смола");
        translationBuilder.add(AVPBlocks.NETHER_RESIN_NODE, "Узел адской смолы");
        translationBuilder.add(AVPBlocks.NETHER_RESIN_VEIN, "Жила адской смолы");
        translationBuilder.add(AVPBlocks.NETHER_RESIN_WEB, "Сплетение адской смолы");

        AVPBlocks.DYE_COLOR_TO_PADDING.forEach(
            (key, value) -> translationBuilder.add(value, format(key.getName()) + " набивка")
        );
        AVPBlocks.DYE_COLOR_TO_PADDING_SLAB.forEach(
            (key, value) -> translationBuilder.add(value, format(key.getName()) + " набивчастая плита")
        );
        AVPBlocks.DYE_COLOR_TO_PADDING_STAIRS.forEach(
            (key, value) -> translationBuilder.add(value, format(key.getName()) + " набивчастые ступеньки")
        );

        AVPBlocks.DYE_COLOR_TO_PANEL_PADDING.forEach(
            (key, value) -> translationBuilder.add(value, format(key.getName()) + " набивчастая панель")
        );
        AVPBlocks.DYE_COLOR_TO_PANEL_PADDING_SLAB.forEach(
            (key, value) -> translationBuilder.add(value, format(key.getName()) + " набивчастая панельная плита")
        );
        AVPBlocks.DYE_COLOR_TO_PANEL_PADDING_STAIRS.forEach(
            (key, value) -> translationBuilder.add(value, format(key.getName()) + " набивчастые панельные ступеньки")
        );

        AVPBlocks.DYE_COLOR_TO_PIPE_PADDING.forEach(
            (key, value) -> translationBuilder.add(value, format(key.getName()) + " набивчастая труба")
        );
        AVPBlocks.DYE_COLOR_TO_PIPE_PADDING_SLAB.forEach(
            (key, value) -> translationBuilder.add(value, format(key.getName()) + " набивчастая трубная плита")
        );
        AVPBlocks.DYE_COLOR_TO_PIPE_PADDING_STAIRS.forEach(
            (key, value) -> translationBuilder.add(value, format(key.getName()) + " набивчастые трубные ступеньки")
        );

        AVPBlocks.DYE_COLOR_TO_CUT_PLASTIC.forEach(
            (key, value) -> translationBuilder.add(value, format(key.getName()) + " точёный пластик")
        );
        AVPBlocks.DYE_COLOR_TO_CUT_PLASTIC_SLAB.forEach(
            (key, value) -> translationBuilder.add(value, format(key.getName()) + " точённая пластиковая плита")
        );
        AVPBlocks.DYE_COLOR_TO_CUT_PLASTIC_STAIRS.forEach(
            (key, value) -> translationBuilder.add(value, format(key.getName()) + " точённые пластиковые ступеньки")
        );

        AVPBlocks.DYE_COLOR_TO_PLASTIC.forEach(
            (key, value) -> translationBuilder.add(value, format(key.getName()) + " пластик")
        );
        AVPBlocks.DYE_COLOR_TO_PLASTIC_SLAB.forEach(
            (key, value) -> translationBuilder.add(value, format(key.getName()) + " пластиковая плита")
        );
        AVPBlocks.DYE_COLOR_TO_PLASTIC_STAIRS.forEach(
            (key, value) -> translationBuilder.add(value, format(key.getName()) + " пластиковые ступеньки")
        );

        translationBuilder.add(AVPBlocks.RAW_BAUXITE_BLOCK, "Блок необработанного алюминия");
        translationBuilder.add(AVPBlocks.RAW_GALENA_BLOCK, "Блок необработанного галенита");
        translationBuilder.add(AVPBlocks.RAW_MONAZITE_BLOCK, "Блок необработанного моназита");
        translationBuilder.add(AVPBlocks.RAW_SILICA_BLOCK, "Блок необработанного кварца");
        translationBuilder.add(AVPBlocks.RAW_TITANIUM_BLOCK, "Блок необработанного титана");
        translationBuilder.add(AVPBlocks.RAW_ZINC_BLOCK, "Блок необработанного цинка");
        translationBuilder.add(AVPBlocks.RAZOR_WIRE, "Колючая проволка");
        translationBuilder.add(AVPBlocks.RESIN, "Смола");
        translationBuilder.add(AVPBlocks.RESIN_NODE, "Узел смолы");
        translationBuilder.add(AVPBlocks.RESIN_VEIN, "Жила смолы");
        translationBuilder.add(AVPBlocks.RESIN_WEB, "Сплетение смолы");
        translationBuilder.add(AVPBlocks.SILICA_GRAVEL, "Кварцевый гравий");
        translationBuilder.add(AVPBlocks.STEEL_BARS, "Стальное решётчастое ограждение");
        translationBuilder.add(AVPBlocks.STEEL_BLOCK, "Блок стали");
        translationBuilder.add(AVPBlocks.STEEL_BUTTON, "Стальная кнопка");
        translationBuilder.add(AVPBlocks.STEEL_CHAIN_FENCE, "Стальной цепной забор");
        translationBuilder.add(AVPBlocks.STEEL_COLUMN, "Стальная колонна");
        translationBuilder.add(AVPBlocks.STEEL_DOOR, "Стальная дверь");
        translationBuilder.add(AVPBlocks.STEEL_FASTENED_SIDING, "Сталь креплённая боком");
        translationBuilder.add(AVPBlocks.STEEL_FASTENED_STANDING, "Сталь креплённая прямо");
        translationBuilder.add(AVPBlocks.STEEL_GRATE, "Стальная решётка");
        translationBuilder.add(AVPBlocks.STEEL_PRESSURE_PLATE, "Стальная нажимная плита");
        translationBuilder.add(AVPBlocks.STEEL_PLATING, "Стальная обшивка");
        translationBuilder.add(AVPBlocks.STEEL_SIDING, "Сталь боком");
        translationBuilder.add(AVPBlocks.STEEL_STANDING, "Сталь прямо");
        translationBuilder.add(AVPBlocks.STEEL_TRAP_DOOR, "Стальной люк");
        translationBuilder.add(AVPBlocks.STEEL_TREAD, "Стальной протектор");
        translationBuilder.add(AVPBlocks.TITANIUM_BLOCK, "Блок титана");
        translationBuilder.add(AVPBlocks.TITANIUM_BUTTON, "Титановая кнопка");
        translationBuilder.add(AVPBlocks.TITANIUM_CHAIN_FENCE, "Титановый цепной забор");
        translationBuilder.add(AVPBlocks.TITANIUM_COLUMN, "Титановая колонна");
        translationBuilder.add(AVPBlocks.TITANIUM_DOOR, "Титановая дверь");
        translationBuilder.add(AVPBlocks.TITANIUM_FASTENED_SIDING, "Титан креплённый боком");
        translationBuilder.add(AVPBlocks.TITANIUM_FASTENED_STANDING, "Титан креплённый прямо");
        translationBuilder.add(AVPBlocks.TITANIUM_GRATE, "Титановая решётка");
        translationBuilder.add(AVPBlocks.TITANIUM_PRESSURE_PLATE, "Титановая нажимная плита");
        translationBuilder.add(AVPBlocks.TITANIUM_PLATING, "Титановая обшивка");
        translationBuilder.add(AVPBlocks.TITANIUM_SIDING, "Титан боком");
        translationBuilder.add(AVPBlocks.TITANIUM_STANDING, "Титан прямо");
        translationBuilder.add(AVPBlocks.TITANIUM_TRAP_DOOR, "Титановый люк");
        translationBuilder.add(AVPBlocks.TITANIUM_TREAD, "Титановый протектор");
        translationBuilder.add(AVPBlocks.URANIUM_BLOCK, "Блок урана");
        translationBuilder.add(AVPBlocks.ZINC_BLOCK, "Блок цинка");
        translationBuilder.add(AVPBlocks.ZINC_ORE, "Цинковая руда");
        translationBuilder.add(AVPBlocks.INDUSTRIAL_FURNACE, "Индустриальная печь");
        translationBuilder.add(AVPBlocks.FERROALUMINUM_SLAB, "Фероалюминиевая плита");
        translationBuilder.add(AVPBlocks.FERROALUMINUM_STAIRS, "Фероалюминиевые ступеньки");
        translationBuilder.add(AVPBlocks.STEEL_SLAB, "Стальная плита");
        translationBuilder.add(AVPBlocks.STEEL_STAIRS, "Стальные ступеньки");
        translationBuilder.add(AVPBlocks.TITANIUM_SLAB, "Титановая плита");
        translationBuilder.add(AVPBlocks.TITANIUM_STAIRS, "Титановые ступеньки");
        translationBuilder.add(AVPBlocks.FERROALUMINUM_SIDING_SLAB, "Фероалюминиевая боковая плита");
        translationBuilder.add(AVPBlocks.FERROALUMINUM_SIDING_STAIRS, "Фероалюминиевые боковые ступеньки");
        translationBuilder.add(AVPBlocks.STEEL_SIDING_SLAB, "Стальная боковая плита");
        translationBuilder.add(AVPBlocks.STEEL_SIDING_STAIRS, "Стальные боковые ступеньки");
        translationBuilder.add(AVPBlocks.TITANIUM_SIDING_SLAB, "Титановая боковая плита");
        translationBuilder.add(AVPBlocks.TITANIUM_SIDING_STAIRS, "Титановые боковые ступеньки");
        translationBuilder.add(AVPBlocks.STEEL_STANDING_SLAB, "Стальная прямая плита");
        translationBuilder.add(AVPBlocks.STEEL_STANDING_STAIRS, "Сатльные прямые ступеньки");
        translationBuilder.add(AVPBlocks.TITANIUM_STANDING_SLAB, "Титановая прямая плита");
        translationBuilder.add(AVPBlocks.TITANIUM_STANDING_STAIRS, "Титановые прямые ступеньки");
        translationBuilder.add(AVPBlocks.FERROALUMINUM_FASTENED_SIDING_SLAB, "Фероалюминиевая креплённая боком плита");
        translationBuilder.add(AVPBlocks.FERROALUMINUM_FASTENED_SIDING_STAIRS, "Фероалюминиевые креплённые боком ступеньки");
        translationBuilder.add(AVPBlocks.STEEL_FASTENED_SIDING_SLAB, "Стальная креплённая боком плита");
        translationBuilder.add(AVPBlocks.STEEL_FASTENED_SIDING_STAIRS, "Стальные креплённые боком ступеньки");
        translationBuilder.add(AVPBlocks.TITANIUM_FASTENED_SIDING_SLAB, "Титановая креплённая боком плита");
        translationBuilder.add(AVPBlocks.TITANIUM_FASTENED_SIDING_STAIRS, "Титановые креплённые боком ступеньки");

        // Creative Mode Tabs
        translationBuilder.add(CreativeModeTabs.BLOCKS_KEY, "ЧПХ: Блоки");
        translationBuilder.add(CreativeModeTabs.COLORED_BLOCKS_KEY, "ЧПХ: Разноцветные блоки");
        translationBuilder.add(CreativeModeTabs.COMBAT_KEY, "ЧПХ: Сражения");
        translationBuilder.add(CreativeModeTabs.INGREDIENTS_KEY, "ЧПХ: Ингредиенты");
        translationBuilder.add(CreativeModeTabs.SPAWN_EGGS_KEY, "ЧПХ: Яйца призыва");
        translationBuilder.add(CreativeModeTabs.TOOLS_AND_UTILITIES_KEY, "ЧПХ: Инструменты & Утилиты");

        // Entities
        translationBuilder.add(AVPEntityTypes.ACID, "Кислота");
        translationBuilder.add(AVPEntityTypes.CHESTBURSTER, "Грудолом");
        translationBuilder.add(AVPEntityTypes.DRONE, "Ксеноморф");
        translationBuilder.add(AVPEntityTypes.FACEHUGGER, "Лицехват");
        translationBuilder.add(AVPEntityTypes.OVAMORPH, "Оваморф");
        translationBuilder.add(AVPEntityTypes.PRAETORIAN, "Ксеноморф-преторианец");
        translationBuilder.add(AVPEntityTypes.QUEEN, "Королева ксеноморфов");
        translationBuilder.add(AVPEntityTypes.WARRIOR, "Ксеноморф-воин");
        translationBuilder.add(AVPEntityTypes.YAUTJA, "Яутжа (хищник)");
        translationBuilder.add(AVPEntityTypes.ROCKET, "Ракета");
        translationBuilder.add(AVPEntityTypes.GRENADE_THROWN, "Граната");
        translationBuilder.add(AVPEntityTypes.SHURIKEN, "Сюрикен");
        translationBuilder.add(AVPEntityTypes.SMART_DISC, "Умный диск");
        translationBuilder.add(AVPEntityTypes.BULLET, "Пуля");
        translationBuilder.add(AVPEntityTypes.MARINE, "Морской котик");

        // Combat Items
        translationBuilder.add(AVPItems.SHURIKEN, "Сюрикен");
        translationBuilder.add(AVPItems.SMART_DISC, "Умный диск");
        translationBuilder.add(AVPItems.GRENADE, "Граната");
        translationBuilder.add(AVPItems.GRENADE_INCENDIARY, "Зажигательная граната");
        translationBuilder.add(AVPItems.GRENADE_IRRADIATED, "Радиоактивная граната");
        translationBuilder.add(AVPItems.CASELESS_BULLET, "Патрон без картриджа");
        translationBuilder.add(ArmorItems.CHITIN_BOOTS, "Хитиновые ботинки");
        translationBuilder.add(ArmorItems.CHITIN_CHESTPLATE, "Хитиновый нагрудник");
        translationBuilder.add(ArmorItems.CHITIN_HELMET, "Хитиновый шлем");
        translationBuilder.add(ArmorItems.CHITIN_LEGGINGS, "Хитиновые штаны");
        translationBuilder.add(AVPItems.F903WE_RIFLE, "F903WE Винтовка");
        translationBuilder.add(AVPItems.FLAMETHROWER_SEVASTOPOL, "Огнемёт (Севастополь)");
        translationBuilder.add(AVPItems.FUEL_TANK, "Топливный бак");
        translationBuilder.add(AVPItems.HEAVY_BULLET, "Тяжёлый патрон");
        translationBuilder.add(ArmorItems.JUNGLE_PREDATOR_BOOTS, "Ботинки хищника");
        translationBuilder.add(ArmorItems.JUNGLE_PREDATOR_CHESTPLATE, "Нагрудник хищника");
        translationBuilder.add(ArmorItems.JUNGLE_PREDATOR_HELMET, "Шлем хищника");
        translationBuilder.add(ArmorItems.JUNGLE_PREDATOR_LEGGINGS, "Штаны хищника");
        translationBuilder.add(AVPItems.M37_12_SHOTGUN, "M37-12 Дробовик");
        translationBuilder.add(AVPItems.M41A_PULSE_RIFLE, "M41A Импульсная винтовка");
        translationBuilder.add(AVPItems.M42A3_SNIPER_RIFLE, "M42A3 Снайперская винтовка");
        translationBuilder.add(AVPItems.M4RA_BATTLE_RIFLE, "M4RA Боевая винтовка");
        translationBuilder.add(AVPItems.M56_SMARTGUN, "M56 Умная пушка");
        translationBuilder.add(AVPItems.M6B_ROCKET_LAUNCHER, "M6B Ракетомёт");
        translationBuilder.add(AVPItems.M88_MOD_4_COMBAT_PISTOL, "88 Mod 4 Боевой пистолет");
        translationBuilder.add(AVPItems.MEDIUM_BULLET, "Средный патрон");
        translationBuilder.add(ArmorItems.MK50_BOOTS, "MK50 ботинки");
        translationBuilder.add(ArmorItems.MK50_CHESTPLATE, "MK50 нагрудник");
        translationBuilder.add(ArmorItems.MK50_HELMET, "MK50 шлем");
        translationBuilder.add(ArmorItems.MK50_LEGGINGS, "MK50 штаны");
        translationBuilder.add(ArmorItems.NETHER_CHITIN_BOOTS, "Адские хитиновые ботинки");
        translationBuilder.add(ArmorItems.NETHER_CHITIN_CHESTPLATE, "Адский хитиновый нагрудник");
        translationBuilder.add(ArmorItems.NETHER_CHITIN_HELMET, "Адский хитиновый шлем");
        translationBuilder.add(ArmorItems.NETHER_CHITIN_LEGGINGS, "Адские хитиновые штаны");
        translationBuilder.add(AVPItems.OLD_PAINLESS, "Старый безболезненный (миниган)");
        translationBuilder.add(ArmorItems.PLATED_CHITIN_BOOTS, "Обшитые хитиновые ботинки");
        translationBuilder.add(ArmorItems.PLATED_CHITIN_CHESTPLATE, "Обшитый хитиновый нагрудник");
        translationBuilder.add(ArmorItems.PLATED_CHITIN_HELMET, "Обшитый хитиновый шлем");
        translationBuilder.add(ArmorItems.PLATED_CHITIN_LEGGINGS, "Обшитые хитиновые штаны");
        translationBuilder.add(ArmorItems.PLATED_NETHER_CHITIN_BOOTS, "Обшитые адские хитиновые ботинки");
        translationBuilder.add(ArmorItems.PLATED_NETHER_CHITIN_CHESTPLATE, "Обшитый адский хитиновый нагрудник");
        translationBuilder.add(ArmorItems.PLATED_NETHER_CHITIN_HELMET, "Обшитый адский хитиновый шлем");
        translationBuilder.add(ArmorItems.PLATED_NETHER_CHITIN_LEGGINGS, "Обшитые адские хитиновые штаны");
        translationBuilder.add(ArmorItems.PRESSURE_BOOTS, "Тяжёлые ботинки");
        translationBuilder.add(ArmorItems.PRESSURE_CHESTPLATE, "Тяжёлый нагрудник");
        translationBuilder.add(ArmorItems.PRESSURE_HELMET, "Тяжёлый шлем");
        translationBuilder.add(ArmorItems.PRESSURE_LEGGINGS, "Тяжёлые штаны");
        translationBuilder.add(AVPItems.ROCKET, "Ракета");
        translationBuilder.add(AVPItems.SHOTGUN_BULLET, "Патрон от дробовика");
        translationBuilder.add(AVPItems.SMALL_BULLET, "Мелкий патрон");
        translationBuilder.add(ArmorItems.STEEL_BOOTS, "Стальные ботинки");
        translationBuilder.add(ArmorItems.STEEL_CHESTPLATE, "Стальной нагрудник");
        translationBuilder.add(ArmorItems.STEEL_HELMET, "Стальной шлем");
        translationBuilder.add(ArmorItems.STEEL_LEGGINGS, "Стальные штаны");
        translationBuilder.add(ArmorItems.TACTICAL_BOOTS, "Тактические ботинки");
        translationBuilder.add(ArmorItems.TACTICAL_CHESTPLATE, "Тактический нагрудник");
        translationBuilder.add(ArmorItems.TACTICAL_HELMET, "Тактический шлем");
        translationBuilder.add(ArmorItems.TACTICAL_LEGGINGS, "Тактические штаны");
        translationBuilder.add(ArmorItems.TACTICAL_CAMO_BOOTS, "Камуфляжные тактические ботинки");
        translationBuilder.add(ArmorItems.TACTICAL_CAMO_CHESTPLATE, "Камуфляжный тактический нагрудник");
        translationBuilder.add(ArmorItems.TACTICAL_CAMO_HELMET, "Камуфляжный тактический шлем");
        translationBuilder.add(ArmorItems.TACTICAL_CAMO_LEGGINGS, "Камуфляжные тактические штаны");
        translationBuilder.add(ArmorItems.TITANIUM_BOOTS, "Титановые ботинки");
        translationBuilder.add(ArmorItems.TITANIUM_CHESTPLATE, "Титановый нагрудник");
        translationBuilder.add(ArmorItems.TITANIUM_HELMET, "Титановый шлем");
        translationBuilder.add(ArmorItems.TITANIUM_LEGGINGS, "Титановые штаны");
        translationBuilder.add(AVPItems.ZX_76_SHOTGUN, "ZX-76 Дробовик");

        // Ingredient Items
        translationBuilder.add(AVPItems.ALUMINUM_INGOT, "Алюминиевый слиток");
        translationBuilder.add(AVPItems.AUTUNITE_DUST, "Аутинитовая пыль");
        translationBuilder.add(AVPItems.BARREL, "Корпус");
        translationBuilder.add(AVPItems.BATTERY_PACK, "Набор батареек");
        translationBuilder.add(AVPItems.BLUEPRINT_F903WE_RIFLE, "Чертёж F903WE винтовки");
        translationBuilder.add(AVPItems.BLUEPRINT_FLAMETHROWER_SEVASTOPOL, "Чертёж огнемёта (Севастополь)");
        translationBuilder.add(AVPItems.BLUEPRINT_M37_12_SHOTGUN, "Чертёж M37-12 дробовика");
        translationBuilder.add(AVPItems.BLUEPRINT_M41A_PULSE_RIFLE, "Чертёж M41A импульсной винтовки");
        translationBuilder.add(AVPItems.BLUEPRINT_M42A3_SNIPER_RIFLE, "Чертёж M42A3 снайперской винтовки");
        translationBuilder.add(AVPItems.BLUEPRINT_M4RA_BATTLE_RIFLE, "Чертёж M4RA боевой винтовки");
        translationBuilder.add(AVPItems.BLUEPRINT_M56_SMARTGUN, "Чертёж M56 умной пушки");
        translationBuilder.add(AVPItems.BLUEPRINT_M6B_ROCKET_LAUNCHER, "Чертёж M6B ракетомёта");
        translationBuilder.add(AVPItems.BLUEPRINT_M88MOD4_COMBAT_PISTOL, "Чертёж M88 Mod 4 боевого пистолета");
        translationBuilder.add(AVPItems.BLUEPRINT_OLD_PAINLESS, "Чертёж Старого безболезненного (минигана)");
        translationBuilder.add(AVPItems.BLUEPRINT_ZX_76_SHOTGUN, "Чертёж ZX-76 дробовика");
        translationBuilder.add(AVPItems.BRASS_INGOT, "Слиток латуни");
        translationBuilder.add(AVPItems.BULLET_TIP, "Наконечник пули");
        translationBuilder.add(AVPItems.CAPACITOR, "Конденсатор");
        translationBuilder.add(AVPItems.CARBON_DUST, "Карбоновая пыль");
        translationBuilder.add(AVPItems.CASELESS_CASING, "Безкорпусный корпус");
        translationBuilder.add(AVPItems.CHITIN, "Хитин");
        translationBuilder.add(AVPItems.CPU, "Процессор");
        translationBuilder.add(AVPItems.DIODE, "Диод");
        translationBuilder.add(AVPItems.FERROALUMINUM_INGOT, "Фероалюминиевый слиток");
        translationBuilder.add(AVPItems.GRIP, "Ручка");
        translationBuilder.add(AVPItems.HEAVY_CASING, "Тяжёлый корпус");
        translationBuilder.add(AVPItems.INTEGRATED_CIRCUIT, "Интегральная схема");
        translationBuilder.add(AVPItems.LEAD_INGOT, "Свинцовый слиток");
        translationBuilder.add(AVPItems.LED, "LED");
        translationBuilder.add(AVPItems.LED_DISPLAY, "LED Дисплей");
        translationBuilder.add(AVPItems.LITHIUM_DUST, "Литиевая пыль");
        translationBuilder.add(AVPItems.MEDIUM_CASING, "Корпус от винтовки");
        translationBuilder.add(AVPItems.MINIGUN_BARREL, "Корпус минигана");
        translationBuilder.add(AVPItems.NEODYMIUM_MAGNET, "Неодиевый магнит");
        translationBuilder.add(AVPItems.NETHER_CHITIN, "Адский хитин");
        translationBuilder.add(AVPItems.NETHER_RESIN_BALL, "Комок адской смолы");
        translationBuilder.add(AVPItems.OVOID_POTTERY_SHERD, "Керамический осколок с изображением оваморфа");
        translationBuilder.add(AVPItems.PARASITE_POTTERY_SHERD, "Керамический осколок с изображением грудолома");
        translationBuilder.add(AVPItems.ROYALTY_POTTERY_SHERD, "Керамический осколок с изображением королевы ксеноморфов");
        translationBuilder.add(AVPItems.PLATED_CHITIN, "Обшитый хитин");
        translationBuilder.add(AVPItems.PLATED_NETHER_CHITIN, "Обшитый адский хитин");
        translationBuilder.add(AVPItems.POLYMER, "Полимер");
        translationBuilder.add(AVPItems.RAW_BAUXITE, "Необработанный алюминий");
        translationBuilder.add(AVPItems.RAW_BRASS, "Необработанная латунь");
        translationBuilder.add(AVPItems.RAW_CRUDE_IRON, "Необработанный грубый металл");
        translationBuilder.add(AVPItems.RAW_FERROBAUXITE, "Необработанный фероалюминий");
        translationBuilder.add(AVPItems.RAW_GALENA, "Необработанный галенит");
        translationBuilder.add(AVPItems.RAW_MONAZITE, "Необработанный моназит");
        translationBuilder.add(AVPItems.RAW_ROYAL_JELLY, "Сырая королевская слизь");
        translationBuilder.add(AVPItems.POISON_JELLY, "Отравленная слизь");
        translationBuilder.add(AVPItems.RAW_SILICA, "Необработанный кварц");
        translationBuilder.add(AVPItems.RAW_TITANIUM, "Необработанный титан");
        translationBuilder.add(AVPItems.RAW_ZINC, "Необработанный цинк");
        translationBuilder.add(AVPItems.RECEIVER, "Получатель");
        translationBuilder.add(AVPItems.REGULATOR, "Регулятор");
        translationBuilder.add(AVPItems.RESIN_BALL, "Смолистый комок");
        translationBuilder.add(AVPItems.RESISTOR, "Резистор");
        translationBuilder.add(AVPItems.ROCKET_BARREL, "Корпус от ракеты");
        translationBuilder.add(AVPItems.SHOTGUN_CASING, "Корпус дробовика");
        translationBuilder.add(AVPItems.SMALL_CASING, "Корпус пистолета");
        translationBuilder.add(AVPItems.SMART_BARREL, "Корпус умной пушки");
        translationBuilder.add(AVPItems.SMART_RECEIVER, "Умный получатель");
        translationBuilder.add(AVPItems.STEEL_INGOT, "Стальной слиток");
        translationBuilder.add(AVPItems.STOCK, "Ложа");
        translationBuilder.add(AVPItems.TITANIUM_INGOT, "Титановый слиток");
        translationBuilder.add(AVPItems.TRANSISTOR, "Транзистор");
        translationBuilder.add(AVPItems.URANIUM_INGOT, "Слиток урана");
        translationBuilder.add(AVPItems.VECTOR_POTTERY_SHERD, "Керамический осколок с изображением лицехвата");
        translationBuilder.add(AVPItems.VERITANIUM_SHARD, "Осколок веританиума");
        translationBuilder.add(AVPItems.ZINC_INGOT, "Цинковый слиток");
        translationBuilder.add(AVPItems.ALUMINUM_NUGGET, "Алюминиевый самородок");
        translationBuilder.add(AVPItems.BRASS_NUGGET, "Самородок латуни");
        translationBuilder.add(AVPItems.FERROALUMINUM_NUGGET, "Фероалюминиевый самородок");
        translationBuilder.add(AVPItems.LEAD_NUGGET, "Свинцовый самородок");
        translationBuilder.add(AVPItems.STEEL_NUGGET, "Стальной самородок");
        translationBuilder.add(AVPItems.TITANIUM_NUGGET, "Титановы самородок");
        translationBuilder.add(AVPItems.URANIUM_NUGGET, "Урановый самородок");
        translationBuilder.add(AVPItems.ZINC_NUGGET, "Цинковый самородок");

        // Tools & Utilities Items
        translationBuilder.add(AVPItems.ARMOR_CASE, "Кейс для брони");
        translationBuilder.add(AVPItems.CANISTER, "Канистра");
        translationBuilder.add(AVPItems.ROYAL_JELLY_CANISTER, "Канистра наполненная королевской слизью");
        translationBuilder.add(AVPItems.STEEL_AXE, "Стальной топор");
        translationBuilder.add(AVPItems.STEEL_HOE, "Стальная мотыка");
        translationBuilder.add(AVPItems.STEEL_PICKAXE, "Стальная кирка");
        translationBuilder.add(AVPItems.STEEL_SHOVEL, "Стальная лопата");
        translationBuilder.add(AVPItems.STEEL_SWORD, "Стальной меч");
        translationBuilder.add(AVPItems.TITANIUM_AXE, "Титановый топор");
        translationBuilder.add(AVPItems.TITANIUM_HOE, "Титановая мотыка");
        translationBuilder.add(AVPItems.TITANIUM_PICKAXE, "Титановая кирка");
        translationBuilder.add(AVPItems.TITANIUM_SHOVEL, "Титановая лопата");
        translationBuilder.add(AVPItems.TITANIUM_SWORD, "Титановый меч");
        translationBuilder.add(AVPItems.VERITANIUM_AXE, "Веритановый топор");
        translationBuilder.add(AVPItems.VERITANIUM_HOE, "Веритановая мотыка");
        translationBuilder.add(AVPItems.VERITANIUM_PICKAXE, "Веритановая кирка");
        translationBuilder.add(AVPItems.VERITANIUM_SHOVEL, "Веритановая лопата");
        translationBuilder.add(AVPItems.VERITANIUM_SWORD, "Веритановый меч");

        // Spawn Egg Items
        translationBuilder.add(SpawnEggItems.ABERRANT_CHESTBURSTER_SPAWN_EGG, "Яйцо призыва аберрантного грудолома");
        translationBuilder.add(SpawnEggItems.ABERRANT_DRONE_SPAWN_EGG, "Яйцо призыва аберрантного ксеноморфа");
        translationBuilder.add(SpawnEggItems.ABERRANT_FACEHUGGER_SPAWN_EGG, "Яйцо призыва аберрантного лицехвата");
        translationBuilder.add(SpawnEggItems.ABERRANT_OVAMORPH_SPAWN_EGG, "Яйцо призыва аберрантного оваморфа");
        translationBuilder.add(SpawnEggItems.ABERRANT_PRAETORIAN_SPAWN_EGG, "Яйцо призыва аберрантного ксеноморфа-преторианца");
        translationBuilder.add(SpawnEggItems.ABERRANT_WARRIOR_SPAWN_EGG, "Яйцо призыва аберрантного ксеноморфа-воина");
        translationBuilder.add(SpawnEggItems.ABERRANT_QUEEN_SPAWN_EGG, "Яйцо призыва абберантной королевы ксеноморфов");
        translationBuilder.add(SpawnEggItems.CHESTBURSTER_SPAWN_EGG, "Яйцо призыва грудолома");
        translationBuilder.add(SpawnEggItems.DRONE_SPAWN_EGG, "Яйцо призыва ксеноморфа");
        translationBuilder.add(SpawnEggItems.FACEHUGGER_SPAWN_EGG, "Яйцо призыва лицехвата");
        translationBuilder.add(SpawnEggItems.NETHER_CHESTBURSTER_SPAWN_EGG, "Яйцо призыва адского грудолома");
        translationBuilder.add(SpawnEggItems.NETHER_DRONE_SPAWN_EGG, "Яйцо призыва адского ксеноморфа");
        translationBuilder.add(SpawnEggItems.NETHER_FACEHUGGER_SPAWN_EGG, "Яйцо призыва адского лицехвата");
        translationBuilder.add(SpawnEggItems.NETHER_OVAMORPH_SPAWN_EGG, "Яйцо призыва адского оваморфа");
        translationBuilder.add(SpawnEggItems.NETHER_PRAETORIAN_SPAWN_EGG, "Яйцо призыва адского ксеноморфа-преторианца");
        translationBuilder.add(SpawnEggItems.NETHER_WARRIOR_SPAWN_EGG, "Яйцо призыва адского ксеноморфа-воина");
        translationBuilder.add(SpawnEggItems.NETHER_QUEEN_SPAWN_EGG, "Яйцо призыва адской королевы ксеноморфов");
        translationBuilder.add(SpawnEggItems.IRRAIATED_DRONE_SPAWN_EGG, "Irraiated Drone Spawn Egg");
        translationBuilder.add(SpawnEggItems.IRRAIATED_WARRIOR_SPAWN_EGG, "Irraiated Warrior Spawn Egg");
        translationBuilder.add(SpawnEggItems.IRRAIATED_PRAETORIAN_SPAWN_EGG, "Irraiated Praetorian Spawn Egg");
        translationBuilder.add(SpawnEggItems.OVAMORPH_SPAWN_EGG, "Яйцо призыва оваморфа");
        translationBuilder.add(SpawnEggItems.PRAETORIAN_SPAWN_EGG, "Яйцо призыва ксеноморфа-преторианца");
        translationBuilder.add(SpawnEggItems.QUEEN_SPAWN_EGG, "Яйцо призыва королевы ксеноморфов");
        translationBuilder.add(SpawnEggItems.WARRIOR_SPAWN_EGG, "Яйцо призыва ксеноморфа-воина");
        translationBuilder.add(SpawnEggItems.YAUTJA_SPAWN_EGG, "Яйцо призыва якутзы (хищника)");
        translationBuilder.add(SpawnEggItems.MARINE_SPAWN_EGG, "Яйцо призыва морского котика");

        // Sounds
        addSound(translationBuilder, AVPSoundEvents.BLOCK_ACID_BURN, "Кислота расщепляет материал");

        addSound(translationBuilder, AVPSoundEvents.ENTITY_XENOMORPH_ATTACK, "Ксеноморф нападает");
        addSound(translationBuilder, AVPSoundEvents.ENTITY_XENOMORPH_DEATH, "Ксеноморф погибает");
        addSound(translationBuilder, AVPSoundEvents.ENTITY_XENOMORPH_HISS, "Ксеноморф шипит");
        addSound(translationBuilder, AVPSoundEvents.ENTITY_XENOMORPH_HURT, "Ксеноморф получил увечие");
        addSound(translationBuilder, AVPSoundEvents.ENTITY_XENOMORPH_IDLE, "Ксеноморф дышит");
        addSound(translationBuilder, AVPSoundEvents.ENTITY_XENOMORPH_LUNGE, "Ксеноморф прыгает");

        addSound(translationBuilder, AVPSoundEvents.ITEM_ARMOR_EQUIP_CHITIN, "Хитиновая броня хлюпает");
        addSound(translationBuilder, AVPSoundEvents.ITEM_ARMOR_EQUIP_MK50, "MK50 броня шелестит");
        addSound(translationBuilder, AVPSoundEvents.ITEM_ARMOR_EQUIP_PRESSURE, "Тяжёлая броня шарудит");
        addSound(translationBuilder, AVPSoundEvents.ITEM_ARMOR_EQUIP_STEEL, "Лязг стальной брони");
        addSound(translationBuilder, AVPSoundEvents.ITEM_ARMOR_EQUIP_TACTICAL, "Тактическая броня шарудит");
        addSound(translationBuilder, AVPSoundEvents.ITEM_ARMOR_EQUIP_TITANIUM, "Лязг титановой брони");
        addSound(translationBuilder, AVPSoundEvents.ITEM_ARMOR_EQUIP_VERITANIUM, "Лязг веритановой брони");

        addSound(translationBuilder, AVPSoundEvents.WEAPON_FLAMETHROWER_SEVASTOPOL_RELOAD_FINISH, "Перезарядка огнемёта завершена");
        addSound(translationBuilder, AVPSoundEvents.WEAPON_FLAMETHROWER_SEVASTOPOL_RELOAD_START, "Огнемёт перезаряжается");
        addSound(translationBuilder, AVPSoundEvents.WEAPON_FLAMETHROWER_SEVASTOPOL_SHOOT, "Огнемёт стреляет");
        addSound(translationBuilder, AVPSoundEvents.WEAPON_FX_RICOCHET_DIRT, "Пуля отрикошетила от земли");
        addSound(translationBuilder, AVPSoundEvents.WEAPON_FX_RICOCHET_GENERIC, "Пуля отрикошетила");
        addSound(translationBuilder, AVPSoundEvents.WEAPON_FX_RICOCHET_GLASS, "Пуля отрикошетила от стекла");
        addSound(translationBuilder, AVPSoundEvents.WEAPON_FX_RICOCHET_METAL, "Пуля отрикошетила от метала");
        addSound(translationBuilder, AVPSoundEvents.WEAPON_GENERIC_RELOAD, "Оружие перезаряжается");
        addSound(translationBuilder, AVPSoundEvents.WEAPON_GENERIC_SHOOT, "Оружие стреляет");
        addSound(translationBuilder, AVPSoundEvents.WEAPON_GENERIC_SHOOT_FAIL, "Оружие клинит");
        addSound(translationBuilder, AVPSoundEvents.WEAPON_M37_12_SHOTGUN_SHOOT, "M37-12 дробовик стреляет");
        addSound(translationBuilder, AVPSoundEvents.WEAPON_M41A_PULSE_RIFLE_SHOOT, "M41A импульсная винтовка стреляет");
        addSound(translationBuilder, AVPSoundEvents.WEAPON_M42A3_SNIPER_RIFLE_SHOOT, "M42A3 снайперская винтовка стреляет");
        addSound(translationBuilder, AVPSoundEvents.WEAPON_M4RA_BATTLE_RIFLE_SHOOT, "M4RA боевая винтовка стреляет");
        addSound(translationBuilder, AVPSoundEvents.WEAPON_M56_SMARTGUN_SHOOT, "M56 умная пушка стреляет");
        addSound(translationBuilder, AVPSoundEvents.WEAPON_M6B_ROCKET_LAUNCHER_RELOAD_FINISH, "M6B Ракетомёт завершает перезарядку");
        addSound(translationBuilder, AVPSoundEvents.WEAPON_M6B_ROCKET_LAUNCHER_RELOAD_START, "M6B Ракетомёт перезаряжается");
        addSound(translationBuilder, AVPSoundEvents.WEAPON_M6B_ROCKET_LAUNCHER_SHOOT, "M6B Ракетомёт стреляет");
        addSound(translationBuilder, AVPSoundEvents.WEAPON_M88_MOD_4_COMBAT_PISTOL_RELOAD, "M88 Mod 4 боевой пистолет перезаряжается");
        addSound(translationBuilder, AVPSoundEvents.WEAPON_M88_MOD_4_COMBAT_PISTOL_SHOOT, "M88 Mod 4 боевой пистолет стреляет");
        addSound(translationBuilder, AVPSoundEvents.WEAPON_OLD_PAINLESS_SHOOT, "Старый безболезненный (миниган) стреляет");
        addSound(translationBuilder, AVPSoundEvents.WEAPON_OLD_PAINLESS_SHOOT_FINISH, "Старый безболезненный (миниган) прекращает стрельбу");
        addSound(translationBuilder, AVPSoundEvents.WEAPON_OLD_PAINLESS_SHOOT_SPINNING, "Корпус старого безболезненного (минигана) вращается");
        addSound(translationBuilder, AVPSoundEvents.WEAPON_OLD_PAINLESS_SHOOT_START, "Корпус старого безболезненного (минигана) начинает вращаться");
        addSound(translationBuilder, AVPSoundEvents.WEAPON_ZX_76_SHOTGUN_SHOOT, "ZX-76 дробовик стреляет");

        // Tooltips
        translationBuilder.add("tooltip.avp.accuracy", "Точность: ");
        translationBuilder.add("tooltip.avp.ammunition", "Патроны: ");
        translationBuilder.add("tooltip.avp.ammunition_type", "Стреляет: ");
        translationBuilder.add("tooltip.avp.damage", "Урон: ");
        translationBuilder.add("tooltip.avp.fire_mode", "Режим стрельбы: ");
        translationBuilder.add("tooltip.avp.fire_rate", "Скорость стрельбы: ");
        translationBuilder.add("tooltip.avp.knockback", "Отбрасывание противника: ");
        translationBuilder.add("tooltip.avp.recoil", "Отдача: ");

        // Keybinds
        translationBuilder.add("key.avp.reload", "Перезарядиться");
        translationBuilder.add("keybind.category.avp.weapons", "ЧПХ оружие");

        // Death messages
        translationBuilder.add("death.attack.acid", "%1$s расчинился в кислоте");
        translationBuilder.add("death.attack.radiation", "%1$s погиб от высокого уровня радиации");
        translationBuilder.add("death.attack.razor_wire", "%1$s заколол себя до смерти колючей проволкой");

        translationBuilder.add("advancements.aliens.root.title", "ЧПХ: Чужие");
        translationBuilder.add("advancements.aliens.root.description", "В Майнкрафте, никто не услышит твой крик");

        translationBuilder.add("advancements.aliens.kill_an_alien.title", "Неидеальный организм");
        translationBuilder.add("advancements.aliens.kill_an_alien.description", "Убей ксеноморфа и живи, чтобы рассказать свою историю");

        translationBuilder.add("advancements.aliens.kill_a_royal_alien.title", "Цареубийство");
        translationBuilder.add("advancements.aliens.kill_a_royal_alien.description", "Убей королевского ксеноморфа");

        translationBuilder.add("advancements.aliens.kill_all_aliens.title", "Ксеноцид");
        translationBuilder.add("advancements.aliens.kill_all_aliens.description", "Убей особину каждого типа ксеноморфов");

        translationBuilder.add("advancements.aliens.chitin_armor.title", "Покрой меня в... э...");
        translationBuilder.add("advancements.aliens.chitin_armor.description", "Оденьте полный набор хитиновой брони");

        translationBuilder.add("advancements.aliens.shear_an_ovamorph.title", "Время овоследований");
        translationBuilder.add("advancements.aliens.shear_an_ovamorph.description", "Освободи оваморфа с его заключения");

        translationBuilder.add("advancements.aliens.plated_chitin_armor.title", "Встаю на колена перед Короной");
        translationBuilder.add("advancements.aliens.plated_chitin_armor.description", "Одеть полный набор обшитой хитиновой брони");

        // Hive boss bar
        translationBuilder.add("bossbar.avp.hive.title", "Улей");

        translationBuilder.add("avp.industrialfurnace.displayName", "Индустриальная печь");
        translationBuilder.add("effect.avp.radiation", "Радиация");
    }

    private void addSound(TranslationBuilder translationBuilder, SoundEvent soundEvent, String value) {
        translationBuilder.add("субтитры." + soundEvent.getLocation().getPath(), value);
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
