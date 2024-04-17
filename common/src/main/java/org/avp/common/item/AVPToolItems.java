package org.avp.common.item;

import net.minecraft.world.item.Item;
import net.minecraft.world.item.ShovelItem;
import net.minecraft.world.item.SwordItem;
import org.avp.api.Holder;
import org.avp.api.item.tool.ModdedAxeItem;
import org.avp.api.item.tool.ModdedHoeItem;
import org.avp.api.item.tool.ModdedPickaxeItem;
import org.avp.common.item.tool.tier.AVPToolTiers;
import org.avp.common.registry.AVPDeferredItemRegistry;

import java.util.function.Supplier;

public class AVPToolItems extends AVPDeferredItemRegistry {

    public static final AVPToolItems INSTANCE = new AVPToolItems();

    public final Holder<Item> ALUMINUM_AXE;

    public final Holder<Item> ALUMINUM_HOE;

    public final Holder<Item> ALUMINUM_PICKAXE;

    public final Holder<Item> ALUMINUM_SHOVEL;

    public final Holder<Item> ALUMINUM_SWORD;

    public final Holder<Item> ORIONITE_AXE;

    public final Holder<Item> ORIONITE_HOE;

    public final Holder<Item> ORIONITE_PICKAXE;

    public final Holder<Item> ORIONITE_SHOVEL;

    public final Holder<Item> ORIONITE_SWORD;

    public final Holder<Item> TITANIUM_AXE;

    public final Holder<Item> TITANIUM_HOE;

    public final Holder<Item> TITANIUM_PICKAXE;

    public final Holder<Item> TITANIUM_SHOVEL;

    public final Holder<Item> TITANIUM_SWORD;

    public final Holder<Item> VERITANIUM_AXE;

    public final Holder<Item> VERITANIUM_HOE;

    public final Holder<Item> VERITANIUM_PICKAXE;

    public final Holder<Item> VERITANIUM_SHOVEL;

    public final Holder<Item> VERITANIUM_SWORD;

    @Override
    protected Holder<Item> createHolder(String registryName, Supplier<Item> supplier) {
        return super.createHolder("tool_" + registryName, supplier);
    }

    private AVPToolItems() {
        ALUMINUM_AXE = createHolder("aluminum_axe", () -> new ModdedAxeItem(AVPToolTiers.ALUMINUM, 5.0F, -3.0F, new Item.Properties()));
        ALUMINUM_HOE = createHolder("aluminum_hoe", () -> new ModdedHoeItem(AVPToolTiers.ALUMINUM, -1, -2.0F, new Item.Properties()));
        ALUMINUM_PICKAXE = createHolder(
            "aluminum_pickaxe",
            () -> new ModdedPickaxeItem(AVPToolTiers.ALUMINUM, 1, -2.8F, new Item.Properties())
        );
        ALUMINUM_SHOVEL = createHolder("aluminum_shovel", () -> new ShovelItem(AVPToolTiers.ALUMINUM, 1.5F, -3.0F, new Item.Properties()));
        ALUMINUM_SWORD = createHolder("aluminum_sword", () -> new SwordItem(AVPToolTiers.ALUMINUM, 3, -2.4F, new Item.Properties()));

        ORIONITE_AXE = createHolder("orionite_axe", () -> new ModdedAxeItem(AVPToolTiers.ORIONITE, 5.0F, -3.0F, new Item.Properties()));
        ORIONITE_HOE = createHolder("orionite_hoe", () -> new ModdedHoeItem(AVPToolTiers.ORIONITE, -3, 0.0F, new Item.Properties()));
        ORIONITE_PICKAXE = createHolder(
            "orionite_pickaxe",
            () -> new ModdedPickaxeItem(AVPToolTiers.ORIONITE, 1, -2.8F, new Item.Properties())
        );
        ORIONITE_SHOVEL = createHolder("orionite_shovel", () -> new ShovelItem(AVPToolTiers.ORIONITE, 1.5F, -3.0F, new Item.Properties()));
        ORIONITE_SWORD = createHolder("orionite_sword", () -> new SwordItem(AVPToolTiers.ORIONITE, 3, -2.4F, new Item.Properties()));

        TITANIUM_AXE = createHolder("titanium_axe", () -> new ModdedAxeItem(AVPToolTiers.TITANIUM, 5.0F, -3.0F, new Item.Properties()));
        TITANIUM_HOE = createHolder("titanium_hoe", () -> new ModdedHoeItem(AVPToolTiers.TITANIUM, -2, -1.0F, new Item.Properties()));
        TITANIUM_PICKAXE = createHolder(
            "titanium_pickaxe",
            () -> new ModdedPickaxeItem(AVPToolTiers.TITANIUM, 1, -2.8F, new Item.Properties())
        );
        TITANIUM_SHOVEL = createHolder("titanium_shovel", () -> new ShovelItem(AVPToolTiers.TITANIUM, 1.5F, -3.0F, new Item.Properties()));
        TITANIUM_SWORD = createHolder("titanium_sword", () -> new SwordItem(AVPToolTiers.TITANIUM, 3, -2.4F, new Item.Properties()));

        VERITANIUM_AXE = createHolder("veritanium_axe", () -> new ModdedAxeItem(AVPToolTiers.VERITANIUM, 5.0F, -3.0F, new Item.Properties()));
        VERITANIUM_HOE = createHolder("veritanium_hoe", () -> new ModdedHoeItem(AVPToolTiers.VERITANIUM, -4, 0.0F, new Item.Properties()));
        VERITANIUM_PICKAXE = createHolder(
            "veritanium_pickaxe",
            () -> new ModdedPickaxeItem(AVPToolTiers.VERITANIUM, 1, -2.8F, new Item.Properties())
        );
        VERITANIUM_SHOVEL = createHolder(
            "veritanium_shovel",
            () -> new ShovelItem(AVPToolTiers.VERITANIUM, 1.5F, -3.0F, new Item.Properties())
        );
        VERITANIUM_SWORD = createHolder("veritanium_sword", () -> new SwordItem(AVPToolTiers.VERITANIUM, 3, -2.4F, new Item.Properties()));
    }
}
