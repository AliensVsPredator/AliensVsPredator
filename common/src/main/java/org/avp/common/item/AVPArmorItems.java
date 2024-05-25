package org.avp.common.item;

import net.minecraft.tags.TagKey;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ArmorMaterial;
import net.minecraft.world.item.Item;
import org.avp.api.Holder;
import org.avp.api.item.ItemData;
import org.avp.api.item.ItemHolderArmorSet;
import org.avp.api.item.model.ItemModelData;
import org.avp.api.item.model.type.ItemModelDataType;
import org.avp.common.item.armor.material.AVPArmorMaterials;
import org.avp.common.registry.AVPDeferredItemRegistry;
import org.avp.common.tag.AVPItemTags;

import java.util.Set;

public class AVPArmorItems extends AVPDeferredItemRegistry {

    public static final AVPArmorItems INSTANCE = new AVPArmorItems();

    public final ItemHolderArmorSet aluminum;

    public final ItemHolderArmorSet mk50;

    public final ItemHolderArmorSet orionite;

    public final ItemHolderArmorSet pressure;

    public final ItemHolderArmorSet tactical;

    public final ItemHolderArmorSet titanium;

    public final ItemHolderArmorSet veritanium;

    public final ItemHolderArmorSet xenomorphChitin;

    protected Holder<Item> createHolder(String registryName, ArmorMaterial armorMaterial, ArmorItem.Type armorType, Set<TagKey<Item>> tags) {
        var itemModelData = new ItemModelData(
            () -> new ArmorItem(armorMaterial, armorType, new Item.Properties()),
            ItemModelDataType.Flat::new
        );
        return createHolder(new ItemData(registryName, itemModelData, tags));
    }

    protected ItemHolderArmorSet createArmorHolderSet(String registryName, ArmorMaterial armorMaterial, Set<TagKey<Item>> tags) {
        return new ItemHolderArmorSet(
            createHolder(registryName + "_helmet", armorMaterial, ArmorItem.Type.HELMET, tags),
            createHolder(registryName + "_body", armorMaterial, ArmorItem.Type.CHESTPLATE, tags),
            createHolder(registryName + "_leggings", armorMaterial, ArmorItem.Type.LEGGINGS, tags),
            createHolder(registryName + "_boots", armorMaterial, ArmorItem.Type.BOOTS, tags)
        );
    }

    protected ItemHolderArmorSet createArmorHolderSet(String registryName, ArmorMaterial armorMaterial) {
        return createArmorHolderSet(registryName, armorMaterial, Set.of());
    }

    @Override
    protected Holder<Item> createHolder(ItemData itemData) {
        return super.createHolder(itemData.withPrefixRegistryName("armor_"));
    }

    private AVPArmorItems() {
        aluminum = createArmorHolderSet("aluminum", AVPArmorMaterials.ALUMINUM);
        mk50 = createArmorHolderSet("mk50", AVPArmorMaterials.MK50);
        orionite = createArmorHolderSet("orionite", AVPArmorMaterials.ORIONITE);
        pressure = createArmorHolderSet("pressure", AVPArmorMaterials.MK50);
        tactical = createArmorHolderSet("tactical", AVPArmorMaterials.TACTICAL);
        titanium = createArmorHolderSet("titanium", AVPArmorMaterials.TITANIUM);
        veritanium = createArmorHolderSet("veritanium", AVPArmorMaterials.VERITANIUM);
        xenomorphChitin = createArmorHolderSet("xenomorph_chitin", AVPArmorMaterials.XENOMORPH_CHITIN, Set.of(AVPItemTags.ACID_IMMUNE));
    }
}
