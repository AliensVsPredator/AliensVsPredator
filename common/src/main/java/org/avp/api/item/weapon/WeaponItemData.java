package org.avp.api.item.weapon;

import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Objects;

import org.avp.api.item.weapon.ammunition.AmmunitionStrategy;
import org.avp.api.item.weapon.reload.ReloadStrategy;
import org.avp.api.item.weapon.shoot.ShootStrategy;

public class WeaponItemData {

    private final float accuracy;

    private final AmmunitionStrategy ammunitionStrategy;

    private final int durability;

    private final List<FireMode> fireModes;

    private final float damage;

    private final float knockback;

    private final ReloadStrategy reloadStrategy;

    private final ShootStrategy shootStrategy;

    public WeaponItemData(
        float accuracy,
        AmmunitionStrategy ammunitionStrategy,
        int durability,
        @NotNull List<FireMode> fireModes,
        float damage,
        float knockback,
        @NotNull ReloadStrategy reloadStrategy,
        ShootStrategy shootStrategy
    ) {
        if (fireModes.isEmpty()) {
            throw new IllegalStateException("Weapons must have at least 1 fire mode!");
        }

        this.accuracy = Math.max(accuracy, 0);
        this.ammunitionStrategy = ammunitionStrategy;
        this.durability = Math.max(durability, 0);
        this.fireModes = fireModes;
        this.damage = Math.max(damage, 0);
        this.knockback = Math.max(knockback, 0);
        this.reloadStrategy = reloadStrategy;
        this.shootStrategy = shootStrategy;
    }

    public FireMode getFireModeByIdOrFirst(String identifier) {
        return this.getFireModes()
            .stream()
            .filter(mode -> Objects.equals(mode.identifier(), identifier))
            .findFirst()
            .orElse(this.getFireModes().get(0));
    }

    public float getAccuracy() {
        return accuracy;
    }

    public AmmunitionStrategy getAmmunitionStrategy() {
        return ammunitionStrategy;
    }

    public int getDurability() {
        return durability;
    }

    public List<FireMode> getFireModes() {
        return fireModes;
    }

    public float getDamage() {
        return damage;
    }

    public float getKnockback() {
        return knockback;
    }

    public ReloadStrategy getReloadStrategy() {
        return reloadStrategy;
    }

    public ShootStrategy getShootStrategy() {
        return shootStrategy;
    }
}
