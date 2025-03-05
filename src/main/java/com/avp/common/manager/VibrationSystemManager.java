package com.avp.common.manager;

import mod.azure.azurelib.common.api.common.entities.AzureVibrationUser;
import mod.azure.azurelib.common.api.common.interfaces.AzureTicker;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntitySelector;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.monster.warden.AngerManagement;
import net.minecraft.world.level.gameevent.DynamicGameEventListener;
import net.minecraft.world.level.gameevent.vibrations.VibrationSystem;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collections;
import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.function.Predicate;

public class VibrationSystemManager implements VibrationSystem {

    protected AngerManagement angerManagement;

    private @Nullable Runnable angerManagementTickCallback;

    private final Mob mob;

    private final Predicate<Entity> targetPredicate;

    private final Data vibrationData;

    private final AzureVibrationUser vibrationUser;

    private final DynamicGameEventListener<Listener> dynamicGameEventListener;

    public VibrationSystemManager(Mob mob) {
        this(mob, DEFAULT_TARGET_PREDICATE_FACTORY.apply(mob));
    }

    public VibrationSystemManager(Mob mob, Predicate<Entity> targetPredicate) {
        this.angerManagement = new AngerManagement(targetPredicate, Collections.emptyList());
        this.dynamicGameEventListener = new DynamicGameEventListener<>(new Listener(this));
        this.mob = mob;
        this.targetPredicate = targetPredicate;
        this.vibrationUser = new AzureVibrationUser(mob, 2.5F, 32);
        this.vibrationData = new Data();
    }

    public void tick() {
        var level = mob.level();

        if (level instanceof ServerLevel serverLevel) {
            AzureTicker.tick(level, vibrationData, vibrationUser);

            if (mob.tickCount % 20 != 0) {
                return;
            }

            angerManagement.tick(serverLevel, targetPredicate);

            if (angerManagementTickCallback != null) {
                angerManagementTickCallback.run();
            }
        }
    }

    public void updateDynamicGameEventListener(@NotNull BiConsumer<DynamicGameEventListener<?>, ServerLevel> biConsumer) {
        if (mob.level() instanceof ServerLevel serverLevel) {
            biConsumer.accept(this.dynamicGameEventListener, serverLevel);
        }
    }

    public VibrationSystemManager setAngerManagementTickCallback(@Nullable Runnable angerManagementTickCallback) {
        this.angerManagementTickCallback = angerManagementTickCallback;
        return this;
    }

    @Override
    public @NotNull Data getVibrationData() {
        return vibrationData;
    }

    @Override
    public @NotNull User getVibrationUser() {
        return vibrationUser;
    }

    public int getActiveAnger() {
        return this.angerManagement.getActiveAnger(mob.getTarget());
    }

    public static final Function<Mob, Predicate<Entity>> DEFAULT_TARGET_PREDICATE_FACTORY = mob -> entity -> {
        if (!(entity instanceof LivingEntity livingEntity)) {
            return false;
        }

        return mob.level() == entity.level() &&
            EntitySelector.NO_CREATIVE_OR_SPECTATOR.test(entity) &&
            !mob.isAlliedTo(entity) &&
            livingEntity.getType() != EntityType.ARMOR_STAND &&
            livingEntity.getType() != EntityType.WARDEN
            && !livingEntity.isInvulnerable()
            && !livingEntity.isDeadOrDying()
            && mob.level().getWorldBorder().isWithinBounds(livingEntity.getBoundingBox());
    };
}
