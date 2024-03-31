package org.avp.api.item.weapon;

import net.minecraft.sounds.SoundEvent;
import org.avp.api.item.weapon.ammo.AmmunitionStrategy;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

import org.avp.api.GameObject;
import org.avp.api.item.weapon.reload.ReloadStrategy;

/**
 * @author Boston Vanseghi
 */
public class WeaponItemData {

    private final float accuracy;

    private final AmmunitionStrategy ammunitionStrategy;

    private final WeaponDamageType weaponDamageType;

    private final List<FireMode> fireModes;

    private final float damage;

    private final float knockback;

    private final int maxAmmunition;

    private final int reloadTimeInTicks;

    private final GameObject<SoundEvent> fireFailSound;

    private final GameObject<SoundEvent> reloadFinishSound;

    private final GameObject<SoundEvent> reloadStartSound;

    private final ReloadStrategy reloadStrategy;

    private final int windUpTimeInTicks;

    public WeaponItemData(
        float accuracy,
        AmmunitionStrategy ammunitionStrategy,
        @NotNull WeaponDamageType weaponDamageType,
        @NotNull List<FireMode> fireModes,
        float damage,
        float knockback,
        int maxAmmunition,
        int reloadTimeInTicks,
        @NotNull GameObject<SoundEvent> fireFailSound,
        GameObject<SoundEvent> reloadFinishSound,
        @NotNull GameObject<SoundEvent> reloadStartSound,
        @NotNull ReloadStrategy reloadStrategy,
        int windUpTimeInTicks
    ) {
        if (fireModes.isEmpty()) {
            throw new IllegalStateException("Weapons must have at least 1 fire mode!");
        }

        this.accuracy = Math.max(accuracy, 0);
        this.ammunitionStrategy = ammunitionStrategy;
        this.weaponDamageType = weaponDamageType;
        this.fireModes = fireModes;
        this.damage = Math.max(damage, 0);
        this.knockback = Math.max(knockback, 0);
        this.maxAmmunition = Math.max(maxAmmunition, 0);
        this.reloadTimeInTicks = Math.max(reloadTimeInTicks, 0);
        this.fireFailSound = fireFailSound;
        this.reloadFinishSound = reloadFinishSound;
        this.reloadStartSound = reloadStartSound;
        this.reloadStrategy = reloadStrategy;
        this.windUpTimeInTicks = windUpTimeInTicks;
    }

    public FireMode getFireMode(String identifier) {
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

    public WeaponDamageType getBulletDamageType() {
        return weaponDamageType;
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

    public int getMaxAmmunition() {
        return maxAmmunition;
    }

    public int getReloadTimeInTicks() {
        return reloadTimeInTicks;
    }

    public GameObject<SoundEvent> getFireFailSound() {
        return fireFailSound;
    }

    public Optional<GameObject<SoundEvent>> getReloadFinishSound() {
        return Optional.ofNullable(reloadFinishSound);
    }

    public GameObject<SoundEvent> getReloadStartSound() {
        return reloadStartSound;
    }

    public ReloadStrategy getReloadStrategy() {
        return reloadStrategy;
    }

    public int getWindUpTimeInTicks() {
        return windUpTimeInTicks;
    }
}
