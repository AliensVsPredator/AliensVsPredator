package org.avp.api.item.weapon.shoot;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.item.ItemStack;

import java.util.Optional;

import org.avp.api.Holder;
import org.avp.api.item.weapon.WeaponItemData;

public abstract class ShootStrategy {

    public static Builder builder() {
        return new Builder();
    }

    private final Holder<SoundEvent> backgroundShootSound;

    private final int backgroundShootSoundFrequency;

    private final Holder<SoundEvent> windDownSound;

    private final Holder<SoundEvent> windUpSound;

    private final int windUpTimeInTicks;

    protected ShootStrategy(
        Holder<SoundEvent> backgroundShootSound,
        int backgroundShootSoundFrequency,
        Holder<SoundEvent> windDownSound,
        Holder<SoundEvent> windUpSound,
        int windUpTimeInTicks
    ) {
        this.backgroundShootSound = backgroundShootSound;
        this.backgroundShootSoundFrequency = backgroundShootSoundFrequency;
        this.windDownSound = windDownSound;
        this.windUpSound = windUpSound;
        this.windUpTimeInTicks = windUpTimeInTicks;
    }

    public abstract void shoot(ServerLevel serverLevel, ServerPlayer serverPlayer, ItemStack itemStack, WeaponItemData weaponItemData);

    public Optional<Holder<SoundEvent>> getBackgroundShootSound() {
        return Optional.ofNullable(backgroundShootSound);
    }

    public int getBackgroundShootSoundFrequency() {
        return backgroundShootSoundFrequency;
    }

    public Optional<Holder<SoundEvent>> getWindDownSound() {
        return Optional.ofNullable(windDownSound);
    }

    public Optional<Holder<SoundEvent>> getWindUpSound() {
        return Optional.ofNullable(windUpSound);
    }

    public int getWindUpTimeInTicks() {
        return windUpTimeInTicks;
    }

    public static class Builder {

        private Holder<SoundEvent> backgroundShootSound;

        private int backgroundShootSoundFrequency;

        private Holder<SoundEvent> windDownSound;

        private Holder<SoundEvent> windUpSound;

        private int windUpTimeInTicks;

        private Builder() {}

        public Builder setBackgroundShootSound(Holder<SoundEvent> backgroundShootSound, int backgroundShootSoundFrequency) {
            this.backgroundShootSound = backgroundShootSound;
            this.backgroundShootSoundFrequency = backgroundShootSoundFrequency;
            return this;
        }

        public Builder setWindingLogic(
            int windUpTimeInTicks,
            Holder<SoundEvent> windUpSound,
            Holder<SoundEvent> windDownSound
        ) {
            this.windUpTimeInTicks = windUpTimeInTicks;
            this.windUpSound = windUpSound;
            this.windDownSound = windDownSound;
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
