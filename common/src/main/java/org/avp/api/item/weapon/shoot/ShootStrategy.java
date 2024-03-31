package org.avp.api.item.weapon.shoot;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.item.ItemStack;
import org.avp.api.GameObject;
import org.avp.api.item.weapon.WeaponItemData;
import org.avp.api.item.weapon.reload.TryReloadBehavior;

import java.util.Optional;

public abstract class ShootStrategy {

    public static Builder builder() {
        return new Builder();
    }

    private final GameObject<SoundEvent> backgroundShootSound;
    private final int backgroundShootSoundFrequency;

    private final GameObject<SoundEvent> windDownSound;

    private final GameObject<SoundEvent> windUpSound;

    private final int windUpTimeInTicks;

    protected ShootStrategy(
        GameObject<SoundEvent> backgroundShootSound,
        int backgroundShootSoundFrequency,
        GameObject<SoundEvent> windDownSound,
        GameObject<SoundEvent> windUpSound,
        int windUpTimeInTicks
    ) {
        this.backgroundShootSound = backgroundShootSound;
        this.backgroundShootSoundFrequency = backgroundShootSoundFrequency;
        this.windDownSound = windDownSound;
        this.windUpSound = windUpSound;
        this.windUpTimeInTicks = windUpTimeInTicks;
    }

    public abstract void shoot(ServerLevel serverLevel, ServerPlayer serverPlayer, ItemStack itemStack, WeaponItemData weaponItemData);

    public Optional<GameObject<SoundEvent>> getBackgroundShootSound() {
        return Optional.ofNullable(backgroundShootSound);
    }

    public int getBackgroundShootSoundFrequency() {
        return backgroundShootSoundFrequency;
    }

    public Optional<GameObject<SoundEvent>> getWindDownSound() {
        return Optional.ofNullable(windDownSound);
    }

    public Optional<GameObject<SoundEvent>> getWindUpSound() {
        return Optional.ofNullable(windUpSound);
    }

    public int getWindUpTimeInTicks() {
        return windUpTimeInTicks;
    }

    public static class Builder {

        private GameObject<SoundEvent> backgroundShootSound;

        private int backgroundShootSoundFrequency;

        private GameObject<SoundEvent> windDownSound;

        private GameObject<SoundEvent> windUpSound;

        private int windUpTimeInTicks;

        private Builder() {
        }

        public Builder setBackgroundShootSound(GameObject<SoundEvent> backgroundShootSound, int backgroundShootSoundFrequency) {
            this.backgroundShootSound = backgroundShootSound;
            this.backgroundShootSoundFrequency = backgroundShootSoundFrequency;
            return this;
        }

        public Builder setWindDownSound(GameObject<SoundEvent> windDownSound) {
            this.windDownSound = windDownSound;
            return this;
        }

        public Builder setWindUpSound(GameObject<SoundEvent> windUpSound) {
            this.windUpSound = windUpSound;
            return this;
        }

        public Builder setWindUpTimeInTicks(int windUpTimeInTicks) {
            this.windUpTimeInTicks = windUpTimeInTicks;
            return this;
        }

        public ShootStrategy build() {
            return new ShootStrategy(backgroundShootSound, backgroundShootSoundFrequency, windDownSound, windUpSound, windUpTimeInTicks) {
                @Override
                public void shoot(ServerLevel serverLevel, ServerPlayer serverPlayer, ItemStack itemStack, WeaponItemData weaponItemData) {
                    // TODO:
                }
            };
        }
    }
}
