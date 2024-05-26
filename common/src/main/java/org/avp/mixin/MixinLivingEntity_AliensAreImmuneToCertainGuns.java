package org.avp.mixin;

import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import org.avp.api.item.weapon.WeaponItemTagHelper;
import org.avp.api.item.weapon.bullet.effect.BulletEffects;
import org.avp.common.item.AbstractAVPWeaponItem;
import org.avp.common.sound.AVPSoundEvents;
import org.avp.common.tag.AVPDamageTypeTags;
import org.avp.common.tag.AVPEntityTypeTags;
import org.avp.common.tag.AVPItemTags;
import org.avp.common.util.MixinUtils;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(LivingEntity.class)
public abstract class MixinLivingEntity_AliensAreImmuneToCertainGuns extends Entity {

    protected MixinLivingEntity_AliensAreImmuneToCertainGuns(EntityType<?> entityType, Level level) {
        super(entityType, level);
    }

    @Inject(at = @At("HEAD"), cancellable = true, method = "hurt")
    void ignoresCertainGunDamage(DamageSource damageSource, float damage, CallbackInfoReturnable<Boolean> callbackInfoReturnable) {
        var self = MixinUtils.<LivingEntity>self(this);
        var type = self.getType();

        if (!type.is(AVPEntityTypeTags.ALIENS))
            return;

        if (!damageSource.is(AVPDamageTypeTags.IS_BULLET_PROJECTILE))
            return;

        var maybePlayer = damageSource.getEntity();

        if (!(maybePlayer instanceof LivingEntity livingEntity))
            return;

        var mainHandItemStack = livingEntity.getMainHandItem();
        var maybeWeapon = mainHandItemStack.getItem();

        if (!(maybeWeapon instanceof AbstractAVPWeaponItem weaponItem)) {
            return;
        }

        var weaponData = weaponItem.getWeaponItemData();
        var bulletEffects = WeaponItemTagHelper.getBulletEffects(mainHandItemStack, weaponData);

        // If the weapon's bullet effect was AP, the xenomorph's immunity is bypassed.
        if (bulletEffects.contains(BulletEffects.ARMOR_PENETRATION)) {
            return;
        }

        if (
            type.is(AVPEntityTypeTags.SMALL_GUNS_IMMUNE) && mainHandItemStack.is(AVPItemTags.SMALL_GUNS) ||
                type.is(AVPEntityTypeTags.MEDIUM_GUNS_IMMUNE) && mainHandItemStack.is(AVPItemTags.MEDIUM_GUNS) ||
                type.is(AVPEntityTypeTags.HEAVY_GUNS_IMMUNE) && mainHandItemStack.is(AVPItemTags.HEAVY_GUNS) ||
                type.is(AVPEntityTypeTags.UBER_GUNS_IMMUNE) && mainHandItemStack.is(AVPItemTags.UBER_GUNS)
        ) {
            self.playSound(AVPSoundEvents.INSTANCE.itemWeaponFxRicochetGeneric.get());
            callbackInfoReturnable.setReturnValue(false);
        }
    }
}
