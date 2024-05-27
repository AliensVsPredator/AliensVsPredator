package org.avp.fabric.common.data.model;

import net.minecraft.data.models.ItemModelGenerators;
import net.minecraft.data.models.model.ModelTemplates;

import org.avp.common.registry.AVPDeferredItemRegistry;

public class AVPFabricItemModelProvider {

    public static void addItemModels(ItemModelGenerators itemModelGenerator) {
        AVPDeferredItemRegistry.getDataEntries().forEach(holderItemDataTuple -> {
            var item = holderItemDataTuple.first().get();
            var itemData = holderItemDataTuple.second();
            var itemModelData = itemData.itemModelData();
            var itemModelDataType = itemModelData.itemModelDataTypeFactory().apply(item);

            switch (itemModelDataType.getGenType()) {
                case FLAT -> itemModelGenerator.generateFlatItem(item, ModelTemplates.FLAT_ITEM);
                case HANDHELD -> itemModelGenerator.generateFlatItem(item, ModelTemplates.FLAT_HANDHELD_ITEM);
                case NONE -> {/* NO-OP */}
            }
        });
    }

    private AVPFabricItemModelProvider() {
        throw new UnsupportedOperationException();
    }
}
