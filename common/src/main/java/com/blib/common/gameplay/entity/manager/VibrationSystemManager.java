package com.blib.common.gameplay.entity.manager;

import mod.azure.azurelib.common.vibration.AzureTicker;
import mod.azure.azurelib.common.vibration.AzureVibrationUser;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.level.gameevent.DynamicGameEventListener;
import net.minecraft.world.level.gameevent.vibrations.VibrationSystem;
import org.jetbrains.annotations.NotNull;

import java.util.function.BiConsumer;

public class VibrationSystemManager implements VibrationSystem {

    private final Mob mob;

    private final Data vibrationData;

    private final AzureVibrationUser vibrationUser;

    private final DynamicGameEventListener<Listener> dynamicGameEventListener;

    public VibrationSystemManager(Mob mob, float speed, int range) {
        this.dynamicGameEventListener = new DynamicGameEventListener<>(new Listener(this));
        this.mob = mob;
        this.vibrationUser = new AzureVibrationUser(mob, speed, range);
        this.vibrationData = new Data();
    }

    public void tick() {
        AzureTicker.tick(mob.level(), vibrationData, vibrationUser);
    }

    public void updateDynamicGameEventListener(@NotNull BiConsumer<DynamicGameEventListener<?>, ServerLevel> biConsumer) {
        if (mob.level() instanceof ServerLevel serverLevel) {
            biConsumer.accept(this.dynamicGameEventListener, serverLevel);
        }
    }

    @Override
    public @NotNull Data getVibrationData() {
        return vibrationData;
    }

    @Override
    public @NotNull User getVibrationUser() {
        return vibrationUser;
    }
}
