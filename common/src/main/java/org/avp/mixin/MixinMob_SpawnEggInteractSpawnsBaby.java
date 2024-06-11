package org.avp.mixin;

import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.SpawnEggItem;
import net.minecraft.world.level.Level;
import org.avp.api.entity.Morphable;
import org.avp.api.util.TypeUtil;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Objects;

@Mixin(Mob.class)
public abstract class MixinMob_SpawnEggInteractSpawnsBaby extends LivingEntity {

    protected MixinMob_SpawnEggInteractSpawnsBaby(EntityType<? extends LivingEntity> entityType, Level level) {
        super(entityType, level);
    }

    @Inject(at = @At("HEAD"), method = "mobInteract", cancellable = true)
    void mobInteract(Player player, InteractionHand interactionHand, CallbackInfoReturnable<InteractionResult> callbackInfoReturnable) {
        var self = TypeUtil.<Mob>self(this);
        var level = self.level();

        if (level.isClientSide) {
            return;
        }

        if (!(self instanceof Morphable morphable)) {
            return;
        }

        var previousFormEntityType = morphable.getEntityTypeForPreviousForm();
        var itemStack = player.getItemInHand(interactionHand);

        if (!(itemStack.getItem() instanceof SpawnEggItem spawnEggItem)) {
            return;
        }

        var type = spawnEggItem.getType(itemStack.getTag());

        if (!Objects.equals(self.getType(), type)) {
            return;
        }

        var previousFormEntity = previousFormEntityType.create(level);

        if (previousFormEntity != null) {
            previousFormEntity.moveTo(self.blockPosition(), self.getYRot(), self.getXRot());
            level.addFreshEntity(previousFormEntity);
        }
    }
}
