package com.human.common.gameplay.item.grenade;

import com.human.common.gameplay.entity.projectile.ThrownGrenade;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.stats.Stats;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;

public class GrenadeItem extends Item {

    private final boolean isIncendiary;

    private final boolean isIrradiated;

    public GrenadeItem(boolean isIncendiary, boolean isIrradiated) {
        super(new Properties().stacksTo(16));
        this.isIncendiary = isIncendiary;
        this.isIrradiated = isIrradiated;
    }

    @Override
    public @NotNull InteractionResultHolder<ItemStack> use(Level level, Player player, @NotNull InteractionHand interactionHand) {
        var itemInHand = player.getItemInHand(interactionHand);
        // TODO: Change sound effect here.
        level.playSound(
            null,
            player.getX(),
            player.getY(),
            player.getZ(),
            SoundEvents.EGG_THROW,
            SoundSource.PLAYERS,
            0.5F,
            0.4F / (level.getRandom().nextFloat() * 0.4F + 0.8F)
        );

        if (!level.isClientSide) {
            var thrownGrenade = new ThrownGrenade(level, player);

            thrownGrenade.setItem(itemInHand);

            if (isIrradiated) {
                thrownGrenade.setIrradiated(true);
            }

            if (isIncendiary) {
                thrownGrenade.setIncendiary(true);
            }

            thrownGrenade.shootFromRotation(player, player.getXRot(), player.getYRot(), 0.0F, 1.0F, 1.0F);
            level.addFreshEntity(thrownGrenade);
        }

        player.awardStat(Stats.ITEM_USED.get(this));

        if (!player.getAbilities().instabuild) {
            itemInHand.shrink(1);
        }

        return InteractionResultHolder.sidedSuccess(itemInHand, level.isClientSide());
    }
}
