package com.blib.common.gameplay;

import com.blib.BLib;
import net.minecraft.core.Direction;
import net.minecraft.core.dispenser.BlockSource;
import net.minecraft.core.dispenser.DefaultDispenseItemBehavior;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.SpawnEggItem;
import net.minecraft.world.level.block.DispenserBlock;
import net.minecraft.world.level.gameevent.GameEvent;
import org.jetbrains.annotations.NotNull;

public class DefaultDispenseSpawnEggItemBehavior extends DefaultDispenseItemBehavior {

    public static final DefaultDispenseSpawnEggItemBehavior INSTANCE = new DefaultDispenseSpawnEggItemBehavior();

    private DefaultDispenseSpawnEggItemBehavior() {}

    @Override
    public @NotNull ItemStack execute(@NotNull BlockSource blockSource, @NotNull ItemStack itemStack) {
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
            BLib.LOGGER.error("Error while dispensing spawn egg from dispenser at {}", blockSource.pos(), exception);
            return ItemStack.EMPTY;
        }

        itemStack.shrink(1);
        blockSource.level().gameEvent(null, GameEvent.ENTITY_PLACE, blockSource.pos());
        return itemStack;
    }
}
