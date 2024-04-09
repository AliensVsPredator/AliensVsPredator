package org.avp.common.item;

import mod.azure.azurelib.common.api.common.animatable.GeoItem;
import mod.azure.azurelib.common.internal.client.RenderProvider;
import mod.azure.azurelib.common.internal.common.core.animatable.instance.AnimatableInstanceCache;
import mod.azure.azurelib.common.internal.common.core.animation.AnimatableManager;
import mod.azure.azurelib.common.internal.common.util.AzureLibUtil;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.ProjectileUtil;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemUtils;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;
import java.util.function.Consumer;
import java.util.function.Supplier;

import org.avp.api.GameObject;
import org.avp.api.Tuple;
import org.avp.api.item.weapon.FireMode;
import org.avp.api.item.weapon.WeaponItemData;
import org.avp.api.item.weapon.WeaponItemTagHelper;
import org.avp.client.render.item.AVPWeaponItemRenderers;
import org.avp.common.config.AVPConfig;
import org.avp.common.network.payload.ClientboundBulletHitBlockPayload;
import org.avp.common.service.Services;
import org.avp.common.util.SoundUtilities;
import org.avp.common.util.TimeUtilities;
import org.avp.server.BlockBreakProgressManager;

public abstract class AbstractAVPWeaponItem extends Item implements GeoItem {

    private static final int START_TICK_PROGRESS = Integer.MAX_VALUE;

    private final AnimatableInstanceCache cache = AzureLibUtil.createInstanceCache(this);

    private final Supplier<Object> renderProvider = GeoItem.makeRenderer(this);

    private final WeaponItemData weaponItemData;

    protected AbstractAVPWeaponItem(Properties properties, WeaponItemData weaponItemData) {
        super(properties.stacksTo(1));
        this.weaponItemData = weaponItemData;
    }

    @Override
    public void releaseUsing(@NotNull ItemStack itemStack, @NotNull Level level, @NotNull LivingEntity livingEntity, int i) {
        var shootingStrategy = weaponItemData.getShootStrategy();
        var windDownSoundOptional = shootingStrategy.getWindDownSound();

        windDownSoundOptional.ifPresent(
            windDownSound -> level.playSound(null, livingEntity.blockPosition(), windDownSound.get(), SoundSource.PLAYERS)
        );

        super.releaseUsing(itemStack, level, livingEntity, i);
    }

    @Override
    public void onUseTick(
        @NotNull Level level,
        @NotNull LivingEntity livingEntity,
        @NotNull ItemStack itemStack,
        int tickCountdown
    ) {
        var tickProgress = START_TICK_PROGRESS - tickCountdown;
        var positiveTickProgress = Math.abs(tickProgress);
        var isFirstTick = positiveTickProgress == 0;

        if (!(livingEntity instanceof Player player))
            return;
        if (player.getCooldowns().isOnCooldown(this))
            return;

        var shootStrategy = weaponItemData.getShootStrategy();
        var windUpSoundOptional = shootStrategy.getWindUpSound();

        if (isFirstTick) {
            windUpSoundOptional.ifPresent(
                windUpSound -> level.playSound(null, player.blockPosition(), windUpSound.get(), SoundSource.PLAYERS)
            );
        }

        var windUpTimeInTicks = shootStrategy.getWindUpTimeInTicks();
        if (positiveTickProgress < windUpTimeInTicks)
            return;

        var fireMode = WeaponItemTagHelper.getFireMode(itemStack, weaponItemData);

        if (!level.isClientSide) {
            var serverLevel = (ServerLevel) level;
            var serverPlayer = (ServerPlayer) player;

            // Background sound that fires as long as the trigger is pulled, regardless if ammo is present.
            var backgroundShootSoundFrequency = shootStrategy.getBackgroundShootSoundFrequency();
            var backgroundShootSoundOptional = shootStrategy.getBackgroundShootSound();

            backgroundShootSoundOptional.ifPresent(backgroundShootSound -> {
                if (
                    positiveTickProgress == windUpTimeInTicks || (positiveTickProgress + windUpTimeInTicks)
                        % backgroundShootSoundFrequency == 0
                ) {
                    level.playSound(null, player.blockPosition(), backgroundShootSound.get(), SoundSource.PLAYERS);
                }
            });

            var hasAmmunition = weaponItemData.getAmmunitionStrategy().hasAmmunition(serverLevel, serverPlayer, itemStack, weaponItemData);
            if (!hasAmmunition) {
                weaponItemData.getReloadStrategy().tryReload(serverLevel, serverPlayer, itemStack, weaponItemData);
                return;
            } else {
                fire(level, player, itemStack, fireMode, positiveTickProgress);
            }
        }

        if (level.isClientSide) {
            var recoil = fireMode.recoil();
            player.attackAnim = recoil;
            player.oAttackAnim = recoil;
        }
    }

    @Override
    public int getUseDuration(ItemStack itemStack) {
        return START_TICK_PROGRESS;
    }

    @Override
    public @NotNull InteractionResultHolder<ItemStack> use(
        @NotNull Level level,
        @NotNull Player player,
        @NotNull InteractionHand interactionHand
    ) {
        return ItemUtils.startUsingInstantly(level, player, interactionHand);
    }

    private void fire(@NotNull Level level, @NotNull Player player, ItemStack itemStack, FireMode fireMode, int tickProgress) {
        var fireRateInTicks = fireMode.fireRateInTicks();
        WeaponItemTagHelper.consumeAmmunition(itemStack, weaponItemData);

        if (fireRateInTicks > 0) {
            player.getCooldowns().addCooldown(this, fireRateInTicks);
        }

        var shootSound = fireMode.shootSound().get();
        var shootSoundFrequency = fireMode.shootSoundFrequency();

        if (tickProgress % shootSoundFrequency == 0) {
            level.playSound(null, player.blockPosition(), shootSound, SoundSource.PLAYERS);
        }

        var hitResult = ProjectileUtil.getHitResultOnViewVector(player, entity -> true, fireMode.range());

        switch (hitResult.getType()) {
            case BLOCK -> handleHitBlock(level, fireMode, (BlockHitResult) hitResult);
            case ENTITY -> handleHitEntity(level, player, (EntityHitResult) hitResult, fireMode);
            case MISS -> { /* Do nothing */ }
        }
    }

    private void handleHitBlock(@NotNull Level level, FireMode fireMode, BlockHitResult hitResult) {
        var blockPos = hitResult.getBlockPos();
        var blockState = level.getBlockState(blockPos);
        var block = blockState.getBlock();
        var soundType = block.getSoundType(blockState);

        GameObject<SoundEvent> ricochetSfx = SoundUtilities.getRicochetSoundForSoundType(soundType);
        level.playSound(null, blockPos, ricochetSfx.get(), SoundSource.BLOCKS);

        if (AVPConfig.General.GUNS_DO_BLOCK_DAMAGE) {
            damageBlock(level, blockPos, block, hitResult.getDirection(), fireMode);
        }
    }

    private void handleHitEntity(@NotNull Level level, Player player, EntityHitResult hitResult, FireMode fireMode) {
        var damage = this.getWeaponItemData().getDamage() * fireMode.consumedAmmunition();
        var entity = hitResult.getEntity();

        entity.invulnerableTime = 0;
        entity.hurt(level.damageSources().generic(), damage);

        // Apply knockback to living entities
        if (entity instanceof LivingEntity livingEntity) {
            livingEntity.knockback(
                this.getWeaponItemData().getKnockback() * fireMode.consumedAmmunition(),
                Mth.sin(player.getYRot() * Mth.DEG_TO_RAD),
                -Mth.cos(player.getYRot() * Mth.DEG_TO_RAD)
            );
        }
    }

    private void damageBlock(@NotNull Level level, BlockPos blockPos, Block block, Direction direction, FireMode fireMode) {
        BlockBreakProgressManager.BLOCK_BREAK_PROGRESS_MAP.compute(blockPos, (key, tuple) -> {
            var cachedValue = tuple == null ? 0 : tuple.second();
            var damage = this.getWeaponItemData().getDamage() * fireMode.consumedAmmunition();
            var newValue = cachedValue + (damage / (2F + block.defaultDestroyTime() / 2F));
            var progress = (int) Mth.clamp(newValue, 0F, 9F);
            level.destroyBlockProgress(Objects.hash(blockPos), blockPos, progress);

            if (progress >= 9) {
                level.destroyBlock(blockPos, false);
                return null;
            }
            return new Tuple<>(System.currentTimeMillis() + TimeUtilities.FIVE_MINUTES_IN_MILLIS, newValue);
        });

        var payload = new ClientboundBulletHitBlockPayload(blockPos, direction);
        Services.NETWORK_HANDLER.sendToAllClients(level.getServer(), payload);
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        // Do nothing
    }

    @Override
    public void createRenderer(Consumer<Object> consumer) {
        consumer.accept(new RenderProvider() {

            private BlockEntityWithoutLevelRenderer renderer;

            @Override
            public BlockEntityWithoutLevelRenderer getCustomRenderer() {
                if (renderer == null) {
                    var supplier = AVPWeaponItemRenderers.WEAPON_ITEM_RENDERERS.get(AbstractAVPWeaponItem.this.getClass());

                    if (supplier != null) {
                        renderer = supplier.get();
                    }
                }
                return renderer;
            }
        });
    }

    public WeaponItemData getWeaponItemData() {
        return weaponItemData;
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return cache;
    }

    @Override
    public Supplier<Object> getRenderProvider() {
        return renderProvider;
    }
}
