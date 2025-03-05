package com.avp.core.common.menu.armor_case;

import net.minecraft.world.Container;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;

import com.avp.core.common.menu.MenuTypes;

public class ArmorCaseMenu extends AbstractContainerMenu {

    private static final int ARMOR_SLOT_COUNT = 4;

    private static final int PLAYER_INVENTORY_COLUMN_COUNT = 9;

    private static final int PLAYER_INVENTORY_ROW_COUNT = 3;

    private static final int SLOT_WIDTH = 18;

    private final Container container;

    public ArmorCaseMenu(int syncID, Inventory inventory) {
        this(syncID, inventory, new SimpleContainer(ARMOR_SLOT_COUNT));
    }

    public ArmorCaseMenu(int syncId, Inventory playerInventory, Container container) {
        super(MenuTypes.ARMOR_CASE.get(), syncId);
        this.container = container;
        container.startOpen(playerInventory.player);

        // Armor case inventory
        createSlotsForCustomInventory(container);
        // The player inventory
        createSlotsForPlayerInventory(playerInventory);
        createSlotsForPlayerHotbar(playerInventory);
    }

    @Override
    public @NotNull ItemStack quickMoveStack(Player player, int i) {
        return ItemStack.EMPTY;
    }

    @Override
    public boolean stillValid(Player player) {
        return container.stillValid(player);
    }

    private void createSlotsForCustomInventory(Container container) {
        var xOffset = 8 + SLOT_WIDTH;
        var yOffset = 48;

        for (var column = 0; column < ARMOR_SLOT_COUNT; column++) {
            var xPos = xOffset + column * SLOT_WIDTH * 2;
            var equipmentSlot = getEquipmentSlotForIndex(column);
            var slot = new ArmorCaseArmorSlot(container, equipmentSlot, column, xPos, yOffset);
            this.addSlot(slot);
        }
    }

    private EquipmentSlot getEquipmentSlotForIndex(int index) {
        return switch (index) {
            case 0 -> EquipmentSlot.HEAD;
            case 1 -> EquipmentSlot.CHEST;
            case 2 -> EquipmentSlot.LEGS;
            default -> EquipmentSlot.FEET;
        };
    }

    private void createSlotsForPlayerInventory(Inventory playerInventory) {
        var xOffset = 8;
        var yOffset = 84;
        var colCount = PLAYER_INVENTORY_COLUMN_COUNT;

        for (var row = 0; row < PLAYER_INVENTORY_ROW_COUNT; row++) {
            for (var column = 0; column < colCount; column++) {
                var slotIndex = column + row * colCount + colCount;
                var xPos = xOffset + column * SLOT_WIDTH;
                var yPos = yOffset + row * SLOT_WIDTH;
                var slot = new Slot(playerInventory, slotIndex, xPos, yPos);
                addSlot(slot);
            }
        }
    }

    private void createSlotsForPlayerHotbar(Inventory playerInventory) {
        var xOffset = 8;
        var yOffset = 142;

        for (var column = 0; column < PLAYER_INVENTORY_COLUMN_COUNT; column++) {
            var xPos = column * SLOT_WIDTH;
            var slot = new Slot(playerInventory, column, xPos + xOffset, yOffset);
            addSlot(slot);
        }
    }
}
