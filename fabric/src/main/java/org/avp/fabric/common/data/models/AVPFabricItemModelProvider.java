package org.avp.fabric.common.data.models;

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
        AVPAmmunitionPartItems.getEntries()
            .forEach(
                itemGameObject -> itemModelGenerator.generateFlatItem(itemGameObject.get(), ModelTemplates.FLAT_ITEM)
            );
        AVPArmorItems.getEntries()
            .forEach(
                itemGameObject -> itemModelGenerator.generateFlatItem(itemGameObject.get(), ModelTemplates.FLAT_ITEM)
            );
        AVPBulletItems.getEntries()
            .forEach(
                itemGameObject -> itemModelGenerator.generateFlatItem(itemGameObject.get(), ModelTemplates.FLAT_ITEM)
            );
        AVPElectronicItems.getEntries()
            .forEach(
                itemGameObject -> itemModelGenerator.generateFlatItem(itemGameObject.get(), ModelTemplates.FLAT_ITEM)
            );
        AVPFoodItems.getEntries()
            .forEach(
                itemGameObject -> itemModelGenerator.generateFlatItem(itemGameObject.get(), ModelTemplates.FLAT_ITEM)
            );
        AVPItems.getEntries()
            .forEach(
                itemGameObject -> itemModelGenerator.generateFlatItem(itemGameObject.get(), ModelTemplates.FLAT_ITEM)
            );
        AVPToolItems.getEntries()
            .forEach(
                itemGameObject -> itemModelGenerator.generateFlatItem(
                    itemGameObject.get(),
                    ModelTemplates.FLAT_HANDHELD_ITEM
                )
            );
        AVPWeaponBlueprintItems.getEntries()
            .forEach(
                itemGameObject -> itemModelGenerator.generateFlatItem(itemGameObject.get(), ModelTemplates.FLAT_ITEM)
            );
        AVPWeaponItems.getEntries()
            .forEach(
                itemGameObject -> itemModelGenerator.generateFlatItem(
                    itemGameObject.get(),
                    ModelTemplates.FLAT_HANDHELD_ITEM
                )
            );
        AVPWeaponPartItems.getEntries()
            .forEach(
                itemGameObject -> itemModelGenerator.generateFlatItem(itemGameObject.get(), ModelTemplates.FLAT_ITEM)
            );
    }

    private AVPFabricItemModelProvider() {
        throw new UnsupportedOperationException();
    }
}
