package org.avp.common.item;

import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.Item;

import org.avp.api.Holder;
import org.avp.common.registry.AVPDeferredItemRegistry;

public class AVPFoodItems extends AVPDeferredItemRegistry {

    public static final AVPFoodItems INSTANCE = new AVPFoodItems();

    public final Holder<Item> doritos = registerFood(
        "doritos",
        new FoodProperties.Builder().alwaysEat().nutrition(2).saturationMod(0.2F)
    );

    public final Holder<Item> doritosCoolRanch = registerFood(
        "doritos_cool_ranch",
        new FoodProperties.Builder().alwaysEat().nutrition(4).saturationMod(0.2F)
    );

    // Slightly worse than raw beef.
    public final Holder<Item> rawTentacle = registerFood(
        "raw_tentacle",
        new FoodProperties.Builder().meat().nutrition(2).saturationMod(0.2F)
    );

    // Slightly worse than cooked beef.
    public final Holder<Item> triloBite = registerFood("trilo_bite", new FoodProperties.Builder().meat().nutrition(7).saturationMod(0.7F));

    private AVPFoodItems() {}

    private Holder<Item> registerFood(String registryName, FoodProperties.Builder foodPropertiesBuilder) {
        var foodProperties = foodPropertiesBuilder.build();
        return createHolder(registryName, () -> new Item(new Item.Properties().food(foodProperties)));
    }
}
