package org.avp.common.item;

import net.minecraft.world.item.Item;

import org.avp.api.Holder;
import org.avp.common.registry.AVPDeferredItemRegistry;

public class AVPElectronicItems extends AVPDeferredItemRegistry {

    public static final AVPElectronicItems INSTANCE = new AVPElectronicItems();

    public final Holder<Item> CAPACITOR;

    public final Holder<Item> CPU;

    public final Holder<Item> DIODE;

    public final Holder<Item> INTEGRATED_CIRCUIT;

    public final Holder<Item> LED;

    public final Holder<Item> LED_DISPLAY;

    public final Holder<Item> MOTHERBOARD;

    public final Holder<Item> POWER_SUPPLY;

    public final Holder<Item> RAM;

    public final Holder<Item> REGULATOR;

    public final Holder<Item> RESISTOR;

    public final Holder<Item> SSD;

    public final Holder<Item> TRANSISTOR;

    protected Holder<Item> createHolder(String registryName) {
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
