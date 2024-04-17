package org.avp.common.item;

import net.minecraft.world.item.Item;

import org.avp.api.Holder;
import org.avp.common.registry.AVPDeferredItemRegistry;

public class AVPElectronicItems extends AVPDeferredItemRegistry {

    public static final AVPElectronicItems INSTANCE = new AVPElectronicItems();

    public final Holder<Item> capacitor;

    public final Holder<Item> cpu;

    public final Holder<Item> diode;

    public final Holder<Item> integratedCircuit;

    public final Holder<Item> led;

    public final Holder<Item> ledDisplay;

    public final Holder<Item> motherboard;

    public final Holder<Item> powerSupply;

    public final Holder<Item> ram;

    public final Holder<Item> regulator;

    public final Holder<Item> resistor;

    public final Holder<Item> ssd;

    public final Holder<Item> transistor;

    private AVPElectronicItems() {
        capacitor = createHolder("capacitor");
        cpu = createHolder("cpu");
        diode = createHolder("diode");
        integratedCircuit = createHolder("integrated_circuit");
        led = createHolder("led");
        ledDisplay = createHolder("led_display");
        motherboard = createHolder("motherboard");
        powerSupply = createHolder("power_supply");
        ram = createHolder("ram");
        regulator = createHolder("regulator");
        resistor = createHolder("resistor");
        ssd = createHolder("ssd");
        transistor = createHolder("transistor");
    }
}
