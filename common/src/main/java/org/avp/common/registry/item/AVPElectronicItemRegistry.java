package org.avp.common.registry.item;

import net.minecraft.world.item.Item;

import org.avp.api.common.registry.AVPDeferredItemRegistry;
import org.avp.api.common.registry.holder.BLHolder;

public class AVPElectronicItemRegistry extends AVPDeferredItemRegistry {

    public static final AVPElectronicItemRegistry INSTANCE = new AVPElectronicItemRegistry();

    public final BLHolder<Item> battery;

    public final BLHolder<Item> capacitor;

    public final BLHolder<Item> cpu;

    public final BLHolder<Item> diode;

    public final BLHolder<Item> integratedCircuit;

    public final BLHolder<Item> led;

    public final BLHolder<Item> ledDisplay;

    public final BLHolder<Item> motherboard;

    public final BLHolder<Item> powerSupply;

    public final BLHolder<Item> ram;

    public final BLHolder<Item> regulator;

    public final BLHolder<Item> resistor;

    public final BLHolder<Item> ssd;

    public final BLHolder<Item> transistor;

    private AVPElectronicItemRegistry() {
        battery = createHolder("battery");
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
