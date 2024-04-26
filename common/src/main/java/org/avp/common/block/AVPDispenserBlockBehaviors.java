package org.avp.common.block;

import net.minecraft.core.Direction;
import net.minecraft.core.dispenser.BlockSource;
import net.minecraft.core.dispenser.DefaultDispenseItemBehavior;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.SpawnEggItem;
import net.minecraft.world.level.block.DispenserBlock;
import net.minecraft.world.level.gameevent.GameEvent;
import org.avp.common.item.AVPSpawnEggItems;
import org.jetbrains.annotations.NotNull;

public class AVPDispenserBlockBehaviors {
    public static final AVPDispenserBlockBehaviors INSTANCE = new AVPDispenserBlockBehaviors();

    private AVPDispenserBlockBehaviors() {}

    public void registerAll() {
        // Borrowed from Mojang's own dispenser behavior logic for vanilla spawn eggs.
        DefaultDispenseItemBehavior defaultdispenseitembehavior = new DefaultDispenseItemBehavior() {
            @Override
            public @NotNull ItemStack execute(BlockSource blockSource, ItemStack itemStack) {
                Direction direction = blockSource.state().getValue(DispenserBlock.FACING);
                EntityType<?> entitytype = ((SpawnEggItem) itemStack.getItem()).getType(itemStack.getTag());

                try {
                    entitytype.spawn(
                        blockSource.level(), itemStack, null, blockSource.pos().relative(direction), MobSpawnType.DISPENSER, direction != Direction.UP, false
                    );
                } catch (Exception exception) {
                    LOGGER.error("Error while dispensing spawn egg from dispenser at {}", blockSource.pos(), exception);
                    return ItemStack.EMPTY;
                }

                itemStack.shrink(1);
                blockSource.level().gameEvent(null, GameEvent.ENTITY_PLACE, blockSource.pos());
                return itemStack;
            }
        };

        AVPSpawnEggItems.INSTANCE.getEntries().forEach(itemHolder -> DispenserBlock.registerBehavior(itemHolder.get(), defaultdispenseitembehavior));
    }
}
