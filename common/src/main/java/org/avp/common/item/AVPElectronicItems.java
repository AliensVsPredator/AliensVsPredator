package org.avp.common.item;

import net.minecraft.world.item.Item;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

import org.avp.api.GameObject;
import org.avp.common.service.Services;

public class AVPElectronicItems {

    private static final List<GameObject<Item>> ENTRIES = new ArrayList<>();

    public static final GameObject<Item> CAPACITOR;

    public static final GameObject<Item> CPU;

    public static final GameObject<Item> DIODE;

    public static final GameObject<Item> INTEGRATED_CIRCUIT;

    public static final GameObject<Item> LED;

    public static final GameObject<Item> LED_DISPLAY;

    public static final GameObject<Item> MOTHERBOARD;

    public static final GameObject<Item> POWER_SUPPLY;

    public static final GameObject<Item> RAM;

    public static final GameObject<Item> REGULATOR;

    public static final GameObject<Item> RESISTOR;

    public static final GameObject<Item> SSD;

    public static final GameObject<Item> TRANSISTOR;

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

    private AVPElectronicItems() {}

    static {
        CAPACITOR = register("capacitor", () -> new Item(new Item.Properties()));
        CPU = register("cpu", () -> new Item(new Item.Properties()));
        DIODE = register("diode", () -> new Item(new Item.Properties()));
        INTEGRATED_CIRCUIT = register("integrated_circuit", () -> new Item(new Item.Properties()));
        LED = register("led", () -> new Item(new Item.Properties()));
        LED_DISPLAY = register("led_display", () -> new Item(new Item.Properties()));
        MOTHERBOARD = register("motherboard", () -> new Item(new Item.Properties()));
        POWER_SUPPLY = register("power_supply", () -> new Item(new Item.Properties()));
        RAM = register("ram", () -> new Item(new Item.Properties()));
        REGULATOR = register("regulator", () -> new Item(new Item.Properties()));
        RESISTOR = register("resistor", () -> new Item(new Item.Properties()));
        SSD = register("ssd", () -> new Item(new Item.Properties()));
        TRANSISTOR = register("transistor", () -> new Item(new Item.Properties()));
    }
}
