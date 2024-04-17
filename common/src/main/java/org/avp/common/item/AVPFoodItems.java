package org.avp.common.item;

import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.Item;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

import org.avp.api.GameObject;
import org.avp.common.registry.AVPDeferredItemRegistry;
import org.avp.common.service.Services;

public class AVPFoodItems extends AVPDeferredItemRegistry {

    public static final AVPFoodItems INSTANCE = new AVPFoodItems();

    public final GameObject<Item> DORITOS = registerFood(
        "doritos",
        new FoodProperties.Builder().alwaysEat().nutrition(2).saturationMod(0.2F)
    );

    public final GameObject<Item> DORITOS_COOL_RANCH = registerFood(
        "doritos_cool_ranch",
        new FoodProperties.Builder().alwaysEat().nutrition(4).saturationMod(0.2F)
    );

    // Slightly worse than raw beef.
    public final GameObject<Item> RAW_TENTACLE = registerFood(
        "raw_tentacle",
        new FoodProperties.Builder().meat().nutrition(2).saturationMod(0.2F)
    );

    // Slightly worse than cooked beef.
    public final GameObject<Item> TRILO_BITE = registerFood("trilo_bite", new FoodProperties.Builder().meat().nutrition(7).saturationMod(0.7F));

    private GameObject<Item> registerFood(String registryName, FoodProperties.Builder foodPropertiesBuilder) {
        var foodProperties = foodPropertiesBuilder.build();
        return createHolder(registryName, () -> new Item(new Item.Properties().food(foodProperties)));
    }

    private AVPFoodItems() {}
}
