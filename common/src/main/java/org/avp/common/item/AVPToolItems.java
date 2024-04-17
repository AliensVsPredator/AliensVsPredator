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

    public final Holder<Item> aluminumAxe;

    public final Holder<Item> aluminumHoe;

    public final Holder<Item> aluminumPickaxe;

    public final Holder<Item> aluminumShovel;

    public final Holder<Item> aluminumSword;

    public final Holder<Item> orioniteAxe;

    public final Holder<Item> orioniteHoe;

    public final Holder<Item> orionitePickaxe;

    public final Holder<Item> orioniteShovel;

    public final Holder<Item> orioniteSword;

    public final Holder<Item> titaniumAxe;

    public final Holder<Item> titaniumHoe;

    public final Holder<Item> titaniumPickaxe;

    public final Holder<Item> titaniumShovel;

    public final Holder<Item> titaniumSword;

    public final Holder<Item> veritaniumAxe;

    public final Holder<Item> veritaniumHoe;

    public final Holder<Item> veritaniumPickaxe;

    public final Holder<Item> veritaniumShovel;

    public final Holder<Item> veritaniumSword;

    private AVPToolItems() {
        aluminumAxe = createHolder("aluminum_axe", () -> new ModdedAxeItem(AVPToolTiers.ALUMINUM, 5.0F, -3.0F, new Item.Properties()));
        aluminumHoe = createHolder("aluminum_hoe", () -> new ModdedHoeItem(AVPToolTiers.ALUMINUM, -1, -2.0F, new Item.Properties()));
        aluminumPickaxe = createHolder(
            "aluminum_pickaxe",
            () -> new ModdedPickaxeItem(AVPToolTiers.ALUMINUM, 1, -2.8F, new Item.Properties())
        );
        aluminumShovel = createHolder("aluminum_shovel", () -> new ShovelItem(AVPToolTiers.ALUMINUM, 1.5F, -3.0F, new Item.Properties()));
        aluminumSword = createHolder("aluminum_sword", () -> new SwordItem(AVPToolTiers.ALUMINUM, 3, -2.4F, new Item.Properties()));

        orioniteAxe = createHolder("orionite_axe", () -> new ModdedAxeItem(AVPToolTiers.ORIONITE, 5.0F, -3.0F, new Item.Properties()));
        orioniteHoe = createHolder("orionite_hoe", () -> new ModdedHoeItem(AVPToolTiers.ORIONITE, -3, 0.0F, new Item.Properties()));
        orionitePickaxe = createHolder(
            "orionite_pickaxe",
            () -> new ModdedPickaxeItem(AVPToolTiers.ORIONITE, 1, -2.8F, new Item.Properties())
        );
        orioniteShovel = createHolder("orionite_shovel", () -> new ShovelItem(AVPToolTiers.ORIONITE, 1.5F, -3.0F, new Item.Properties()));
        orioniteSword = createHolder("orionite_sword", () -> new SwordItem(AVPToolTiers.ORIONITE, 3, -2.4F, new Item.Properties()));

        titaniumAxe = createHolder("titanium_axe", () -> new ModdedAxeItem(AVPToolTiers.TITANIUM, 5.0F, -3.0F, new Item.Properties()));
        titaniumHoe = createHolder("titanium_hoe", () -> new ModdedHoeItem(AVPToolTiers.TITANIUM, -2, -1.0F, new Item.Properties()));
        titaniumPickaxe = createHolder(
            "titanium_pickaxe",
            () -> new ModdedPickaxeItem(AVPToolTiers.TITANIUM, 1, -2.8F, new Item.Properties())
        );
        titaniumShovel = createHolder("titanium_shovel", () -> new ShovelItem(AVPToolTiers.TITANIUM, 1.5F, -3.0F, new Item.Properties()));
        titaniumSword = createHolder("titanium_sword", () -> new SwordItem(AVPToolTiers.TITANIUM, 3, -2.4F, new Item.Properties()));

        veritaniumAxe = createHolder("veritanium_axe", () -> new ModdedAxeItem(AVPToolTiers.VERITANIUM, 5.0F, -3.0F, new Item.Properties()));
        veritaniumHoe = createHolder("veritanium_hoe", () -> new ModdedHoeItem(AVPToolTiers.VERITANIUM, -4, 0.0F, new Item.Properties()));
        veritaniumPickaxe = createHolder(
            "veritanium_pickaxe",
            () -> new ModdedPickaxeItem(AVPToolTiers.VERITANIUM, 1, -2.8F, new Item.Properties())
        );
        veritaniumShovel = createHolder(
            "veritanium_shovel",
            () -> new ShovelItem(AVPToolTiers.VERITANIUM, 1.5F, -3.0F, new Item.Properties())
        );
        veritaniumSword = createHolder("veritanium_sword", () -> new SwordItem(AVPToolTiers.VERITANIUM, 3, -2.4F, new Item.Properties()));
    }

    @Override
    protected Holder<Item> createHolder(String registryName, Supplier<Item> supplier) {
        return super.createHolder("tool_" + registryName, supplier);
    }
}
