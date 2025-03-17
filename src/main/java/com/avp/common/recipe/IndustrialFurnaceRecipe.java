package com.avp.common.recipe;

import com.avp.AVP;
import com.avp.AVPResources;
import com.avp.common.recipe.input.IndustrialFurnaceRecipeInput;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.*;
import net.minecraft.world.level.Level;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import java.util.HashMap;
import java.util.Map;

public class IndustrialFurnaceRecipe extends AbstractCookingRecipe {
    public static final Map<Item, Item> MELTING_RECIPES = new HashMap<>();
    private static final float SPEED_MULTIPLIER = 0.5f; // 50% faster than regular furnace

    static {
        // Initialize melting recipes based on the provided list
        registerMeltingRecipe(Items.COBBLESTONE, Items.STONE);
        registerMeltingRecipe(Items.COBBLED_DEEPSLATE, Items.DEEPSLATE);
        registerMeltingRecipe(Items.WHITE_TERRACOTTA, Items.WHITE_GLAZED_TERRACOTTA);
        registerMeltingRecipe(Items.ORANGE_TERRACOTTA, Items.ORANGE_GLAZED_TERRACOTTA);
        registerMeltingRecipe(Items.MAGENTA_TERRACOTTA, Items.MAGENTA_GLAZED_TERRACOTTA);
        registerMeltingRecipe(Items.LIGHT_BLUE_TERRACOTTA, Items.LIGHT_BLUE_GLAZED_TERRACOTTA);
        registerMeltingRecipe(Items.YELLOW_TERRACOTTA, Items.YELLOW_GLAZED_TERRACOTTA);
        registerMeltingRecipe(Items.LIME_TERRACOTTA, Items.LIME_GLAZED_TERRACOTTA);
        registerMeltingRecipe(Items.PINK_TERRACOTTA, Items.PINK_GLAZED_TERRACOTTA);
        registerMeltingRecipe(Items.GRAY_TERRACOTTA, Items.GRAY_GLAZED_TERRACOTTA);
        registerMeltingRecipe(Items.LIGHT_GRAY_TERRACOTTA, Items.LIGHT_GRAY_GLAZED_TERRACOTTA);
        registerMeltingRecipe(Items.CYAN_TERRACOTTA, Items.CYAN_GLAZED_TERRACOTTA);
        registerMeltingRecipe(Items.PURPLE_TERRACOTTA, Items.PURPLE_GLAZED_TERRACOTTA);
        registerMeltingRecipe(Items.BLUE_TERRACOTTA, Items.BLUE_GLAZED_TERRACOTTA);
        registerMeltingRecipe(Items.BROWN_TERRACOTTA, Items.BROWN_GLAZED_TERRACOTTA);
        registerMeltingRecipe(Items.GREEN_TERRACOTTA, Items.GREEN_GLAZED_TERRACOTTA);
        registerMeltingRecipe(Items.RED_TERRACOTTA, Items.RED_GLAZED_TERRACOTTA);
        registerMeltingRecipe(Items.BLACK_TERRACOTTA, Items.BLACK_GLAZED_TERRACOTTA);
        registerMeltingRecipe(Items.QUARTZ_BLOCK, Items.SMOOTH_QUARTZ);
        registerMeltingRecipe(Items.SANDSTONE, Items.SMOOTH_SANDSTONE);
        registerMeltingRecipe(Items.STONE, Items.SMOOTH_STONE);
        registerMeltingRecipe(Items.BASALT, Items.SMOOTH_BASALT);
        registerMeltingRecipe(Items.RED_SANDSTONE, Items.SMOOTH_RED_SANDSTONE);
        registerMeltingRecipe(Items.STONE_BRICKS, Items.CRACKED_STONE_BRICKS);
        registerMeltingRecipe(Items.DEEPSLATE_BRICKS, Items.CRACKED_DEEPSLATE_BRICKS);
        registerMeltingRecipe(Items.DEEPSLATE_TILES, Items.CRACKED_DEEPSLATE_TILES);
        registerMeltingRecipe(Items.POLISHED_BLACKSTONE_BRICKS, Items.CRACKED_POLISHED_BLACKSTONE_BRICKS);
        registerMeltingRecipe(Items.NETHER_BRICKS, Items.CRACKED_NETHER_BRICKS);
        registerMeltingRecipe(Items.WET_SPONGE, Items.SPONGE);
        registerMeltingRecipe(Items.SAND, Items.GLASS);
        registerMeltingRecipe(Items.RED_SAND, Items.GLASS);
        registerMeltingRecipe(Items.MUD, Items.CLAY);
        registerMeltingRecipe(Items.CLAY, Items.TERRACOTTA);
    }

    private static void registerMeltingRecipe(Item input, Item output) {
        MELTING_RECIPES.put(input, output);
    }

    public IndustrialFurnaceRecipe(String group, CookingBookCategory cookingBookCategory, Ingredient ingredient, ItemStack result, float experience, int cookingTime) {
        super(AVPRecipes.INDUSTRIAL_FURNACE_RECIPE_TYPE, group, cookingBookCategory, ingredient, result, experience, cookingTime);
    }

    /**
     * Creates a recipe from an item if it has a registered melting recipe
     */
    public static IndustrialFurnaceRecipe fromItem(Item input) {
        Item output = MELTING_RECIPES.get(input);
        if (output == null) {
            return null;
        }

        int vanillaTime = 200;
        int processTime = (int)(vanillaTime * SPEED_MULTIPLIER);
        String recipeId = BuiltInRegistries.ITEM.getKey(input).getPath() + "_to_" +
                BuiltInRegistries.ITEM.getKey(output).getPath();

        return new IndustrialFurnaceRecipe(
                recipeId,
                determineRecipeCategory(input),
                Ingredient.of(input),
                new ItemStack(output),
                0.1f, // Default experience
                processTime
        );
    }

    private static CookingBookCategory determineRecipeCategory(Item item) {
        return CookingBookCategory.BLOCKS;
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return AVPRecipes.INDUSTRIAL_FURNACE_RECIPE_SERIALIZER;
    }

    @Override
    public boolean isSpecial() {
        return true;
    }
}