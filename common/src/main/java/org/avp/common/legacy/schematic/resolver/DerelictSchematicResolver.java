package org.avp.common.legacy.schematic.resolver;

import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;

import java.util.Map;

import org.avp.common.data.block.EngineerShipBlockSetDataContainer;
import org.avp.common.data.block.IndustrialGlassBlockSetDataContainer;

public class DerelictSchematicResolver {

    public static final Map<String, Block> RESOLVER_MAP = Map.<String, Block>ofEntries(
        Map.entry("avp:blackgoo", Blocks.WATER),
        Map.entry("avp:engineershipbrick1", EngineerShipBlockSetDataContainer.INSTANCE.brick1.getHolder().get()),
        Map.entry("avp:engineershipbrick2", EngineerShipBlockSetDataContainer.INSTANCE.brick2.getHolder().get()),
        Map.entry("avp:engineershipbrick3", EngineerShipBlockSetDataContainer.INSTANCE.brick3.getHolder().get()),
        Map.entry("avp:engineershipcolumn1", EngineerShipBlockSetDataContainer.INSTANCE.column1.getHolder().get()),
        Map.entry("avp:engineershipcolumn2", EngineerShipBlockSetDataContainer.INSTANCE.column2.getHolder().get()),
        Map.entry("avp:engineershipgravel", EngineerShipBlockSetDataContainer.INSTANCE.gravel.getHolder().get()),
        Map.entry("avp:engineershipmaterial1", EngineerShipBlockSetDataContainer.INSTANCE.material1.getHolder().get()),
        Map.entry("avp:engineershipmaterial2", EngineerShipBlockSetDataContainer.INSTANCE.material2.getHolder().get()),
        Map.entry("avp:engineershiprock2", EngineerShipBlockSetDataContainer.INSTANCE.rock2.getHolder().get()),
        Map.entry("avp:engineershiprock3", EngineerShipBlockSetDataContainer.INSTANCE.rock3.getHolder().get()),
        Map.entry("avp:engineershipwall1", EngineerShipBlockSetDataContainer.INSTANCE.wall1.getHolder().get()),
        Map.entry("avp:engineershipwall2", EngineerShipBlockSetDataContainer.INSTANCE.wall2.getHolder().get()),
        Map.entry("avp:engineershipwall3", EngineerShipBlockSetDataContainer.INSTANCE.wall3.getHolder().get()),
        Map.entry("avp:engineershipwall4", EngineerShipBlockSetDataContainer.INSTANCE.wall4.getHolder().get()),
        Map.entry("avp:industrialglass", IndustrialGlassBlockSetDataContainer.INSTANCE.base.getHolder().get()),
        Map.entry("minecraft:air", Blocks.AIR),
        Map.entry("minecraft:carrots", Blocks.CARROTS),
        Map.entry("minecraft:potatoes", Blocks.POTATOES),
        Map.entry("minecraft:stained_glass", Blocks.CYAN_STAINED_GLASS),
        Map.entry("minecraft:wheat", Blocks.WHEAT),

        // WOOL PLACEHOLDERS
        Map.entry("avp:engineershipwall4.corner", Blocks.BLACK_WOOL),
        Map.entry("avp:engineershipcolumn2.slope", Blocks.BLUE_WOOL),
        Map.entry("avp:engineershipwall2.corner", Blocks.BROWN_WOOL),
        Map.entry("avp:engineershipmaterial1.corner", Blocks.CYAN_WOOL),
        Map.entry("avp:engineershipcolumn1.invertedridge", Blocks.GRAY_WOOL),
        Map.entry("avp:engineershipwall3.slope", Blocks.GREEN_WOOL),
        Map.entry("avp:engineershiprock3.invertedcorner", Blocks.LIGHT_BLUE_WOOL),
        Map.entry("avp:engineershipwall2.invertedcorner", Blocks.LIGHT_GRAY_WOOL),
        Map.entry("avp:engineershiprock3.slope", Blocks.LIME_WOOL),
        Map.entry("avp:engineershipmaterial1.ridge", Blocks.MAGENTA_WOOL),
        Map.entry("avp:vent.wall.corner", Blocks.ORANGE_WOOL),
        Map.entry("avp:engineershiprock3.ridge", Blocks.PINK_WOOL),
        Map.entry("avp:engineershipmaterial1.slope", Blocks.PURPLE_WOOL),
        Map.entry("avp:engineershipwall4.slope", Blocks.RED_WOOL),
        Map.entry("avp:engineershipwall3.invertedcorner", Blocks.WHITE_WOOL),
        Map.entry("avp:vent.wall", Blocks.YELLOW_WOOL),

        // CONCRETE PLACEHOLDERS
        Map.entry("avp:engineershipcolumn1.slope", Blocks.BLACK_CONCRETE),
        Map.entry("avp:engineershipmaterial1.smartridge", Blocks.BLUE_CONCRETE),
        Map.entry("avp:engineershipcolumn1.invertedcorner", Blocks.BROWN_CONCRETE),
        Map.entry("avp:engineershiprock3.corner", Blocks.CYAN_CONCRETE),
        Map.entry("avp:engineershipcolumn1.corner", Blocks.GRAY_CONCRETE),
        Map.entry("avp:shipdecor3", Blocks.GREEN_CONCRETE),
        Map.entry("avp:engineershipwall2.slope", Blocks.LIGHT_BLUE_CONCRETE),
        Map.entry("avp:engineershiprock2.invertedcorner", Blocks.LIGHT_GRAY_CONCRETE),
        Map.entry("avp:engineershipmaterial1.invertedcorner", Blocks.LIME_CONCRETE),
        Map.entry("avp:engineershiprock3.smartridge", Blocks.MAGENTA_CONCRETE),
        Map.entry("avp:vent.wall.ridge", Blocks.ORANGE_CONCRETE),
        Map.entry("avp:engineershipcolumn1.smartridge", Blocks.PINK_CONCRETE),
        Map.entry("avp:engineershipmaterial2.slope", Blocks.PURPLE_CONCRETE),
        Map.entry("avp:vent.wall.slope", Blocks.RED_CONCRETE),
        Map.entry("avp:engineershipwall1.corner", Blocks.WHITE_CONCRETE),
        Map.entry("avp:shipdecor3.smartinvertedridge", Blocks.YELLOW_CONCRETE),

        // REMAINING PLACEHOLDERS
        Map.entry("avp:engineershipcolumn1.ridge", Blocks.STONE_BRICKS),
        Map.entry("avp:engineershipmaterial2.ridge", Blocks.CRACKED_POLISHED_BLACKSTONE_BRICKS),
        Map.entry("avp:engineershiprock2.slope", Blocks.END_STONE_BRICKS),
        Map.entry("avp:engineershipcolumn2.invertedcorner", Blocks.PURPUR_BLOCK),

        // TILE ENTITY PLACEHOLDERS
        Map.entry("avp:stasismechanism", Blocks.REDSTONE_BLOCK),
        Map.entry("avp:lv223portal", Blocks.EMERALD_BLOCK),
        Map.entry("avp:powercell", Blocks.DIAMOND_BLOCK),
        Map.entry("avp:engineership.ampule", Blocks.GOLD_BLOCK)
    );

    private DerelictSchematicResolver() {
        throw new UnsupportedOperationException();
    }
}
