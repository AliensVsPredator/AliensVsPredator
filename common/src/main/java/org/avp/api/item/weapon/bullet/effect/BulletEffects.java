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

        @Override
        public int getColor() {
            return 0xFFBAB728;
        }
    };

    public static final BulletEffect ARMOR_PENETRATION = new BulletEffect("penetration") {

        @Override
        public DamageSource getDamageSource(LivingEntity shootingEntity, Entity entity) {
            // TODO: This should return a custom damage source for armor penetrating bullets.
            return super.getDamageSource(shootingEntity, entity);
        }

        @Override
        public void applyEffect(Entity entity) {/*NO-OP*/}

        @Override
        public int getColor() {
            return 0xFF62428C;
        }
    };

    public static final BulletEffect ELECTRIC = new BulletEffect("electric") {

        @Override
        public void applyEffect(Entity entity) {
            if (entity instanceof LivingEntity livingEntity) {
                livingEntity.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, 3 * 20, 1));
            }
        }

        @Override
        public int getColor() {
            return 0xFF335DC1;
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

        @Override
        public int getColor() {
            return 0xFFB12615;
        }
    };

    public static final BulletEffect INCENDIARY = new BulletEffect("incendiary") {

        @Override
        public void applyEffect(Entity entity) {
            entity.setSecondsOnFire(5);
        }

        @Override
        public int getColor() {
            return 0xFFFCA100;
        }
    };

    public static final BulletEffect STANDARD = new BulletEffect("standard") {
        @Override
        public void applyEffect(Entity entity) {/*NO-OP*/}

        @Override
        public int getColor() {
            return 0;
        }
    };

    private BulletEffects() {
        throw new UnsupportedOperationException();
    }
}
