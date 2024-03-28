package org.avp.common.item;

import mod.azure.azurelib.common.api.common.animatable.GeoItem;
import mod.azure.azurelib.common.internal.client.RenderProvider;
import mod.azure.azurelib.common.internal.common.core.animatable.instance.AnimatableInstanceCache;
import mod.azure.azurelib.common.internal.common.core.animation.AnimatableManager;
import mod.azure.azurelib.common.internal.common.util.AzureLibUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
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
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import org.avp.api.item.weapon.FireMode;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.function.Consumer;
import java.util.function.Supplier;

import org.avp.api.item.weapon.WeaponItemData;
import org.avp.common.sound.AVPSoundEvents;
import org.avp.common.util.GameObject;
import org.avp.mixin.MixinMinecraftAccessor;

public abstract class AbstractAVPWeaponItem extends Item implements GeoItem {

    // TODO: CLEAR THIS PERIODICALLY.
    // FIXME: FR FR ON JAH, CLEAN THIS RN
    private static final Map<BlockPos, Float> BLOCK_BREAK_PROGRESS_MAP = new HashMap<>();

    private static final String AMMUNITION_KEY = "Ammunition";

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
        var tag = itemStack.getOrCreateTag();
        var ammunition = tag.getInt(AMMUNITION_KEY);
        var fireModeIdentifier = tag.getString("FireMode");
        var fireMode = this.getWeaponItemData().getFireMode(fireModeIdentifier);

        if (fireModeIdentifier.isEmpty()) {
            tag.putString("FireMode", fireMode.identifier());
        }

        if (level.isClientSide) {
            ((MixinMinecraftAccessor) Minecraft.getInstance()).setRightClickDelay(0);
        }

        if (!level.isClientSide) {
            if (ammunition <= 0) {
                reload(level, player, itemStack);
                return super.use(level, player, interactionHand);
            } else {
                fire(level, player, tag, fireMode);
            }
        }

        if (level.isClientSide) {
            var recoil = fireMode.recoil();
            player.attackAnim = recoil;
            player.oAttackAnim = recoil;
        }

        return super.use(level, player, interactionHand);
    }

    private void fire(@NotNull Level level, @NotNull Player player, CompoundTag tag, FireMode fireMode) {
        var fireRateInTicks = fireMode.fireRateInTicks();
        tag.putInt(AMMUNITION_KEY, tag.getInt(AMMUNITION_KEY) - fireMode.consumedAmmunition());

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

        GameObject<SoundEvent> ricochetSfx = getRicochetSound(soundType);

        level.playSound(null, blockPos, ricochetSfx.get(), SoundSource.BLOCKS);

        BLOCK_BREAK_PROGRESS_MAP.compute(blockPos, (key, value) -> {
            var cachedValue = value == null ? 0 : value;
            var damage = this.getWeaponItemData().getDamage() * fireMode.consumedAmmunition();
            var newValue = cachedValue + (damage / (2F + block.defaultDestroyTime() / 2F));
            var progress = (int) Mth.clamp(newValue, 0F, 9F);
            level.destroyBlockProgress(Objects.hash(blockPos), blockPos, progress);

            if (progress >= 9) {
                level.destroyBlock(blockPos, false);
                return null;
            }
            return newValue;
        });

        // FIXME: Use packet to emit particles, otherwise will crash server.
        // TODO: Scale with weapon bullet count + damage
        for (int i = 0; i < 16; i++) {
            Minecraft.getInstance().particleEngine.crack(blockPos, hitResult.getDirection());
        }
    }

    private static GameObject<SoundEvent> getRicochetSound(SoundType soundType) {
        GameObject<SoundEvent> ricochetSfx;

        if (soundType == SoundType.GLASS) {
            ricochetSfx = AVPSoundEvents.ITEM_WEAPON_FX_RICOCHET_GLASS;
        } else if (soundType == SoundType.GRAVEL) {
            ricochetSfx = AVPSoundEvents.ITEM_WEAPON_FX_RICOCHET_DIRT;
        } else if (soundType == SoundType.METAL) {
            ricochetSfx = AVPSoundEvents.ITEM_WEAPON_FX_RICOCHET_METAL;
        } else {
            ricochetSfx = AVPSoundEvents.ITEM_WEAPON_FX_RICOCHET_GENERIC;
        }
        return ricochetSfx;
    }

    private void reload(Level level, Player player, ItemStack itemStack) {
        var maxAmmunition = this.getWeaponItemData().getMaxAmmunition();
        var reloadTimeInTicks = this.getWeaponItemData().getReloadTimeInTicks();

        player.getCooldowns().addCooldown(this, reloadTimeInTicks);
        var tag = itemStack.getOrCreateTag();
        tag.putInt(AMMUNITION_KEY, maxAmmunition);
        // TODO: Consume ammunition item.

        var reloadSound = this.getWeaponItemData().getReloadSound().get();
        level.playSound(null, player.blockPosition(), reloadSound, SoundSource.PLAYERS);
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
