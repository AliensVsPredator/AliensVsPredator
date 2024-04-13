package org.avp.common.item;

import net.minecraft.world.item.Item;
import net.minecraft.world.item.ShovelItem;
import net.minecraft.world.item.SwordItem;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

import org.avp.api.GameObject;
import org.avp.api.item.tool.ModdedAxeItem;
import org.avp.api.item.tool.ModdedHoeItem;
import org.avp.api.item.tool.ModdedPickaxeItem;
import org.avp.common.item.tool.tier.AVPToolTiers;
import org.avp.common.service.Services;

public class AVPToolItems {

    private static final List<GameObject<Item>> ENTRIES = new ArrayList<>();

    public static final GameObject<Item> ALUMINUM_AXE;

    public static final GameObject<Item> ALUMINUM_HOE;

    public static final GameObject<Item> ALUMINUM_PICKAXE;

    public static final GameObject<Item> ALUMINUM_SHOVEL;

    public static final GameObject<Item> ALUMINUM_SWORD;

    public static final GameObject<Item> ORIONITE_AXE;

    public static final GameObject<Item> ORIONITE_HOE;

    public static final GameObject<Item> ORIONITE_PICKAXE;

    public static final GameObject<Item> ORIONITE_SHOVEL;

    public static final GameObject<Item> ORIONITE_SWORD;

    public static final GameObject<Item> TITANIUM_AXE;

    public static final GameObject<Item> TITANIUM_HOE;

    public static final GameObject<Item> TITANIUM_PICKAXE;

    public static final GameObject<Item> TITANIUM_SHOVEL;

    public static final GameObject<Item> TITANIUM_SWORD;

    public static final GameObject<Item> VERITANIUM_AXE;

    public static final GameObject<Item> VERITANIUM_HOE;

    public static final GameObject<Item> VERITANIUM_PICKAXE;

    public static final GameObject<Item> VERITANIUM_SHOVEL;

    public static final GameObject<Item> VERITANIUM_SWORD;

    public static void forceInitialization() {
        // This method doesn't need to do anything
    }

    public static List<GameObject<Item>> getEntries() {
        return ENTRIES;
    }

    private static GameObject<Item> register(String registryName, Supplier<Item> itemSupplier) {
        var gameObject = Services.ITEM_REGISTRY.register("tool_" + registryName, itemSupplier);
        ENTRIES.add(gameObject);
        return gameObject;
    }

    private AVPToolItems() {}

    static {
        ALUMINUM_AXE = register("aluminum_axe", () -> new ModdedAxeItem(AVPToolTiers.ALUMINUM, 5.0F, -3.0F, new Item.Properties()));
        ALUMINUM_HOE = register("aluminum_hoe", () -> new ModdedHoeItem(AVPToolTiers.ALUMINUM, -1, -2.0F, new Item.Properties()));
        ALUMINUM_PICKAXE = register(
            "aluminum_pickaxe",
            () -> new ModdedPickaxeItem(AVPToolTiers.ALUMINUM, 1, -2.8F, new Item.Properties())
        );
        ALUMINUM_SHOVEL = register("aluminum_shovel", () -> new ShovelItem(AVPToolTiers.ALUMINUM, 1.5F, -3.0F, new Item.Properties()));
        ALUMINUM_SWORD = register("aluminum_sword", () -> new SwordItem(AVPToolTiers.ALUMINUM, 3, -2.4F, new Item.Properties()));

        ORIONITE_AXE = register("orionite_axe", () -> new ModdedAxeItem(AVPToolTiers.ORIONITE, 5.0F, -3.0F, new Item.Properties()));
        ORIONITE_HOE = register("orionite_hoe", () -> new ModdedHoeItem(AVPToolTiers.ORIONITE, -3, 0.0F, new Item.Properties()));
        ORIONITE_PICKAXE = register(
            "orionite_pickaxe",
            () -> new ModdedPickaxeItem(AVPToolTiers.ORIONITE, 1, -2.8F, new Item.Properties())
        );
        ORIONITE_SHOVEL = register("orionite_shovel", () -> new ShovelItem(AVPToolTiers.ORIONITE, 1.5F, -3.0F, new Item.Properties()));
        ORIONITE_SWORD = register("orionite_sword", () -> new SwordItem(AVPToolTiers.ORIONITE, 3, -2.4F, new Item.Properties()));

        TITANIUM_AXE = register("titanium_axe", () -> new ModdedAxeItem(AVPToolTiers.TITANIUM, 5.0F, -3.0F, new Item.Properties()));
        TITANIUM_HOE = register("titanium_hoe", () -> new ModdedHoeItem(AVPToolTiers.TITANIUM, -2, -1.0F, new Item.Properties()));
        TITANIUM_PICKAXE = register(
            "titanium_pickaxe",
            () -> new ModdedPickaxeItem(AVPToolTiers.TITANIUM, 1, -2.8F, new Item.Properties())
        );
        TITANIUM_SHOVEL = register("titanium_shovel", () -> new ShovelItem(AVPToolTiers.TITANIUM, 1.5F, -3.0F, new Item.Properties()));
        TITANIUM_SWORD = register("titanium_sword", () -> new SwordItem(AVPToolTiers.TITANIUM, 3, -2.4F, new Item.Properties()));

        VERITANIUM_AXE = register("veritanium_axe", () -> new ModdedAxeItem(AVPToolTiers.VERITANIUM, 5.0F, -3.0F, new Item.Properties()));
        VERITANIUM_HOE = register("veritanium_hoe", () -> new ModdedHoeItem(AVPToolTiers.VERITANIUM, -4, 0.0F, new Item.Properties()));
        VERITANIUM_PICKAXE = register(
            "veritanium_pickaxe",
            () -> new ModdedPickaxeItem(AVPToolTiers.VERITANIUM, 1, -2.8F, new Item.Properties())
        );
        VERITANIUM_SHOVEL = register(
            "veritanium_shovel",
            () -> new ShovelItem(AVPToolTiers.VERITANIUM, 1.5F, -3.0F, new Item.Properties())
        );
        VERITANIUM_SWORD = register("veritanium_sword", () -> new SwordItem(AVPToolTiers.VERITANIUM, 3, -2.4F, new Item.Properties()));
    }
}
