package org.avp.api.item.weapon.reload;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.item.ItemStack;

import java.util.Optional;

import org.avp.api.Holder;
import org.avp.api.item.weapon.WeaponItemData;

public abstract class ReloadStrategy {

    public static Builder builder(int reloadTimeInTicks) {
        return new Builder(reloadTimeInTicks);
    }

    private final Holder<SoundEvent> reloadFinishSound;

    private final Holder<SoundEvent> reloadStartSound;

    private final int reloadTimeInTicks;

    protected ReloadStrategy(Holder<SoundEvent> reloadFinishSound, Holder<SoundEvent> reloadStartSound, int reloadTimeInTicks) {
        this.reloadFinishSound = reloadFinishSound;
        this.reloadStartSound = reloadStartSound;
        this.reloadTimeInTicks = reloadTimeInTicks;
    }

    public abstract void tryReload(ServerLevel serverLevel, ServerPlayer serverPlayer, ItemStack itemStack, WeaponItemData weaponItemData);

    public Optional<Holder<SoundEvent>> getReloadFinishSound() {
        return Optional.ofNullable(reloadFinishSound);
    }

    public Holder<SoundEvent> getReloadStartSound() {
        return reloadStartSound;
    }

    public int getReloadTimeInTicks() {
        return reloadTimeInTicks;
    }

    public static class Builder {

        private Holder<SoundEvent> reloadFinishSound;

        private Holder<SoundEvent> reloadStartSound;

        private final int reloadTimeInTicks;

        private TryReloadBehavior tryReloadBehavior;

        private Builder(int reloadTimeInTicks) {
            this.reloadTimeInTicks = reloadTimeInTicks;
            this.tryReloadBehavior = TryReloadBehavior.NO_OP;
        }

        public Builder setReloadFinishSound(Holder<SoundEvent> reloadFinishSound) {
            this.reloadFinishSound = reloadFinishSound;
            return this;
        }

        public Builder setReloadStartSound(Holder<SoundEvent> reloadStartSound) {
            this.reloadStartSound = reloadStartSound;
            return this;
        }

        public Builder setTryReloadBehavior(TryReloadBehavior tryReloadBehavior) {
            this.tryReloadBehavior = tryReloadBehavior;
            return this;
        }

        public ReloadStrategy build() {
            return new ReloadStrategy(reloadFinishSound, reloadStartSound, reloadTimeInTicks) {

                @Override
                public void tryReload(
                    ServerLevel serverLevel,
                    ServerPlayer serverPlayer,
                    ItemStack itemStack,
                    WeaponItemData weaponItemData
                ) {
                    tryReloadBehavior.tryReload(serverLevel, serverPlayer, itemStack, weaponItemData);
                }
            };
        }
    }
}
