package org.avp.common.item;

import mod.azure.azurelib.common.api.common.animatable.GeoItem;
import mod.azure.azurelib.common.internal.client.RenderProvider;
import mod.azure.azurelib.common.internal.common.core.animatable.instance.AnimatableInstanceCache;
import mod.azure.azurelib.common.internal.common.core.animation.AnimatableManager;
import mod.azure.azurelib.common.internal.common.util.AzureLibUtil;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.ProjectileUtil;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemUtils;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import org.avp.common.tag.AVPBlockTags;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.function.Consumer;
import java.util.function.Supplier;

import org.avp.api.Holder;
import org.avp.api.item.weapon.FireMode;
import org.avp.api.item.weapon.WeaponItemData;
import org.avp.api.item.weapon.WeaponItemTagHelper;
import org.avp.client.render.item.AVPWeaponItemRenderers;
import org.avp.common.config.AVPConfig;
import org.avp.common.damage.AVPDamageSources;
import org.avp.common.network.payload.ClientboundBulletHitBlockPayload;
import org.avp.common.service.Services;
import org.avp.common.util.AVPPredicates;
import org.avp.common.util.SoundUtils;
import org.avp.common.util.TooltipUtils;
import org.avp.server.BlockBreakProgressManager;

public abstract class AbstractAVPWeaponItem extends Item implements GeoItem {

    private static final int START_TICK_PROGRESS = Integer.MAX_VALUE;

    private final AnimatableInstanceCache cache = AzureLibUtil.createInstanceCache(this);

    private final Supplier<Object> renderProvider = GeoItem.makeRenderer(this);

    private final WeaponItemData weaponItemData;

    protected AbstractAVPWeaponItem(Properties properties, WeaponItemData weaponItemData) {
        super(properties.stacksTo(1).durability(weaponItemData.getDurability()));
        this.weaponItemData = weaponItemData;
    }

    @Override
    public boolean isValidRepairItem(@NotNull ItemStack toRepair, ItemStack repairIngredient) {
        return repairIngredient.is(AVPItems.INSTANCE.ingotSteel.get());
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

        var fireMode = WeaponItemTagHelper.getOrSetFireMode(itemStack, weaponItemData);

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

            if (hasAmmunition) {
                fire(level, player, itemStack, fireMode, positiveTickProgress);
            } else {
                weaponItemData.getReloadStrategy().tryReload(serverLevel, serverPlayer, itemStack, weaponItemData);
            }
        }

        if (level.isClientSide) {
            var recoil = fireMode.recoil();
            player.attackAnim = recoil;
            player.oAttackAnim = recoil;
        }
    }

    @Override
    public int getUseDuration(@NotNull ItemStack itemStack) {
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
        WeaponItemTagHelper.consumeAmmunition(itemStack, weaponItemData);

        itemStack.hurtAndBreak(1, player, thePlayer -> thePlayer.broadcastBreakEvent(EquipmentSlot.MAINHAND));

        var fireRateInTicks = fireMode.fireRateInTicks();

        if (fireRateInTicks > 0) {
            player.getCooldowns().addCooldown(this, fireRateInTicks);
        }

        var shootSound = fireMode.shootSound().get();
        var shootSoundFrequency = fireMode.shootSoundFrequency();

        if (tickProgress % shootSoundFrequency == 0) {
            level.playSound(null, player.blockPosition(), shootSound, SoundSource.PLAYERS);
        }

        var hitResult = ProjectileUtil.getHitResultOnViewVector(
            player,
            entity -> entity.getType() == EntityType.END_CRYSTAL || AVPPredicates.IS_LIVING.test(entity),
            fireMode.range()
        );

        switch (hitResult.getType()) {
            case BLOCK -> handleHitBlock(level, fireMode, (BlockHitResult) hitResult);
            case ENTITY -> handleHitEntity(player, itemStack, (EntityHitResult) hitResult, fireMode);
            case MISS -> { /* Do nothing */ }
        }
    }

    private void handleHitBlock(@NotNull Level level, FireMode fireMode, BlockHitResult hitResult) {
        var blockPos = hitResult.getBlockPos();
        var direction = hitResult.getDirection();
        var blockState = level.getBlockState(blockPos);
        var block = blockState.getBlock();
        var soundType = block.getSoundType(blockState);

        Holder<SoundEvent> ricochetSfx = SoundUtils.getRicochetSoundForSoundType(soundType);
        level.playSound(null, blockPos, ricochetSfx.get(), SoundSource.BLOCKS);

        damageBlock(level, blockPos, fireMode);

        var payload = new ClientboundBulletHitBlockPayload(blockPos, direction);
        Services.NETWORK_SERVICE.sendToAllClients(level.getServer(), payload);
    }

    private void handleHitEntity(Player player, ItemStack itemStack, EntityHitResult hitResult, FireMode fireMode) {
        var hitEntity = hitResult.getEntity();
        var damage = this.getWeaponItemData().getDamage() * fireMode.consumedAmmunition();

        var damageSource = AVPDamageSources.INSTANCE.bullet.get().covertIndirectDamageSource(player.level(), player);
        hitEntity.hurt(damageSource, damage);

        if (hitEntity instanceof LivingEntity livingEntity) {
            livingEntity.invulnerableTime = 0;
            livingEntity.setLastHurtByMob(player);

            livingEntity.knockback(
                this.getWeaponItemData().getKnockback() * fireMode.consumedAmmunition(),
                Mth.sin(player.getYRot() * Mth.DEG_TO_RAD),
                -Mth.cos(player.getYRot() * Mth.DEG_TO_RAD)
            );

            var bulletEffects = WeaponItemTagHelper.getBulletEffects(itemStack, weaponItemData);
            bulletEffects.forEach(bulletEffect -> bulletEffect.applyEffect(livingEntity));
        }
    }

    private void damageBlock(@NotNull Level level, BlockPos blockPos, FireMode fireMode) {
        // Only damage blocks if both are true.
        if (!AVPConfig.General.GUNS_DO_BLOCK_DAMAGE)
            return;
        if (!level.getGameRules().getBoolean(GameRules.RULE_PROJECTILESCANBREAKBLOCKS))
            return;

        var blockState = level.getBlockState(blockPos);

        // Only damage blocks if they should be destroyed.
        if (blockState.is(AVPBlockTags.SHOULD_NOT_BE_DESTROYED)) {
            return;
        }

        var damage = this.getWeaponItemData().getDamage() * fireMode.consumedAmmunition();
        BlockBreakProgressManager.damage(level, blockPos, damage);
    }

    @Override
    public void appendHoverText(
        @NotNull ItemStack itemStack,
        @Nullable Level level,
        @NotNull List<Component> components,
        @NotNull TooltipFlag tooltipFlag
    ) {
        super.appendHoverText(itemStack, level, components, tooltipFlag);
        var fireMode = WeaponItemTagHelper.getOrSetFireMode(itemStack, weaponItemData);
        TooltipUtils.appendLabel(
            components,
            "tooltip.avp.fire_mode",
            Component.literal(fireMode.identifier() + " (" + fireMode.consumedAmmunition() + " / Shot)")
        );
        TooltipUtils.appendLabel(
            components,
            "tooltip.avp.ammunition",
            Component.literal(
                WeaponItemTagHelper.getAmmunition(itemStack, weaponItemData) + " / " + weaponItemData.getAmmunitionStrategy()
                    .getMaxAmmunition()
            )
        );
        TooltipUtils.appendLabel(
            components,
            "tooltip.avp.ammunition_type",
            Component.translatable(
                "item." + WeaponItemTagHelper.getOrSetActiveAmmunitionType(itemStack, weaponItemData).replace(":", ".")
            )
        );
        TooltipUtils.appendLabel(components, "tooltip.avp.damage", Component.literal(Double.toString(weaponItemData.getDamage())));
        TooltipUtils.appendLabel(components, "tooltip.avp.fire_rate", Component.literal(fireMode.fireRateInTicks() / 20D + " / Sec"));
        TooltipUtils.appendLabel(components, "tooltip.avp.accuracy", Component.literal(Float.toString(weaponItemData.getAccuracy())));
        TooltipUtils.appendLabel(components, "tooltip.avp.recoil", Component.literal(Float.toString(fireMode.recoil())));
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
