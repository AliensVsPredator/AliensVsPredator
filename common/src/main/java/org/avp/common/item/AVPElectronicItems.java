package org.avp.common.item;

import net.minecraft.world.item.Item;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

import org.avp.api.GameObject;
import org.avp.common.registry.AVPDeferredItemRegistry;
import org.avp.common.service.Services;

public class AVPElectronicItems extends AVPDeferredItemRegistry {

    public static final AVPElectronicItems INSTANCE = new AVPElectronicItems();

    public final GameObject<Item> CAPACITOR;

    public final GameObject<Item> CPU;

    public final GameObject<Item> DIODE;

    public final GameObject<Item> INTEGRATED_CIRCUIT;

    public final GameObject<Item> LED;

    public final GameObject<Item> LED_DISPLAY;

    public final GameObject<Item> MOTHERBOARD;

    public final GameObject<Item> POWER_SUPPLY;

    public final GameObject<Item> RAM;

    public final GameObject<Item> REGULATOR;

    public final GameObject<Item> RESISTOR;

    public final GameObject<Item> SSD;

    public final GameObject<Item> TRANSISTOR;

    protected GameObject<Item> createHolder(String registryName) {
        return createHolder(registryName, () -> new Item(new Item.Properties()));
    }

    private AVPElectronicItems() {
        CAPACITOR = createHolder("capacitor");
        CPU = createHolder("cpu");
        DIODE = createHolder("diode");
        INTEGRATED_CIRCUIT = createHolder("integrated_circuit");
        LED = createHolder("led");
        LED_DISPLAY = createHolder("led_display");
        MOTHERBOARD = createHolder("motherboard");
        POWER_SUPPLY = createHolder("power_supply");
        RAM = createHolder("ram");
        REGULATOR = createHolder("regulator");
        RESISTOR = createHolder("resistor");
        SSD = createHolder("ssd");
        TRANSISTOR = createHolder("transistor");
    }
}
