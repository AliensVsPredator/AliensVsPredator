package com.human.common.gameplay.item;

import com.human.common.gameplay.component.ArmorCaseContainerContents;
import com.human.common.gameplay.menu.armor_case.ArmorCaseMenu;
import com.human.common.registry.init.HumanDataComponents;
import com.lib.common.data.TooltipHintBuilder;
import com.lib.common.model.TooltipCategoryType;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.stream.Stream;

import com.avp.common.data.TooltipTranslationKeys;

public class ArmorCaseItem extends Item {

    private static final List<Component> TOOLTIP_COMPONENTS = new TooltipHintBuilder()
        .addCategory(TooltipCategoryType.WHEN_USED)
        .addPositiveEffect(TooltipTranslationKeys.EFFECT_AUTO_EQUIP_ARMOR_SET)
        .addCategory(TooltipCategoryType.WHEN_USED_ON_ARMOR_STAND)
        .addPositiveEffect(TooltipTranslationKeys.EFFECT_AUTO_EQUIP_ARMOR_STAND_ARMOR_SET)
        .build();

    private static final MenuProvider PROVIDER = new MenuProvider() {

        @Override
        public @NotNull Component getDisplayName() {
            return Component.empty();
        }

        @Override
        public @NotNull AbstractContainerMenu createMenu(int i, @NotNull Inventory inventory, @NotNull Player player) {
            return new ArmorCaseMenu(i, inventory);
        }
    };

    public ArmorCaseItem(Properties properties) {
        super(properties);
    }

    @Override
    public @NotNull InteractionResultHolder<ItemStack> use(Level level, @NotNull Player player, @NotNull InteractionHand interactionHand) {
        if (!level.isClientSide) {
            // TODO: Fix armor case menu.
            // if (player.isCrouching()) {
            // player.openMenu(PROVIDER);
            // } else {
            swapArmorSlots(player, player.getItemInHand(interactionHand));
            // }
        }

        return super.use(level, player, interactionHand);
    }

    @Override
    public void appendHoverText(
        @NotNull ItemStack itemStack,
        @NotNull TooltipContext tooltipContext,
        @NotNull List<Component> list,
        @NotNull TooltipFlag tooltipFlag
    ) {
        super.appendHoverText(itemStack, tooltipContext, list, tooltipFlag);
        list.addAll(TOOLTIP_COMPONENTS);

        var container = itemStack.getOrDefault(HumanDataComponents.ARMOR_CASE_CONTAINER.get(), ArmorCaseContainerContents.EMPTY);

        if (container.equals(ArmorCaseContainerContents.EMPTY)) {
            return;
        }

        list.add(CommonComponents.EMPTY);

        Stream.of(container.head(), container.chest(), container.legs(), container.feet())
            .filter(stack -> !stack.isEmpty())
            .forEach(stack -> list.add(stack.getHoverName().plainCopy().withStyle(ChatFormatting.GRAY)));
    }

    public static void swapArmorSlots(LivingEntity livingEntity, ItemStack itemStack) {
        var container = itemStack.getComponents()
            .getOrDefault(HumanDataComponents.ARMOR_CASE_CONTAINER.get(), ArmorCaseContainerContents.EMPTY);

        var headItemStack = livingEntity.getItemBySlot(EquipmentSlot.HEAD);
        var chestItemStack = livingEntity.getItemBySlot(EquipmentSlot.CHEST);
        var legsItemStack = livingEntity.getItemBySlot(EquipmentSlot.LEGS);
        var feetItemStack = livingEntity.getItemBySlot(EquipmentSlot.FEET);

        livingEntity.setItemSlot(EquipmentSlot.HEAD, container.head());
        livingEntity.setItemSlot(EquipmentSlot.CHEST, container.chest());
        livingEntity.setItemSlot(EquipmentSlot.LEGS, container.legs());
        livingEntity.setItemSlot(EquipmentSlot.FEET, container.feet());

        var newContainer = new ArmorCaseContainerContents(headItemStack, chestItemStack, legsItemStack, feetItemStack);

        itemStack.set(HumanDataComponents.ARMOR_CASE_CONTAINER.get(), newContainer);
    }
}
