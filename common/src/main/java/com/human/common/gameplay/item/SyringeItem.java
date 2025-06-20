package com.human.common.gameplay.item;

import com.alien.common.model.alien.GeneCarrier;
import com.alien.common.registry.GeneBonusDataRegistry;
import com.human.common.gameplay.component.SyringeContents;
import com.human.common.gameplay.component.SyringeMode;
import com.human.common.registry.init.HumanDataComponents;
import com.lib.common.gameplay.gene.GeneBonusDataEntry;
import com.lib.common.gameplay.gene.GeneOperationType;
import com.lib.common.gameplay.gene.GeneRegistry;
import com.lib.common.gameplay.gene.Genes;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.List;

import com.avp.AVPResources;

public class SyringeItem extends Item {

    private static final SyringeMode DEFAULT_SYRINGE_MODE = SyringeMode.EXTRACT;

    public SyringeItem() {
        super(new Item.Properties().stacksTo(1).component(HumanDataComponents.SYRINGE_MODE.get(), DEFAULT_SYRINGE_MODE));
    }

    @Override
    public @NotNull InteractionResultHolder<ItemStack> use(
        @NotNull Level level,
        @NotNull Player player,
        @NotNull InteractionHand hand
    ) {
        var stack = player.getItemInHand(hand);

        if (player.isShiftKeyDown()) {
            if (!level.isClientSide) {
                cycleSyringeMode(stack, player);
            }

            return InteractionResultHolder.sidedSuccess(stack, level.isClientSide);
        } else {
            if (!level.isClientSide) {
                var mode = stack.getOrDefault(HumanDataComponents.SYRINGE_MODE.get(), DEFAULT_SYRINGE_MODE);

                // If mode is EMPTY, forcibly set SYRINGE_CONTENTS to its EMPTY value.
                if (mode == SyringeMode.EMPTY) {
                    stack.set(HumanDataComponents.SYRINGE_CONTENTS.get(), SyringeContents.EMPTY);
                    // TODO: Use proper translatable here.
                    player.displayClientMessage(Component.literal("Genes have been cleared."), true);
                }
            }

            return super.use(level, player, hand);
        }
    }

    @Override
    public @NotNull InteractionResult useOn(@NotNull UseOnContext context) {
        var player = context.getPlayer();

        if (player == null) {
            return super.useOn(context);
        }

        if (player.isShiftKeyDown()) {
            if (!context.getLevel().isClientSide()) {
                cycleSyringeMode(context.getItemInHand(), context.getPlayer());
            }

            return InteractionResult.SUCCESS;
        } else {
            if (!context.getLevel().isClientSide()) {
                var stack = context.getItemInHand();
                var mode = stack.getOrDefault(HumanDataComponents.SYRINGE_MODE.get(), DEFAULT_SYRINGE_MODE);

                if (mode == SyringeMode.EMPTY) {
                    stack.set(HumanDataComponents.SYRINGE_CONTENTS.get(), SyringeContents.EMPTY);
                    // TODO: Use proper translatable here.
                    player.displayClientMessage(Component.literal("Genes have been cleared."), true);
                }
            }

            return super.useOn(context);
        }
    }

    private void cycleSyringeMode(ItemStack stack, Player player) {
        var current = stack.getOrDefault(HumanDataComponents.SYRINGE_MODE.get(), DEFAULT_SYRINGE_MODE);
        var next = nextMode(current);
        stack.set(HumanDataComponents.SYRINGE_MODE.get(), next);

        player.displayClientMessage(
            // TODO: Use proper translatable here.
            Component.literal("Switched mode to ")
                .append(
                    Component.literal(next.toString())
                        .withStyle(ChatFormatting.YELLOW)
                ),
            true
        );
    }

    private SyringeMode nextMode(SyringeMode current) {
        var values = SyringeMode.values();
        var nextOrdinal = (current.ordinal() + 1) % values.length;
        return values[nextOrdinal];
    }

    @Override
    public @NotNull InteractionResult interactLivingEntity(
        @NotNull ItemStack stack,
        @NotNull Player player,
        @NotNull LivingEntity interactionTarget,
        @NotNull InteractionHand usedHand
    ) {
        var syringeContents = stack.getOrDefault(HumanDataComponents.SYRINGE_CONTENTS.get(), SyringeContents.EMPTY);
        var syringeMode = stack.getOrDefault(HumanDataComponents.SYRINGE_MODE.get(), DEFAULT_SYRINGE_MODE);

        switch (syringeMode) {
            case EMPTY -> {
                stack.set(HumanDataComponents.SYRINGE_CONTENTS.get(), SyringeContents.EMPTY);
                // Assign stack back to player hand to update client-side.
                player.setItemInHand(usedHand, stack);
                return InteractionResult.SUCCESS;
            }
            case EXTRACT -> {
                // Extract genes from mob.
                var geneBonusMapData = GeneBonusDataRegistry.getOrDefault(interactionTarget.getType());

                if (geneBonusMapData.isEmpty()) {
                    return super.interactLivingEntity(stack, player, interactionTarget, usedHand);
                }

                // Create map to merge genes into.
                var currentGeneBonusMap = new HashMap<>(syringeContents.toMap());
                // Overwrite genes.
                currentGeneBonusMap.putAll(geneBonusMapData);
                // Convert back to list.
                var geneBonusDataEntries = currentGeneBonusMap.entrySet()
                    .stream()
                    .map(
                        entry -> new GeneBonusDataEntry(
                            entry.getKey().resourceLocation(),
                            entry.getKey().operation(),
                            entry.getValue()
                        )
                    )
                    .toList();
                // Assign syringe contents back to syringe item stack.
                stack.set(HumanDataComponents.SYRINGE_CONTENTS.get(), new SyringeContents(geneBonusDataEntries));
                // Assign stack back to player hand to update client-side.
                player.setItemInHand(usedHand, stack);
                // Apply "fake" damage to the entity.
                // TODO: Add a proper damage source here.
                interactionTarget.hurt(interactionTarget.damageSources().generic(), 0.01F);
                // TODO: Use proper translatable here.
                player.displayClientMessage(Component.literal("Extracted genes from target."), true);
                return InteractionResult.SUCCESS;
            }
            case INJECT -> {
                var geneCarrier = (GeneCarrier) interactionTarget;
                geneCarrier.getOrCreateGeneManager().putDormantGenes(syringeContents.toMap());
                // TODO: This is terribly unsafe, don't manually create the resource location here.
                var resourceLocation = AVPResources.location(Genes.GENETIC_INTEGRITY.get().id());
                geneCarrier.getOrCreateGeneManager().addDormantGene(resourceLocation, GeneOperationType.ADDITIVE, -0.34);

                stack.set(HumanDataComponents.SYRINGE_CONTENTS.get(), SyringeContents.EMPTY);
                // Assign stack back to player hand to update client-side.
                player.setItemInHand(usedHand, stack);
                // Apply "fake" damage to the entity.
                // TODO: Add a proper damage source here.
                interactionTarget.hurt(interactionTarget.damageSources().generic(), 0.01F);
                // TODO: Use proper translatable here.
                player.displayClientMessage(Component.literal("Injected target with genes."), true);
                return InteractionResult.SUCCESS;
            }
        }

        // Nothing new was added or modified; fall back to default behavior.
        return super.interactLivingEntity(stack, player, interactionTarget, usedHand);
    }

    @Override
    public void appendHoverText(
        @NotNull ItemStack itemStack,
        @NotNull TooltipContext tooltipContext,
        @NotNull List<Component> list,
        @NotNull TooltipFlag tooltipFlag
    ) {
        super.appendHoverText(itemStack, tooltipContext, list, tooltipFlag);
        // TODO: Add these once the syringe is functional.
        // list.addAll(TOOLTIP_COMPONENTS);
        // TODO: Use translatable here.
        list.add(Component.literal("Shift + Right Click to change mode.").withStyle(ChatFormatting.GRAY));
        list.add(CommonComponents.EMPTY);

        var mode = itemStack.getOrDefault(HumanDataComponents.SYRINGE_MODE.get(), DEFAULT_SYRINGE_MODE);

        list.add(
            Component.literal("Current Mode: ")
                .append(
                    Component.literal(mode.toString())
                        .withStyle(ChatFormatting.YELLOW)
                )
        );

        var syringeContents = itemStack.getOrDefault(HumanDataComponents.SYRINGE_CONTENTS.get(), SyringeContents.EMPTY);

        if (syringeContents.equals(SyringeContents.EMPTY)) {
            return;
        }

        list.add(CommonComponents.EMPTY);
        // TODO: Make this translatable.
        list.add(
            Component.literal("Genes:")
                .withStyle(ChatFormatting.YELLOW)
        );

        // TODO: Pretty this up later.
        syringeContents.toMap()
            .entrySet()
            .stream()
            .filter(entry -> GeneRegistry.getValue(entry.getKey().resourceLocation()).isSome())
            .forEach(
                entry -> list.add(
                    Component.translatable(GeneRegistry.getValue(entry.getKey().resourceLocation()).unwrap().getTranslationKey())
                        .append(Component.literal(": "))
                        .append(
                            Component.literal(format(entry.getKey().operation(), entry.getValue()))
                                .withStyle(getColorForValue(entry.getValue()))
                        )
                )
            );
    }

    private String format(GeneOperationType operation, double value) {
        var suffix = operation == GeneOperationType.MULTIPLICATIVE ? "%" : "";
        var sign = value > 0 ? "+" : "";
        var formattedValue = String.format("%.2f", operation == GeneOperationType.MULTIPLICATIVE ? value * 100 : value);

        return sign + formattedValue + suffix;
    }

    private ChatFormatting getColorForValue(double value) {
        if (value > 0) {
            return ChatFormatting.GREEN;
        } else if (value < 0) {
            return ChatFormatting.RED;
        }

        return ChatFormatting.GRAY;
    }
}
