package com.avp.mixin;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ChorusFruitItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import com.avp.common.entity.living.Host;
import com.avp.common.entity.type.AVPEntityTypes;
import com.avp.common.util.AVPPredicates;

@Mixin(ChorusFruitItem.class)
public class MixinItem_ChorusEmbryo {

    @Inject(method = "finishUsingItem", at = @At("HEAD"), cancellable = true)
    private void removeEmbyro(ItemStack stack, Level level, LivingEntity livingEntity, CallbackInfoReturnable<ItemStack> cir) {
        if (!(livingEntity instanceof Host host)) {
            return;
        }

        if (!level.isClientSide() && stack.is(Items.CHORUS_FRUIT) && AVPPredicates.hasEmbryo(livingEntity)) {
            var parasite = AVPEntityTypes.CHESTBURSTER.create(level);

            if (parasite != null) {
                var randomX = livingEntity.getX() + (livingEntity.getRandom().nextDouble() - 0.5) * 16.0;
                var randomY = Mth.clamp(
                    livingEntity.getY() + (livingEntity.getRandom().nextInt(16) - 8),
                    level.getMinBuildHeight(),
                    (level.getMinBuildHeight() + ((ServerLevel) level).getLogicalHeight() - 1)
                );
                var randomZ = livingEntity.getZ() + (livingEntity.getRandom().nextDouble() - 0.5) * 16.0;
                parasite.setPos(randomX, randomY, randomZ);
                copyEntityTagData(livingEntity, parasite);
                level.addFreshEntity(parasite);
                // Copies effects from previous entity to the next
                for (var effect : livingEntity.getActiveEffects()) {
                    parasite.addEffect(new MobEffectInstance(effect));
                }
                if (livingEntity instanceof Player player) {
                    player.resetCurrentImpulseContext();
                    player.getCooldowns().addCooldown(stack.getItem(), 20);
                }
                host.clearParasiteSourceType();
                level.playSound(
                    null,
                    livingEntity.getX(),
                    livingEntity.getY(),
                    livingEntity.getZ(),
                    SoundEvents.CHORUS_FRUIT_TELEPORT,
                    SoundSource.PLAYERS
                );
                cir.setReturnValue(stack);
            }
        }
    }

    private void copyEntityTagData(Entity oldEntity, Entity newEntity) {
        // Step 1: Save old entity's data to a CompoundTag
        var oldEntityData = new CompoundTag();
        oldEntity.save(oldEntityData);

        // Removes specific fields that shouldn't be copied.
        oldEntityData.remove("id"); // Entity id shouldn't carry over since we're creating a new entity.
        oldEntityData.remove("UUID"); // Each entity must have a unique UUID.
        oldEntityData.remove("Pos"); // Position is set separately.
        oldEntityData.remove("Motion"); // Velocity is handled separately.
        oldEntityData.remove("Rotation"); // Rotation is set separately.
        oldEntityData.remove("Health"); // Health should be whatever the new entity's health is.
        oldEntityData.remove("attributes"); // New entity shouldn't take on attributes of the old entity.

        // Step 2: Load the data into the new entity
        newEntity.load(oldEntityData);
    }
}
