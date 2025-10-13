package com.human.common.gameplay.entity.living.human.marine.ai.model;

import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.item.ItemEntity;

import com.avp.common.model.inventory.AVPInventory;

public sealed interface ItemTarget<T> {

    Location location();

    T strategy();

    double score();

    record Hands<T>(
        Location location,
        InteractionHand interactionHand,
        double score,
        T strategy
    ) implements ItemTarget<T> {

        public Hands(InteractionHand interactionHand, double score, T strategy) {
            this(Location.HANDS, interactionHand, score, strategy);
        }
    }

    record Inventory<T>(
        Location location,
        AVPInventory.Entry entry,
        double score,
        T strategy
    ) implements ItemTarget<T> {

        public Inventory(AVPInventory.Entry entry, double score, T strategy) {
            this(Location.INVENTORY, entry, score, strategy);
        }
    }

    record World<T>(
        Location location,
        ItemEntity itemEntity,
        double score,
        T strategy
    ) implements ItemTarget<T> {

        public World(ItemEntity itemEntity, double score, T strategy) {
            this(Location.WORLD, itemEntity, score, strategy);
        }
    }

    enum Location {
        HANDS,
        INVENTORY,
        WORLD,
        NONE
    }
}
