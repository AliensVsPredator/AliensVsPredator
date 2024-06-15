package org.avp.mixin;

import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.boss.EnderDragonPart;
import net.minecraft.world.entity.boss.enderdragon.EnderDragon;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

import org.avp.common.data.tag.AVPDamageTypeTags;

@Mixin(EnderDragon.class)
public abstract class MixinEnderDragon_ReduceDamageFromBullets extends Mob {

    protected MixinEnderDragon_ReduceDamageFromBullets(EntityType<? extends Mob> entityType, Level level) {
        super(entityType, level);
    }

    @ModifyVariable(
        method = "hurt(Lnet/minecraft/world/entity/boss/EnderDragonPart;Lnet/minecraft/world/damagesource/DamageSource;F)Z",
        at = @At("HEAD"),
        argsOnly = true
    )
    float hurt(float damage, EnderDragonPart enderDragonPart, DamageSource damageSource) {
        if (damageSource.is(AVPDamageTypeTags.IS_BULLET_PROJECTILE)) {
            return 1F;
        }

        return damage;
    }
}
