package org.avp.common.registry.item;

import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.Item;

import org.avp.api.common.registry.AVPDeferredItemRegistry;
import org.avp.api.common.registry.holder.BLHolder;

public class AVPFoodItemRegistry extends AVPDeferredItemRegistry {

    public static final AVPFoodItemRegistry INSTANCE = new AVPFoodItemRegistry();

    public final BLHolder<Item> doritos = registerFood(
        "doritos",
        new FoodProperties.Builder().alwaysEat().nutrition(2).saturationMod(0.2F)
    );

    public final BLHolder<Item> doritosCoolRanch = registerFood(
        "doritos_cool_ranch",
        new FoodProperties.Builder().alwaysEat().nutrition(4).saturationMod(0.2F)
    );

    // Slightly worse than raw beef.
    public final BLHolder<Item> rawTentacle = registerFood(
        "raw_tentacle",
        new FoodProperties.Builder().meat().nutrition(2).saturationMod(0.2F)
    );

    // Slightly worse than cooked beef.
    public final BLHolder<Item> triloBite = registerFood(
        "trilo_bite",
        new FoodProperties.Builder().meat().nutrition(7).saturationMod(0.7F)
    );

    private AVPFoodItemRegistry() {}

    private BLHolder<Item> registerFood(String registryName, FoodProperties.Builder foodPropertiesBuilder) {
        var foodProperties = foodPropertiesBuilder.build();
        return createHolder(registryName, new Item.Properties().food(foodProperties));
    }
}
