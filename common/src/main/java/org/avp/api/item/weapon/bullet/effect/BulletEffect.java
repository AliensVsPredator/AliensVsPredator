package org.avp.api.item.weapon.bullet.effect;

import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;

public abstract class BulletEffect {

    private final String identifier;

    protected BulletEffect(String identifier) {
        this.identifier = identifier;
    }

    public abstract int getColor();

    public abstract void applyEffect(Entity entity);

    public DamageSource getDamageSource(LivingEntity shootingEntity, Entity entity) {
        return shootingEntity instanceof Player player
            ? entity.damageSources().playerAttack(player)
            : entity.damageSources().mobAttack(shootingEntity);
    }

    public String getIdentifier() {
        return identifier;
    }
}
