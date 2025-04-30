package com.avp.common.block;

import net.minecraft.core.Direction;
import net.minecraft.core.dispenser.BlockSource;
import net.minecraft.core.dispenser.DefaultDispenseItemBehavior;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.SpawnEggItem;
import net.minecraft.world.level.block.DispenserBlock;
import net.minecraft.world.level.gameevent.GameEvent;
import org.jetbrains.annotations.NotNull;

import com.avp.AVP;
import com.avp.common.item.SpawnEggItems;

public class DispenserBlockBehaviors {

    public static void initialize() {
        // Borrowed from Mojang's own dispenser behavior logic for vanilla spawn eggs.
        var defaultDispenseItemBehavior = new DefaultDispenseItemBehavior() {

            @Override
            public @NotNull ItemStack execute(BlockSource blockSource, ItemStack itemStack) {
                var direction = blockSource.state().getValue(DispenserBlock.FACING);
                var entitytype = ((SpawnEggItem) itemStack.getItem()).getType(itemStack);

                try {
                    entitytype.spawn(
                        blockSource.level(),
                        itemStack,
                        null,
                        blockSource.pos().relative(direction),
                        MobSpawnType.DISPENSER,
                        direction != Direction.UP,
                        false
                    );
                } catch (Exception exception) {
                    AVP.LOGGER.error("Error while dispensing spawn egg from dispenser at {}", blockSource.pos(), exception);
                    return ItemStack.EMPTY;
                }

                itemStack.shrink(1);
                blockSource.level().gameEvent(null, GameEvent.ENTITY_PLACE, blockSource.pos());
                return itemStack;
            }
        };

        SpawnEggItems.getAll()
            .forEach(spawnEggItem -> DispenserBlock.registerBehavior(spawnEggItem, defaultDispenseItemBehavior));
    }

    private DispenserBlockBehaviors() {
        throw new UnsupportedOperationException();
    }
}
