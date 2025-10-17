package com.human.common.gameplay.entity.living.human.marine.ai.model;

import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.item.ItemEntity;

import com.avp.common.model.inventory.AVPInventory;

public sealed interface ItemTarget {

    Location location();

    record Equipped(
        EquipmentSlot equipmentSlot
    ) implements ItemTarget {

        @Override
        public Location location() {
            return Location.EQUIPPED;
        }
    }

    record Inventory(
        AVPInventory.Entry entry
    ) implements ItemTarget {

        @Override
        public Location location() {
            return Location.INVENTORY;
        }
    }

    record World(
        ItemEntity itemEntity
    ) implements ItemTarget {

        @Override
        public Location location() {
            return Location.WORLD;
        }
    }

    enum None implements ItemTarget {

        INSTANCE;

        @Override
        public Location location() {
            return Location.NONE;
        }

        @Override
        public String toString() {
            return "NONE";
        }
    }

    enum Location {
        EQUIPPED,
        INVENTORY,
        WORLD,
        NONE
    }
}
