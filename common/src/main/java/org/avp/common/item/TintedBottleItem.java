package org.avp.common.item;

import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.stats.Stats;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.ProjectileUtil;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemUtils;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.HitResult;
import org.jetbrains.annotations.NotNull;

import org.avp.common.entity.Acid;

public class TintedBottleItem extends Item {

    public TintedBottleItem(Properties properties) {
        super(properties);
    }

    @Override
    public @NotNull InteractionResultHolder<ItemStack> use(
        @NotNull Level level,
        @NotNull Player player,
        @NotNull InteractionHand interactionHand
    ) {
        var hitResult = ProjectileUtil.getHitResultOnViewVector(player, Acid.class::isInstance, 4F);

        if (hitResult.getType() == HitResult.Type.ENTITY) {
            level.playSound(
                player,
                player.getX(),
                player.getY(),
                player.getZ(),
                SoundEvents.BOTTLE_FILL,
                SoundSource.NEUTRAL,
                1,
                1
            );
            ItemStack itemstack = player.getItemInHand(interactionHand);
            return InteractionResultHolder.sidedSuccess(
                this.turnBottleIntoItem(itemstack, player, new ItemStack(AVPItems.INSTANCE.bottleTintedAcid.get())),
                level.isClientSide()
            );
        }

        return super.use(level, player, interactionHand);
    }

    protected ItemStack turnBottleIntoItem(ItemStack bottle, Player player, ItemStack filledBottle) {
        player.awardStat(Stats.ITEM_USED.get(this));
        return ItemUtils.createFilledResult(bottle, player, filledBottle);
    }
}
