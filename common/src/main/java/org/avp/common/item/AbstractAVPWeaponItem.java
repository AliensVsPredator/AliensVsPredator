package org.avp.common.item;

import mod.azure.azurelib.common.api.common.animatable.GeoItem;
import mod.azure.azurelib.common.internal.client.RenderProvider;
import mod.azure.azurelib.common.internal.common.core.animatable.instance.AnimatableInstanceCache;
import mod.azure.azurelib.common.internal.common.core.animation.AnimatableManager;
import mod.azure.azurelib.common.internal.common.util.AzureLibUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
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
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import org.avp.api.item.weapon.WeaponItemTagHelper;
import org.avp.common.util.SoundUtilities;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;
import java.util.function.Consumer;
import java.util.function.Supplier;

import org.avp.api.Tuple;
import org.avp.api.item.weapon.FireMode;
import org.avp.api.item.weapon.WeaponItemData;
import org.avp.common.util.TimeUtilities;
import org.avp.common.network.payload.ClientboundBulletHitBlockPayload;
import org.avp.common.service.Services;
import org.avp.api.GameObject;
import org.avp.mixin.MixinMinecraftAccessor;
import org.avp.server.BlockBreakProgressManager;

public abstract class AbstractAVPWeaponItem extends Item implements GeoItem {

    private final AnimatableInstanceCache cache = AzureLibUtil.createInstanceCache(this);

    private final Supplier<Object> renderProvider = GeoItem.makeRenderer(this);

    private final WeaponItemData weaponItemData;

    protected AbstractAVPWeaponItem(Properties properties, WeaponItemData weaponItemData) {
        super(properties);
        this.weaponItemData = weaponItemData;
    }

    protected abstract BlockEntityWithoutLevelRenderer createRenderer();

    @Override
    public @NotNull InteractionResultHolder<ItemStack> use(
        @NotNull Level level,
        @NotNull Player player,
        @NotNull InteractionHand interactionHand
    ) {
        var itemStack = player.getItemInHand(interactionHand);
        var fireMode = WeaponItemTagHelper.getFireMode(itemStack, weaponItemData);

        if (level.isClientSide) {
            ((MixinMinecraftAccessor) Minecraft.getInstance()).setRightClickDelay(0);
        }

        if (!level.isClientSide) {
            var ammunition = WeaponItemTagHelper.getAmmunition(itemStack);
            if (ammunition <= 0) {
                weaponItemData.getReloadStrategy().tryReload((ServerLevel) level, (ServerPlayer) player, itemStack, weaponItemData);
                return super.use(level, player, interactionHand);
            } else {
                fire(level, player, itemStack, fireMode);
            }
        }

        if (level.isClientSide) {
            var recoil = fireMode.recoil();
            player.attackAnim = recoil;
            player.oAttackAnim = recoil;
        }

        return super.use(level, player, interactionHand);
    }

    private void fire(@NotNull Level level, @NotNull Player player, ItemStack itemStack, FireMode fireMode) {
        var fireRateInTicks = fireMode.fireRateInTicks();
        WeaponItemTagHelper.consumeAmmunition(itemStack, weaponItemData);

        if (fireRateInTicks > 0) {
            player.getCooldowns().addCooldown(this, fireRateInTicks);
        }

        var fireSound = fireMode.fireSound().get();
        level.playSound(null, player.blockPosition(), fireSound, SoundSource.PLAYERS);

        var hitResult = ProjectileUtil.getHitResultOnViewVector(player, entity -> true, 128.0D);

        switch (hitResult.getType()) {
            case BLOCK -> damageBlock(level, (BlockHitResult) hitResult, fireMode);
            case ENTITY -> damageEntity(level, player, (EntityHitResult) hitResult, fireMode);
        }
    }

    private void damageEntity(@NotNull Level level, Player player, EntityHitResult hitResult, FireMode fireMode) {
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

    private void damageBlock(@NotNull Level level, BlockHitResult hitResult, FireMode fireMode) {
        var blockPos = hitResult.getBlockPos();
        var blockState = level.getBlockState(blockPos);
        var block = blockState.getBlock();
        var soundType = block.getSoundType(blockState);

        GameObject<SoundEvent> ricochetSfx = SoundUtilities.getRicochetSoundForSoundType(soundType);
        level.playSound(null, blockPos, ricochetSfx.get(), SoundSource.BLOCKS);

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

        var payload = new ClientboundBulletHitBlockPayload(blockPos, hitResult.getDirection());
        Services.NETWORK_HANDLER.sendToAllClients(level.getServer(), payload);
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        // Do nothing
    }

    @Override
    public void createRenderer(Consumer<Object> consumer) {
        consumer.accept(new RenderProvider() {

            private final BlockEntityWithoutLevelRenderer renderer = AbstractAVPWeaponItem.this.createRenderer();

            @Override
            public BlockEntityWithoutLevelRenderer getCustomRenderer() {
                return this.renderer;
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
