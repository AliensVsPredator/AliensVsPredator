package org.avp.common.registry.item;

import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ShovelItem;
import net.minecraft.world.item.SwordItem;

import java.util.Set;

import org.avp.api.registry.holder.BLHolder;
import org.avp.api.util.function.QuadFunction;
import org.avp.api.data.item.ItemData;
import org.avp.api.data.item.ItemModelData;
import org.avp.api.data.item.ItemModelDataType;
import org.avp.api.game.item.tool.CustomAxeItem;
import org.avp.api.game.item.tool.CustomHoeItem;
import org.avp.api.game.item.tool.CustomPickaxeItem;
import org.avp.api.game.item.tool.ToolTier;
import org.avp.common.game.item.tool.AVPToolTiers;
import org.avp.api.registry.AVPDeferredItemRegistry;

public class AVPToolItemRegistry extends AVPDeferredItemRegistry {

    public static final AVPToolItemRegistry INSTANCE = new AVPToolItemRegistry();

    public final BLHolder<Item> aluminumAxe;

    public final BLHolder<Item> aluminumHoe;

    public final BLHolder<Item> aluminumPickaxe;

    public final BLHolder<Item> aluminumShovel;

    public final BLHolder<Item> aluminumSword;

    public final BLHolder<Item> orioniteAxe;

    public final BLHolder<Item> orioniteHoe;

    public final BLHolder<Item> orionitePickaxe;

    public final BLHolder<Item> orioniteShovel;

    public final BLHolder<Item> orioniteSword;

    public final BLHolder<Item> titaniumAxe;

    public final BLHolder<Item> titaniumHoe;

    public final BLHolder<Item> titaniumPickaxe;

    public final BLHolder<Item> titaniumShovel;

    public final BLHolder<Item> titaniumSword;

    public final BLHolder<Item> veritaniumAxe;

    public final BLHolder<Item> veritaniumHoe;

    public final BLHolder<Item> veritaniumPickaxe;

    public final BLHolder<Item> veritaniumShovel;

    public final BLHolder<Item> veritaniumSword;

    @Override
    protected BLHolder<Item> createHolder(ItemData itemData) {
        return super.createHolder(itemData.withPrefixRegistryName("tool_"));
    }

    protected BLHolder<Item> createToolHolder2(
        String registryName,
        ToolTier toolTier,
        int attackDamage,
        float attackSpeed,
        QuadFunction<ToolTier, Integer, Float, Item.Properties, Item> toolFactory,
        Set<TagKey<Item>> tags
    ) {
        return createToolHolder(
            registryName,
            toolTier,
            attackDamage,
            attackSpeed,
            (a, b, c, d) -> toolFactory.apply(a, b.intValue(), c, d),
            tags
        );
    }

    protected BLHolder<Item> createToolHolder(
        String registryName,
        ToolTier toolTier,
        float attackDamage,
        float attackSpeed,
        QuadFunction<ToolTier, Float, Float, Item.Properties, Item> toolFactory,
        Set<TagKey<Item>> tags
    ) {
        var itemModelData = new ItemModelData(
            () -> toolFactory.apply(toolTier, attackDamage, attackSpeed, new Item.Properties()),
            ItemModelDataType.HandHeld::new
        );

        return createHolder(new ItemData(registryName, itemModelData, tags));
    }

    private AVPToolItemRegistry() {
        var axeTags = Set.of(ItemTags.AXES, ItemTags.TOOLS);
        var hoeTags = Set.of(ItemTags.HOES, ItemTags.TOOLS);
        var shovelTags = Set.of(ItemTags.SHOVELS, ItemTags.TOOLS);
        var pickaxeTags = Set.of(ItemTags.PICKAXES, ItemTags.TOOLS);
        var swordTags = Set.of(ItemTags.SWORDS);

        aluminumAxe = createToolHolder("aluminum_axe", AVPToolTiers.ALUMINUM, 5, -3, CustomAxeItem::new, axeTags);
        aluminumHoe = createToolHolder2("aluminum_hoe", AVPToolTiers.ALUMINUM, -1, -2, CustomHoeItem::new, hoeTags);
        aluminumPickaxe = createToolHolder2("aluminum_pickaxe", AVPToolTiers.ALUMINUM, 1, -2.8F, CustomPickaxeItem::new, pickaxeTags);
        aluminumShovel = createToolHolder("aluminum_shovel", AVPToolTiers.ALUMINUM, 1.5F, -3, ShovelItem::new, shovelTags);
        aluminumSword = createToolHolder2("aluminum_sword", AVPToolTiers.ALUMINUM, 3, -2.4F, SwordItem::new, swordTags);

        orioniteAxe = createToolHolder("orionite_axe", AVPToolTiers.ORIONITE, 5, -3, CustomAxeItem::new, axeTags);
        orioniteHoe = createToolHolder2("orionite_hoe", AVPToolTiers.ORIONITE, -3, 0, CustomHoeItem::new, hoeTags);
        orionitePickaxe = createToolHolder2("orionite_pickaxe", AVPToolTiers.ORIONITE, 1, -2.8F, CustomPickaxeItem::new, pickaxeTags);
        orioniteShovel = createToolHolder("orionite_shovel", AVPToolTiers.ORIONITE, 1.5F, -3, ShovelItem::new, shovelTags);
        orioniteSword = createToolHolder2("orionite_sword", AVPToolTiers.ORIONITE, 3, -2.4F, SwordItem::new, swordTags);

        titaniumAxe = createToolHolder("titanium_axe", AVPToolTiers.TITANIUM, 5, -3, CustomAxeItem::new, axeTags);
        titaniumHoe = createToolHolder2("titanium_hoe", AVPToolTiers.TITANIUM, -2, -1, CustomHoeItem::new, hoeTags);
        titaniumPickaxe = createToolHolder2("titanium_pickaxe", AVPToolTiers.TITANIUM, 1, -2.8F, CustomPickaxeItem::new, pickaxeTags);
        titaniumShovel = createToolHolder("titanium_shovel", AVPToolTiers.TITANIUM, 1.5F, -3, ShovelItem::new, shovelTags);
        titaniumSword = createToolHolder2("titanium_sword", AVPToolTiers.TITANIUM, 3, -2.4F, SwordItem::new, swordTags);

        veritaniumAxe = createToolHolder("veritanium_axe", AVPToolTiers.VERITANIUM, 5, -3, CustomAxeItem::new, axeTags);
        veritaniumHoe = createToolHolder2("veritanium_hoe", AVPToolTiers.VERITANIUM, -4, 0, CustomHoeItem::new, hoeTags);
        veritaniumPickaxe = createToolHolder2("veritanium_pickaxe", AVPToolTiers.VERITANIUM, 1, -2.8F, CustomPickaxeItem::new, pickaxeTags);
        veritaniumShovel = createToolHolder("veritanium_shovel", AVPToolTiers.VERITANIUM, 1.5F, -3, ShovelItem::new, shovelTags);
        veritaniumSword = createToolHolder2("veritanium_sword", AVPToolTiers.VERITANIUM, 3, -2.4F, SwordItem::new, swordTags);
    }
}
