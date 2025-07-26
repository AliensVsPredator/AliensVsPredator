package com.avp.mixin;

import com.lib.common.gameplay.gene.GeneOperationType;
import com.lib.common.gameplay.gene.Genes;
import com.lib.common.model.GeneCarrier;
import net.minecraft.world.Difficulty;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Mob.class)
public abstract class MixinMob_GeneCarrier extends LivingEntity implements GeneCarrier {

    public MixinMob_GeneCarrier(EntityType<? extends Mob> entityType, Level level) {
        super(entityType, level);
    }

    @Inject(at = @At("RETURN"), method = "doHurtTarget")
    private void avp$doHurtTarget(Entity target, CallbackInfoReturnable<Boolean> cir) {
        var didHurt = cir.getReturnValueZ();

        if (!didHurt) {
            return;
        }

        var poisonChance = getOrCreateGeneManager().getGeneContainer()
            .getActiveGeneMap()
            .getValue(Genes.POISON, GeneOperationType.MULTIPLICATIVE);

        if (getRandom().nextDouble() < poisonChance && target instanceof LivingEntity livingEntity) {
            var seconds = 0;

            if (level().getDifficulty() == Difficulty.NORMAL) {
                seconds = 7;
            } else if (level().getDifficulty() == Difficulty.HARD) {
                seconds = 15;
            }

            if (seconds > 0) {
                livingEntity.addEffect(new MobEffectInstance(MobEffects.POISON, seconds * 20, 0), this);
            }
        }
    }
}
