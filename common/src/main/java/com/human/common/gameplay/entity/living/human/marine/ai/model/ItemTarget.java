package com.human.common.gameplay.entity.living.human.marine.ai.model;

import com.human.common.gameplay.entity.living.human.marine.ai.fri.strategy.FRIStrategy;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.item.ItemEntity;

import com.avp.common.model.inventory.AVPInventory;

public sealed interface ItemTarget {

    Location location();

    FRIStrategy strategy();

    double score();

    record Hands(
        Location location,
        InteractionHand interactionHand,
        double score,
        FRIStrategy strategy
    ) implements ItemTarget {

        public Hands(InteractionHand interactionHand, double score, FRIStrategy strategy) {
            this(Location.HANDS, interactionHand, score, strategy);
        }
    }

    record Inventory(
        Location location,
        AVPInventory.Entry entry,
        double score,
        FRIStrategy strategy
    ) implements ItemTarget {

        public Inventory(AVPInventory.Entry entry, double score, FRIStrategy strategy) {
            this(Location.INVENTORY, entry, score, strategy);
        }
    }

    record World(
        Location location,
        ItemEntity itemEntity,
        double score,
        FRIStrategy strategy
    ) implements ItemTarget {

        public World(ItemEntity itemEntity, double score, FRIStrategy strategy) {
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
