package org.avp.common.game.item;

import mod.azure.azurelib.common.api.common.animatable.GeoItem;
import mod.azure.azurelib.common.internal.client.RenderProvider;
import mod.azure.azurelib.common.internal.common.core.animatable.instance.AnimatableInstanceCache;
import mod.azure.azurelib.common.internal.common.core.animation.AnimatableManager;
import mod.azure.azurelib.common.internal.common.util.AzureLibUtil;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemUtils;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.function.Consumer;
import java.util.function.Supplier;

import org.avp.api.common.weapon.WeaponItemStack;
import org.avp.api.common.weapon.data.WeaponData;
import org.avp.api.common.weapon.reload.ReloadBehavior;
import org.avp.api.util.TooltipUtils;
import org.avp.client.render.item.AVPWeaponItemRenderers;
import org.avp.common.registry.item.AVPItemRegistry;

public abstract class AbstractAVPWeaponItem extends Item implements GeoItem {

    private static final int START_TICK_PROGRESS = Integer.MAX_VALUE;

    private final AnimatableInstanceCache cache = AzureLibUtil.createInstanceCache(this);

    private final Supplier<Object> renderProvider = GeoItem.makeRenderer(this);

    private final WeaponData weaponData;

    protected AbstractAVPWeaponItem(Properties properties, WeaponData weaponData) {
        super(properties.stacksTo(1).durability(weaponData.getDurability()));
        this.weaponData = weaponData;
    }

    @Override
    public boolean isValidRepairItem(@NotNull ItemStack toRepair, ItemStack repairIngredient) {
        return repairIngredient.is(AVPItemRegistry.INSTANCE.ingotSteel.get());
    }

    @Override
    public void releaseUsing(@NotNull ItemStack itemStack, @NotNull Level level, @NotNull LivingEntity livingEntity, int i) {
        var weaponItemStack = new WeaponItemStack(itemStack, weaponData);
        var fireModeData = weaponItemStack.getOrSetFireMode();
        var windData = fireModeData.windData();
        var windDownSoundHolder = windData.windDownSoundHolder();

        windDownSoundHolder.ifPresent(
            windDownSound -> level.playSound(null, livingEntity.blockPosition(), windDownSound, SoundSource.PLAYERS)
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

        var weaponItemStack = new WeaponItemStack(itemStack, weaponData);
        var fireMode = weaponItemStack.getOrSetFireMode();
        var shootData = fireMode.shootData();
        var windData = fireMode.windData();
        var windUpTimeInTicks = windData.windUpTimeInTicks();
        var windUpSoundHolder = windData.windUpSoundHolder();

        if (isFirstTick) {
            windUpSoundHolder.ifPresent(
                windUpSound -> level.playSound(null, player.blockPosition(), windUpSound, SoundSource.PLAYERS)
            );
        }

        if (positiveTickProgress < windUpTimeInTicks)
            return;

        if (!level.isClientSide) {
            var serverLevel = (ServerLevel) level;
            var serverPlayer = (ServerPlayer) player;

            // Background sound that fires as long as the trigger is pulled, regardless if ammo is present.
            var backgroundShootSoundFrequency = shootData.backgroundShootSoundFrequencyInTicks();
            var backgroundShootSoundHolder = shootData.backgroundShootSoundHolder();

            backgroundShootSoundHolder.ifPresent(backgroundShootSound -> {
                if (
                    positiveTickProgress == windUpTimeInTicks || (positiveTickProgress + windUpTimeInTicks)
                        % backgroundShootSoundFrequency == 0
                ) {
                    level.playSound(null, player.blockPosition(), backgroundShootSound, SoundSource.PLAYERS);
                }
            });

            var hasAmmunition = fireMode.ammunitionData().hasAmmunitionBehavior().hasAmmunition(serverLevel, serverPlayer, weaponItemStack);
            var reloadBehavior = fireMode.reloadData().reloadBehavior();

            if (hasAmmunition || serverPlayer.isCreative()) {
                if (reloadBehavior == ReloadBehavior.LOAD_FROM_INVENTORY) {
                    reloadBehavior.tryReload(serverLevel, serverPlayer, weaponItemStack);
                }
                fire(level, player, weaponItemStack, positiveTickProgress);
            } else {
                reloadBehavior.tryReload(serverLevel, serverPlayer, weaponItemStack);
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

    private void fire(@NotNull Level level, @NotNull Player player, WeaponItemStack weaponItemStack, int tickProgress) {
        var fireMode = weaponItemStack.getOrSetFireMode();

        weaponItemStack.consumeAmmunition();

        weaponItemStack.getItemStack().hurtAndBreak(1, player, thePlayer -> thePlayer.broadcastBreakEvent(EquipmentSlot.MAINHAND));

        var fireRateInTicks = fireMode.fireRateInTicks();

        if (fireRateInTicks > 0) {
            player.getCooldowns().addCooldown(this, fireRateInTicks);
        }

        var shootData = fireMode.shootData();
        var shootSound = shootData.shootSoundHolder().get();
        var shootSoundFrequency = shootData.shootSoundFrequencyInTicks();

        if (shootSoundFrequency <= 0 || tickProgress % shootSoundFrequency == 0) {
            level.playSound(null, player.blockPosition(), shootSound, SoundSource.PLAYERS);
        }

        var weaponAttack = fireMode.createWeaponAttack(weaponItemStack, player);
        weaponAttack.shoot();
    }

    @Override
    public void appendHoverText(
        @NotNull ItemStack itemStack,
        @Nullable Level level,
        @NotNull List<Component> components,
        @NotNull TooltipFlag tooltipFlag
    ) {
        super.appendHoverText(itemStack, level, components, tooltipFlag);
        var weaponItemStack = new WeaponItemStack(itemStack, weaponData);
        var fireMode = weaponItemStack.getOrSetFireMode();
        TooltipUtils.appendLabel(
            components,
            "tooltip.avp.fire_mode",
            Component.literal(fireMode.identifier() + " (" + fireMode.ammunitionData().consumedAmmunition() + " / Shot)")
        );
        TooltipUtils.appendLabel(
            components,
            "tooltip.avp.ammunition",
            Component.literal(weaponItemStack.getAmmunition() + " / " + fireMode.ammunitionData().maxAmmunition())
        );
        TooltipUtils.appendLabel(
            components,
            "tooltip.avp.ammunition_type",
            Component.translatable(
                "item." + weaponItemStack.getOrSetActiveAmmunitionType().replace(":", ".")
            )
        );
        TooltipUtils.appendLabel(components, "tooltip.avp.damage", Component.literal(Double.toString(fireMode.damage())));
        TooltipUtils.appendLabel(components, "tooltip.avp.fire_rate", Component.literal(fireMode.fireRateInTicks() / 20D + " / Sec"));
        TooltipUtils.appendLabel(components, "tooltip.avp.accuracy", Component.literal(Float.toString(fireMode.accuracy())));
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
                    var supplier = AVPWeaponItemRenderers.WEAPON_ITEM_RENDERERS.get(weaponData);

                    if (supplier != null) {
                        renderer = supplier.get();
                    }
                }
                return renderer;
            }
        });
    }

    public WeaponData getWeaponData() {
        return weaponData;
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
