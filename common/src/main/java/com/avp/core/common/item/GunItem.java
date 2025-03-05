package com.avp.core.common.item;

import com.avp.core.common.component.DataComponents;
import com.avp.core.common.item.gun.FireModeConfig;
import com.avp.core.common.item.gun.GunConfig;
import com.avp.core.common.item.gun.GunData;
import com.avp.core.common.item.gun.attack.GunAttackConfig;
import com.avp.core.common.util.EnchantmentUtil;
import com.avp.core.common.util.GunLightUtil;
import com.avp.core.common.util.TooltipUtil;
import com.avp.core.server.ServerScheduler;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.EquipmentSlotGroup;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemUtils;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.ItemAttributeModifiers;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;

import java.time.Duration;
import java.util.List;
import java.util.Objects;

public class GunItem extends Item {

    private static final int START_TICK_PROGRESS = Integer.MAX_VALUE;

    protected final GunConfig gunConfig;

    public GunItem(GunConfig gunConfig) {
        super(new Item.Properties().stacksTo(1).durability(gunConfig.durability()).attributes(createAttributes()));
        this.gunConfig = gunConfig;
    }

    private static ItemAttributeModifiers createAttributes() {
        return ItemAttributeModifiers.builder()
            .add(
                Attributes.ATTACK_SPEED,
                new AttributeModifier(BASE_ATTACK_SPEED_ID, (float) -0.1, AttributeModifier.Operation.ADD_VALUE),
                EquipmentSlotGroup.MAINHAND
            )
            .build();
    }

    protected void playReleaseUsingAnimations(Entity shooter, ItemStack itemStack) {}

    protected void playUseAnimations(Entity shooter, ItemStack itemStack) {}

    @Override
    public boolean isValidRepairItem(@NotNull ItemStack toRepair, ItemStack repairIngredient) {
        return repairIngredient.is(AVPItems.STEEL_INGOT.get());
    }

    @Override
    public int getEnchantmentValue() {
        return 1;
    }

    @Override
    public void releaseUsing(@NotNull ItemStack itemStack, @NotNull Level level, @NotNull LivingEntity livingEntity, int i) {
        var fireModeConfig = gunConfig.getDefaultFireMode();
        var shootFinishSoundEvent = fireModeConfig.shootFinishSoundEvent();

        if (shootFinishSoundEvent != null) {
            level.playSound(null, livingEntity.blockPosition(), shootFinishSoundEvent.get(), SoundSource.PLAYERS);
        }

        playReleaseUsingAnimations(livingEntity, itemStack);

        super.releaseUsing(itemStack, level, livingEntity, i);
    }

    @Override
    public void onUseTick(Level level, @NotNull LivingEntity livingEntity, @NotNull ItemStack itemStack, int tickCountdown) {
        if (level.isClientSide) {
            return;
        }

        if (!(livingEntity instanceof Player player)) {
            return;
        }

        if (player.getCooldowns().isOnCooldown(this)) {
            return;
        }

        playUseAnimations(player, itemStack);

        var tickProgress = START_TICK_PROGRESS - tickCountdown;
        var positiveTickProgress = Math.abs(tickProgress);
        var isFirstTick = positiveTickProgress == 0;
        var fireModeConfig = gunConfig.getDefaultFireMode();
        var shootStartSoundEvent = fireModeConfig.shootStartSoundEvent();
        var shootDelayInTicks = fireModeConfig.shootDelayInTicks();
        var primaryShootSoundFrequencyInTicks = fireModeConfig.primaryShootSoundFrequencyInTicks();
        var secondaryShootSoundFrequencyInTicks = fireModeConfig.secondaryShootSoundFrequencyInTicks();

        if (shootStartSoundEvent != null && isFirstTick) {
            level.playSound(null, player.blockPosition(), shootStartSoundEvent.get(), SoundSource.PLAYERS);
        }

        if (positiveTickProgress < shootDelayInTicks) {
            return;
        }

        tryShoot(
            level,
            itemStack,
            player,
            fireModeConfig,
            positiveTickProgress,
            shootDelayInTicks,
            secondaryShootSoundFrequencyInTicks,
            primaryShootSoundFrequencyInTicks,
            tickProgress
        );
    }

    protected void tryShoot(
        Level level,
        ItemStack itemStack,
        Player player,
        FireModeConfig fireModeConfig,
        int positiveTickProgress,
        int shootDelayInTicks,
        int secondaryShootSoundFrequencyInTicks,
        int primaryShootSoundFrequencyInTicks,
        int tickProgress
    ) {
        int currentAmmunition = itemStack.getOrDefault(DataComponents.AMMUNITION.get(), 0);
        var isPlayerCreative = player.isCreative() || player.isSpectator();
        var hasInfinity = EnchantmentUtil.getLevel(level, itemStack, Enchantments.INFINITY) > 0;

        if (!isPlayerCreative && !hasInfinity && currentAmmunition <= 0) {
            reload((ServerPlayer) player);
            return;
        }

        var gunAttackConfig = new GunAttackConfig(gunConfig, fireModeConfig, player, itemStack);
        var gunAttack = fireModeConfig
            .gunAttackSupplier()
            .apply(gunAttackConfig);

        gunAttack.shoot();

        if (!isPlayerCreative) {
            if (!hasInfinity) {
                itemStack.set(DataComponents.AMMUNITION.get(), Math.max(currentAmmunition - fireModeConfig.consumedAmmunitionPerShot(), 0));
            }

            itemStack.hurtAndBreak(1, player, EquipmentSlot.MAINHAND);
        }

        var secondaryShootSoundEvent = fireModeConfig.secondaryShootSoundEvent();

        if (
            secondaryShootSoundEvent != null &&
                (positiveTickProgress == shootDelayInTicks || (positiveTickProgress + shootDelayInTicks)
                    % secondaryShootSoundFrequencyInTicks == 0)
        ) {
            level.playSound(null, player.blockPosition(), secondaryShootSoundEvent.get(), SoundSource.PLAYERS);
        }

        if (primaryShootSoundFrequencyInTicks <= 0 || tickProgress % primaryShootSoundFrequencyInTicks == 0) {
            level.playSound(null, player.blockPosition(), fireModeConfig.primaryShootSoundEvent().get(), SoundSource.PLAYERS);
        }

        player.getCooldowns().addCooldown(this, fireModeConfig.cooldownInTicks());

        GunLightUtil.spawnLightSource(player);
    }

    @Override
    public int getUseDuration(@NotNull ItemStack itemStack, @NotNull LivingEntity livingEntity) {
        return START_TICK_PROGRESS;
    }

    @Override
    public @NotNull InteractionResultHolder<ItemStack> use(@NotNull Level level, @NotNull Player player, @NotNull InteractionHand interactionHand) {
        return ItemUtils.startUsingInstantly(level, player, interactionHand);
    }

    @Override
    public void appendHoverText(@NotNull ItemStack itemStack, @NotNull TooltipContext tooltipContext, @NotNull List<Component> list, @NotNull TooltipFlag tooltipFlag) {
        super.appendHoverText(itemStack, tooltipContext, list, tooltipFlag);

        int currentAmmunition = itemStack.getOrDefault(DataComponents.AMMUNITION.get(), 0);
        var fireMode = gunConfig.getDefaultFireMode();
        var itemSupplier = gunConfig.ammunitionItemSupplier();

        // TODO:
        // TooltipUtils.appendLabel(
        // list,
        // "tooltip.avp.fire_mode",
        // Component.literal(fireMode.identifier() + " (" + fireMode.ammunitionData().consumedAmmunition() + " / Shot)")
        // );

        if (itemSupplier != null) {
            var item = itemSupplier.get();

            TooltipUtil.appendLabel(
                list,
                "tooltip.avp.ammunition_type",
                Component.translatable(item.asItem().getDescriptionId())
            );
        }

        TooltipUtil.appendLabel(
            list,
            "tooltip.avp.ammunition",
            Component.literal(currentAmmunition + " / " + gunConfig.maximumAmmunition())
        );
        TooltipUtil.appendLabel(
            list,
            "tooltip.avp.damage",
            Component.literal(ItemAttributeModifiers.ATTRIBUTE_MODIFIER_FORMAT.format(fireMode.damage()))
        );
        TooltipUtil.appendLabel(
            list,
            "tooltip.avp.knockback",
            Component.literal(ItemAttributeModifiers.ATTRIBUTE_MODIFIER_FORMAT.format(fireMode.knockback()))
        );
        TooltipUtil.appendLabel(
            list,
            "tooltip.avp.fire_rate",
            Component.literal(
                ItemAttributeModifiers.ATTRIBUTE_MODIFIER_FORMAT.format(fireMode.cooldownInTicks() / 20D) + " / Sec"
            )
        );
    }

    public GunConfig gunConfig() {
        return gunConfig;
    }

    public static void reload(ServerPlayer player) {
        if (player == null) {
            return;
        }

        var level = player.level();
        var usedItemHand = player.getUsedItemHand();
        var itemStack = player.getItemInHand(usedItemHand);
        var item = itemStack.getItem();
        int currentAmmunition = itemStack.getOrDefault(DataComponents.AMMUNITION.get(), 0);

        if (!(item instanceof GunItem gunItem)) {
            return;
        }

        var gunConfig = gunItem.gunConfig();
        var maximumAmmunition = gunConfig.maximumAmmunition();

        // TODO: Kinda hacky, find a better way to do this.
        if (gunConfig == GunData.OLD_PAINLESS) {
            return;
        }

        if (currentAmmunition >= maximumAmmunition) {
            return;
        }

        var fireModeConfig = gunConfig.getDefaultFireMode();
        var reloadStartSoundEvent = fireModeConfig.reloadStartSoundEvent();
        var reloadTimeModifier = EnchantmentUtil.getLevel(level, itemStack, Enchantments.QUICK_CHARGE) * 0.2;
        var reloadAmount = gunConfig.reloadAmount();
        var reloadTimeInTicks = (int) (gunConfig.reloadTimeInTicks() * (1 - reloadTimeModifier));
        var ammunitionItemSupplier = gunConfig.ammunitionItemSupplier();

        if (ammunitionItemSupplier == null) {
            return;
        }

        var ammunitionItem = ammunitionItemSupplier.get();
        var neededAmmunition = (int) Math.ceil((maximumAmmunition - currentAmmunition) / ((float) reloadAmount));
        var playerInventory = player.getInventory();
        var ammunitionCount = playerInventory.countItem(ammunitionItem.asItem());
        var ammunitionCountToConsume = Math.min(neededAmmunition, ammunitionCount);

        if (ammunitionCountToConsume == 0) {
            return;
        }

        consumeItemAmountFromInventory(ammunitionCountToConsume, playerInventory, ammunitionItem);

        if (reloadStartSoundEvent != null) {
            level.playSound(null, player.blockPosition(), reloadStartSoundEvent.get(), SoundSource.PLAYERS);
        }

        player.getCooldowns().addCooldown(itemStack.getItem(), reloadTimeInTicks);

        itemStack.set(
            DataComponents.AMMUNITION.get(),
            Math.min(currentAmmunition + (ammunitionCountToConsume * reloadAmount), maximumAmmunition)
        );

        ServerScheduler.schedule(() -> {
            var reloadFinishSoundEvent = fireModeConfig.reloadFinishSoundEvent();

            if (reloadFinishSoundEvent != null) {
                var interactionHand = player.getUsedItemHand();
                var itemInHand = player.getItemInHand(interactionHand);

                if (Objects.equals(itemStack, itemInHand)) {
                    level.playSound(null, player.blockPosition(), reloadFinishSoundEvent.get(), SoundSource.PLAYERS);
                }
            }
        }, Duration.ofMillis(reloadTimeInTicks * 50L));
    }

    protected static boolean consumeItemAmountFromInventory(
        int ammunitionCountToConsume,
        Inventory playerInventory,
        ItemLike ammunitionItem
    ) {
        var consumeTracker = ammunitionCountToConsume;

        for (var i = 0; i < playerInventory.items.size(); i++) {
            var playerItemStack = playerInventory.items.get(i);

            if (playerItemStack.is(ammunitionItem.asItem())) {
                var consumeCount = Math.min(playerItemStack.getCount(), consumeTracker);
                playerItemStack.shrink(consumeCount);
                consumeTracker -= consumeCount;

                if (consumeTracker == 0) {
                    break;
                }
            }
        }

        return consumeTracker < ammunitionCountToConsume;
    }
}
