package com.avp.core.common.menu.armor_case;

import com.mojang.datafixers.util.Pair;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.Container;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.inventory.InventoryMenu;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.Equipable;
import net.minecraft.world.item.ItemStack;

import java.util.Map;

public class ArmorCaseArmorSlot extends Slot {

    private static final Map<EquipmentSlot, ResourceLocation> TEXTURE_EMPTY_SLOTS = Map.of(
        EquipmentSlot.FEET,
        InventoryMenu.EMPTY_ARMOR_SLOT_BOOTS,
        EquipmentSlot.LEGS,
        InventoryMenu.EMPTY_ARMOR_SLOT_LEGGINGS,
        EquipmentSlot.CHEST,
        InventoryMenu.EMPTY_ARMOR_SLOT_CHESTPLATE,
        EquipmentSlot.HEAD,
        InventoryMenu.EMPTY_ARMOR_SLOT_HELMET
    );

    private final EquipmentSlot equipmentSlot;

    public ArmorCaseArmorSlot(Container container, EquipmentSlot equipmentSlot, int i, int j, int k) {
        super(container, i, j, k);
        this.equipmentSlot = equipmentSlot;
    }

    @Override
    public boolean mayPlace(ItemStack itemStack) {
        var equipable = Equipable.get(itemStack);
        return equipable != null && equipmentSlot == equipable.getEquipmentSlot();
    }

    @Override
    public int getMaxStackSize() {
        return 1;
    }

    @Override
    public Pair<ResourceLocation, ResourceLocation> getNoItemIcon() {
        return Pair.of(InventoryMenu.BLOCK_ATLAS, TEXTURE_EMPTY_SLOTS.get(equipmentSlot));
    }
}
