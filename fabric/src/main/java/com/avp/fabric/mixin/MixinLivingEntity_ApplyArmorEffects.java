package com.avp.fabric.mixin;

import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.avp.fabric.common.item.AVPItemTags;
import com.avp.fabric.common.util.AVPPredicates;

@Mixin(LivingEntity.class)
public abstract class MixinLivingEntity_ApplyArmorEffects extends Entity {

    @Shadow
    protected abstract int increaseAirSupply(int airSupply);

    protected MixinLivingEntity_ApplyArmorEffects(EntityType<?> entityType, Level level) {
        super(entityType, level);
    }

    @Inject(at = @At("HEAD"), method = "tick")
    public void tick(CallbackInfo callbackInfo) {
        var self = LivingEntity.class.cast(this);

        var supplyAir = false;

        if (isWearingFullMK50SuitArmor(self)) {
            self.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, 5, 0, true, false, true));
            supplyAir = true;
        } else if (isWearingFullPressureSuitArmor(self)) {
            supplyAir = true;
        } else if (isWearingFullFireResistantArmor(self)) {
            self.addEffect(new MobEffectInstance(MobEffects.FIRE_RESISTANCE, 5, 0, true, false, true));
        } else if (isWearingFullPredatorArmor(self)) {
            self.addEffect(new MobEffectInstance(MobEffects.JUMP, 5, 0, true, false, true));
            self.addEffect(new MobEffectInstance(MobEffects.DAMAGE_BOOST, 5, 0, true, false, true));
        }

        if (supplyAir) {
            setAirSupply(increaseAirSupply(getAirSupply()));
        }
    }

    @Unique
    private boolean isWearingFullFireResistantArmor(LivingEntity self) {
        return AVPPredicates.hasFullArmorSetMatching(self, (itemStack -> itemStack.is(AVPItemTags.FIRE_RESISTANT_ARMOR)));
    }

    @Unique
    private boolean isWearingFullPredatorArmor(LivingEntity self) {
        return AVPPredicates.hasFullArmorSetMatching(self, (itemStack -> itemStack.is(AVPItemTags.PREDATOR_ARMOR)));
    }

    @Unique
    private boolean isWearingFullMK50SuitArmor(LivingEntity self) {
        return AVPPredicates.hasFullArmorSetMatching(self, (itemStack -> itemStack.is(AVPItemTags.MK50_ARMOR)));
    }

    @Unique
    private boolean isWearingFullPressureSuitArmor(LivingEntity self) {
        return AVPPredicates.hasFullArmorSetMatching(self, (itemStack -> itemStack.is(AVPItemTags.PRESSURE_ARMOR)));
    }
}
