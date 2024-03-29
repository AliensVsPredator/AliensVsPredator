package org.avp.common.item;

import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.Item;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

import org.avp.common.service.Services;
import org.avp.api.GameObject;

/**
 * @author Boston Vanseghi
 */
public class AVPFoodItems {

    private static final List<GameObject<Item>> ENTRIES = new ArrayList<>();

    public static final GameObject<Item> DORITOS;

    public static final GameObject<Item> DORITOS_COOL_RANCH;

    public static final GameObject<Item> RAW_TENTACLE;

    public static final GameObject<Item> TRILO_BITE;

    public static void forceInitialization() {
        // This method doesn't need to do anything
    }

    public static List<GameObject<Item>> getEntries() {
        return ENTRIES;
    }

    private static GameObject<Item> register(String registryName, Supplier<Item> itemSupplier) {
        var gameObject = Services.ITEM_REGISTRY.register(registryName, itemSupplier);
        ENTRIES.add(gameObject);
        return gameObject;
    }

    private static GameObject<Item> registerFood(String registryName, FoodProperties.Builder foodPropertiesBuilder) {
        var foodProperties = foodPropertiesBuilder.build();
        return register(registryName, () -> new Item(new Item.Properties().food(foodProperties)));
    }

    private AVPFoodItems() {}

    static {
        DORITOS = registerFood(
            "doritos",
            new FoodProperties.Builder().alwaysEat().nutrition(2).saturationMod(0.2F)
        );
        DORITOS_COOL_RANCH = registerFood(
            "doritos_cool_ranch",
            new FoodProperties.Builder().alwaysEat().nutrition(4).saturationMod(0.2F)
        );

        // Slightly worse than raw beef.
        RAW_TENTACLE = registerFood(
            "raw_tentacle",
            new FoodProperties.Builder().meat().nutrition(2).saturationMod(0.2F)
        );

        // Slightly worse than cooked beef.
        TRILO_BITE = registerFood("trilo_bite", new FoodProperties.Builder().meat().nutrition(7).saturationMod(0.7F));
    }
}
