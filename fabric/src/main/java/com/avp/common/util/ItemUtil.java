package com.avp.common.util;

import com.bvanseg.just.functional.option.Option;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;

public class ItemUtil {

    public static Option<ItemEntity> drop(
        LivingEntity livingEntity,
        ItemStack itemStackToDrop,
        boolean dropAround,
        boolean includeThrowerName
    ) {
        if (itemStackToDrop.isEmpty()) {
            return Option.none();
        } else {
            if (livingEntity.level().isClientSide) {
                livingEntity.swing(InteractionHand.MAIN_HAND);
            }

            var d = livingEntity.getEyeY() - 0.3F;
            var itemEntity = new ItemEntity(livingEntity.level(), livingEntity.getX(), d, livingEntity.getZ(), itemStackToDrop);

            itemEntity.setPickUpDelay(40);

            if (includeThrowerName) {
                itemEntity.setThrower(livingEntity);
            }

            if (dropAround) {
                var f = livingEntity.getRandom().nextFloat() * 0.5F;
                var g = livingEntity.getRandom().nextFloat() * (float) (Math.PI * 2);
                itemEntity.setDeltaMovement(-Mth.sin(g) * f, 0.2F, Mth.cos(g) * f);
            } else {
                var g = Mth.sin(livingEntity.getXRot() * (float) (Math.PI / 180.0));
                var h = Mth.cos(livingEntity.getXRot() * (float) (Math.PI / 180.0));
                var i = Mth.sin(livingEntity.getYRot() * (float) (Math.PI / 180.0));
                var j = Mth.cos(livingEntity.getYRot() * (float) (Math.PI / 180.0));
                var k = livingEntity.getRandom().nextFloat() * (float) (Math.PI * 2);
                var l = 0.02F * livingEntity.getRandom().nextFloat();

                itemEntity.setDeltaMovement(
                    (double) (-i * h * 0.3F) + Math.cos(k) * (double) l,
                    -g * 0.3F + 0.1F + (livingEntity.getRandom().nextFloat() - livingEntity.getRandom().nextFloat()) * 0.1F,
                    (double) (j * h * 0.3F) + Math.sin(k) * (double) l
                );
            }

            return Option.some(itemEntity);
        }
    }
}
