package org.avp.api.item.weapon.reload;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.item.ItemStack;

import org.avp.api.GameObject;
import org.avp.api.item.weapon.WeaponItemData;

import java.util.Optional;

public abstract class ReloadStrategy {

    public static Builder builder(int reloadTimeInTicks) {
        return new Builder(reloadTimeInTicks);
    }

    private final GameObject<SoundEvent> reloadFinishSound;

    private final GameObject<SoundEvent> reloadStartSound;

    private final int reloadTimeInTicks;

    protected ReloadStrategy(GameObject<SoundEvent> reloadFinishSound, GameObject<SoundEvent> reloadStartSound, int reloadTimeInTicks) {
        this.reloadFinishSound = reloadFinishSound;
        this.reloadStartSound = reloadStartSound;
        this.reloadTimeInTicks = reloadTimeInTicks;
    }

    public abstract void tryReload(ServerLevel serverLevel, ServerPlayer serverPlayer, ItemStack itemStack, WeaponItemData weaponItemData);

    public Optional<GameObject<SoundEvent>> getReloadFinishSound() {
        return Optional.ofNullable(reloadFinishSound);
    }

    public GameObject<SoundEvent> getReloadStartSound() {
        return reloadStartSound;
    }

    public int getReloadTimeInTicks() {
        return reloadTimeInTicks;
    }

    public static class Builder {

        private GameObject<SoundEvent> reloadFinishSound;

        private GameObject<SoundEvent> reloadStartSound;

        private final int reloadTimeInTicks;

        private TryReloadBehavior tryReloadBehavior;

        private Builder(int reloadTimeInTicks) {
            this.reloadTimeInTicks = reloadTimeInTicks;
            this.tryReloadBehavior = TryReloadBehavior.NO_OP;
        }

        public Builder setReloadFinishSound(GameObject<SoundEvent> reloadFinishSound) {
            this.reloadFinishSound = reloadFinishSound;
            return this;
        }

        public Builder setReloadStartSound(GameObject<SoundEvent> reloadStartSound) {
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
                public void tryReload(ServerLevel serverLevel, ServerPlayer serverPlayer, ItemStack itemStack, WeaponItemData weaponItemData) {
                    tryReloadBehavior.tryReload(serverLevel, serverPlayer, itemStack, weaponItemData);
                }
            };
        }
    }
}
