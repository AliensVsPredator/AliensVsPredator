package org.avp.fabric.common.data.model;

import net.minecraft.data.models.ItemModelGenerators;
import net.minecraft.data.models.model.ModelTemplates;

import org.avp.common.item.AVPAmmunitionPartItems;
import org.avp.common.item.AVPArmorItems;
import org.avp.common.item.AVPBulletItems;
import org.avp.common.item.AVPElectronicItems;
import org.avp.common.item.AVPFoodItems;
import org.avp.common.item.AVPItems;
import org.avp.common.item.AVPToolItems;
import org.avp.common.item.AVPWeaponBlueprintItems;
import org.avp.common.item.AVPWeaponItems;
import org.avp.common.item.AVPWeaponPartItems;

public class AVPFabricItemModelProvider {

    public static void addItemModels(ItemModelGenerators itemModelGenerator) {
        AVPAmmunitionPartItems.INSTANCE.getValues()
            .forEach(
                holder -> itemModelGenerator.generateFlatItem(holder.get(), ModelTemplates.FLAT_ITEM)
            );
        AVPArmorItems.INSTANCE.getValues()
            .forEach(
                holder -> itemModelGenerator.generateFlatItem(holder.get(), ModelTemplates.FLAT_ITEM)
            );
        AVPBulletItems.INSTANCE.getValues()
            .forEach(
                holder -> itemModelGenerator.generateFlatItem(holder.get(), ModelTemplates.FLAT_ITEM)
            );
        AVPElectronicItems.INSTANCE.getValues()
            .forEach(
                holder -> itemModelGenerator.generateFlatItem(holder.get(), ModelTemplates.FLAT_ITEM)
            );
        AVPFoodItems.INSTANCE.getValues()
            .forEach(
                holder -> itemModelGenerator.generateFlatItem(holder.get(), ModelTemplates.FLAT_ITEM)
            );
        AVPItems.INSTANCE.getValues()
            .forEach(
                holder -> itemModelGenerator.generateFlatItem(holder.get(), ModelTemplates.FLAT_ITEM)
            );
        AVPToolItems.INSTANCE.getValues()
            .forEach(
                holder -> itemModelGenerator.generateFlatItem(
                    holder.get(),
                    ModelTemplates.FLAT_HANDHELD_ITEM
                )
            );
        AVPWeaponBlueprintItems.INSTANCE.getValues()
            .forEach(
                holder -> itemModelGenerator.generateFlatItem(holder.get(), ModelTemplates.FLAT_ITEM)
            );
        AVPWeaponItems.INSTANCE.getValues()
            .forEach(
                holder -> itemModelGenerator.generateFlatItem(
                    holder.get(),
                    ModelTemplates.FLAT_HANDHELD_ITEM
                )
            );
        AVPWeaponPartItems.INSTANCE.getValues()
            .forEach(
                holder -> itemModelGenerator.generateFlatItem(holder.get(), ModelTemplates.FLAT_ITEM)
            );
    }

    private AVPFabricItemModelProvider() {
        throw new UnsupportedOperationException();
    }
}
