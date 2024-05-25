package org.avp.common.item;

import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ShovelItem;
import net.minecraft.world.item.SwordItem;

import java.util.Set;

import org.avp.api.Holder;
import org.avp.api.QuadFunction;
import org.avp.api.item.ItemData;
import org.avp.api.item.model.ItemModelData;
import org.avp.api.item.model.type.ItemModelDataType;
import org.avp.api.item.tool.ModdedAxeItem;
import org.avp.api.item.tool.ModdedHoeItem;
import org.avp.api.item.tool.ModdedPickaxeItem;
import org.avp.common.item.tool.tier.AVPToolTier;
import org.avp.common.item.tool.tier.AVPToolTiers;
import org.avp.common.registry.AVPDeferredItemRegistry;

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

    @Override
    protected Holder<Item> createHolder(ItemData itemData) {
        return super.createHolder(itemData.withPrefixRegistryName("tool_"));
    }

    protected Holder<Item> createToolHolder2(
        String registryName,
        AVPToolTier toolTier,
        int attackDamage,
        float attackSpeed,
        QuadFunction<AVPToolTier, Integer, Float, Item.Properties, Item> toolFactory,
        Set<TagKey<Item>> tags
    ) {
        return createToolHolder(registryName, toolTier, attackDamage, attackSpeed, (a, b, c, d) -> toolFactory.apply(a, b.intValue(), c, d), tags);
    }

    protected Holder<Item> createToolHolder(
        String registryName,
        AVPToolTier toolTier,
        float attackDamage,
        float attackSpeed,
        QuadFunction<AVPToolTier, Float, Float, Item.Properties, Item> toolFactory,
        Set<TagKey<Item>> tags
    ) {
        var itemModelData = new ItemModelData(
            () -> toolFactory.apply(toolTier, attackDamage, attackSpeed, new Item.Properties()),
            ItemModelDataType.HandHeld::new
        );

        return createHolder(new ItemData(registryName, itemModelData, tags));
    }

    private AVPToolItems() {
        var axeTags = Set.of(ItemTags.AXES, ItemTags.TOOLS);
        var hoeTags = Set.of(ItemTags.HOES, ItemTags.TOOLS);
        var shovelTags = Set.of(ItemTags.SHOVELS, ItemTags.TOOLS);
        var pickaxeTags = Set.of(ItemTags.PICKAXES, ItemTags.TOOLS);
        var swordTags = Set.of(ItemTags.SWORDS);

        aluminumAxe = createToolHolder("aluminum_axe", AVPToolTiers.ALUMINUM, 5, -3, ModdedAxeItem::new, axeTags);
        aluminumHoe = createToolHolder2("aluminum_hoe", AVPToolTiers.ALUMINUM, -1, -2, ModdedHoeItem::new, hoeTags);
        aluminumPickaxe = createToolHolder2("aluminum_pickaxe",AVPToolTiers.ALUMINUM, 1, -2.8F, ModdedPickaxeItem::new, pickaxeTags);
        aluminumShovel = createToolHolder("aluminum_shovel", AVPToolTiers.ALUMINUM, 1.5F, -3, ShovelItem::new, shovelTags);
        aluminumSword = createToolHolder2("aluminum_sword", AVPToolTiers.ALUMINUM, 3, -2.4F, SwordItem::new, swordTags);

        orioniteAxe = createToolHolder("orionite_axe", AVPToolTiers.ORIONITE, 5, -3, ModdedAxeItem::new, axeTags);
        orioniteHoe = createToolHolder2("orionite_hoe", AVPToolTiers.ORIONITE, -3, 0, ModdedHoeItem::new, hoeTags);
        orionitePickaxe = createToolHolder2("orionite_pickaxe", AVPToolTiers.ORIONITE, 1, -2.8F, ModdedPickaxeItem::new, pickaxeTags);
        orioniteShovel = createToolHolder("orionite_shovel", AVPToolTiers.ORIONITE, 1.5F, -3, ShovelItem::new, shovelTags);
        orioniteSword = createToolHolder2("orionite_sword", AVPToolTiers.ORIONITE, 3, -2.4F, SwordItem::new, swordTags);

        titaniumAxe = createToolHolder("titanium_axe", AVPToolTiers.TITANIUM, 5, -3, ModdedAxeItem::new, axeTags);
        titaniumHoe = createToolHolder2("titanium_hoe", AVPToolTiers.TITANIUM, -2, -1, ModdedHoeItem::new, hoeTags);
        titaniumPickaxe = createToolHolder2("titanium_pickaxe", AVPToolTiers.TITANIUM, 1, -2.8F, ModdedPickaxeItem::new, pickaxeTags);
        titaniumShovel = createToolHolder("titanium_shovel", AVPToolTiers.TITANIUM, 1.5F, -3, ShovelItem::new, shovelTags);
        titaniumSword = createToolHolder2("titanium_sword", AVPToolTiers.TITANIUM, 3, -2.4F, SwordItem::new, swordTags);

        veritaniumAxe = createToolHolder("veritanium_axe", AVPToolTiers.VERITANIUM, 5, -3, ModdedAxeItem::new, axeTags);
        veritaniumHoe = createToolHolder2("veritanium_hoe", AVPToolTiers.VERITANIUM, -4, 0, ModdedHoeItem::new, hoeTags);
        veritaniumPickaxe = createToolHolder2("veritanium_pickaxe", AVPToolTiers.VERITANIUM, 1, -2.8F, ModdedPickaxeItem::new, pickaxeTags);
        veritaniumShovel = createToolHolder("veritanium_shovel", AVPToolTiers.VERITANIUM, 1.5F, -3, ShovelItem::new, shovelTags);
        veritaniumSword = createToolHolder2("veritanium_sword", AVPToolTiers.VERITANIUM, 3, -2.4F, SwordItem::new, swordTags);
    }
}
