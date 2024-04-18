package org.avp.api.item.weapon.bullet.effect;

import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;

public class BulletEffects {

    public static final BulletEffect ACID = new BulletEffect("acid") {

        @Override
        public void applyEffect(Entity entity) {
            if (entity instanceof LivingEntity livingEntity) {
                // TODO: Add disintegration effect
            }
        }
    };

    public static final BulletEffect ARMOR_PENETRATION = new BulletEffect("penetration") {

        @Override
        public DamageSource getDamageSource(LivingEntity shootingEntity, Entity entity) {
            // TODO: This should return a custom damage source for armor penetrating bullets.
            return super.getDamageSource(shootingEntity, entity);
        }
    };

    public static final BulletEffect ELECTRIC = new BulletEffect("electric") {

        @Override
        public void applyEffect(Entity entity) {
            if (entity instanceof LivingEntity livingEntity) {
                livingEntity.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, 3 * 20, 1));
            }
        }
    };

    public static final BulletEffect EXPLOSIVE = new BulletEffect("explosive") {

        @Override
        public void applyEffect(Entity entity) {
            entity.level()
                .explode(
                    entity,
                    entity.getX(),
                    entity.getY() + entity.getBbHeight() / 2,
                    entity.getZ(),
                    1F,
                    Level.ExplosionInteraction.NONE
                );
        }
    };

    public static final BulletEffect INCENDIARY = new BulletEffect("incendiary") {

        @Override
        public void applyEffect(Entity entity) {
            entity.setSecondsOnFire(5);
        }
    };

    public static final BulletEffect STANDARD = new BulletEffect("standard") {};

    private BulletEffects() {
        throw new UnsupportedOperationException();
    }
}
