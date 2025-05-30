package com.human.common.gameplay.item;

import com.human.common.gameplay.item.gun.GunConfig;
import com.human.common.gameplay.item.gun.pipeline.GunShootContext;
import com.human.common.gameplay.item.old_painless.OldPainlessAnimationRefs;
import com.lib.common.gameplay.util.TooltipUtil;
import mod.azure.azurelib.rewrite.animation.dispatch.command.AzCommand;
import mod.azure.azurelib.rewrite.animation.play_behavior.AzPlayBehaviors;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlotGroup;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemUtils;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.ItemAttributeModifiers;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;

import java.util.List;

import com.avp.common.registry.init.AVPDataComponents;
import com.avp.common.registry.init.item.AVPItems;

public class GunItem extends Item {

    protected static final int START_TICK_PROGRESS = Integer.MAX_VALUE;

    private final GunConfig gunConfig;

    public AzCommand idle;

    public AzCommand shoot;

    public static final AzCommand reload = AzCommand.create(
        OldPainlessAnimationRefs.MAIN_CONTROLLER_NAME,
        OldPainlessAnimationRefs.RELOAD_ANIMATION_NAME,
        AzPlayBehaviors.PLAY_ONCE
    );

    public GunItem(GunConfig gunConfig) {
        super(
            new Item.Properties().stacksTo(1)
                .component(AVPDataComponents.IS_FIRING.get(), false)
                .durability(gunConfig.durability())
                .attributes(createAttributes())
        );
        this.gunConfig = gunConfig;
        idle = AzCommand.create(
            OldPainlessAnimationRefs.MAIN_CONTROLLER_NAME,
            OldPainlessAnimationRefs.IDLE_ANIMATION_NAME,
            AzPlayBehaviors.LOOP
        );
        shoot = AzCommand.create(
            OldPainlessAnimationRefs.MAIN_CONTROLLER_NAME,
            OldPainlessAnimationRefs.SHOOT_ANIMATION_NAME,
            AzPlayBehaviors.PLAY_ONCE
        );
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

    protected void playReleaseUsingAnimations(Entity shooter, ItemStack itemStack) {
        idle.sendForItem(shooter, itemStack);
    }

    protected void playUseAnimations(Entity shooter, ItemStack itemStack) {
        shoot.sendForItem(shooter, itemStack);
    }

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
    public void onUseTick(@NotNull Level level, @NotNull LivingEntity livingEntity, @NotNull ItemStack itemStack, int tickCountdown) {
        // Lack of server/client side check here is deliberate.
        if (!(livingEntity instanceof Player player)) {
            return;
        }

        var tickProgress = Math.abs(START_TICK_PROGRESS - tickCountdown);

        GunShootContext.create(player, itemStack, tickProgress)
            .map(GunShootContext::shoot)
            .ifSome(result -> {
                switch (result) {
                    // No side effects to run for these results at the time of writing.
                    case COOLDOWN, DELAYED, FAILURE, RELOADING -> { /* NO-OP */ }
                    case SHOT -> {
                        itemStack.set(AVPDataComponents.IS_FIRING.get(), true);
                        playUseAnimations(livingEntity, itemStack);
                    }
                }
            });
    }

    @Override
    public int getUseDuration(@NotNull ItemStack itemStack, @NotNull LivingEntity livingEntity) {
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

    @Override
    public void inventoryTick(@NotNull ItemStack itemStack, @NotNull Level level, @NotNull Entity entity, int i, boolean bl) {
        var isFiring = itemStack.get(AVPDataComponents.IS_FIRING.get());

        if (
            Boolean.TRUE.equals(isFiring)
                && entity instanceof LivingEntity livingEntity
                && !livingEntity.isUsingItem()
        ) {
            itemStack.set(AVPDataComponents.IS_FIRING.get(), false);
            playReleaseUsingAnimations(livingEntity, itemStack);
        }

        super.inventoryTick(itemStack, level, entity, i, bl);
    }

    public GunConfig getGunConfig() {
        return gunConfig;
    }

    @Override
    public void appendHoverText(
        @NotNull ItemStack itemStack,
        @NotNull TooltipContext tooltipContext,
        @NotNull List<Component> list,
        @NotNull TooltipFlag tooltipFlag
    ) {
        super.appendHoverText(itemStack, tooltipContext, list, tooltipFlag);

        int currentAmmunition = itemStack.getOrDefault(AVPDataComponents.AMMUNITION.get(), 0);
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

        // TODO: Don't hardcode old painless here.
        if (this != AVPItems.OLD_PAINLESS.get()) {
            TooltipUtil.appendLabel(
                list,
                "tooltip.avp.ammunition",
                Component.literal(currentAmmunition + " / " + gunConfig.maximumAmmunition())
            );
        }

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
}
